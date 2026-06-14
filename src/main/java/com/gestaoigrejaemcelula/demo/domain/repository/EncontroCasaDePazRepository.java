package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.EncontroCasaDePaz;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface EncontroCasaDePazRepository extends JpaRepository<EncontroCasaDePaz, Long> {
    List<EncontroCasaDePaz> findByCasaDePazId(Long casaDePazId);
    boolean existsByCasaDePazIdAndDataEncontro(Long casaDePazId, LocalDate dataEncontro);
}