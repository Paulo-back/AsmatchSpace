package com.projeto.AsmatchSpace.app.Controller;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.ClienteRepository;
import com.projeto.AsmatchSpace.app.Domain.Diario.DiarioSintoma;
import com.projeto.AsmatchSpace.app.Domain.Diario.DiarioSintomaService;
import com.projeto.AsmatchSpace.app.Domain.Diario.PDF.DiarioSintomaPdfBuilder;
import com.projeto.AsmatchSpace.app.Domain.Diario.PDF.RelatorioPdfService;
import com.projeto.AsmatchSpace.app.Security.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    @Autowired
    private ClienteRepository clienteRepository;
    @Autowired
    private TokenService tokenService;

    private static final Logger log = LoggerFactory.getLogger(LembreteController.class);

    private Long getUserId(HttpServletRequest request) {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer "))
            throw new RuntimeException("Token não enviado");

        return tokenService.getUserId(header.replace("Bearer ", ""));
    }

    private Cliente getClienteLogado(Long usuarioId) {
        return clienteRepository.findByUsuarioId(usuarioId)
                .orElseThrow(() -> new RuntimeException("Cliente não encontrado para o usuário logado"));
    }


    @GetMapping("/diario/{meses}")
    public ResponseEntity<?> gerarPdf(
            @PathVariable int meses,
            HttpServletRequest request) {

        // 🔐 Pega ID do usuário pelo token
        Long idUsuario = getUserId(request);
        Cliente cliente = getClienteLogado(idUsuario);

        // 🔎 Busca apenas dados do cliente logado
        List<DiarioSintoma> dados =
                diarioService.buscarUltimosMeses(
                        cliente.getId(), meses);

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
