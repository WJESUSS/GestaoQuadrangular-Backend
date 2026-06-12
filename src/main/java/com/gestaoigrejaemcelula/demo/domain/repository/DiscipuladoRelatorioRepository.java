package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.DiscipuladoRelatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface DiscipuladoRelatorioRepository extends JpaRepository<DiscipuladoRelatorio, Long> {

    // ── Derived queries simples (sem alteração) ──────────────────────────────

    List<DiscipuladoRelatorio> findBySemanaInicioAndSemanaFim(LocalDate inicio, LocalDate fim);
    List<DiscipuladoRelatorio> findBySemanaInicioBetween(LocalDate inicio, LocalDate fim);
    boolean existsByMembroIdAndSemanaInicioAndSemanaFim(Long membroId, LocalDate inicio, LocalDate fim);

    Optional<DiscipuladoRelatorio> findByMembroIdAndSemanaInicioAndSemanaFim(
            Long membroId, LocalDate semanaInicio, LocalDate semanaFim);

    // ── findAllComDetalhes e findAllCompletos eram idênticos ao findAllWithEagerRelationships.
    //    Removidos para evitar confusão e manutenção duplicada.
    //    Use findAllWithEagerRelationships() em todos os lugares. ─────────────

    /**
     * Traz todos os registros com JOIN FETCH para evitar N+1.
     * Atenção: sem paginação isso carrega TUDO em memória.
     * Se a tabela crescer, considere receber um Pageable.
     */
    @Query("""
        SELECT r FROM DiscipuladoRelatorio r
        JOIN FETCH r.lider l
        LEFT JOIN FETCH l.celula
        JOIN FETCH r.membro
        ORDER BY r.semanaInicio DESC
    """)
    List<DiscipuladoRelatorio> findAllWithEagerRelationships();

    // ── Alertas nativos ──────────────────────────────────────────────────────

    /**
     * Membros com 3+ faltas no domingo (sem acompanhamento registrado).
     * Projeção via interface evita Object[] e é type-safe.
     * Se não quiser criar a interface agora, mantenha Object[] — sem prejuízo funcional.
     */
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
            @Param("mes") int mes,
            @Param("ano") int ano,
            @Param("mesRef") String mesRef);

    /**
     * Igual ao anterior, mas limiar = 2 e LEFT JOIN (membro pode não ter célula).
     */
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
            @Param("mes") int mes,
            @Param("ano") int ano,
            @Param("mesRef") String mesRef);

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
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
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
}