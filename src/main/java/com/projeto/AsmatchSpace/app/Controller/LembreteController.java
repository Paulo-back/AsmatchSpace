package com.projeto.AsmatchSpace.app.Controller;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.ClienteRepository;
import com.projeto.AsmatchSpace.app.Domain.Lembrete.*;
import com.projeto.AsmatchSpace.app.Security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/lembretes")
public class LembreteController {

    @Autowired
    private LembreteRepository repository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private TokenService tokenService;

    private static final Logger log = LoggerFactory.getLogger(LembreteController.class);

    private Long getUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer "))
            throw new RuntimeException("Token não enviado");

        return tokenService.getUserId(header.replace("Bearer ", ""));
    }

    private Cliente getClienteLogado(Long usuarioId) {
        return clienteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
    }

    // CADASTRAR
    @PostMapping("/cadastro")
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroLembrete dados, HttpServletRequest request) {

        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        var lembrete = new Lembretes(dados, cliente);
        repository.save(lembrete);

        return ResponseEntity.ok(new DadosDetalhamentoLembrete(lembrete));
    }

    // LISTAR
    @GetMapping("/listar")
    public ResponseEntity listar(HttpServletRequest request) {

        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        var lista = repository.findAllByClienteId(cliente.getId())
                .stream()
                .map(DadosListagemLembrete::new)
                .toList();

        return ResponseEntity.ok(lista);
    }

    // ATUALIZAR
    @PutMapping("/atualizar/{id}")
    @Transactional
    public ResponseEntity atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizarLembrete dados,
            HttpServletRequest request) {

        // 1. Pega ID do usuário via token
        Long idUsuarioDoToken = getUserId(request);
        log.info("➡ TOKEN USER ID = {}", idUsuarioDoToken);

        // 2. Carrega cliente logado pelo usuário_id
        Cliente clienteLogado = clienteRepository.findByUsuarioId(idUsuarioDoToken)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        log.info("➡ CLIENTE LOGADO ID = {}", clienteLogado.getId());

        // 3. Busca o lembrete
        var lembrete = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lembrete não encontrado"));

        // 4. Extrai ID do usuário dono do lembrete
        Long donoDoLembreteUsuarioId = lembrete.getCliente().getUsuario().getId();
        log.info("➡ DONO DO LEMBRETE (usuario_id) = {}", donoDoLembreteUsuarioId);

        // 5. Comparação correta (usuario_id → usuario_id)
        if (!donoDoLembreteUsuarioId.equals(idUsuarioDoToken)) {
            log.warn("🚨 NEGADO: token {} ≠ lembrete.dono {}", idUsuarioDoToken, donoDoLembreteUsuarioId);
            return ResponseEntity.status(403).body("Você não pode atualizar lembretes de outro usuário.");
        }

        // 6. Atualiza
        lembrete.atualizarInformacoes(dados);

        log.info("✅ ACESSO PERMITIDO");
        return ResponseEntity.ok(new DadosDetalhamentoLembrete(lembrete));
    }



    // DELETE
    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity deletar(@PathVariable Long id, HttpServletRequest request) {

        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        var lembrete = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lembrete não encontrado"));

        if (!lembrete.getCliente().getId().equals(cliente.getId()))
            return ResponseEntity.status(403).body("Você não pode excluir lembretes de outro usuário.");

        repository.delete(lembrete);

        return ResponseEntity.noContent().build();
    }
}

