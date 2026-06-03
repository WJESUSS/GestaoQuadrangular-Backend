package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.enums.StatusMissao70;
import com.gestaoigrejaemcelula.demo.domain.entity.Missao70;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;

public interface Missao70Repository extends JpaRepository<Missao70, Long> {

    @Query("""
        SELECT m FROM Missao70 m
        LEFT JOIN FETCH m.celula
        LEFT JOIN FETCH m.lider
        LEFT JOIN FETCH m.auxiliar
        LEFT JOIN FETCH m.visitantes
    """)
    List<Missao70> findAllWithAssociations();

    @Query("""
        SELECT m FROM Missao70 m
        LEFT JOIN FETCH m.celula
        LEFT JOIN FETCH m.lider
        LEFT JOIN FETCH m.auxiliar
        LEFT JOIN FETCH m.visitantes
        WHERE m.celula.id = :celulaId
    """)
    List<Missao70> findByCelulaId(@Param("celulaId") Long celulaId);

    @Query("""
        SELECT m FROM Missao70 m
        LEFT JOIN FETCH m.celula
        LEFT JOIN FETCH m.lider
        LEFT JOIN FETCH m.auxiliar
        LEFT JOIN FETCH m.visitantes
        WHERE m.id = :id
    """)
    Optional<Missao70> findByIdWithAssociations(@Param("id") Long id);

    @Query("""
        SELECT m FROM Missao70 m
        LEFT JOIN FETCH m.celula
        LEFT JOIN FETCH m.lider
        LEFT JOIN FETCH m.auxiliar
        LEFT JOIN FETCH m.visitantes
        WHERE m.status = :status
    """)
    List<Missao70> findByStatus(@Param("status") StatusMissao70 status);

    @Query("""
        SELECT m FROM Missao70 m
        LEFT JOIN FETCH m.celula
        LEFT JOIN FETCH m.lider
        LEFT JOIN FETCH m.auxiliar
        LEFT JOIN FETCH m.visitantes
        WHERE m.celula.id = :celulaId AND m.status = :status
    """)
    List<Missao70> findByCelulaIdAndStatus(
            @Param("celulaId") Long celulaId,
            @Param("status") StatusMissao70 status
    );
}