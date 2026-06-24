package com.gestaoigrejaemcelula.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "webhooks_whatsapp")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class RegistroWebhook {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "tipo_evento", length = 50)
    private String tipoEvento;

    @Column(name = "numero_destino", length = 20)
    private String numeroDestino;

    @Column(name = "status", length = 30)
    private String status;

    @Column(name = "id_mensagem", length = 100)
    private String idMensagem;

    @Column(columnDefinition = "TEXT")
    private String payload;

    @Column(name = "recebido_em", nullable = false)
    private LocalDateTime recebidoEm = LocalDateTime.now();
}
