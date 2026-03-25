package com.gestaoigrejaemcelula.demo.domain.repository;




import com.gestaoigrejaemcelula.demo.domain.entity.FichaEncontro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface FichaEncontroRepository extends JpaRepository<FichaEncontro, Long> {

    List<FichaEncontro> findByNomeConvidador(String nomeConvidador);

    List<FichaEncontro> findByDataInicio(LocalDate dataInicio);

    long countByIdIsNotNull();

    // CORREÇÃO AQUI: O nome deve bater com o atributo da Entity
    List<FichaEncontro> findByDataInicioBetween(LocalDate inicio, LocalDate fim);

    List<FichaEncontro> findByNomeConvidadoContainingIgnoreCase(String nome);

    List<FichaEncontro> findByNomeEncontro(String nomeEncontro);

    @Query("""
       SELECT f.nomeConvidador, COUNT(f)
       FROM FichaEncontro f
       GROUP BY f.nomeConvidador
    """)
    List<Object[]> totalPorConvidador();

    List<FichaEncontro> findByUsuarioIdOrderByDataCriacaoDesc(Long usuarioId);
}