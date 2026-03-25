package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.DiscipuladoRelatorio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface DiscipuladoRelatorioRepository extends JpaRepository<DiscipuladoRelatorio, Long> {

    List<DiscipuladoRelatorio> findBySemanaInicioAndSemanaFim(LocalDate inicio, LocalDate fim);

    boolean existsByMembroIdAndSemanaInicioAndSemanaFim(Long membroId, LocalDate inicio, LocalDate fim);
    @Query("SELECT r FROM DiscipuladoRelatorio r " +
            "LEFT JOIN FETCH r.celula " + // O segredo é o FETCH
            "LEFT JOIN FETCH r.lider " +
            "LEFT JOIN FETCH r.membro")
    List<DiscipuladoRelatorio> findAllComDetalhes();
    /**
     * Busca membros que faltaram em ambos os cultos de domingo (manhã e noite)
     * pelo menos 3 vezes dentro do mês e ano selecionados.
     */
    @Query(value = """
        SELECT 
            m.id, 
            m.nome, 
            m.telefone, 
            c.nome as nome_celula, 
            COUNT(dr.id) as total_faltas
        FROM discipulado_relatorio dr
        JOIN membros m ON m.id = dr.membro_id
        JOIN celulas c ON c.id = m.celula_id
        WHERE dr.domingo_manha = false 
          AND dr.domingo_noite = false
          AND EXTRACT(MONTH FROM dr.semana_inicio) = :mes
          AND EXTRACT(YEAR FROM dr.semana_inicio) = :ano
          AND m.id NOT IN (
              SELECT da.membro_id 
              FROM discipulado_acompanhamento da 
              WHERE da.mes_referencia = :mesRef
          )
        GROUP BY m.id, m.nome, m.telefone, c.nome
        HAVING COUNT(dr.id) >= 3
    """, nativeQuery = true)
    List<Object[]> buscarAlertasPastor(
            @Param("mes") int mes,
            @Param("ano") int ano,
            @Param("mesRef") String mesRef
    );
    @Query("SELECT r FROM DiscipuladoRelatorio r " +
            "JOIN FETCH r.lider l " +
            "LEFT JOIN FETCH l.celula c " + // Busca a célula ligada ao líder
            "JOIN FETCH r.membro m")
    List<DiscipuladoRelatorio> findAllCompletos();
    @Query("SELECT r FROM DiscipuladoRelatorio r " +
            "JOIN FETCH r.lider l " +
            "LEFT JOIN FETCH l.celula c " +
            "JOIN FETCH r.membro m")
    List<DiscipuladoRelatorio> findAllWithEagerRelationships();

}
