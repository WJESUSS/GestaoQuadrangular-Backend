package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.LancamentoTesouraria;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public interface LancamentoTesourariaRepository extends JpaRepository<LancamentoTesouraria, Long> {
    List<LancamentoTesouraria> findByMembroNome(String membroNome);
    @Query("SELECT l FROM LancamentoTesouraria l WHERE MONTH(l.dataLancamento) = :mes AND YEAR(l.dataLancamento) = :ano")
    List<LancamentoTesouraria> findByMesAno(@Param("mes") int mes, @Param("ano") int ano);

    @Query("SELECT l FROM LancamentoTesouraria l WHERE MONTH(l.dataLancamento) = :mes AND YEAR(l.dataLancamento) = :ano")
    List<LancamentoTesouraria> findByMesAndAno(@Param("mes") int mes, @Param("ano") int ano);

    // Soma total de dízimos
    @Query("SELECT SUM(l.valorDizimo) FROM LancamentoTesouraria l WHERE MONTH(l.dataLancamento) = :mes AND YEAR(l.dataLancamento) = :ano")
    BigDecimal totalDizimoPorMesAno(@Param("mes") int mes, @Param("ano") int ano);

    // Soma total de ofertas por mês/ano
    @Query("SELECT SUM(l.valorOferta) FROM LancamentoTesouraria l WHERE MONTH(l.dataLancamento) = :mes AND YEAR(l.dataLancamento) = :ano")
    BigDecimal totalOfertaPorMesAno(@Param("mes") int mes, @Param("ano") int ano);

    // Soma total de ofertas por tipo
    @Query("SELECT SUM(l.valorOferta) FROM LancamentoTesouraria l WHERE MONTH(l.dataLancamento) = :mes AND YEAR(l.dataLancamento) = :ano AND l.tipoOferta = :tipo")
    BigDecimal totalOfertaPorMesAnoETipo(@Param("mes") int mes, @Param("ano") int ano, @Param("tipo") String tipo);
    @Query("SELECT COUNT(l) FROM LancamentoTesouraria l WHERE l.membroNome = :membroNome AND MONTH(l.dataLancamento) = :mes AND YEAR(l.dataLancamento) = :ano")
    long countByMembroAndMesAno(@Param("membroNome") String membroNome,
                                @Param("mes") int mes,
                                @Param("ano") int ano);

    @Query("SELECT l.tipoOferta, SUM(COALESCE(l.valorDizimo, 0) + COALESCE(l.valorOferta, 0)) FROM LancamentoTesouraria l GROUP BY l.tipoOferta")
    List<Object[]> sumAgrupadoPorTipo();

    @Query("SELECT l.membroNome, SUM(COALESCE(l.valorDizimo, 0)), SUM(COALESCE(l.valorOferta, 0)) FROM LancamentoTesouraria l GROUP BY l.membroNome")
    List<Object[]> sumAgrupadoPorMembro();

    @Query("SELECT l.membroNome FROM LancamentoTesouraria l WHERE MONTH(l.dataLancamento) = :mes AND YEAR(l.dataLancamento) = :ano GROUP BY l.membroNome")
    List<String> findMembrosComLancamentoNoMes(@Param("mes") int mes, @Param("ano") int ano);

    @Query("SELECT MONTH(l.dataLancamento) as mes, SUM(COALESCE(l.valorDizimo, 0)), SUM(COALESCE(l.valorOferta, 0)) FROM LancamentoTesouraria l WHERE YEAR(l.dataLancamento) = :ano GROUP BY MONTH(l.dataLancamento) ORDER BY MONTH(l.dataLancamento)")
    List<Object[]> comparativoAnual(@Param("ano") int ano);

}
