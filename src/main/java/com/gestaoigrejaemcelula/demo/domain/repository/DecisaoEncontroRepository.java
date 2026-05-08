package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.DecisaoEncontro;
import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DecisaoEncontroRepository extends JpaRepository<DecisaoEncontro, Long> {
    List<DecisaoEncontro> findByEncontroCasaDePazId(Long casaDePazId);
    long countByEncontro_CasaDePaz_IdAndTipoDecisao(Long casaDePazId, DecisaoEspiritual tipo);
}