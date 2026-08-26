package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.RankingFinalizado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RankingFinalizadoRepository extends JpaRepository<RankingFinalizado, Long> {

    List<RankingFinalizado> findByMesAnoOrderByPosicaoAsc(String mesAno);

    boolean existsByMesAno(String mesAno);
}
