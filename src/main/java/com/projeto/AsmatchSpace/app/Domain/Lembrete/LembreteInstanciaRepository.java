package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface LembreteInstanciaRepository extends JpaRepository<LembreteInstancia, Long> {

    List<LembreteInstancia> findAllByTemplateClienteIdAndDataInstancia(
            Long clienteId, LocalDate data);

    Optional<LembreteInstancia> findByTemplateIdAndDataInstancia(
            Long templateId, LocalDate data);

    List<LembreteInstancia> findAllByTemplateClienteIdAndDataInstanciaBetweenOrderByDataInstanciaDescHorarioEfetivoAsc(
            Long clienteId, LocalDate inicio, LocalDate fim);

    List<LembreteInstancia> findAllByTemplateIdInAndDataInstanciaBetween(
            List<Long> templateIds, LocalDate inicio, LocalDate fim);
}