package com.gestaoigrejaemcelula.demo.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

// Se o seu projeto ainda estiver no Spring Boot 2.x (javax em vez de jakarta),
// troque os imports acima de "jakarta.persistence.*" para "javax.persistence.*".
@Entity
@Table(name = "numero_bloqueado")
@Data
@NoArgsConstructor
public class NumeroBloqueado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 30)
    private String numero;

    @Column(length = 255)
    private String motivo;

    @Column(name = "bloqueado_em", nullable = false)
    private LocalDateTime bloqueadoEm;

    @PrePersist
    public void prePersist() {
        if (bloqueadoEm == null) {
            bloqueadoEm = LocalDateTime.now();
        }
    }
}