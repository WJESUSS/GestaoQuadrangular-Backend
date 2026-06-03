package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.Meta;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface MetaRepository extends JpaRepository<Meta, Long> {

    // Buscar todas as metas de uma célula
    List<Meta> findByCelulaIdOrderByMesAnoDesc(Long celulaId);

    // Buscar metas ativas de uma célula
    List<Meta> findByCelulaIdAndAtivaOrderByMesAnoDesc(Long celulaId, boolean ativa);

    // Buscar meta por célula e tipo
    List<Meta> findByCelulaIdAndTipoMeta(Long celulaId, String tipoMeta);

    // Buscar meta por célula, tipo e mês
    Optional<Meta> findByCelulaIdAndTipoMetaAndMesAno(Long celulaId, String tipoMeta, LocalDate mesAno);

    // Buscar metas do mês/ano atual
    List<Meta> findByMesAnoAndAtiva(LocalDate mesAno, boolean ativa);

    // Contar metas concluídas de uma célula
    @Query("SELECT COUNT(m) FROM Meta m WHERE m.celula.id = :celulaId AND m.metaAlcancada >= m.metaTotal AND m.ativa = true")
    long contarMetasConcluidas(@Param("celulaId") Long celulaId);

    // Buscar metas próximas de conclusão (80% ou mais)
    @Query("SELECT m FROM Meta m WHERE m.celula.id = :celulaId AND (m.metaAlcancada * 100 / m.metaTotal) >= 80 AND m.ativa = true")
    List<Meta> encontrarMetasProximasConclusao(@Param("celulaId") Long celulaId);

    // Buscar metas em atraso (com progresso menor que 50% no meio do mês)
    @Query("SELECT m FROM Meta m WHERE m.celula.id = :celulaId AND (m.metaAlcancada * 100 / m.metaTotal) < 50 AND m.ativa = true")
    List<Meta> encontrarMetasEmAtraso(@Param("celulaId") Long celulaId);
}