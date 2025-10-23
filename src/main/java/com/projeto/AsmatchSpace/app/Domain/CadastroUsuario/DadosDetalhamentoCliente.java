package com.projeto.AsmatchSpace.app.Domain.CadastroUsuario;

import com.projeto.AsmatchSpace.app.Domain.endereco.Endereco;

public record DadosDetalhamentoCliente(String nome, String email, String telefone, String cpf, Integer idade,
                                       String sexo, Endereco endereco, String problema_respiratorio,
                                       String medicamentos, String alergias, String contatoEmergencia,
                                       String senha) {
}



//public record DadosDetalhamentoPaciente(String nome, String email, String telefone, String cpf, Endereco endereco) {
//    public DadosDetalhamentoPaciente(Paciente paciente) {
//        this(paciente.getNome(), paciente.getEmail(), paciente.getTelefone(), paciente.getCpf(), paciente.getEndereco());
//    }
//}




//private Long id;
//
//private String nome;
//private String email;
//private String cpf;
//private String telefone;
//private int idade;
//private String sexo;
//private String endereco;
//private String cidade;
//private String estado;
//private String Problema_respiratorio;//talvez transformar em um ENUM
//private String medicamentos;
//private String alergias;
//private String contatoEmergencia;
//private String senha;