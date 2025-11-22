package com.projeto.AsmatchSpace.app.Controller;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.ClienteRepository;
import com.projeto.AsmatchSpace.app.Domain.Diario.*;
import com.projeto.AsmatchSpace.app.Security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
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

        var diario = new DiarioSintoma(dados, cliente);
        repository.save(diario);

        return ResponseEntity.ok(new DadosDetalhamentoDiario(diario));
    }

    // ---- LISTAR ---- //
    @GetMapping("/listar")
    public ResponseEntity listar(HttpServletRequest request) {

        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        var lista = repository.findAllByClienteId(cliente.getId())
                .stream()
                .map(DadosListagemDiario::new)
                .toList();

        return ResponseEntity.ok(lista);
    }

    // ---- ATUALIZAR ---- //
    @PutMapping("/atualizar")
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizarDiario dados, HttpServletRequest request) {

        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        var diario = repository.findById(dados.id())
                .orElseThrow(() -> new RuntimeException("Diário não encontrado"));

        // impede atualizar de outro cliente
        if (!diario.getCliente().getId().equals(cliente.getId()))
            return ResponseEntity.status(403).body("Você não pode atualizar registros de outro usuário.");

        diario.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoDiario(diario));
    }

    // ---- DELETAR ---- //
    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity deletar(@PathVariable Long id, HttpServletRequest request) {

        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        var diario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diário não encontrado"));

        // impede excluir de outro cliente
        if (!diario.getCliente().getId().equals(cliente.getId()))
            return ResponseEntity.status(403).body("Você não pode excluir registros de outro usuário.");

        repository.delete(diario);
        return ResponseEntity.noContent().build();
    }
}


