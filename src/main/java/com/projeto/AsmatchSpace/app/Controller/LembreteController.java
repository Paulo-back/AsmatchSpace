package com.projeto.AsmatchSpace.app.Controller;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.ClienteRepository;
import com.projeto.AsmatchSpace.app.Domain.Lembrete.*;
import com.projeto.AsmatchSpace.app.Domain.Usuario.Usuario;
import com.projeto.AsmatchSpace.app.Security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
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
    public ResponseEntity atualizar(@PathVariable Long id,
                                    @RequestBody @Valid DadosAtualizarLembrete dados) {

        // Usuário logado via SecurityContext
        Usuario usuarioLogado = (Usuario) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
        Long usuarioIdDoToken = usuarioLogado.getId();

        log.info("➡ Usuario ID Logado: {}", usuarioIdDoToken);

        // Busca o cliente baseado no usuario_id do token
        Cliente clienteLogado = clienteRepository.findByUsuarioId(usuarioIdDoToken)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        log.info("Cliente Logado ID: {}", clienteLogado.getId());

        // Busca o lembrete
        var lembrete = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lembrete não encontrado"));

        log.info("Lembrete {} pertence ao Cliente UsuarioID {}", id, lembrete.getCliente().getUsuario().getId());

        // // Verifica se o lembrete pertence ao usuário
        // if (!lembrete.getCliente().getUsuario().getId().equals(usuarioIdDoToken)) {

        //     log.warn("🚨 ACESSO NEGADO: Token Usuario {} ≠ Dono Usuario {}",
        //             usuarioIdDoToken,
        //             lembrete.getCliente().getUsuario().getId());

        //     return ResponseEntity.status(403).body("Você não pode atualizar lembretes de outro usuário.");
        // }

        // Atualização
        lembrete.atualizarInformacoes(dados);

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

