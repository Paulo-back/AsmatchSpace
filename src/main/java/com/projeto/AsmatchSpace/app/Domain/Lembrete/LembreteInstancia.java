package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "lembrete_instancias")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LembreteInstancia {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "template_id")
    private LembreteTemplate template;

    private LocalDate dataInstancia;
    private LocalTime horarioEfetivo;

    @Enumerated(EnumType.STRING)
    private StatusInstancia status = StatusInstancia.PENDENTE;

    private boolean notificacaoEnviada = false;

    public LembreteInstancia(LembreteTemplate template, LocalDate data) {
        this.template        = template;
        this.dataInstancia   = data;
        this.horarioEfetivo  = template.getHorario();
        this.status          = StatusInstancia.PENDENTE;
        this.notificacaoEnviada = false;
    }
}