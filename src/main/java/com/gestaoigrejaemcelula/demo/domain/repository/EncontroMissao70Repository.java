package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.EncontroMissao70;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EncontroMissao70Repository extends JpaRepository<EncontroMissao70, Long> {

    List<EncontroMissao70> findByMissao70IdOrderByNumeroSemanaAsc(Long missao70Id);
}
