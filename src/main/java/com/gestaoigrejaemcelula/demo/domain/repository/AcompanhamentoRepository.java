package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.DiscipuladoAcompanhamento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AcompanhamentoRepository extends JpaRepository<DiscipuladoAcompanhamento, Long> {

    /**
     * Verifica se o membro já recebeu acompanhamento no mês específico.
     * Útil para evitar que o pastor registre duas vezes o mesmo cuidado.
     */
    boolean existsByMembroIdAndMesReferencia(Long membroId, String mesReferencia);
}