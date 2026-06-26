package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.NumeroBloqueado;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface NumeroBloqueadoRepository extends JpaRepository<NumeroBloqueado, Long> {

    boolean existsByNumero(String numero);

    Optional<NumeroBloqueado> findByNumero(String numero);

    // Métodos de escrita DERIVADOS (deleteBy...) não recebem transação automática
    // do Spring Data como save()/deleteById() recebem — por isso precisam de
    // @Transactional explícito aqui (e @Modifying, já que altera dados).
    @Modifying
    @Transactional
    void deleteByNumero(String numero);

    List<NumeroBloqueado> findAllByOrderByBloqueadoEmDesc();
}