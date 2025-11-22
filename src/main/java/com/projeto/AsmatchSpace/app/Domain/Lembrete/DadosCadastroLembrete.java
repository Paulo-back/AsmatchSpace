package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastroLembrete(
        @NotBlank String titulo,
        @NotBlank String horario,
        @NotBlank String data
) {}
