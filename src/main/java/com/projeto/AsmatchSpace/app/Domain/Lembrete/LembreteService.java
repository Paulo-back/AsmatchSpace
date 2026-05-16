package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
public class LembreteService {

    @Autowired private LembreteTemplateRepository templateRepository;
    @Autowired private LembreteInstanciaRepository instanciaRepository;

    // --- Templates ---

    public LembreteTemplate cadastrarTemplate(DadosCadastroLembreteTemplate dados, Cliente cliente) {
        var template = new LembreteTemplate(dados, cliente);
        return templateRepository.save(template);
    }

    public LembreteTemplate buscarTemplatePorId(Long id, Cliente cliente) {
        var template = templateRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Template não encontrado"));
        verificarDono(template.getCliente().getId(), cliente.getId());
        return template;
    }

    public void deletarTemplate(Long id, Cliente cliente) {
        var template = buscarTemplatePorId(id, cliente);
        templateRepository.delete(template);
    }

    // --- Instâncias ---

    /**
     * Chamado pelo Android ao abrir a tela de lembretes.
     * Gera instâncias para hoje de todos os templates ativos do cliente.
     * Idempotente — o UNIQUE(template_id, data_instancia) no banco impede duplicatas.
     */
    public List<DadosInstanciaDoDia> gerarEListarInstanciasDeHoje(Cliente cliente) {
        LocalDate hoje = LocalDate.now();
        List<LembreteTemplate> templates = templateRepository.findAllByClienteId(cliente.getId());

        for (LembreteTemplate t : templates) {
            if (t.ativoEm(hoje)) {
                boolean jaExiste = instanciaRepository
                        .findByTemplateIdAndDataInstancia(t.getId(), hoje)
                        .isPresent();
                if (!jaExiste) {
                    instanciaRepository.save(new LembreteInstancia(t, hoje));
                }
            }
        }

        return instanciaRepository
                .findAllByTemplateClienteIdAndDataInstancia(cliente.getId(), hoje)
                .stream()
                .map(DadosInstanciaDoDia::new)
                .toList();
    }
    public List<DadosDetalhamentoTemplate> listarTemplates(Long clienteId) {
        LocalDate hoje = LocalDate.now();
        return templateRepository.findAllByClienteId(clienteId)
                .stream()
                .map(t -> {
                    String statusHoje = instanciaRepository
                            .findByTemplateIdAndDataInstancia(t.getId(), hoje)
                            .map(i -> i.getStatus().name())
                            .orElse(null);
                    return new DadosDetalhamentoTemplate(t, statusHoje);
                })
                .toList();
    }


    public LembreteInstancia atualizarStatus(Long instanciaId, StatusInstancia novoStatus, Cliente cliente) {
        var instancia = instanciaRepository.findById(instanciaId)
                .orElseThrow(() -> new RuntimeException("Instância não encontrada"));
        verificarDono(instancia.getTemplate().getCliente().getId(), cliente.getId());
        instancia.setStatus(novoStatus);
        return instanciaRepository.save(instancia);
    }

    private void verificarDono(Long donoId, Long clienteId) {
        if (!donoId.equals(clienteId))
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    "Você não tem permissão para acessar este recurso.");
    }
}