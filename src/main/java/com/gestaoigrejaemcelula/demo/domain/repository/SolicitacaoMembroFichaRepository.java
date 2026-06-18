package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.SolicitacaoMembroFicha;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusSolicitacaoMembro;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SolicitacaoMembroFichaRepository extends JpaRepository<SolicitacaoMembroFicha, Long> {
    List<SolicitacaoMembroFicha> findByLiderIdOrderByDataSolicitacaoDesc(Long liderId);

    Page<SolicitacaoMembroFicha> findByStatus(StatusSolicitacaoMembro status, Pageable pageable);
}
