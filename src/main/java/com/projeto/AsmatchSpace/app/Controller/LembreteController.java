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
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para o usuário logado"));
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
    @PutMapping("/atualizar")
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizarLembrete dados, HttpServletRequest request) {

        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        var lembrete = repository.findById(dados.id())
                .orElseThrow(() -> new RuntimeException("Lembrete não encontrado"));

        // impede atualizar lembrete de outro cliente
        if (!lembrete.getCliente().getId().equals(cliente.getId()))
            return ResponseEntity.status(403).body("Você não pode atualizar lembretes de outro usuário.");

        lembrete.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoLembrete(lembrete));
    }


    // DELETE
    @DeleteMapping("/deletar/{id}")
    @Transactional
    public ResponseEntity deletar(@PathVariable Long id, HttpServletRequest request) {

        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        var lembrete = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lembrete não encontrado"));

        // impede deletar lembrete de outro cliente
        if (!lembrete.getCliente().getId().equals(cliente.getId()))
            return ResponseEntity.status(403).body("Você não pode excluir lembretes de outro usuário.");

        repository.delete(lembrete);

        return ResponseEntity.noContent().build();
    }

}

