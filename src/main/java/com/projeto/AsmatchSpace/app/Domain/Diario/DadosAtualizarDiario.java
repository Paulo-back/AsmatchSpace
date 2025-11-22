package com.projeto.AsmatchSpace.app.Domain.Diario;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizarDiario(
        @NotNull Long id,
        String data,
        String horario,
        String intensidade,
        String descricao
) {}

