package com.projeto.AsmatchSpace.app.Domain.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RedefinirSenhaComCodigoRequest(
        @NotBlank @Email String email,
        @NotBlank @Pattern(regexp = "\\d{6}", message = "Código deve ter 6 dígitos") String codigo,
        @NotBlank @Size(min = 6) String novaSenha
) {}