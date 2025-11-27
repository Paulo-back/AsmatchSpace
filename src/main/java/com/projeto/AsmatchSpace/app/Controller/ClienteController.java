package com.projeto.AsmatchSpace.app.Controller;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.*;
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
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private TokenService tokenService;

    @PostMapping("/cadastro")
    @Transactional
    public ResponseEntity cadastrar(@RequestBody @Valid DadosCadastroCliente dados, UriComponentsBuilder uriBuilder){
        var senhaCriptografada = passwordEncoder.encode(dados.senha());
        var usuario = new Usuario(dados.email(),senhaCriptografada);
        usuarioRepository.save(usuario);

        var cliente = new Cliente(dados,usuario);
        repository.save(cliente);
        System.out.println("Cliente cadastrado com sucesso!");
        var uri = uriBuilder.path("/cadastro/{id}").buildAndExpand(cliente.getId()).toUri();
        return ResponseEntity.created(uri).body(new DadosDetalhamentoCliente(cliente));
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




    @PutMapping("/atualizar")
    @Transactional
    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizarCliente dados, HttpServletRequest request) {

        // Extrai ID do usuário logado
        String token = request.getHeader("Authorization").replace("Bearer ", "");
        Long idLogado = tokenService.getUserId(token);

        // Obtém role (ROLE_USER ou ROLE_ADMIN)
        var authentication = SecurityContextHolder.getContext().getAuthentication();
        String role = authentication.getAuthorities()
                .stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");


        Cliente cliente;

        // ADMIN pode atualizar qualquer cliente (usando ID passado)
        if (role.equals("ROLE_ADMIN")) {
            cliente = repository.findById(dados.id())
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado"));
        }
        // USER só pode atualizar o próprio cliente vinculado
        else {
            cliente = repository.findByUsuarioId(idLogado)
                    .orElseThrow(() -> new RuntimeException("Cliente não encontrado para o usuário logado"));
        }

        // Atualiza os dados
        cliente.atualizarInformacoes(dados);

        return ResponseEntity.ok(new DadosDetalhamentoCliente(cliente));
    }




//    @PutMapping("/atualizar")
//    @Transactional
//    public ResponseEntity atualizar(@RequestBody @Valid DadosAtualizarCliente dados, HttpServletRequest request) {
//
//
//        String token = request.getHeader("Authorization").replace("Bearer ", "");
//        Long idLogado = tokenService.getUserId(token);
//
//        var authentication = SecurityContextHolder.getContext().getAuthentication();
//        String role = authentication.getAuthorities().stream().findFirst().get().getAuthority(); // ROLE_USER ou ROLE_ADMIN
//
//        // Se não for admin, só pode atualizar ele mesmo
//        var cliente = repository.getReferenceById(dados.id());
//        if (!role.equals("ROLE_ADMIN") && !idLogado.equals(cliente.getUsuario().getId())) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Você só pode atualizar seu próprio perfil.");
//        }
//
//        cliente.atualizarInformacoes(dados);
//
//        return ResponseEntity.ok(new DadosDetalhamentoCliente(cliente));
//    }

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


    @DeleteMapping("/delete/{id}")
    @Transactional
    public ResponseEntity deletar(@PathVariable Long id){

        var cliente = repository.getReferenceById(id);
        repository.delete(cliente);

        return ResponseEntity.noContent().build();
    }

    //Listar
    @GetMapping("/listagem")
    public ResponseEntity<Page<DadosListagemClientes>> listar(@PageableDefault(size = 10, sort = {"nome"}) Pageable paginacao) {
        var page = repository.findAllByAtivoTrue(paginacao).map(DadosListagemClientes::new);
        return ResponseEntity.ok(page);
    }



}




