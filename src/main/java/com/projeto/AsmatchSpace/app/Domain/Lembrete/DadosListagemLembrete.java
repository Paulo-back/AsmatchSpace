package com.projeto.AsmatchSpace.app.Domain.Lembrete;

public record DadosListagemLembrete(
        Long id,
        String titulo,
        String horario,
        Boolean concluido
) {
    public DadosListagemLembrete(Lembretes lembrete) {
        this(
                lembrete.getId(),
                lembrete.getTitulo(),
                lembrete.getHorario(),
                lembrete.isConcluido()
        );
    }
}

