package com.projeto.AsmatchSpace.app.Controller;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.ClienteRepository;
import com.projeto.AsmatchSpace.app.Domain.Diario.*;
import com.projeto.AsmatchSpace.app.Security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/diario")
public class DiarioSintomaController {

    @Autowired
    private DiarioSintomaRepository repository;
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private TokenService tokenService;

    @Autowired
    private DiarioSintomaService diarioService;

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

    // ---- CADASTRAR ---- //
    @PostMapping("/cadastro")
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroDiario dados, HttpServletRequest request) {
        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        var diario = diarioService.cadastrar(dados, cliente);
        return ResponseEntity.ok(new DadosDetalhamentoDiario(diario));
    }

    // ---- LISTAR ---- //
    @GetMapping("/listar")
    public ResponseEntity<Page<DadosListagemDiario>> listar(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            HttpServletRequest request) {

        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        Pageable pageable = PageRequest.of(page, size);
        Page<DadosListagemDiario> resultado = diarioService.listar(cliente.getId(), pageable);

        return ResponseEntity.ok(resultado);
    }

    // ---- ATUALIZAR ---- //
    @PutMapping("/atualizar/{id}")  // ou @PutMapping("/atualizar/{id}") se quiser manter o nome
    @Transactional
    public ResponseEntity<?> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizarDiario dados,
            HttpServletRequest request) {

        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        var diario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diário não encontrado"));

        // Impede atualizar registro de outro cliente
        if (!diario.getCliente().getId().equals(cliente.getId())) {
            return ResponseEntity.status(403)
                    .body("Você não pode atualizar registros de outro usuário.");
        }


        diario.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoDiario(diario));
    }

    // ---- DELETAR ---- //
    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity deletar(@PathVariable Long id, HttpServletRequest request) {
        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        diarioService.deletar(id, cliente);
        return ResponseEntity.noContent().build();
    }
}


