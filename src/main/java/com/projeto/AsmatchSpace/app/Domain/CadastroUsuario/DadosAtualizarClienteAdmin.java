package com.projeto.AsmatchSpace.app.Domain.CadastroUsuario;

import com.projeto.AsmatchSpace.app.Domain.endereco.DadosEndereco;
import jakarta.validation.Valid;

import java.time.LocalDate;

// ─────────────────────────────────────────────────────────────
//  DadosAtualizarClienteAdmin
//  Usado exclusivamente no PUT /clientes/atualizar/{id} quando
//  o usuário logado tem ROLE_ADMIN.
//  Inclui email, role, ativo e todos os campos clínicos/endereço.
// ─────────────────────────────────────────────────────────────
public record DadosAtualizarClienteAdmin(

        // dados pessoais
        String    nome,
        String    email,
        String    telefone,
        String    cpf,
        LocalDate dataNascimento,
        String    sexo,

        // acesso
        String  role,
        Boolean ativo,

        // saúde
        String problema_respiratorio,
        String medicamentos,
        String alergias,
        String contatoEmergencia,

        // endereço
        @Valid
        DadosEndereco endereco

) {}