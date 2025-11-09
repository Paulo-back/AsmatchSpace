package com.projeto.AsmatchSpace.app.Domain.CadastroUsuario;

import com.projeto.AsmatchSpace.app.Domain.endereco.Endereco;
import jakarta.persistence.Embedded;

public record DadosListagemClientes(
        Long id, String nome, String email, String telefone,String problema_respiratorio,
        String medicamentos, String alergias, String contatoEmergencia
) {
    public DadosListagemClientes(Cliente cliente){
        this(cliente.getId(), cliente.getNome(), cliente.getEmail(), cliente.getTelefone(), cliente.getProblema_respiratorio(),
                cliente.getMedicamentos(), cliente.getAlergias(), cliente.getContatoEmergencia());
    }

}
//public record DadosListagemPaciente(Long id, String nome, String email, String cpf) {
//    public DadosListagemPaciente(Paciente paciente) {
//        this(paciente.getId(), paciente.getNome(), paciente.getEmail(), paciente.getCpf());
//    }
//}

//private String nome;
//private String email;
//private String cpf;
//private String telefone;
//private int idade;
//private String sexo;
//@Embedded
//private Endereco endereco;
//private String problema_respiratorio;//talvez transformar em um ENUM
//private String medicamentos;
//private String alergias;
//private String contatoEmergencia;
//private Boolean ativo;