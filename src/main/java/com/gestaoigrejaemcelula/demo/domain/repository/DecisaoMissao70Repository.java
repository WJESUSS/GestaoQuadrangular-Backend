package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.DecisaoMissao70;
import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DecisaoMissao70Repository extends JpaRepository<DecisaoMissao70, Long> {

    long countByEncontro_Missao70_IdAndTipoDecisao(Long missao70Id, DecisaoEspiritual tipo);
}