package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "lembretes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Lembretes {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String titulo;
    private String horario;
    private String data;
    private boolean concluido;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public Lembretes(DadosCadastroLembrete dados, Cliente cliente) {
        this.titulo = dados.titulo();
        this.horario = dados.horario();
        this.data = dados.data();
        this.concluido = false;
        this.cliente = cliente;
    }

    public void atualizarInformacoes(DadosAtualizarLembrete dados) {
        if (dados.titulo() != null) this.titulo = dados.titulo();
        if (dados.horario() != null) this.horario = dados.horario();
        if (dados.data() != null) this.data = dados.data();
        if (dados.concluido() != null) this.concluido = dados.concluido();
    }
}

