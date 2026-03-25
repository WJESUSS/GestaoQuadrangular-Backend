package com.gestaoigrejaemcelula.demo.domain.repository;


import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.Visitante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Collection;
import java.util.List;

public interface VisitanteRepository extends JpaRepository<Visitante, Long> {

    List<Visitante> findByNomeContainingIgnoreCase(String nome);
    List<Visitante> findByCelulaIdAndAtivoTrue(Long celulaId);

    List<Visitante> findByCelulaId(Long celulaId);



}

