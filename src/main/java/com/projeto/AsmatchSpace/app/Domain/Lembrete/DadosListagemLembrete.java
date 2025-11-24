package com.projeto.AsmatchSpace.app.Domain.Lembrete;

public record DadosListagemLembrete(
        Long id,
        String titulo,
        String horario,
        String data,
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

