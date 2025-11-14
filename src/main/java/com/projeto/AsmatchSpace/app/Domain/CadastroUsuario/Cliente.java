package com.projeto.AsmatchSpace.app.Domain.CadastroUsuario;

import com.projeto.AsmatchSpace.app.Domain.Usuario.Usuario;
import com.projeto.AsmatchSpace.app.Domain.Usuario.UsuarioRepository;
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

    @OneToOne
    @JoinColumn(name = "usuario_id")
    private Usuario usuario;

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
    private Boolean ativo;
//    private String senha;

    public Cliente(DadosCadastroCliente dados, Usuario usuario) {
        this.nome = dados.nome();
        this.email = dados.email();
        this.telefone = dados.telefone();
        this.cpf = dados.cpf();
        this.idade = dados.idade();
        this.sexo = dados.sexo();
        this.usuario = usuario;
        this.ativo = true;
    }

    public void atualizarInformacoes(DadosAtualizarCliente dados) {
        if (dados.medicamentos() != null)
            this.medicamentos = dados.medicamentos();

        if (dados.telefone() != null)
            this.telefone = dados.telefone();

        if (dados.endereco() != null)
            endereco.atualizarInformacoes(dados.endereco());

        if (dados.sexo() != null)
            this.sexo = dados.sexo();

        if (dados.problema_respiratorio() != null)
            this.problema_respiratorio = dados.problema_respiratorio();

        if (dados.alergias() != null)
            this.alergias = dados.alergias();

        if (dados.contatoEmergencia() != null)
            this.contatoEmergencia = dados.contatoEmergencia();

    }


    public void inativar() {
            this.ativo = false;

    }
}