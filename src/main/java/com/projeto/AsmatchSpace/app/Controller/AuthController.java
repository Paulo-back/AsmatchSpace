package com.projeto.AsmatchSpace.app.Controller;

import com.projeto.AsmatchSpace.app.Domain.Usuario.AuthService;
import com.projeto.AsmatchSpace.app.Domain.Usuario.RedefinirSenhaComCodigoRequest;
import com.projeto.AsmatchSpace.app.Domain.Usuario.SolicitarCodigoRequest;
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

    @PostMapping("/recuperar-senha/solicitar")
    public ResponseEntity<?> solicitarCodigo(@RequestBody @Valid SolicitarCodigoRequest req) {
        return authService.solicitarCodigoRecuperacao(req.email());
    }

    @PostMapping("/recuperar-senha/redefinir")
    public ResponseEntity<?> redefinirSenha(@RequestBody @Valid RedefinirSenhaComCodigoRequest req) {
        return authService.redefinirSenhaComCodigo(req);
    }
}