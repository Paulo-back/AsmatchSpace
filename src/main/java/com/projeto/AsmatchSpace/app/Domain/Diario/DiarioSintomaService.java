package com.projeto.AsmatchSpace.app.Domain.Diario;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class DiarioSintomaService {

    @Autowired
    private DiarioSintomaRepository repository;

    public List<DiarioSintoma> buscarUltimosMeses(
            Long clienteId,
            int meses) {

        LocalDate fim = LocalDate.now();
        LocalDate inicio = fim.minusMonths(meses);

        return repository.buscarPorClienteEPeriodo(
                clienteId,
                inicio.toString(),
                fim.toString()
        );
    }
}
