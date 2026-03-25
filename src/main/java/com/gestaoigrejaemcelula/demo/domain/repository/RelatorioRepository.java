package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.Relatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface RelatorioRepository extends JpaRepository<Relatorio, Long> {

    // Relatórios após uma data específica
    List<Relatorio> findByDataReuniaoAfter(LocalDate data);
    // Busca todos os relatórios onde dataReuniao >= data passada
    List<Relatorio> findByDataReuniaoGreaterThanEqual(LocalDate data);

    // Versão ordenada (mais recente primeiro)

    // Se quiser também limitar por célula (muito útil)
    List<Relatorio> findByCelulaIdAndDataReuniaoGreaterThanEqual(Long celulaId, LocalDate data);

    // Intervalo completo (between)

    // Relatórios de uma célula específica, ordenados por data mais recente
    List<Relatorio> findByCelulaIdOrderByDataReuniaoDesc(Long celulaId);

    // Contagem de relatórios enviados por uma célula em um mês específico
    @Query("SELECT COUNT(r) FROM Relatorio r " +
            "WHERE r.celula.id = :celulaId AND MONTH(r.dataReuniao) = :mes")
    long countRelatoriosPorMes(@Param("celulaId") Long celulaId, @Param("mes") int mes);

    // Soma total de visitantes (apenas os manuais) no período
    @Query("SELECT SUM(r.quantidadeVisitantes) FROM Relatorio r " +
            "WHERE r.dataReuniao BETWEEN :inicio AND :fim")
    Integer totalVisitantesNoPeriodo(@Param("inicio") LocalDate inicio, @Param("fim") LocalDate fim);

    // Busca relatórios entre duas datas, carregando a célula junto (JOIN FETCH)
    @Query("""
    SELECT r FROM Relatorio r
    JOIN FETCH r.celula
    WHERE r.dataReuniao BETWEEN :inicio AND :fim
    ORDER BY r.dataReuniao DESC
    """)
    List<Relatorio> findRelatoriosEntreDatasComCelula(
            @Param("inicio") LocalDate inicio,
            @Param("fim") LocalDate fim
    );
    List<Relatorio> findByDataReuniaoGreaterThanEqualOrderByDataReuniaoDesc(LocalDate data);
    // Versão simples (sem fetch), se não precisar carregar a célula imediatamente
    List<Relatorio> findByDataReuniaoBetween(LocalDate inicio, LocalDate fim);

    @Query("""
    SELECT r FROM Relatorio r
    WHERE r.dataReuniao BETWEEN :inicio AND :fim
""")
    List<Relatorio> findByDataBetween(LocalDate inicio, LocalDate fim);

}