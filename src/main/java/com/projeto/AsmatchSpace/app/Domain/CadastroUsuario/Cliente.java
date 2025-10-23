package com.projeto.AsmatchSpace.app.Domain.CadastroUsuario;

import com.projeto.AsmatchSpace.app.Domain.endereco.Endereco;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@EqualsAndHashCode(of = "id")
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "Cliente")
@Table(name = "clientes")
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nome;
    private String email;
    private String cpf;
    private String telefone;
    private int idade;
    private String sexo;
    @Embedded
    private Endereco endereco;
    private String problema_respiratorio;//talvez transformar em um ENUM
    private String medicamentos;
    private String alergias;
    private String contatoEmergencia;
    private String senha;

    public Cliente(DadosCadastroCliente dados) {
        this.nome = dados.nome();
        this.email = dados.email();
        this.telefone = dados.telefone();
        this.cpf = dados.cpf();
        this.endereco = new Endereco(dados.endereco());
        this.alergias = dados.alergias();
        this.contatoEmergencia = dados.contatoEmergencia();
        this.senha = dados.senha();
        this.problema_respiratorio = dados.problema_respiratorio();
        this.idade = dados.idade();
        this.sexo = dados.sexo();
        this.medicamentos = dados.medicamentos();
    }

}