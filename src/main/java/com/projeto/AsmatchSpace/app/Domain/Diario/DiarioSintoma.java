package com.projeto.AsmatchSpace.app.Domain.Diario;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "diario_sintomas")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DiarioSintoma {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String data;
    private String horario;
    private String intensidade;
    @Column(columnDefinition = "TEXT")
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "cliente_id")
    private Cliente cliente;

    public DiarioSintoma(DadosCadastroDiario dados, Cliente cliente) {
        this.data = dados.data();
        this.horario = dados.horario();
        this.intensidade = dados.intensidade();
        this.descricao = dados.descricao();
        this.cliente = cliente;
    }

    public void atualizarInformacoes(DadosAtualizarDiario dados) {
        if (dados.data() != null) this.data = dados.data();
        if (dados.horario() != null) this.horario = dados.horario();
        if (dados.intensidade() != null) this.intensidade = dados.intensidade();
        if (dados.descricao() != null) this.descricao = dados.descricao();
    }
}

