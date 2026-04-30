package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.DiscipuladoAcompanhamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DiscipuladoAcompanhamentoRepository
        extends JpaRepository<DiscipuladoAcompanhamento, Long> {

    /**
     * Conta membros que ainda não foram marcados como acompanhados no mês específico.
     */
    @Query("""
        SELECT COUNT(a)
        FROM DiscipuladoAcompanhamento a
        WHERE a.mesReferencia = :mes
          AND a.acompanhado = false
    """)
    Long membrosNaoAcompanhados(@Param("mes") String mes);

    /**
     * Versão para o Painel do Pastor:
     * Busca membros com 2 ou mais faltas no mês que NÃO possuem registro de acompanhamento.
     */
    @Query(value = """
    SELECT COALESCE(COUNT(*), 0) FROM (
        SELECT dr.membro_id
        FROM discipulado_relatorio dr
        JOIN membros m ON m.id = dr.membro_id
        WHERE (dr.domingo_manha = false OR dr.domingo_noite = false) 
          AND EXTRACT(MONTH FROM dr.semana_inicio) = :mes
          AND EXTRACT(YEAR FROM dr.semana_inicio) = :ano
          AND m.id NOT IN (
              SELECT da.membro_id 
              FROM discipulado_acompanhamento da 
              WHERE da.mes_referencia = :mesRef
          )
        GROUP BY dr.membro_id
        HAVING COUNT(dr.id) >= 2
    ) AS alertas
    """, nativeQuery = true)
    Long contarPendentesPastor(
            @Param("mes") int mes,
            @Param("ano") int ano,
            @Param("mesRef") String mesRef
    );

    /**
     * Versão Real/Rigorosa:
     * Busca membros com 3 ou mais faltas totais (dia inteiro) no mês.
     */
    @Query(value = """
    SELECT COALESCE(COUNT(*), 0) FROM (
        SELECT dr.membro_id
        FROM discipulado_relatorio dr
        JOIN membros m ON m.id = dr.membro_id
        WHERE dr.domingo_manha = false 
          AND dr.domingo_noite = false
          AND EXTRACT(MONTH FROM dr.semana_inicio) = :mes
          AND EXTRACT(YEAR FROM dr.semana_inicio) = :ano
          AND m.id NOT IN (
              SELECT da.membro_id 
              FROM discipulado_acompanhamento da 
              WHERE da.mes_referencia = :mesRef
          )
        GROUP BY dr.membro_id
        HAVING COUNT(dr.id) >= 3
    ) AS alertas
    """, nativeQuery = true)
    Long contarPendentesReal(
            @Param("mes") int mes,
            @Param("ano") int ano,
            @Param("mesRef") String mesRef
    );

}
