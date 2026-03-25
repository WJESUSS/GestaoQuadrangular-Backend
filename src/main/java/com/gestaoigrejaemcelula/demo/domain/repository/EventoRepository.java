package com.gestaoigrejaemcelula.demo.domain.repository;



import com.gestaoigrejaemcelula.demo.domain.entity.Evento;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoEvento;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface EventoRepository extends JpaRepository<Evento, Long> {

    List<Evento> findByTipo(TipoEvento tipo);

    List<Evento> findByDataBetween(LocalDate inicio, LocalDate fim);
}

