package com.gestaoigrejaemcelula.demo.domain.repository;


import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.Visitante;
import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;

public interface VisitanteRepository extends JpaRepository<Visitante, Long> {

    List<Visitante> findByNomeContainingIgnoreCase(String nome);
    Page<Visitante> findAllByArquivadoFalse(Pageable pageable);
    Page<Visitante> findByArquivadoTrue(Pageable pageable);
    List<Visitante> findByCelulaIdAndAtivoTrueAndArquivadoFalse(Long celulaId);

    List<Visitante> findByArquivadoTrue();

    List<Visitante> findAllByArquivadoFalse();
    List<Visitante> findByCelulaId(Long celulaId);
    long countByCelulaIdAndDecisaoEspiritualAndAtivoTrue(Long celulaId, DecisaoEspiritual decisaoEspiritual);

    @Query("SELECT v.decisaoEspiritual, COUNT(v) FROM Visitante v WHERE v.celula.id = :celulaId AND v.ativo = true AND v.decisaoEspiritual IN :decisoes GROUP BY v.decisaoEspiritual")
    List<Object[]> countPorDecisao(@Param("celulaId") Long celulaId, @Param("decisoes") List<DecisaoEspiritual> decisoes);

}

