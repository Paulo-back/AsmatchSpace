package com.projeto.AsmatchSpace.app.Domain.Diario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DiarioSintomaRepository extends JpaRepository<DiarioSintoma, Long> {

    List<DiarioSintoma> findAllByClienteId(Long clienteId);

    // Método paginado — substitui o findAllByClienteId no listar
    Page<DiarioSintoma> findAllByClienteIdOrderByDataDesc(Long clienteId, Pageable pageable);

    @Query("""
        SELECT d FROM DiarioSintoma d
        WHERE d.cliente.id = :clienteId
        AND d.data BETWEEN :inicio AND :fim
        ORDER BY d.data ASC
    """)
    List<DiarioSintoma> buscarPorClienteEPeriodo(
            Long clienteId,
            LocalDate inicio,
            LocalDate fim
    );
}

