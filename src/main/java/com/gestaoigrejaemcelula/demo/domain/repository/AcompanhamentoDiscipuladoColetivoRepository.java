package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.AcompanhamentoDiscipuladoColetivo;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoEstudoDiscipulado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface AcompanhamentoDiscipuladoColetivoRepository
        extends JpaRepository<AcompanhamentoDiscipuladoColetivo, Long> {

    @Query("""
        SELECT DISTINCT c FROM AcompanhamentoDiscipuladoColetivo c
        LEFT JOIN FETCH c.participantes p
        LEFT JOIN FETCH p.membro
        WHERE c.id = :id
    """)
    Optional<AcompanhamentoDiscipuladoColetivo> buscarComParticipantesPorId(@Param("id") Long id);

    @Query("""
        SELECT DISTINCT c FROM AcompanhamentoDiscipuladoColetivo c
        LEFT JOIN FETCH c.participantes p
        LEFT JOIN FETCH p.membro
        WHERE (:celulaId IS NULL OR c.celula.id = :celulaId)
          AND (:membroId IS NULL OR EXISTS (
                SELECT 1 FROM AcompanhamentoDiscipuladoColetivoParticipante x
                WHERE x.discipulado = c AND x.membro.id = :membroId))
          AND (:inicio IS NULL OR c.data >= :inicio)
          AND (:fim IS NULL OR c.data <= :fim)
          AND (:tema IS NULL OR LOWER(c.tema) LIKE LOWER(CONCAT('%', CAST(:tema AS string), '%')))
          AND (:tipoEstudo IS NULL OR c.tipoEstudo = :tipoEstudo)
        ORDER BY c.data DESC, c.id DESC
    """)
    List<AcompanhamentoDiscipuladoColetivo> buscarComFiltros(
            @Param("celulaId") Long celulaId,
            @Param("membroId") Long membroId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("tema") String tema,
            @Param("tipoEstudo") TipoEstudoDiscipulado tipoEstudo);

    @Query("""
        SELECT DISTINCT c FROM AcompanhamentoDiscipuladoColetivo c
        LEFT JOIN FETCH c.participantes p
        LEFT JOIN FETCH p.membro
        WHERE c.celula.id IN :celulaIds
          AND (:membroId IS NULL OR EXISTS (
                SELECT 1 FROM AcompanhamentoDiscipuladoColetivoParticipante x
                WHERE x.discipulado = c AND x.membro.id = :membroId))
          AND (:inicio IS NULL OR c.data >= :inicio)
          AND (:fim IS NULL OR c.data <= :fim)
          AND (:tema IS NULL OR LOWER(c.tema) LIKE LOWER(CONCAT('%', CAST(:tema AS string), '%')))
          AND (:tipoEstudo IS NULL OR c.tipoEstudo = :tipoEstudo)
        ORDER BY c.data DESC, c.id DESC
    """)
    List<AcompanhamentoDiscipuladoColetivo> buscarPorCelulasComFiltros(
            @Param("celulaIds") List<Long> celulaIds,
            @Param("membroId") Long membroId,
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim,
            @Param("tema") String tema,
            @Param("tipoEstudo") TipoEstudoDiscipulado tipoEstudo);
}
