package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import java.time.LocalDate;
import java.time.LocalTime;

public record DadosDetalhamentoTemplate(
        Long id,
        String titulo,
        LocalTime horario,
        LocalDate dataInicio,
        LocalDate dataFim,
        TipoRecorrencia tipoRecorrencia,
        String diasSemana
) {
    public DadosDetalhamentoTemplate(LembreteTemplate t) {
        this(t.getId(), t.getTitulo(), t.getHorario(),
                t.getDataInicio(), t.getDataFim(),
                t.getTipoRecorrencia(), t.getDiasSemana());
    }
}