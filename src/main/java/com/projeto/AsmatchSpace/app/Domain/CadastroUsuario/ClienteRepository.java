package com.projeto.AsmatchSpace.app.Domain.CadastroUsuario;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ClienteRepository extends JpaRepository<Cliente, Long>{

    Page<Cliente> findAllByAtivoTrue(Pageable paginacao);


    @Query("""
            SELECT c.ativo
            FROM Cliente c
            WHERE
            c.id = :id
            """)
    Boolean findAtivoByid(Long id);

    Optional<Cliente> findByUsuarioId(Long usuarioId);



}



