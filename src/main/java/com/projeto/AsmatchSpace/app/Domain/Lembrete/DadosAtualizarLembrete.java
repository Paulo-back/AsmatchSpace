package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalTime;

public record DadosAtualizarLembrete(
        String titulo,
        LocalTime horario,
        LocalDate data,
        Boolean concluido
) {}

