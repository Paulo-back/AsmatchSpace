package com.projeto.AsmatchSpace.app.Domain.Usuario;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record SolicitarCodigoRequest(
        @NotBlank @Email String email
) {}