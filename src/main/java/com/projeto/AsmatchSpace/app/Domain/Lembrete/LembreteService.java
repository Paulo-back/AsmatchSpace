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
     * Passado (inicio..hoje): gera instâncias que ainda não existem e lista do banco.
     * Futuro (amanhã..fim): NÃO persiste — monta DTOs a partir dos templates ativos.
     * Isso evita gerar 30×N rows de uma vez e estoura o Render free tier.
     */
    public List<DadosInstanciaDoDia> gerarEListarInstanciasPorPeriodo(
            Cliente cliente, int diasPassados, int diasFuturos) {

        LocalDate hoje   = LocalDate.now();
        LocalDate inicio = hoje.minusDays(diasPassados);
        LocalDate fim    = hoje.plusDays(diasFuturos);

        List<LembreteTemplate> templates = templateRepository.findAllByClienteId(cliente.getId());

        // 1. Gera instâncias do passado até hoje (idempotente)
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

        // 2. Busca instâncias persistidas (passado + hoje)
        List<DadosInstanciaDoDia> resultado = new ArrayList<>(
                instanciaRepository
                        .findAllByTemplateClienteIdAndDataInstanciaBetweenOrderByDataInstanciaDescHorarioEfetivoAsc(
                                cliente.getId(), inicio, hoje)
                        .stream()
                        .map(DadosInstanciaDoDia::new)
                        .toList()
        );

        // 3. Monta DTOs futuros a partir dos templates — sem persistir
        for (LocalDate data = hoje.plusDays(1); !data.isAfter(fim); data = data.plusDays(1)) {
            final LocalDate dataFinal = data;
            for (LembreteTemplate t : templates) {
                if (t.ativoEm(dataFinal)) {
                    // Cria instância temporária só para montar o DTO — não salva
                    LembreteInstancia virtual = new LembreteInstancia(t, dataFinal);
                    resultado.add(new DadosInstanciaDoDia(virtual));
                }
            }
        }

        // 4. Ordena tudo: futuro primeiro (datas maiores no topo), depois horário
        resultado.sort(Comparator
                .comparing(DadosInstanciaDoDia::data).reversed()
                .thenComparing(DadosInstanciaDoDia::horario));

        return resultado;
    }
}