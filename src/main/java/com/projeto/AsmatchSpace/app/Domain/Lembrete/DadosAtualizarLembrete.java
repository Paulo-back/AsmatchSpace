package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizarLembrete(
//        @NotNull Long id,
        String titulo,
        String horario,
        String data,
        Boolean concluido
) {}

