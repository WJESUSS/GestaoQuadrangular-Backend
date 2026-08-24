package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.AcompanhamentoDiscipuladoIndividual;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusAcompanhamentoDiscipulado;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoEstudoDiscipulado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AcompanhamentoDiscipuladoIndividualRepository
        extends JpaRepository<AcompanhamentoDiscipuladoIndividual, Long> {

    boolean existsByMembro_IdAndSemanaInicioAndStatus(
            Long membroId,
            LocalDate semanaInicio,
            StatusAcompanhamentoDiscipulado status);

    Optional<AcompanhamentoDiscipuladoIndividual> findByIdAndCelula_Id(Long id, Long celulaId);

    List<AcompanhamentoDiscipuladoIndividual> findByCelula_Id(Long celulaId);

    List<AcompanhamentoDiscipuladoIndividual> findByMembro_IdOrderByDataDescIdDesc(Long membroId);

    @Query("""
        SELECT i FROM AcompanhamentoDiscipuladoIndividual i
        WHERE (:celulaId IS NULL OR i.celula.id = :celulaId)
          AND (:membroId IS NULL OR i.membro.id = :membroId)
          AND (:inicio IS NULL OR i.data >= :inicio)
          AND (:fim IS NULL OR i.data <= :fim)
          AND (:tema IS NULL OR LOWER(i.tema) LIKE LOWER(CONCAT('%', CAST(:tema AS string), '%')))
          AND (:tipoEstudo IS NULL OR i.tipoEstudo = :tipoEstudo)
        ORDER BY i.data DESC, i.id DESC
    """)
    List<AcompanhamentoDiscipuladoIndividual> buscarComFiltros(
            @Param("celulaId") Long celulaId,
            @Param("membroId") Long membroId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("tema") String tema,
            @Param("tipoEstudo") TipoEstudoDiscipulado tipoEstudo);

    @Query("""
        SELECT i FROM AcompanhamentoDiscipuladoIndividual i
        WHERE i.celula.id IN :celulaIds
          AND (:membroId IS NULL OR i.membro.id = :membroId)
          AND (:inicio IS NULL OR i.data >= :inicio)
          AND (:fim IS NULL OR i.data <= :fim)
          AND (:tema IS NULL OR LOWER(i.tema) LIKE LOWER(CONCAT('%', CAST(:tema AS string), '%')))
          AND (:tipoEstudo IS NULL OR i.tipoEstudo = :tipoEstudo)
        ORDER BY i.data DESC, i.id DESC
    """)
    List<AcompanhamentoDiscipuladoIndividual> buscarPorCelulasComFiltros(
            @Param("celulaIds") List<Long> celulaIds,
            @Param("membroId") Long membroId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("tema") String tema,
            @Param("tipoEstudo") TipoEstudoDiscipulado tipoEstudo);

    @Query(value = """
        SELECT ai.celula_id AS celulaId, COUNT(*) AS unidades
        FROM acompanhamento_discipulado_individual ai
        WHERE ai.status = 'CONCLUIDO'
          AND ai.data >= TO_DATE(:mes || '-01', 'YYYY-MM-DD')
          AND ai.data <  TO_DATE(:mes || '-01', 'YYYY-MM-DD') + INTERVAL '1 MONTH'
        GROUP BY ai.celula_id
    """, nativeQuery = true)
    List<Object[]> contarUnidadesPorCelulaNoMes(@Param("mes") String mesAno);
}
