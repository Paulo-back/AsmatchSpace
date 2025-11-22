package com.projeto.AsmatchSpace.app.Domain.Diario;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiarioSintomaRepository extends JpaRepository<DiarioSintoma, Long> {

    List<DiarioSintoma> findAllByClienteId(Long clienteId);

}

