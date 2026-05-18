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

    /**
     * Gera e lista instâncias dentro de uma janela:
     *   [hoje - diasPassados ... hoje + diasFuturos]
     *
     * Geração só ocorre para o passado até hoje (não faz sentido gerar o futuro
     * porque o status ainda é desconhecido — elas serão geradas no próprio dia).
     * Para datas futuras, apenas lista os templates ativos (sem persistir instância).
     */
    public List<DadosInstanciaDoDia> gerarEListarInstanciasPorPeriodo(
            Cliente cliente, int diasPassados, int diasFuturos) {

        LocalDate hoje  = LocalDate.now();
        LocalDate inicio = hoje.minusDays(diasPassados);
        LocalDate fim    = hoje.plusDays(diasFuturos);

        List<LembreteTemplate> templates = templateRepository.findAllByClienteId(cliente.getId());

        // Gera instâncias apenas do passado até hoje (inclusive)
        for (LocalDate data = inicio; !data.isAfter(hoje); data = data.plusDays(1)) {
            for (LembreteTemplate t : templates) {
                if (t.ativoEm(data)) {
                    boolean jaExiste = instanciaRepository
                            .findByTemplateIdAndDataInstancia(t.getId(), data)
                            .isPresent();
                    if (!jaExiste)
                        instanciaRepository.save(new LembreteInstancia(t, data));
                }
            }
        }

        // Gera instâncias futuras (amanhã em diante) — sem duplicar
        for (LocalDate data = hoje.plusDays(1); !data.isAfter(fim); data = data.plusDays(1)) {
            for (LembreteTemplate t : templates) {
                if (t.ativoEm(data)) {
                    boolean jaExiste = instanciaRepository
                            .findByTemplateIdAndDataInstancia(t.getId(), data)
                            .isPresent();
                    if (!jaExiste)
                        instanciaRepository.save(new LembreteInstancia(t, data));
                }
            }
        }

        return instanciaRepository
                .findAllByTemplateClienteIdAndDataInstanciaBetweenOrderByDataInstanciaDescHorarioEfetivoAsc(
                        cliente.getId(), inicio, fim)
                .stream()
                .map(DadosInstanciaDoDia::new)
                .toList();
    }
}