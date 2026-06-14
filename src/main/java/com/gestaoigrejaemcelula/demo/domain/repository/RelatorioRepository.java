package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.Relatorio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RelatorioRepository extends JpaRepository<Relatorio, Long> {

    // ── Paginação (substitui findAll() sem limite) ───────────────────────────

    @Query("""
        SELECT r FROM Relatorio r
        JOIN FETCH r.celula c
        LEFT JOIN FETCH c.lider
        ORDER BY r.dataReuniao DESC
    """)
    Page<Relatorio> findAllComCelula(Pageable pageable);

    // ── Por célula ───────────────────────────────────────────────────────────

    @Query("""
        SELECT r FROM Relatorio r
        JOIN FETCH r.celula c
        LEFT JOIN FETCH c.lider
        WHERE c.id = :celulaId
        ORDER BY r.dataReuniao DESC
    """)
    List<Relatorio> findByCelulaIdOrderByDataReuniaoDesc(@Param("celulaId") Long celulaId);

    @Query("""
        SELECT r FROM Relatorio r
        JOIN FETCH r.celula c
        LEFT JOIN FETCH c.lider
        WHERE c.id = :celulaId
          AND r.dataReuniao >= :data
        ORDER BY r.dataReuniao DESC
    """)
    List<Relatorio> findByCelulaIdAndDataReuniaoGreaterThanEqual(
            @Param("celulaId") Long celulaId,
            @Param("data") LocalDate data);

    // ── Por data ─────────────────────────────────────────────────────────────

    @Query("""
        SELECT r FROM Relatorio r
        JOIN FETCH r.celula c
        LEFT JOIN FETCH c.lider
        WHERE r.dataReuniao >= :data
        ORDER BY r.dataReuniao DESC
    """)
    List<Relatorio> findByDataReuniaoGreaterThanEqual(@Param("data") LocalDate data);

    /**
     * Query principal para intervalos de data — com JOIN FETCH.
     * Usada em buscarResumoSemana e buscarPorSemana.
     */
    @EntityGraph(attributePaths = {"celula", "celula.lider", "presentes", "visitantesPresentes"})
    @Query("""
    SELECT r FROM Relatorio r
    WHERE r.dataReuniao BETWEEN :inicio AND :fim
    ORDER BY r.dataReuniao DESC
""")
    List<Relatorio> findRelatoriosEntreDatasComCelula(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim);

    /**
     * Alias para findRelatoriosEntreDatasComCelula — mantido para não quebrar
     * chamadas existentes que usem findByDataReuniaoBetween.
     */
    default List<Relatorio> findByDataReuniaoBetween(LocalDate inicio, LocalDate fim) {
        return findRelatoriosEntreDatasComCelula(inicio, fim);
    }

    // ── Não realizadas ───────────────────────────────────────────────────────

    @Query("""
        SELECT r FROM Relatorio r
        JOIN FETCH r.celula c
        LEFT JOIN FETCH c.lider
        WHERE r.realizada = false
          AND r.dataCadastro BETWEEN :inicio AND :fim
    """)
    List<Relatorio> findByRealizadaFalseAndDataCadastroBetween(
            @Param("inicio") LocalDateTime inicio,
            @Param("fim") LocalDateTime fim);

    // ── Agregações ───────────────────────────────────────────────────────────

    @Query("SELECT COUNT(r) FROM Relatorio r WHERE r.celula.id = :celulaId AND MONTH(r.dataReuniao) = :mes")
    long countRelatoriosPorMes(@Param("celulaId") Long celulaId, @Param("mes") int mes);

    @Query("SELECT COALESCE(SUM(r.quantidadeVisitantes), 0) FROM Relatorio r WHERE r.dataReuniao BETWEEN :inicio AND :fim")
    int totalVisitantesNoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    boolean existsByCelulaIdAndDataReuniao(Long celulaId, LocalDate dataReuniao);
}