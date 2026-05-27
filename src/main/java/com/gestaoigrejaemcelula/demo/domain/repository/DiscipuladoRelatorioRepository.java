package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.DiscipuladoRelatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
public interface DiscipuladoRelatorioRepository extends JpaRepository<DiscipuladoRelatorio, Long> {

    List<DiscipuladoRelatorio> findBySemanaInicioAndSemanaFim(LocalDate inicio, LocalDate fim);
    List<DiscipuladoRelatorio> findBySemanaInicioBetween(LocalDate inicio, LocalDate fim);
    boolean existsByMembroIdAndSemanaInicioAndSemanaFim(Long membroId, LocalDate inicio, LocalDate fim);

    @Query("SELECT r FROM DiscipuladoRelatorio r " +
            "LEFT JOIN FETCH r.celula " +
            "LEFT JOIN FETCH r.lider " +
            "LEFT JOIN FETCH r.membro")
    List<DiscipuladoRelatorio> findAllComDetalhes();

    @Query(value = """
        SELECT 
            m.id, 
            m.nome, 
            m.telefone, 
            c.nome as nome_celula, 
            COUNT(dr.id) as total_faltas
        FROM discipulado_relatorio dr
        JOIN membros m ON m.id = dr.membro_id
        JOIN celulas c ON c.id = m.celula_id
        WHERE dr.domingo_manha = false 
          AND dr.domingo_noite = false
          AND EXTRACT(MONTH FROM dr.semana_inicio) = :mes
          AND EXTRACT(YEAR FROM dr.semana_inicio) = :ano
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
            @Param("mesRef") String mesRef
    );

    @Query("SELECT r FROM DiscipuladoRelatorio r " +
            "JOIN FETCH r.lider l " +
            "LEFT JOIN FETCH l.celula c " +
            "JOIN FETCH r.membro m")
    List<DiscipuladoRelatorio> findAllCompletos();

    @Query("SELECT r FROM DiscipuladoRelatorio r " +
            "JOIN FETCH r.lider l " +
            "LEFT JOIN FETCH l.celula c " +
            "JOIN FETCH r.membro m")
    List<DiscipuladoRelatorio> findAllWithEagerRelationships();

    @Query(value = """
        SELECT 
            m.id, 
            m.nome, 
            m.telefone, 
            COALESCE(c.nome, 'Sem Célula') as nome_celula,
            COUNT(dr.id) as total_faltas
        FROM discipulado_relatorio dr
        JOIN membros m ON m.id = dr.membro_id
        LEFT JOIN celulas c ON m.celula_id = c.id
        WHERE (dr.domingo_manha = false AND dr.domingo_noite = false)
          AND EXTRACT(MONTH FROM dr.semana_inicio) = :mes
          AND EXTRACT(YEAR FROM dr.semana_inicio) = :ano
          AND m.id NOT IN (
              SELECT da.membro_id FROM discipulado_acompanhamento da 
              WHERE da.mes_referencia = :mesRef
          )
        GROUP BY m.id, m.nome, m.telefone, c.nome
        HAVING COUNT(dr.id) >= 2
    """, nativeQuery = true)
    List<Object[]> buscarAlertasDetalhados(
            @Param("mes") int mes,
            @Param("ano") int ano,
            @Param("mesRef") String mesRef
    );

    @Query("SELECT r FROM DiscipuladoRelatorio r " +
            "JOIN FETCH r.membro " +
            "LEFT JOIN FETCH r.celula " +
            "LEFT JOIN FETCH r.lider " +
            "WHERE r.semanaInicio = :inicio " +
            "AND r.semanaFim = :fim " +
            "AND r.celula.id = :celulaId")
    List<DiscipuladoRelatorio> findBySemanaInicioAndSemanaFimAndCelulaId(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("celulaId") Long celulaId
    );

    Optional<DiscipuladoRelatorio> findByMembroIdAndSemanaInicioAndSemanaFim(
            Long membroId, LocalDate semanaInicio, LocalDate semanaFim
    );

    // ── CORRIGIDO: JOIN FETCH garante que membro e célula são carregados
    //    na mesma query — sem LazyInitializationException e sem N+1 queries.
    @Query("SELECT r FROM DiscipuladoRelatorio r " +
            "JOIN FETCH r.membro " +
            "JOIN FETCH r.celula " +
            "WHERE r.celula.id = :celulaId " +
            "ORDER BY r.semanaInicio DESC")
    List<DiscipuladoRelatorio> findByCelulaIdOrderBySemanaInicioDesc(
            @Param("celulaId") Long celulaId
    );

}