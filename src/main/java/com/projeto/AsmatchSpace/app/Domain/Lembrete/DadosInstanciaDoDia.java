package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import java.time.LocalDate;
import java.time.LocalTime;

public record DadosInstanciaDoDia(
        Long instanciaId,
        Long templateId,
        String titulo,
        LocalTime horario,
        LocalDate data,
        StatusInstancia status
) {
    public DadosInstanciaDoDia(LembreteInstancia i) {
        this(i.getId(),
                i.getTemplate().getId(),
                i.getTemplate().getTitulo(),
                i.getHorarioEfetivo(),
                i.getDataInstancia(),
                i.getStatus());
    }


}