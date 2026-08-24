package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.AcompanhamentoDiscipuladoColetivoParticipante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AcompanhamentoDiscipuladoColetivoParticipanteRepository
        extends JpaRepository<AcompanhamentoDiscipuladoColetivoParticipante, Long> {

    List<AcompanhamentoDiscipuladoColetivoParticipante>
            findByDiscipulado_Celula_Id(Long celulaId);

    @Query(value = """
        SELECT ac.celula_id AS celulaId, COUNT(ap.id) AS unidades
        FROM acompanhamento_discipulado_coletivo_participante ap
        INNER JOIN acompanhamento_discipulado_coletivo ac ON ac.id = ap.discipulado_coletivo_id
        WHERE ac.status = 'CONCLUIDO'
          AND ac.data >= TO_DATE(:mes || '-01', 'YYYY-MM-DD')
          AND ac.data <  TO_DATE(:mes || '-01', 'YYYY-MM-DD') + INTERVAL '1 MONTH'
        GROUP BY ac.celula_id
    """, nativeQuery = true)
    List<Object[]> contarUnidadesPorCelulaNoMes(@Param("mes") String mesAno);
}
