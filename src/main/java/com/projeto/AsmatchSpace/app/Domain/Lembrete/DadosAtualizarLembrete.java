package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import jakarta.validation.constraints.NotNull;

public record DadosAtualizarLembrete(
        String titulo,
        String horario,
        String data,
        Boolean concluido
) {}

