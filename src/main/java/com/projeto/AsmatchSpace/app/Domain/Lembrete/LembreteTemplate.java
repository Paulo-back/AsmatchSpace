package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Entity
@Table(name = "lembrete_templates")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class LembreteTemplate {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private LocalTime horario;
    private LocalDate dataInicio;
    private LocalDate dataFim;
    @Transient
    public String statusHoje;

    @Enumerated(EnumType.STRING)
    private TipoRecorrencia tipoRecorrencia = TipoRecorrencia.NENHUMA;

    // "1,3,5" = seg, qua, sex (DayOfWeek values)
    private String diasSemana;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public LembreteTemplate(DadosCadastroLembreteTemplate dados, Cliente cliente) {
        this.titulo          = dados.titulo();
        this.horario         = dados.horario();
        this.dataInicio      = dados.dataInicio();
        this.dataFim         = dados.dataFim();
        this.tipoRecorrencia = dados.tipoRecorrencia() != null
                ? dados.tipoRecorrencia() : TipoRecorrencia.NENHUMA;
        this.diasSemana      = dados.diasSemana();
        this.cliente         = cliente;
    }

    public void atualizar(DadosAtualizarLembreteTemplate dados) {
        if (dados.titulo()          != null) this.titulo          = dados.titulo();
        if (dados.horario()         != null) this.horario         = dados.horario();
        if (dados.dataInicio()      != null) this.dataInicio      = dados.dataInicio();
        if (dados.dataFim()         != null) this.dataFim         = dados.dataFim();
        if (dados.tipoRecorrencia() != null) this.tipoRecorrencia = dados.tipoRecorrencia();
        if (dados.diasSemana()      != null) this.diasSemana      = dados.diasSemana();
    }

    public boolean ativoEm(LocalDate data) {
        if (data.isBefore(dataInicio)) return false;
        if (dataFim != null && data.isAfter(dataFim)) return false;
        return switch (tipoRecorrencia) {
            case NENHUMA  -> data.isEqual(dataInicio);
            case DIARIA   -> true;
            case SEMANAL  -> diasSemana != null &&
                    diasSemana.contains(String.valueOf(data.getDayOfWeek().getValue()));
        };
    }
}
