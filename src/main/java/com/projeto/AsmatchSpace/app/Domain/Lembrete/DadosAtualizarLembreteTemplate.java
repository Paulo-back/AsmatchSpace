package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import java.time.LocalDate;
import java.time.LocalTime;

public record DadosAtualizarLembreteTemplate(
        String titulo,
        LocalTime horario,
        LocalDate dataInicio,
        LocalDate dataFim,
        TipoRecorrencia tipoRecorrencia,
        String diasSemana
) {}