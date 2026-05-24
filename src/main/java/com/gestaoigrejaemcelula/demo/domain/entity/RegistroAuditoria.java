package com.gestaoigrejaemcelula.demo.domain.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "registro_auditoria", indexes = {
        @Index(name = "idx_auditoria_entidade",   columnList = "entidade, entidadeId"),
        @Index(name = "idx_auditoria_usuario",     columnList = "usuarioNome"),
        @Index(name = "idx_auditoria_data",        columnList = "dataHora"),
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RegistroAuditoria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Ex: "MEMBRO", "VISITANTE", "CELULA", "FICHA", "USUARIO" */
    @Column(nullable = false, length = 40)
    private String entidade;

    /** PK da entidade auditada */
    @Column(nullable = false)
    private Long entidadeId;

    /** Nome legível do registro (ex: "João Silva") */
    @Column(length = 120)
    private String entidadeNome;

    /** CREATE · UPDATE · DELETE · APPROVE · REJECT */
    @Column(nullable = false, length = 20)
    private String acao;

    /** JSON com os campos alterados: {"campo": {"de": "...", "para": "..."}} */
    @Column(columnDefinition = "TEXT")
    private String detalhes;

    /** Quem fez a alteração */
    @Column(nullable = false, length = 120)
    private String usuarioNome;

    @Column(length = 120)
    private String usuarioEmail;

    /** Quem aprovou (nullable — só preenchido quando acao = APPROVE/REJECT) */
    @Column(length = 120)
    private String aprovadorNome;

    @Column(length = 120)
    private String aprovadorEmail;

    @Column(nullable = false)
    private LocalDateTime dataHora;

    /** IP de origem da requisição */
    @Column(length = 50)
    private String ipOrigem;
}
