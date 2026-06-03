package com.gestaoigrejaemcelula.demo.domain.repository;



import com.gestaoigrejaemcelula.demo.domain.entity.JornadaEspiritual;
import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface JornadaEspiritualRepository extends JpaRepository<JornadaEspiritual, Long> {

    /** Todas as etapas de um visitante, ordenadas por data */
    List<JornadaEspiritual> findByVisitanteIdOrderByDataRegistroAsc(Long visitanteId);

    /** Etapa específica de um visitante (para checar duplicata ou buscar para deletar) */
    Optional<JornadaEspiritual> findByVisitanteIdAndDecisao(Long visitanteId, DecisaoEspiritual decisao);

    /** Verifica se o visitante já possui a decisão registrada */
    boolean existsByVisitanteIdAndDecisao(Long visitanteId, DecisaoEspiritual decisao);

    /**
     * Conta quantas vezes uma decisão foi registrada para visitantes de uma célula.
     * Substitui o antigo countByCelulaIdAndDecisaoEspiritual do VisitanteRepository.
     * Cada registro na tabela jornada_espiritual = +1 no contador (nunca sobrescreve).
     */
    @Query("""
        SELECT COUNT(j) FROM JornadaEspiritual j
        WHERE j.visitante.celula.id = :celulaId
          AND j.decisao = :decisao
    """)
    long countByCelulaIdAndDecisao(
            @Param("celulaId") Long celulaId,
            @Param("decisao") DecisaoEspiritual decisao
    );

    /**
     * Conta decisões de uma célula dentro de um período (para metas mensais/trimestrais/anuais).
     */
    @Query("""
        SELECT COUNT(j) FROM JornadaEspiritual j
        WHERE j.visitante.celula.id = :celulaId
          AND j.decisao = :decisao
          AND j.dataRegistro BETWEEN :inicio AND :fim
    """)
    long countByCelulaIdAndDecisaoAndPeriodo(
            @Param("celulaId") Long celulaId,
            @Param("decisao") DecisaoEspiritual decisao,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );

    /**
     * Relatório de todas as decisões de uma célula no período.
     * Útil para exibir histórico detalhado.
     */
    @Query("""
        SELECT j FROM JornadaEspiritual j
        WHERE j.visitante.celula.id = :celulaId
          AND j.dataRegistro BETWEEN :inicio AND :fim
        ORDER BY j.dataRegistro DESC
    """)
    List<JornadaEspiritual> findByCelulaIdAndPeriodo(
            @Param("celulaId") Long celulaId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );
}
