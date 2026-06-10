package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.Presenca;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface PresencaRepository extends JpaRepository<Presenca, Long> {
    List<Presenca> findByRelatorioId(Long relatorioId);
    List<Presenca> findByRelatorioIdAndPresenteFalse(Long relatorioId);
}