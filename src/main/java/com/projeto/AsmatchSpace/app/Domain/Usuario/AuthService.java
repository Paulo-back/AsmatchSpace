package com.projeto.AsmatchSpace.app.Domain.Usuario;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.ClienteRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class AuthService {

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;

    private final Map<String, String> tokenRedefinicaoMap = new ConcurrentHashMap<>();
    private final Map<String, Long>   tokenExpiracaoMap   = new ConcurrentHashMap<>();
    private static final long TTL_MS = 15 * 60 * 1000L;

    public AuthService(ClienteRepository clienteRepository, PasswordEncoder passwordEncoder) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder   = passwordEncoder;
    }

    public ResponseEntity<?> consultarInfoRecuperacao(String email) {
        Optional<Cliente> opt = clienteRepository.findByEmail(email);

        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("E-mail não encontrado.");
        }

        Cliente cliente = opt.get();
        boolean temCpf = cliente.getCpf() != null && !cliente.getCpf().isBlank();

        Map<String, Object> resposta = new HashMap<>();
        resposta.put("temCpf", temCpf);

        return ResponseEntity.ok(resposta);
    }

    public ResponseEntity<?> verificarIdentidade(VerificarIdentidadeRequest req) {
        Optional<Cliente> opt = clienteRepository.findByEmail(req.getEmail());

        if (opt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Dados não conferem com nosso cadastro.");
        }

        Cliente cliente = opt.get();

        if (!cliente.getDataNascimento().equals(req.getDataNascimento())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Dados não conferem com nosso cadastro.");
        }

        if (cliente.getCpf() != null && !cliente.getCpf().isBlank()) {
            if (req.getCpf() == null) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Dados não conferem com nosso cadastro.");
            }
            // Normaliza ambos os lados antes de comparar
            String cpfBanco = cliente.getCpf().replaceAll("[^0-9]", "");
            String cpfReq   = req.getCpf().replaceAll("[^0-9]", "");
            if (!cpfBanco.equals(cpfReq)) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                        .body("Dados não conferem com nosso cadastro.");
            }
        }

        String token = UUID.randomUUID().toString();
        tokenRedefinicaoMap.put(token, cliente.getEmail());
        tokenExpiracaoMap.put(token, System.currentTimeMillis() + TTL_MS);

        return ResponseEntity.ok(new VerificarIdentidadeResponse(token));
    }

    public ResponseEntity<?> redefinirSenha(RedefinirSenhaRequest req) {
        String token = req.getTokenRedefinicao();

        if (!tokenRedefinicaoMap.containsKey(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token inválido ou expirado.");
        }

        if (System.currentTimeMillis() > tokenExpiracaoMap.get(token)) {
            tokenRedefinicaoMap.remove(token);
            tokenExpiracaoMap.remove(token);
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body("Token expirado. Tente novamente.");
        }

        String email = tokenRedefinicaoMap.get(token);
        Cliente cliente = clienteRepository.findByEmail(email).orElseThrow();
        cliente.getUsuario().setSenha(passwordEncoder.encode(req.getNovaSenha()));
        clienteRepository.save(cliente);

        tokenRedefinicaoMap.remove(token);
        tokenExpiracaoMap.remove(token);

        return ResponseEntity.ok("Senha redefinida com sucesso.");
    }
}