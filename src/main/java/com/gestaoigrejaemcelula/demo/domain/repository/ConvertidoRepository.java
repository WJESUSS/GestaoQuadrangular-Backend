package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.Convertido;
import com.gestaoigrejaemcelula.demo.domain.enums.StatusConvertido;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ConvertidoRepository extends JpaRepository<Convertido, Long> {

    Page<Convertido> findByStatus(StatusConvertido status, Pageable pageable);

    boolean existsByCpfAndStatus(String cpf, StatusConvertido status);
}
