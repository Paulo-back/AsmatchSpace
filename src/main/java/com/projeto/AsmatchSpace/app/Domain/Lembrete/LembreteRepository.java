package com.projeto.AsmatchSpace.app.Domain.Lembrete;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LembreteRepository extends JpaRepository<Lembretes, Long> {

    List<Lembretes> findAllByClienteId(Long clienteId);

    Page<Lembretes> findAllByClienteIdOrderByDataAscHorarioAsc(Long clienteId, Pageable pageable);
}


