package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.enums.StatusMissao70;
import com.gestaoigrejaemcelula.demo.domain.entity.Missao70;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        LEFT JOIN FETCH m.terceiroMembro
        LEFT JOIN FETCH m.visitantes
    """)
    List<Missao70> findAllWithAssociations();

    @Query("""
        SELECT m FROM Missao70 m
        LEFT JOIN FETCH m.celula
        LEFT JOIN FETCH m.lider
        LEFT JOIN FETCH m.auxiliar
        LEFT JOIN FETCH m.terceiroMembro
        LEFT JOIN FETCH m.visitantes
        WHERE m.celula.id = :celulaId
    """)
    List<Missao70> findByCelulaId(@Param("celulaId") Long celulaId);

    @Query("""
        SELECT m FROM Missao70 m
        LEFT JOIN FETCH m.celula
        LEFT JOIN FETCH m.lider
        LEFT JOIN FETCH m.auxiliar
        LEFT JOIN FETCH m.terceiroMembro
        LEFT JOIN FETCH m.visitantes
        WHERE m.id = :id
    """)
    Optional<Missao70> findByIdWithAssociations(@Param("id") Long id);

    @Query("""
        SELECT m FROM Missao70 m
        LEFT JOIN FETCH m.celula
        LEFT JOIN FETCH m.lider
        LEFT JOIN FETCH m.auxiliar
        LEFT JOIN FETCH m.terceiroMembro
        LEFT JOIN FETCH m.visitantes
        WHERE m.status = :status
    """)
    List<Missao70> findByStatus(@Param("status") StatusMissao70 status);

    @Query("""
        SELECT m FROM Missao70 m
        LEFT JOIN FETCH m.celula
        LEFT JOIN FETCH m.lider
        LEFT JOIN FETCH m.auxiliar
        LEFT JOIN FETCH m.terceiroMembro
        LEFT JOIN FETCH m.visitantes
        WHERE m.celula.id = :celulaId AND m.status = :status
    """)
    List<Missao70> findByCelulaIdAndStatus(
            @Param("celulaId") Long celulaId,
            @Param("status") StatusMissao70 status
    );

    @Query(value = """
        SELECT m FROM Missao70 m
        LEFT JOIN FETCH m.celula
        LEFT JOIN FETCH m.lider
        LEFT JOIN FETCH m.auxiliar
        LEFT JOIN FETCH m.terceiroMembro
    """,
           countQuery = "SELECT COUNT(m) FROM Missao70 m")
    Page<Missao70> findAllWithAssociationsPaginado(Pageable pageable);

    @Query(value = """
        SELECT m FROM Missao70 m
        LEFT JOIN FETCH m.celula
        LEFT JOIN FETCH m.lider
        LEFT JOIN FETCH m.auxiliar
        LEFT JOIN FETCH m.terceiroMembro
        WHERE m.celula.id = :celulaId
    """,
           countQuery = "SELECT COUNT(m) FROM Missao70 m WHERE m.celula.id = :celulaId")
    Page<Missao70> findByCelulaIdPaginado(@Param("celulaId") Long celulaId, Pageable pageable);

    @Query(value = """
        SELECT m FROM Missao70 m
        LEFT JOIN FETCH m.celula
        LEFT JOIN FETCH m.lider
        LEFT JOIN FETCH m.auxiliar
        LEFT JOIN FETCH m.terceiroMembro
        WHERE m.status = :status
    """,
           countQuery = "SELECT COUNT(m) FROM Missao70 m WHERE m.status = :status")
    Page<Missao70> findByStatusPaginado(@Param("status") StatusMissao70 status, Pageable pageable);

    @Query(value = """
        SELECT m FROM Missao70 m
        LEFT JOIN FETCH m.celula
        LEFT JOIN FETCH m.lider
        LEFT JOIN FETCH m.auxiliar
        LEFT JOIN FETCH m.terceiroMembro
        WHERE m.celula.id = :celulaId AND m.status = :status
    """,
           countQuery = "SELECT COUNT(m) FROM Missao70 m WHERE m.celula.id = :celulaId AND m.status = :status")
    Page<Missao70> findByCelulaIdAndStatusPaginado(
            @Param("celulaId") Long celulaId,
            @Param("status") StatusMissao70 status,
            Pageable pageable);
}