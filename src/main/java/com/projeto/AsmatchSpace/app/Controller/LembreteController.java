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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
@RequestMapping("/lembretes")
public class LembreteController {

    @Autowired
    private LembreteService lembreteService;
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
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para o usuário logado"));
    }

    @PostMapping("/cadastro")
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroLembrete dados, HttpServletRequest request) {
        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        var lembrete = lembreteService.cadastrar(dados, cliente);
        return ResponseEntity.ok(new DadosDetalhamentoLembrete(lembrete));
    }

    @GetMapping("/listar")
    public ResponseEntity<Page<DadosListagemLembrete>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        Pageable pageable = PageRequest.of(page, size);
        return ResponseEntity.ok(lembreteService.listar(cliente.getId(), pageable));
    }

    @PutMapping("/atualizar/{id}")
    @Transactional
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizarLembrete dados,
            HttpServletRequest request) {

        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        // atualizar pode ficar aqui por ora — mover pro service depois se quiser
        var lembrete = lembreteService.buscarPorId(id, cliente);
        lembrete.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoLembrete(lembrete));
    }

    @DeleteMapping("/deletar/{id}")
    @Transactional
    public ResponseEntity deletar(@PathVariable Long id, HttpServletRequest request) {
        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        lembreteService.deletar(id, cliente);
        return ResponseEntity.noContent().build();
    }
}

