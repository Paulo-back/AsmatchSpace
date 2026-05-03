package com.projeto.AsmatchSpace.app.Domain.CadastroUsuario;

import com.projeto.AsmatchSpace.app.Domain.CpfUtil;
import com.projeto.AsmatchSpace.app.Domain.Usuario.Usuario;
import com.projeto.AsmatchSpace.app.Domain.Usuario.UsuarioRepository;
import com.projeto.AsmatchSpace.app.Domain.endereco.Endereco;
import com.projeto.AsmatchSpace.app.exception.ValidacaoException;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

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
//    private int idade;
    private LocalDate dataNascimento;
    private String sexo;
    @Embedded
    private Endereco endereco;
    private String problema_respiratorio;//talvez transformar em um ENUM
    private String medicamentos;
    private String alergias;
    private String contatoEmergencia;
    private Boolean ativo;
//    private String senha;

    public Cliente(DadosCadastroCliente dados, Usuario usuario, String cpfNormalizado) {
        this.nome = dados.nome();
        this.email = dados.email();
        this.telefone = dados.telefone();
        this.cpf = cpfNormalizado; // já validado no service
        this.dataNascimento = dados.dataNascimento();
        this.sexo = dados.sexo();
        this.usuario = usuario;
        this.ativo = true;

        this.medicamentos = dados.medicamentos();
        this.problema_respiratorio = dados.problema_respiratorio();
        this.contatoEmergencia = dados.contatoEmergencia();
        this.endereco = dados.endereco();
    }

    public void atualizarInformacoes(DadosAtualizarCliente dados) {

        if (dados.medicamentos() != null)
            this.medicamentos = dados.medicamentos();

        if (dados.telefone() != null)
            this.telefone = dados.telefone();

        if (dados.endereco() != null)
            this.endereco.atualizarInformacoes(dados.endereco());

        if (dados.sexo() != null)
            this.sexo = dados.sexo();

        if (dados.problema_respiratorio() != null)
            this.problema_respiratorio = dados.problema_respiratorio();

        if (dados.alergias() != null)
            this.alergias = dados.alergias();

        if (dados.contatoEmergencia() != null)
            this.contatoEmergencia = dados.contatoEmergencia();

//        if (dados.idade() != null)
//            this.idade = dados.idade();
        if (dados.dataNascimento() != null)
            this.dataNascimento = dados.dataNascimento();

        if (dados.nome() != null)
            this.nome = dados.nome();

        if (dados.cpf() != null)
            this.cpf = dados.cpf(); // sem validação aqui
    }


    public void inativar() {
            this.ativo = false;

    }
    public void reativar() {
        this.ativo = true;

    }

    public void atualizarCpf(String cpf) {
        this.cpf = cpf;
    }
}