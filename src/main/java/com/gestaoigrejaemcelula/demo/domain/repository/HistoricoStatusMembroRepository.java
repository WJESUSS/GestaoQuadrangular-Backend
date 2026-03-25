package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.HistoricoStatusMembro;
import org.springframework.data.jpa.repository.JpaRepository;

public interface HistoricoStatusMembroRepository
        extends JpaRepository<HistoricoStatusMembro, Long> {
}