package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import com.projeto.AsmatchSpace.app.Domain.CadastroUsuario.Cliente;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
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
                if (!jaExiste)
                    instanciaRepository.save(new LembreteInstancia(t, hoje));
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
     * Padrão: diasPassados=7, diasFuturos=0 (só hoje como "futuro").
     * O botão de filtro controla apenas diasPassados.
     */
    public List<DadosInstanciaDoDia> gerarEListarInstanciasPorPeriodo(
            Cliente cliente, int diasPassados) {

        LocalDate hoje   = LocalDate.now();
        LocalDate inicio = hoje.minusDays(diasPassados);

        List<LembreteTemplate> templates = templateRepository.findAllByClienteId(cliente.getId());
        if (templates.isEmpty())
            return List.of();

        List<Long> templateIds = templates.stream()
                .map(LembreteTemplate::getId)
                .toList();

        // 1 query para buscar TODAS as instâncias já existentes no período
        List<LembreteInstancia> existentes = instanciaRepository
                .findAllByTemplateIdInAndDataInstanciaBetween(templateIds, inicio, hoje);

        // Monta Set para checagem O(1): "templateId|data"
        java.util.Set<String> chaves = new java.util.HashSet<>();
        for (LembreteInstancia i : existentes)
            chaves.add(i.getTemplate().getId() + "|" + i.getDataInstancia());

        // Gera apenas as que faltam — sem queries individuais
        List<LembreteInstancia> novas = new ArrayList<>();
        for (LocalDate data = inicio; !data.isAfter(hoje); data = data.plusDays(1)) {
            for (LembreteTemplate t : templates) {
                if (t.ativoEm(data) && !chaves.contains(t.getId() + "|" + data)) {
                    novas.add(new LembreteInstancia(t, data));
                }
            }
        }

        if (!novas.isEmpty())
            instanciaRepository.saveAll(novas);  // 1 batch insert

        // Retorna do banco ordenado
        return instanciaRepository
                .findAllByTemplateClienteIdAndDataInstanciaBetweenOrderByDataInstanciaDescHorarioEfetivoAsc(
                        cliente.getId(), inicio, hoje)
                .stream()
                .map(DadosInstanciaDoDia::new)
                .toList();
    }
    }
