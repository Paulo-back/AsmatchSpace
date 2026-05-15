package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface LembreteTemplateRepository extends JpaRepository<LembreteTemplate, Long> {
    List<LembreteTemplate> findAllByClienteId(Long clienteId);
}
