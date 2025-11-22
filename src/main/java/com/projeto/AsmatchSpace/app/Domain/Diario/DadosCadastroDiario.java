package com.projeto.AsmatchSpace.app.Domain.Diario;

import jakarta.validation.constraints.NotBlank;

public record DadosCadastroDiario(
        @NotBlank String data,
        @NotBlank String horario,
        @NotBlank String intensidade,
        @NotBlank String descricao
) {}
