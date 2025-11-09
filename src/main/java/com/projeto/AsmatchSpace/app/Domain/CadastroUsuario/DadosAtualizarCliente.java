package com.projeto.AsmatchSpace.app.Domain.CadastroUsuario;

import com.projeto.AsmatchSpace.app.Domain.endereco.DadosEndereco;
import com.projeto.AsmatchSpace.app.Domain.endereco.Endereco;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;

public record DadosAtualizarCliente(
    Long id,
    String medicamentos,
    String telefone,
    String sexo,
    String problema_respiratorio,
    String alergias,
    String contatoEmergencia,
    @Valid
    DadosEndereco endereco
            ){
}



//public record DadosAtualizacaoPaciente(
//        Long id,
//        String nome,
//        String telefone,
//        @Valid DadosEndereco endereco
//) {
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
//private String senha;