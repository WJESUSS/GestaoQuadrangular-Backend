package com.gestaoigrejaemcelula.demo.domain.repository;


import com.gestaoigrejaemcelula.demo.domain.entity.HistoricoCelula;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface HistoricoCelulaRepository
        extends JpaRepository<HistoricoCelula, Long> {

    List<HistoricoCelula> findByMembroId(Long membroId);

    HistoricoCelula findByMembroIdAndDataSaidaIsNull(Long membroId);
}

