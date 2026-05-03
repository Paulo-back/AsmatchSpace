package com.projeto.AsmatchSpace.app.Controller;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.*;
import com.projeto.AsmatchSpace.app.Domain.Usuario.Role;
import com.projeto.AsmatchSpace.app.Domain.Usuario.Usuario;
import com.projeto.AsmatchSpace.app.Domain.Usuario.UsuarioRepository;
import com.projeto.AsmatchSpace.app.Security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;


@RestController
@RequestMapping("clientes")
//@SecurityRequirement(name = "bearer-key")
public class ClienteController {

    @Autowired
    ClienteRepository repository;

    @Autowired
    UsuarioRepository usuarioRepository;

    @Autowired
    private ClienteService clienteService;


    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/cadastro")
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroCliente dados,
                                    UriComponentsBuilder uriBuilder) {

        var senhaCriptografada = passwordEncoder.encode(dados.senha());
        var usuario = new Usuario(dados.email(), senhaCriptografada);
        usuarioRepository.save(usuario);

        var cliente = clienteService.cadastrar(dados, usuario);

        var uri = uriBuilder.path("/cadastro/{id}")
                .buildAndExpand(cliente.getId())
                .toUri();

        return ResponseEntity.created(uri)
                .body(new DadosDetalhamentoCliente(cliente));
    }

    @GetMapping("/me")
    public ResponseEntity<DadosDetalhamentoCliente> getMe(HttpServletRequest request) {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body(null);
        }

        String token = header.substring(7);
        Long idUsuario = tokenService.getUserId(token);

        var cliente = repository.findByUsuarioId(idUsuario)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return ResponseEntity.ok(new DadosDetalhamentoCliente(cliente));
    }

    @GetMapping("/me/id")
    public ResponseEntity<Long> getMeuId(HttpServletRequest request) {
        String token = request.getHeader("Authorization").substring(7);
        Long usuarioId = tokenService.getUserId(token);

        Cliente cliente = repository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        return ResponseEntity.ok(cliente.getId());
    }



//PADRAO
    @PutMapping("/atualizar")
    @Transactional
    public ResponseEntity<DadosDetalhamentoCliente> atualizarProprioPerfil(
            @RequestBody @Valid DadosAtualizarCliente dados,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization").substring(7);
        Long idLogado = tokenService.getUserId(token);

        Cliente cliente = repository.findByUsuarioId(idLogado)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        cliente.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoCliente(cliente));
    }

    @PutMapping("/senha")
    @Transactional
    public ResponseEntity alterarSenha(
            @RequestBody @Valid DadosAlterarSenha dados,
            HttpServletRequest request) {

        String token = request.getHeader("Authorization").substring(7);
        Long idLogado = tokenService.getUserId(token);

        Usuario usuario = usuarioRepository.findById(idLogado)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        // Valida senha atual
        if (!passwordEncoder.matches(dados.senhaAtual(), usuario.getSenha())) {
            return ResponseEntity.status(400).body("Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(dados.novaSenha()));
        return ResponseEntity.noContent().build();
    }




    @DeleteMapping("/inativar/{id}")
    @Transactional
    public ResponseEntity inativar(
            @PathVariable Long id,
            HttpServletRequest request
    ) {

        // 1 – Extrai o token do header
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(401).body("Token inválido ou ausente");
        }

        String token = header.substring(7);
        Long idLogado = tokenService.getUserId(token);

        // 2 – Pega a role do usuário logado
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        Cliente cliente;

        // 3 – ADMIN pode deletar qualquer ID enviado
        if (role.equals("ROLE_ADMIN")) {
            cliente = repository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        }
        // 4 – USER só deleta o próprio cliente
        else {
            cliente = repository.findByUsuarioId(idLogado)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado para o usuário logado"));
        }

        // 5 – Executa o delete lógico
        cliente.inativar();

        return ResponseEntity.noContent().build();
    }


//    @DeleteMapping("/delete/{id}")
//    @Transactional
//    public ResponseEntity deletar(@PathVariable Long id){
//
//        var cliente = repository.getReferenceById(id);
//        repository.delete(cliente);
//
//        return ResponseEntity.noContent().build();
//    }

    // 1 ── LISTAGEM

    @GetMapping("/listagem")
    public ResponseEntity<Page<?>> listar(
            @PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao,
            HttpServletRequest request) {

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities()
                .stream().findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        if ("ROLE_ADMIN".equals(role)) {
            // Admin vê todos + dados completos (incluindo role e ativo)
            var page = repository.findAll(paginacao)
                    .map(DadosDetalhamentoAdmin::new);
            return ResponseEntity.ok(page);
        }

        // Usuários comuns: só ativos, só dados básicos
        var page = repository.findAllByAtivoTrue(paginacao)
                .map(DadosListagemClientes::new);
        return ResponseEntity.ok(page);
    }

    // 2 ── ATUALIZAÇÃO ADMIN

    @PutMapping("/atualizar/{id}")
    @Transactional
    public ResponseEntity<DadosDetalhamentoAdmin> atualizar(
            @PathVariable Long id,
            @RequestBody @Valid DadosAtualizarClienteAdmin dados,
            HttpServletRequest request) {

        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            return ResponseEntity.status(401).build();
        }

        String token    = header.substring(7);
        Long   idLogado = tokenService.getUserId(token);

        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String roleAtual = authentication.getAuthorities()
                .stream().findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");

        if (!"ROLE_ADMIN".equals(roleAtual)) {
            return ResponseEntity.status(403).build();
        }

        Cliente cliente = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));

        // 1 ── Atualiza campos da entidade Cliente via método existente
        cliente.atualizarInformacoes(new DadosAtualizarCliente(
                dados.nome(),
                dados.medicamentos(),
                dados.telefone(),
                dados.sexo(),
                dados.dataNascimento(),
                dados.problema_respiratorio(),
                dados.cpf(),
                dados.alergias(),
                dados.contatoEmergencia(),
                dados.endereco()
        ));

        // 2 ── Atualiza login (email) no Usuario vinculado
        if (dados.email() != null && !dados.email().isBlank()) {
            cliente.getUsuario().setLogin(dados.email());
        }

        // 3 ── Atualiza role no Usuario — converte String → enum Role
        if (dados.role() != null && !dados.role().isBlank()) {
            try {
                // Frontend envia "ADMIN", "USER", "MEDICO"
                // O enum armazena "ROLE_ADMIN", "ROLE_USER", "ROLE_MEDICO"
                String roleStr = dados.role().startsWith("ROLE_")
                        ? dados.role()
                        : "ROLE_" + dados.role();
                cliente.getUsuario().setRole(Role.valueOf(roleStr));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }

        // 4 ── Atualiza status ativo no Cliente
        if (dados.ativo() != null) {
            if (dados.ativo()) {
                cliente.reativar();
            } else {
                cliente.inativar();
            }
        }

        return ResponseEntity.ok(new DadosDetalhamentoAdmin(cliente));
    }


}




