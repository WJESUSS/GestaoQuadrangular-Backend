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
        c.id AS celulaId,
        c.nome AS nomeCelula,
        u.nome AS lider,
        COALESCE(COUNT(r.id), 0) * 10.0 AS presencaMedia,
        0 AS visitantes,
        0 AS consolidados,
        0 AS batismos,
        FALSE AS multiplicou,
        0 AS pontuacao
    FROM celulas c
    LEFT JOIN usuarios u ON u.id = c.lider_id
    LEFT JOIN relatorio r 
        ON r.celula_id = c.id
        AND r.data_reuniao >= TO_DATE(:mes || '-01', 'YYYY-MM-DD')
        AND r.data_reuniao < (TO_DATE(:mes || '-01', 'YYYY-MM-DD') + INTERVAL '1 MONTH')
    GROUP BY c.id, c.nome, u.nome
""", nativeQuery = true)
    List<RankingCelulaProjection> buscarDadosRankingNativo(@Param("mes") String mesAno);}