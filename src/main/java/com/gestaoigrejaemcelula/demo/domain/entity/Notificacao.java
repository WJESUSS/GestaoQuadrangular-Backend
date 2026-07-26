package com.gestaoigrejaemcelula.demo.domain.entity;

import com.gestaoigrejaemcelula.demo.domain.entity.Usuario;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "notificacoes")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Notificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", nullable = false)
    private Usuario usuario;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String mensagem;

    @Enumerated(EnumType.STRING)
    private TipoNotificacao tipo;

    private boolean lida = false;

    private LocalDateTime dataEnvio = LocalDateTime.now();

    private LocalDateTime dataLida;

    @Column(nullable = false)
    private String titulo;

    private String link;

    public enum TipoNotificacao {
        MULTIPLICACAO_CELULA,
        APROVACAO_SOLICITACAO,
        REJEICAO_SOLICITACAO,
        AVISO_GERAL,
        RELATORIO_PENDENTE, LEMBRETE_REUNIAO,
        SOLICITACAO_ALTERACAO,
        RELATORIO_MENSAL_COMPLETO
    }
}