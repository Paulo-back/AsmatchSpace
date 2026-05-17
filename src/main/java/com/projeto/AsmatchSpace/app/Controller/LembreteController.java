package com.projeto.AsmatchSpace.app.Controller;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.ClienteRepository;
import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import com.projeto.AsmatchSpace.app.Domain.Lembrete.*;
import com.projeto.AsmatchSpace.app.Security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/lembretes")
public class LembreteController {

    @Autowired private LembreteService lembreteService;
    @Autowired private ClienteRepository clienteRepository;
    @Autowired private TokenService tokenService;

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

    // ---- Templates ----

    @PostMapping("/templates")
    @Transactional
    public ResponseEntity<DadosDetalhamentoTemplate> cadastrarTemplate(
            @RequestBody @Valid DadosCadastroLembreteTemplate dados,
            HttpServletRequest request) {
        Cliente cliente = getClienteLogado(getUserId(request));
        var template = lembreteService.cadastrarTemplate(dados, cliente);
        return ResponseEntity.ok(new DadosDetalhamentoTemplate(template, null));
    }

    @GetMapping("/templates")
    public ResponseEntity<List<DadosDetalhamentoTemplate>> listarTemplates(
            HttpServletRequest request) {
        Cliente cliente = getClienteLogado(getUserId(request));
        List<DadosDetalhamentoTemplate> lista = lembreteService.listarTemplates(cliente.getId());
        return ResponseEntity.ok(lista);
    }

    @GetMapping("/instancias")
    @Transactional
    public ResponseEntity<List<DadosInstanciaDoDia>> instanciasPorPeriodo(
            @RequestParam(defaultValue = "7") int dias,
            HttpServletRequest request) {
        Cliente cliente = getClienteLogado(getUserId(request));
        return ResponseEntity.ok(lembreteService.gerarEListarInstanciasPorPeriodo(cliente, dias));
    }

    @PutMapping("/templates/{id}")
    @Transactional
    public ResponseEntity<DadosDetalhamentoTemplate> atualizarTemplate(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizarLembreteTemplate dados,
            HttpServletRequest request) {
        Cliente cliente = getClienteLogado(getUserId(request));
        var template = lembreteService.buscarTemplatePorId(id, cliente);
        template.atualizar(dados);
        return ResponseEntity.ok(new DadosDetalhamentoTemplate(template, null));
    }

    @DeleteMapping("/templates/{id}")
    @Transactional
    public ResponseEntity<Void> deletarTemplate(
            @PathVariable Long id, HttpServletRequest request) {
        Cliente cliente = getClienteLogado(getUserId(request));
        lembreteService.deletarTemplate(id, cliente);
        return ResponseEntity.noContent().build();
    }

    // ---- Instâncias ----

    /**
     * Android chama este endpoint ao abrir a tela de lembretes.
     * Gera as instâncias de hoje (se necessário) e retorna a lista do dia.
     */
    @PostMapping("/instancias/hoje")
    @Transactional
    public ResponseEntity<List<DadosInstanciaDoDia>> instanciasDeHoje(
            HttpServletRequest request) {
        Cliente cliente = getClienteLogado(getUserId(request));
        return ResponseEntity.ok(lembreteService.gerarEListarInstanciasDeHoje(cliente));
    }

    @PatchMapping("/instancias/{id}/status")
    @Transactional
    public ResponseEntity<Void> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusInstancia status,
            HttpServletRequest request) {
        Cliente cliente = getClienteLogado(getUserId(request));
        lembreteService.atualizarStatus(id, status, cliente);
        return ResponseEntity.noContent().build();
    }
}