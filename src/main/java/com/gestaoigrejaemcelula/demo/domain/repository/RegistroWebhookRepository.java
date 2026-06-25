package com.gestaoigrejaemcelula.demo.domain.repository;

import com.gestaoigrejaemcelula.demo.domain.entity.RegistroWebhook;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface RegistroWebhookRepository extends JpaRepository<RegistroWebhook, Long> {
    List<RegistroWebhook> findAllByOrderByRecebidoEmDesc();

    @Query("""
        SELECT r FROM RegistroWebhook r
        WHERE (:tipoEvento IS NULL OR r.tipoEvento = :tipoEvento)
          AND (:status     IS NULL OR r.status     = :status)
          AND (:busca      IS NULL OR r.numeroDestino LIKE %:busca%
                                   OR r.idMensagem    LIKE %:busca%)
        ORDER BY r.recebidoEm DESC
    """)
    List<RegistroWebhook> filtrar(
            @Param("tipoEvento") String tipoEvento,
            @Param("status")     String status,
            @Param("busca")      String busca
    );

    // Métricas
    long countByTipoEvento(String tipoEvento);
    long countByStatus(String status);
    long countByRecebidoEmAfter(LocalDateTime desde);
}


