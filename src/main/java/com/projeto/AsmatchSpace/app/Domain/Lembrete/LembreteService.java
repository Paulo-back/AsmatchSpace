package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class LembreteService {

    @Autowired
    private LembreteRepository repository;

    public Lembretes cadastrar(DadosCadastroLembrete dados, Cliente cliente) {
        var lembrete = new Lembretes(dados, cliente);
        return repository.save(lembrete);
    }

    public Page<DadosListagemLembrete> listar(Long clienteId, Pageable pageable) {
        return repository.findAllByClienteIdOrderByDataAscHorarioAsc(clienteId, pageable)
                .map(DadosListagemLembrete::new);
    }

    public Lembretes buscarPorId(Long id, Cliente cliente) {
        var lembrete = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lembrete não encontrado"));

        if (!lembrete.getCliente().getId().equals(cliente.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode atualizar lembretes de outro usuário.");

        return lembrete;
    }


    public void deletar(Long id, Cliente cliente) {
        var lembrete = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Lembrete não encontrado"));

        if (!lembrete.getCliente().getId().equals(cliente.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode excluir lembretes de outro usuário.");

        repository.delete(lembrete);
    }

}
