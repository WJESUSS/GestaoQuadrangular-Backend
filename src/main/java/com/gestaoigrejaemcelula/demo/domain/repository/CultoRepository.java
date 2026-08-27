package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.aplication.dto.CultoRelatorioCampanhaDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.CultoRelatorioComparativoDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.CultoRelatorioPregadorDTO;
import com.gestaoigrejaemcelula.demo.aplication.dto.CultoRelatorioResumoDTO;
import com.gestaoigrejaemcelula.demo.domain.entity.Culto;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoCulto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface CultoRepository extends JpaRepository<Culto, UUID> {

    Optional<Culto> findByDataAndTipoCultoAndHorario(LocalDate data, TipoCulto tipoCulto, String horario);

    Page<Culto> findByDataBetweenAndTipoCultoAndPregadorContainingIgnoreCaseAndCampanhaAtivaAndRegistradoPorId(
            LocalDate dataInicio, LocalDate dataFim,
            TipoCulto tipoCulto, String pregador,
            Boolean campanhaAtiva, Long registradoPorId,
            Pageable pageable);

    Page<Culto> findByDataBetween(LocalDate dataInicio, LocalDate dataFim, Pageable pageable);

    List<Culto> findByDataBetweenAndTipoCultoOrderByDataDesc(LocalDate dataInicio, LocalDate dataFim, TipoCulto tipoCulto);

    @Query("""
        SELECT c FROM Culto c
        WHERE (:dataInicio IS NULL OR c.data >= :dataInicio)
          AND (:dataFim IS NULL OR c.data <= :dataFim)
          AND (:tipoCulto IS NULL OR c.tipoCulto = :tipoCulto)
          AND (:pregador IS NULL OR LOWER(c.pregador) LIKE LOWER(CONCAT('%', CAST(:pregador AS String), '%')))
          AND (:campanha IS NULL OR c.campanhaAtiva = :campanha)
          AND (:registradoPor IS NULL OR c.registradoPor.id = :registradoPor)
        ORDER BY c.data DESC
    """)
    Page<Culto> filtrar(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            @Param("tipoCulto") TipoCulto tipoCulto,
            @Param("pregador") String pregador,
            @Param("campanha") Boolean campanha,
            @Param("registradoPor") Long registradoPor,
            Pageable pageable);

    @Query("""
        SELECT new com.gestaoigrejaemcelula.demo.aplication.dto.CultoRelatorioResumoDTO(
            COUNT(c),
            COALESCE(SUM(c.quantidadeMembros), 0),
            COALESCE(SUM(c.visitantesSimpatizantes), 0),
            COALESCE(SUM(c.totalCriancas), 0),
            COALESCE(SUM(c.quantidadeDiaconos), 0),
            COALESCE(SUM(c.totalGeral), 0),
            CASE WHEN COUNT(c) > 0 THEN CAST(SUM(c.totalGeral) AS DOUBLE) / COUNT(c) ELSE 0.0 END,
            CAST(COALESCE((SELECT AVG(c2.totalGeral) FROM Culto c2 WHERE c2.tipoCulto = 'VITORIA' AND c2.data BETWEEN :dataInicio AND :dataFim), 0) AS DOUBLE),
            CAST(COALESCE((SELECT AVG(c2.totalGeral) FROM Culto c2 WHERE c2.tipoCulto = 'SANTA_CEIA' AND c2.data BETWEEN :dataInicio AND :dataFim), 0) AS DOUBLE),
            CAST(COALESCE((SELECT AVG(c2.totalGeral) FROM Culto c2 WHERE c2.tipoCulto = 'CELEBRACAO' AND c2.data BETWEEN :dataInicio AND :dataFim), 0) AS DOUBLE),
            CAST(COALESCE((SELECT AVG(c2.totalGeral) FROM Culto c2 WHERE c2.tipoCulto = 'MISSOES' AND c2.data BETWEEN :dataInicio AND :dataFim), 0) AS DOUBLE)
        )
        FROM Culto c
        WHERE c.data BETWEEN :dataInicio AND :dataFim
    """)
    CultoRelatorioResumoDTO resumo(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim);

    @Query("""
        SELECT new com.gestaoigrejaemcelula.demo.aplication.dto.CultoRelatorioComparativoDTO(
            c.data, c.tipoCulto,
            c.totalGeral, c.quantidadeMembros,
            c.visitantesSimpatizantes, c.totalCriancas, c.quantidadeDiaconos
        )
        FROM Culto c
        WHERE c.data BETWEEN :dataInicio AND :dataFim
        ORDER BY c.data ASC
    """)
    Page<CultoRelatorioComparativoDTO> comparativo(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            Pageable pageable);

    @Query(
        value = """
        SELECT new com.gestaoigrejaemcelula.demo.aplication.dto.CultoRelatorioCampanhaDTO(
            c.nomeCampanha, COUNT(c), SUM(c.totalGeral),
            CAST(SUM(c.totalGeral) AS DOUBLE) / COUNT(c)
        )
        FROM Culto c
        WHERE c.campanhaAtiva = true
          AND c.nomeCampanha IS NOT NULL
          AND c.data BETWEEN :dataInicio AND :dataFim
        GROUP BY c.nomeCampanha
        ORDER BY c.nomeCampanha
        """,
        countQuery = "SELECT COUNT(DISTINCT c.nomeCampanha) FROM Culto c WHERE c.campanhaAtiva = true AND c.nomeCampanha IS NOT NULL AND c.data BETWEEN :dataInicio AND :dataFim"
    )
    Page<CultoRelatorioCampanhaDTO> porCampanha(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            Pageable pageable);

    @Query(
        value = """
        SELECT new com.gestaoigrejaemcelula.demo.aplication.dto.CultoRelatorioPregadorDTO(
            c.pregador, COUNT(c),
            CAST(SUM(c.totalGeral) AS DOUBLE) / COUNT(c),
            MAX(c.totalGeral), MIN(c.totalGeral)
        )
        FROM Culto c
        WHERE c.data BETWEEN :dataInicio AND :dataFim
        GROUP BY c.pregador
        ORDER BY COUNT(c) DESC
        """,
        countQuery = "SELECT COUNT(DISTINCT c.pregador) FROM Culto c WHERE c.data BETWEEN :dataInicio AND :dataFim"
    )
    Page<CultoRelatorioPregadorDTO> porPregador(
            @Param("dataInicio") LocalDate dataInicio,
            @Param("dataFim") LocalDate dataFim,
            Pageable pageable);

    @Query("""
        SELECT COALESCE(AVG(c.totalGeral), 0.0)
        FROM Culto c
        WHERE c.tipoCulto = :tipoCulto
          AND c.data < :data
    """)
    Double mediaTotalGeralPorTipoAteData(
            @Param("tipoCulto") TipoCulto tipoCulto,
            @Param("data") LocalDate data);

    @Query("""
        SELECT COALESCE(AVG(c.totalGeral), 0.0)
        FROM Culto c
        WHERE c.tipoCulto = :tipoCulto
    """)
    Double mediaTotalGeralPorTipo(@Param("tipoCulto") TipoCulto tipoCulto);
}
