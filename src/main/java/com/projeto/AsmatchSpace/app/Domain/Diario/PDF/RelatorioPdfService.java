package com.projeto.AsmatchSpace.app.Domain.Diario.PDF;

import com.projeto.AsmatchSpace.app.Domain.Diario.DiarioSintoma;
import com.projeto.AsmatchSpace.app.Domain.Diario.DiarioSintomaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RelatorioPdfService {

    @Autowired
    private DiarioSintomaService diarioService;

    public byte[] gerarRelatorio(
            Long clienteId,
            String nomeCliente,
            int meses) {

        List<DiarioSintoma> dados =
                diarioService.buscarUltimosMeses(
                        clienteId, meses);

        return DiarioSintomaPdfBuilder.build(
                dados, meses);
    }
}
