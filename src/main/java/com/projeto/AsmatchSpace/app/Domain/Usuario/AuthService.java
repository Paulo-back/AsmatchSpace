package com.projeto.AsmatchSpace.app.Domain.Usuario;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.ClienteRepository;
import com.projeto.AsmatchSpace.app.Security.EmailService;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Map;
import java.util.Optional;

@Service
public class AuthService {

    private static final ZoneId ZONA_SP = ZoneId.of("America/Sao_Paulo");
    private static final int VALIDADE_CODIGO_MINUTOS = 15;
    private static final int INTERVALO_REENVIO_SEGUNDOS = 60;

    private final ClienteRepository clienteRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;
    private final SecureRandom random = new SecureRandom();

    public AuthService(ClienteRepository clienteRepository,
                       PasswordEncoder passwordEncoder,
                       EmailService emailService) {
        this.clienteRepository = clienteRepository;
        this.passwordEncoder = passwordEncoder;
        this.emailService = emailService;
    }

    @Transactional
    public ResponseEntity<?> solicitarCodigoRecuperacao(String email) {
        Optional<Cliente> opt = clienteRepository.findByEmail(email);

        if (opt.isPresent()) {
            Usuario usuario = opt.get().getUsuario();
            LocalDateTime agora = LocalDateTime.now(ZONA_SP);

            // Rate limit simples: bloqueia reenvio antes de 60s
            if (usuario.getCodigoExpiracao() != null) {
                LocalDateTime ultimoEnvio = usuario.getCodigoExpiracao()
                        .minusMinutes(VALIDADE_CODIGO_MINUTOS);
                if (ultimoEnvio.plusSeconds(INTERVALO_REENVIO_SEGUNDOS).isAfter(agora)) {
                    return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS)
                            .body(Map.of("mensagem",
                                    "Aguarde um minuto antes de solicitar um novo código."));
                }
            }

            String codigo = String.format("%06d", random.nextInt(1_000_000));
            usuario.definirCodigoRecuperacao(codigo, agora.plusMinutes(VALIDADE_CODIGO_MINUTOS));

            emailService.enviarCodigoRecuperacao(email, codigo);
        }

        // Resposta idêntica com ou sem cadastro — evita enumeração de emails
        return ResponseEntity.ok(Map.of("mensagem",
                "Se o email estiver cadastrado, um código foi enviado."));
    }

    @Transactional
    public ResponseEntity<?> redefinirSenhaComCodigo(RedefinirSenhaComCodigoRequest req) {
        Optional<Cliente> opt = clienteRepository.findByEmail(req.email());

        if (opt.isEmpty() || !opt.get().getUsuario().codigoValido(req.codigo())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("mensagem", "Código inválido ou expirado."));
        }

        Usuario usuario = opt.get().getUsuario();
        usuario.setSenha(passwordEncoder.encode(req.novaSenha()));
        usuario.registrarTrocaDeSenha();
        usuario.limparCodigoRecuperacao(); // uso único

        return ResponseEntity.ok(Map.of("mensagem", "Senha redefinida com sucesso."));
    }
}