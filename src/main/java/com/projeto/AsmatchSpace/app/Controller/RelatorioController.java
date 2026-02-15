package com.projeto.AsmatchSpace.app.Controller;

import com.projeto.AsmatchSpace.app.Domain.Diario.DiarioSintoma;
import com.projeto.AsmatchSpace.app.Domain.Diario.DiarioSintomaService;
import com.projeto.AsmatchSpace.app.Domain.Diario.PDF.DiarioSintomaPdfBuilder;
import com.projeto.AsmatchSpace.app.Domain.Diario.PDF.RelatorioPdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/relatorios")
public class RelatorioController {

    @Autowired
    private RelatorioPdfService pdfService;
    @Autowired
    private DiarioSintomaService diarioService;

    @GetMapping("/diario/{clienteId}/{meses}")
    public ResponseEntity<?> gerarPdf(
            @PathVariable Long clienteId,
            @PathVariable int meses) {

        List<DiarioSintoma> dados =
                diarioService.buscarUltimosMeses(
                        clienteId, meses);

        if (dados.isEmpty()) {
            return ResponseEntity
                    .status(HttpStatus.NO_CONTENT)
                    .body("Não há registros para este período.");
        }

        byte[] pdf =
                DiarioSintomaPdfBuilder.build(
                        dados, meses);

        return ResponseEntity.ok()
                .header("Content-Disposition",
                        "attachment; filename=diario_sintomas.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdf);
    }
}
