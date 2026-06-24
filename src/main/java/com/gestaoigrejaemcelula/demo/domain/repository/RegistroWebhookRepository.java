package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.RegistroWebhook;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RegistroWebhookRepository extends JpaRepository<RegistroWebhook, Long> {
    List<RegistroWebhook> findAllByOrderByRecebidoEmDesc();
}
