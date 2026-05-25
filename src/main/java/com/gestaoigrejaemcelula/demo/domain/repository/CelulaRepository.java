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
    List<Celula> findAllByAtivaTrue();
    @Query("SELECT c FROM Celula c " +
            "LEFT JOIN FETCH c.membros " +          // <--- isso carrega TODOS os membros
            "LEFT JOIN FETCH c.lider " +            // opcional, mas bom
            "WHERE c.lider.id = :liderId AND c.ativa = true")
    Optional<Celula> findByLiderIdWithMembros(@Param("liderId") Long liderId);

    // 🔎 Busca células ativas
    List<Celula> findByStatusMultiplicacaoIn(List<Celula.StatusMultiplicacao> statuses);

    @Query("SELECT c FROM Celula c LEFT JOIN FETCH c.membros WHERE c.id = :id")
    Optional<Celula> findByIdWithMembros(@Param("id") Long id);

    // 🔎 Busca por nome
    List<Celula> findByNomeContainingIgnoreCase(String nome);

    // 🔎 Busca célula do líder
    Optional<Celula> findByLider_Id(Long liderId);

    // 🔎 Busca célula ativa do líder (RECOMENDADO)
    @EntityGraph(attributePaths = {"membros"})
    Optional<Celula> findByLider_IdAndAtivaTrue(Long liderId);

    List<Celula> findByAtivaTrue();


    List<Celula> findByStatusMultiplicacao(Celula.StatusMultiplicacao statusMultiplicacao);

    Long countByAtivaTrue();

    @Query(value = """
    SELECT
        c.id                                        AS celulaId,
        c.nome                                      AS nomeCelula,
        u.nome                                      AS lider,
        COALESCE(AVG(membros_count.total), 0)       AS presencaMedia,
        COALESCE(SUM(
            COALESCE(rv.visitantes_cadastrados, 0) +
            COALESCE(r.quantidade_visitantes, 0)
        ), 0)                                       AS visitantes,
        0                                           AS consolidados,
        0                                           AS batismos,
        FALSE                                       AS multiplicou,
        COALESCE(SUM(CASE WHEN v.decisao_espiritual = 'ACEITOU_JESUS'  THEN 1 ELSE 0 END), 0) AS aceitouJesus,
        COALESCE(SUM(CASE WHEN v.decisao_espiritual = 'BATISMO_AGUAS'  THEN 1 ELSE 0 END), 0) AS desejaBatismo,
        COALESCE(SUM(CASE WHEN v.decisao_espiritual = 'RECONCILIOU'    THEN 1 ELSE 0 END), 0) AS reconciliou,
        0                                           AS pontuacao
    FROM celulas c
    LEFT JOIN usuarios u ON u.id = c.lider_id
    LEFT JOIN relatorio r
        ON r.celula_id = c.id
        AND r.data_reuniao >= TO_DATE(:mes || '-01', 'YYYY-MM-DD')
        AND r.data_reuniao <  TO_DATE(:mes || '-01', 'YYYY-MM-DD') + INTERVAL '1 MONTH'
    LEFT JOIN relatorio_visitantes_presenca rvp ON rvp.relatorio_id = r.id
    LEFT JOIN visitantes v ON v.id = rvp.visitante_id
    LEFT JOIN (
        SELECT rmp.relatorio_id, COUNT(rmp.membro_id) AS total
        FROM relatorio_membros_presenca rmp
        GROUP BY rmp.relatorio_id
    ) membros_count ON membros_count.relatorio_id = r.id
    LEFT JOIN (
        SELECT rvp2.relatorio_id, COUNT(rvp2.visitante_id) AS visitantes_cadastrados
        FROM relatorio_visitantes_presenca rvp2
        GROUP BY rvp2.relatorio_id
    ) rv ON rv.relatorio_id = r.id
    WHERE c.ativo = TRUE
    GROUP BY c.id, c.nome, u.nome
""", nativeQuery = true)
    List<RankingCelulaProjection> buscarDadosRankingNativo(@Param("mes") String mesAno);}