package com.projeto.AsmatchSpace.app.Domain.Diario;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiarioSintomaService {

    @Autowired
    private DiarioSintomaRepository repository;



    public DiarioSintoma cadastrar(DadosCadastroDiario dados, Cliente cliente) {
        var diario = new DiarioSintoma(dados, cliente);
        return repository.save(diario);
    }

    public void deletar(Long id, Cliente cliente) {
        var diario = repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Diário não encontrado"));

        if (!diario.getCliente().getId().equals(cliente.getId()))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Você não pode excluir registros de outro usuário.");

        repository.delete(diario);
    }


    public Page<DadosListagemDiario> listar(Long clienteId, Pageable pageable) {
        return repository.findAllByClienteIdOrderByDataDesc(clienteId, pageable)
                .map(DadosListagemDiario::new);
    }

    public List<DiarioSintoma> buscarUltimosMeses(Long clienteId, int meses) {
        LocalDate fim = LocalDate.now();
        LocalDate inicio = fim.minusMonths(meses);
        return repository.buscarPorClienteEPeriodo(clienteId, inicio, fim);
    }
}
