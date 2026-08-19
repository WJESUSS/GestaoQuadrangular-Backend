package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.aplication.dto.RankingCelulaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.RankingCelulaProjection;
import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface CelulaRepository extends JpaRepository<Celula, Long> {

    @Query("SELECT c FROM Celula c LEFT JOIN FETCH c.lider WHERE c.ativa = true")
    List<Celula> findAllByAtivaTrue();

    @Query("SELECT c FROM Celula c " +
            "LEFT JOIN FETCH c.membros " +
            "LEFT JOIN FETCH c.lider " +
            "WHERE c.lider.id = :liderId AND c.ativa = true")
    Optional<Celula> findByLiderIdWithMembros(@Param("liderId") Long liderId);

    List<Celula> findByStatusMultiplicacaoIn(List<Celula.StatusMultiplicacao> statuses);

    @Query("SELECT c FROM Celula c LEFT JOIN FETCH c.membros WHERE c.id = :id")
    Optional<Celula> findByIdWithMembros(@Param("id") Long id);

    List<Celula> findByNomeContainingIgnoreCase(String nome);

    Optional<Celula> findByLider_Id(Long liderId);

    @EntityGraph(attributePaths = {"membros"})
    Optional<Celula> findByLider_IdAndAtivaTrue(Long liderId);

    @Query("SELECT c FROM Celula c LEFT JOIN FETCH c.lider WHERE c.ativa = true")
    List<Celula> findByAtivaTrue();

    List<Celula> findByStatusMultiplicacao(Celula.StatusMultiplicacao statusMultiplicacao);

    Long countByAtivaTrue();

    @Query(value = """
    SELECT
        c.id                                            AS celulaId,
        c.nome                                          AS nomeCelula,
        u.nome                                          AS lider,

        -- Média de membros presentes por reunião no mês
        COALESCE((
            SELECT AVG(mc.total)
            FROM (
                SELECT rmp.relatorio_id, COUNT(rmp.membro_id) AS total
                FROM relatorio_membros_presenca rmp
                INNER JOIN relatorio r2 ON r2.id = rmp.relatorio_id
                WHERE r2.celula_id = c.id
                  AND r2.data_reuniao >= TO_DATE(:mes || '-01', 'YYYY-MM-DD')
                  AND r2.data_reuniao <  TO_DATE(:mes || '-01', 'YYYY-MM-DD') + INTERVAL '1 MONTH'
                GROUP BY rmp.relatorio_id
            ) mc
        ), 0)                                           AS presencaMedia,

        -- Total de visitantes únicos no mês (sem dupla contagem)
        COALESCE((
            SELECT COUNT(DISTINCT rvp.visitante_id)
            FROM relatorio_visitantes_presenca rvp
            INNER JOIN relatorio r3 ON r3.id = rvp.relatorio_id
            WHERE r3.celula_id = c.id
              AND r3.data_reuniao >= TO_DATE(:mes || '-01', 'YYYY-MM-DD')
              AND r3.data_reuniao <  TO_DATE(:mes || '-01', 'YYYY-MM-DD') + INTERVAL '1 MONTH'
        ), 0)                                           AS visitantes,

        0                                               AS consolidados,
        0                                               AS batismos,
        FALSE                                           AS multiplicou,

        -- Decisões espirituais: conta visitante uma vez por mês (DISTINCT por visitante)
        COALESCE((
            SELECT COUNT(DISTINCT v.id)
            FROM relatorio_visitantes_presenca rvp
            INNER JOIN relatorio r4 ON r4.id = rvp.relatorio_id
            INNER JOIN visitantes v  ON v.id  = rvp.visitante_id
            WHERE r4.celula_id = c.id
              AND r4.data_reuniao >= TO_DATE(:mes || '-01', 'YYYY-MM-DD')
              AND r4.data_reuniao <  TO_DATE(:mes || '-01', 'YYYY-MM-DD') + INTERVAL '1 MONTH'
              AND v.decisao_espiritual = 'ACEITOU_JESUS'
        ), 0)                                           AS aceitouJesus,

        COALESCE((
            SELECT COUNT(DISTINCT v.id)
            FROM relatorio_visitantes_presenca rvp
            INNER JOIN relatorio r5 ON r5.id = rvp.relatorio_id
            INNER JOIN visitantes v  ON v.id  = rvp.visitante_id
            WHERE r5.celula_id = c.id
              AND r5.data_reuniao >= TO_DATE(:mes || '-01', 'YYYY-MM-DD')
              AND r5.data_reuniao <  TO_DATE(:mes || '-01', 'YYYY-MM-DD') + INTERVAL '1 MONTH'
              AND v.decisao_espiritual = 'BATISMO_AGUAS'
        ), 0)                                           AS desejaBatismo,

        COALESCE((
            SELECT COUNT(DISTINCT v.id)
            FROM relatorio_visitantes_presenca rvp
            INNER JOIN relatorio r6 ON r6.id = rvp.relatorio_id
            INNER JOIN visitantes v  ON v.id  = rvp.visitante_id
            WHERE r6.celula_id = c.id
              AND r6.data_reuniao >= TO_DATE(:mes || '-01', 'YYYY-MM-DD')
              AND r6.data_reuniao <  TO_DATE(:mes || '-01', 'YYYY-MM-DD') + INTERVAL '1 MONTH'
              AND v.decisao_espiritual = 'RECONCILIOU'
        ), 0)                                           AS reconciliou,

        0                                               AS pontuacao

    FROM celulas c
    LEFT JOIN usuarios u ON u.id = c.lider_id
    WHERE c.ativo = TRUE
      AND EXISTS (
          SELECT 1
          FROM relatorio r
          WHERE r.celula_id = c.id
            AND r.data_reuniao >= TO_DATE(:mes || '-01', 'YYYY-MM-DD')
            AND r.data_reuniao <  TO_DATE(:mes || '-01', 'YYYY-MM-DD') + INTERVAL '1 MONTH'
      )
    ORDER BY c.nome
""", nativeQuery = true)
    List<RankingCelulaProjection> buscarDadosRankingNativo(@Param("mes") String mesAno);
}