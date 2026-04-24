package com.projeto.AsmatchSpace.app.Controller;

import com.projeto.AsmatchSpace.app.Domain.Usuario.AuthService;
import com.projeto.AsmatchSpace.app.Domain.Usuario.RedefinirSenhaRequest;
import com.projeto.AsmatchSpace.app.Domain.Usuario.VerificarIdentidadeRequest;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @GetMapping("/recuperar-senha/info")
    public ResponseEntity<?> consultarInfoRecuperacao(@RequestParam String email) {
        return authService.consultarInfoRecuperacao(email);
    }

    @PostMapping("/recuperar-senha/verificar")
    public ResponseEntity<?> verificarIdentidade(@RequestBody @Valid VerificarIdentidadeRequest req) {
        return authService.verificarIdentidade(req);
    }

    @PostMapping("/recuperar-senha/redefinir")
    public ResponseEntity<?> redefinirSenha(@RequestBody @Valid RedefinirSenhaRequest req) {
        return authService.redefinirSenha(req);
    }
}