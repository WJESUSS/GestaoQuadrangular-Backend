package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.DiscipuladoRelatorio;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiscipuladoRelatorioRepository extends JpaRepository<DiscipuladoRelatorio, Long> {

    // ── Derived queries simples ──────────────────────────────────────────────

    List<DiscipuladoRelatorio> findBySemanaInicioAndSemanaFim(LocalDate inicio, LocalDate fim);
    List<DiscipuladoRelatorio> findBySemanaInicioBetween(LocalDate inicio, LocalDate fim);
    boolean existsByMembroIdAndSemanaInicioAndSemanaFim(Long membroId, LocalDate inicio, LocalDate fim);

    Optional<DiscipuladoRelatorio> findByMembroIdAndSemanaInicioAndSemanaFim(
            Long membroId, LocalDate semanaInicio, LocalDate semanaFim);

    // ── Eager (sem paginação — uso restrito a consultas pequenas) ────────────

    @Query("""
        SELECT r FROM DiscipuladoRelatorio r
        JOIN FETCH r.lider l
        LEFT JOIN FETCH l.celula
        JOIN FETCH r.membro
        ORDER BY r.semanaInicio DESC
    """)
    List<DiscipuladoRelatorio> findAllWithEagerRelationships();

    // ── Histórico paginado por célula ────────────────────────────────────────
    //
    //  ATENÇÃO: JOIN FETCH + Pageable gera HHH90003004 (warning do Hibernate
    //  sobre paginação em memória). Para evitar isso usamos duas queries:
    //
    //  1) countQuery  → conta as semanas distintas (rápida, sem fetch)
    //  2) query       → busca somente os IDs da página, depois faz o fetch
    //
    //  A abordagem aqui retorna todos os registros da página de semanas.
    //  O agrupamento por semana é feito no service (igual ao que já existe).
    // ────────────────────────────────────────────────────────────────────────

    /**
     * Retorna as datas distintas de semana para uma célula, ordenadas DESC.
     * Usada pelo service para saber quais semanas compõem a página.
     */
    @Query("""
        SELECT DISTINCT r.semanaInicio, r.semanaFim
        FROM DiscipuladoRelatorio r
        WHERE r.celula.id = :celulaId
        ORDER BY r.semanaInicio DESC
    """)
    Page<Object[]> findSemanasPaginadas(
            @Param("celulaId") Long celulaId,
            Pageable pageable);

    /**
     * Busca todos os registros de uma semana específica (para montar o DTO).
     * Mantém JOIN FETCH — sem risco de paginação em memória porque
     * o filtro já restringe a uma única semana.
     */
    @Query("""
        SELECT r FROM DiscipuladoRelatorio r
        JOIN FETCH r.membro
        JOIN FETCH r.celula
        LEFT JOIN FETCH r.lider
        WHERE r.celula.id    = :celulaId
          AND r.semanaInicio = :inicio
          AND r.semanaFim    = :fim
    """)
    List<DiscipuladoRelatorio> findRegistrosDaSemana(
            @Param("celulaId") Long celulaId,
            @Param("inicio")   LocalDate inicio,
            @Param("fim")      LocalDate fim);

    // ── Queries por semana/célula ────────────────────────────────────────────

    @Query("""
        SELECT r FROM DiscipuladoRelatorio r
        JOIN FETCH r.membro
        LEFT JOIN FETCH r.celula
        LEFT JOIN FETCH r.lider
        WHERE r.semanaInicio = :inicio
          AND r.semanaFim    = :fim
          AND r.celula.id    = :celulaId
    """)
    List<DiscipuladoRelatorio> findBySemanaInicioAndSemanaFimAndCelulaId(
            @Param("inicio")   LocalDate inicio,
            @Param("fim")      LocalDate fim,
            @Param("celulaId") Long celulaId);

    @Query("""
        SELECT r FROM DiscipuladoRelatorio r
        JOIN FETCH r.membro
        JOIN FETCH r.celula
        WHERE r.celula.id = :celulaId
        ORDER BY r.semanaInicio DESC
    """)
    List<DiscipuladoRelatorio> findByCelulaIdOrderBySemanaInicioDesc(
            @Param("celulaId") Long celulaId);

    // ── Alertas nativos ──────────────────────────────────────────────────────

    @Query(value = """
        SELECT
            m.id,
            m.nome,
            m.telefone,
            c.nome          AS nome_celula,
            COUNT(dr.id)    AS total_faltas
        FROM discipulado_relatorio dr
        JOIN membros m  ON m.id = dr.membro_id
        JOIN celulas c  ON c.id = m.celula_id
        WHERE dr.domingo_manha = false
          AND dr.domingo_noite = false
          AND EXTRACT(MONTH FROM dr.semana_inicio) = :mes
          AND EXTRACT(YEAR  FROM dr.semana_inicio) = :ano
          AND m.id NOT IN (
              SELECT da.membro_id
              FROM discipulado_acompanhamento da
              WHERE da.mes_referencia = :mesRef
          )
        GROUP BY m.id, m.nome, m.telefone, c.nome
        HAVING COUNT(dr.id) >= 3
    """, nativeQuery = true)
    List<Object[]> buscarAlertasPastor(
            @Param("mes")    int mes,
            @Param("ano")    int ano,
            @Param("mesRef") String mesRef);

    @Query(value = """
        SELECT
            m.id,
            m.nome,
            m.telefone,
            COALESCE(c.nome, 'Sem Célula') AS nome_celula,
            COUNT(dr.id)                   AS total_faltas
        FROM discipulado_relatorio dr
        JOIN membros m      ON m.id = dr.membro_id
        LEFT JOIN celulas c ON m.celula_id = c.id
        WHERE dr.domingo_manha = false
          AND dr.domingo_noite = false
          AND EXTRACT(MONTH FROM dr.semana_inicio) = :mes
          AND EXTRACT(YEAR  FROM dr.semana_inicio) = :ano
          AND m.id NOT IN (
              SELECT da.membro_id
              FROM discipulado_acompanhamento da
              WHERE da.mes_referencia = :mesRef
          )
        GROUP BY m.id, m.nome, m.telefone, c.nome
        HAVING COUNT(dr.id) >= 2
    """, nativeQuery = true)
    List<Object[]> buscarAlertasDetalhados(
            @Param("mes")    int mes,
            @Param("ano")    int ano,
            @Param("mesRef") String mesRef);
}