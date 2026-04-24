package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import java.time.LocalDate;
import java.time.LocalTime;

public record DadosListagemLembrete(
        Long id,
        String titulo,
        LocalTime horario,
        LocalDate data,
        Boolean concluido
) {
    public DadosListagemLembrete(Lembretes lembrete) {
        this(
                lembrete.getId(),
                lembrete.getTitulo(),
                lembrete.getHorario(),
                lembrete.getData(),
                lembrete.isConcluido()
        );
    }
}

