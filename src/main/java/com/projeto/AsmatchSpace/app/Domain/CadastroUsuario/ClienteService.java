package com.projeto.AsmatchSpace.app.Domain.CadastroUsuario;

import com.projeto.AsmatchSpace.app.Domain.CpfUtil;
import com.projeto.AsmatchSpace.app.Domain.Usuario.Usuario;
import com.projeto.AsmatchSpace.app.exception.ValidacaoException;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.ZoneId;

@Service
public class ClienteService {

    private static final ZoneId ZONA_SP = ZoneId.of("America/Sao_Paulo");
    private static final int IDADE_MAXIMA = 120;

    @Autowired
    private ClienteRepository repository;

    @Transactional
    public Cliente cadastrar(DadosCadastroCliente dados, Usuario usuario) {
        String cpfNormalizado = null;

        if (dados.cpf() != null) {
            cpfNormalizado = CpfUtil.normalizar(dados.cpf());

            if (!CpfUtil.isValido(cpfNormalizado)) {
                throw new ValidacaoException("CPF inválido");
            }

            if (repository.existsByCpf(cpfNormalizado)) {
                throw new ValidacaoException("CPF já cadastrado");
            }
        }

        validarDataNascimento(dados.dataNascimento());

        var cliente = new Cliente(dados, usuario, cpfNormalizado);
        return repository.save(cliente);
    }

    @Transactional
    public Cliente atualizar(Cliente cliente, DadosAtualizarCliente dados) {

        if (dados.cpf() != null) {
            String cpfNormalizado = CpfUtil.normalizar(dados.cpf());

            if (!CpfUtil.isValido(cpfNormalizado)) {
                throw new ValidacaoException("CPF inválido");
            }


            if (repository.existsByCpf(cpfNormalizado) &&
                    !cpfNormalizado.equals(cliente.getCpf())) {

                throw new ValidacaoException("CPF já cadastrado");
            }

            cliente.atualizarCpf(cpfNormalizado);
        }

        validarDataNascimento(dados.dataNascimento());

        // ✅ atualiza os outros campos
        cliente.atualizarInformacoes(dados);

        return cliente;
    }

    private void validarDataNascimento(LocalDate dataNascimento) {
        if (dataNascimento == null) {
            return;
        }

        LocalDate hoje = LocalDate.now(ZONA_SP);

        if (dataNascimento.isAfter(hoje)) {
            throw new ValidacaoException("Data de nascimento não pode ser futura");
        }

        if (dataNascimento.isBefore(hoje.minusYears(IDADE_MAXIMA))) {
            throw new ValidacaoException("Data de nascimento inválida");
        }
    }
}