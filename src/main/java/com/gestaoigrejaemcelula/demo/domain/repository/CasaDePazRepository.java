package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.CasaDePaz;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusCasaDePaz;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface CasaDePazRepository extends JpaRepository<CasaDePaz, Long> {

    // ── JOIN FETCH garante que celula, lider e auxiliar são carregados na mesma query ──
    @Query("""
        SELECT c FROM CasaDePaz c
        JOIN FETCH c.celula
        JOIN FETCH c.lider
        JOIN FETCH c.auxiliar
        LEFT JOIN FETCH c.visitantes
        WHERE c.celula.id = :celulaId
    """)
    List<CasaDePaz> findByCelulaId(@Param("celulaId") Long celulaId);

    @Query("""
        SELECT c FROM CasaDePaz c
        JOIN FETCH c.celula
        JOIN FETCH c.lider
        JOIN FETCH c.auxiliar
        LEFT JOIN FETCH c.visitantes
    """)
    List<CasaDePaz> findAllWithAssociations();

    @Query("""
        SELECT c FROM CasaDePaz c
        JOIN FETCH c.celula
        JOIN FETCH c.lider
        JOIN FETCH c.auxiliar
        LEFT JOIN FETCH c.visitantes
        WHERE c.celula.id = :celulaId AND c.status = :status
    """)
    List<CasaDePaz> findByCelulaIdAndStatus(
            @Param("celulaId") Long celulaId,
            @Param("status") StatusCasaDePaz status
    );

    @Query("""
        SELECT c FROM CasaDePaz c
        JOIN FETCH c.celula
        JOIN FETCH c.lider
        JOIN FETCH c.auxiliar
        LEFT JOIN FETCH c.visitantes
        WHERE c.status = :status
    """)
    List<CasaDePaz> findByStatus(@Param("status") StatusCasaDePaz status);

    @Query("""
        SELECT c FROM CasaDePaz c
        JOIN FETCH c.celula
        JOIN FETCH c.lider
        JOIN FETCH c.auxiliar
        LEFT JOIN FETCH c.visitantes
        WHERE c.id = :id
    """)
    Optional<CasaDePaz> findByIdWithAssociations(@Param("id") Long id);
}