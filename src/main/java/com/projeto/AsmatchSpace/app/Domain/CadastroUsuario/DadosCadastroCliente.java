package com.projeto.AsmatchSpace.app.Domain.CadastroUsuario;

import com.projeto.AsmatchSpace.app.Domain.endereco.DadosEndereco;
import com.projeto.AsmatchSpace.app.Domain.endereco.Endereco;
import jakarta.persistence.Embedded;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;

import java.time.LocalDate;

public record DadosCadastroCliente(

        @NotBlank(message = "Nome é obrigatório")
        String nome,

        @NotBlank(message = "E-mail é obrigatório")
        @Email(message = "E-mail inválido")
        String email,

//        @NotBlank(message = "Telefone é obrigatório")
//        @Pattern(
//        regexp = "\\d{10,11}",
//        message = "Telefone deve conter 10 ou 11 números")

        @Pattern(
                regexp = "^$|\\d{10,11}",
                message = "Telefone deve conter 10 ou 11 números"
        )
        String telefone,

//        @NotBlank(message = "CPF é obrigatório")
//        @Pattern(
//                regexp = "\\d{3}\\.?\\d{3}\\.?\\d{3}\\-?\\d{2}",
//                message = "CPF deve estar no formato 000.000.000-00"
//        )

        @Pattern(
                regexp = "^$|\\d{3}\\.?\\d{3}\\.?\\d{3}\\-?\\d{2}",
                message = "CPF deve estar no formato 000.000.000-00"
        )
        String cpf,

        @NotBlank(message = "Senha é obrigatória")
        @Size(min = 8, message = "Senha deve ter no mínimo 8 caracteres")
//        @Pattern(
//                regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&]).{8,}$",
//                message = "Senha deve ter no mínimo 8 caracteres, incluindo letra, número e caractere especial")
        String senha,

        @NotBlank(message = "Sexo é obrigatório")
        String sexo,

//        @NotNull(message = "Idade é obrigatória")
//        Integer idade,
        @NotNull(message = "Data de nascimento é obrigatória")
        LocalDate dataNascimento,

        //podem ser null
        String medicamentos,
        String problema_respiratorio,
        @Pattern(
                regexp = "^$|\\d{10,11}",
                message = "Telefone de emergência deve conter 10 ou 11 números"
        )
        String contatoEmergencia,
        @Valid
        Endereco endereco
        ) {
}
