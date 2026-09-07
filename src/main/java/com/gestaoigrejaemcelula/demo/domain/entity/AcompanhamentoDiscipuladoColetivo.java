package com.gestaoigrejaemcelula.demo.domain.entity;

import com.gestaoigrejaemcelula.demo.domain.enums.StatusAcompanhamentoDiscipulado;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoEstudoDiscipulado;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
@Getter
@Setter
@Entity
@Table(name = "acompanhamento_discipulado_coletivo", indexes = {
        @Index(name = "idx_acomp_col_celula_data", columnList = "celula_id, data"),
        @Index(name = "idx_acomp_col_lider_data", columnList = "lider_id, data")
})
public class AcompanhamentoDiscipuladoColetivo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "lider_id", nullable = false)
    private Usuario lider;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "celula_id", nullable = false)
    private Celula celula;

    @Column(nullable = false)
    private LocalDate data;

    @Column(nullable = false)
    private LocalTime horario;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_estudo", nullable = false, length = 40)
    private TipoEstudoDiscipulado tipoEstudo;

    @Column(name = "tipo_estudo_outro", length = 120)
    private String tipoEstudoOutro;

    @Column(nullable = false, length = 255)
    private String tema;

    @Column(length = 255)
    private String local;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAcompanhamentoDiscipulado status = StatusAcompanhamentoDiscipulado.CONCLUIDO;

    @OneToMany(mappedBy = "discipulado", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AcompanhamentoDiscipuladoColetivoParticipante> participantes = new ArrayList<>();

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @Column(name = "criado_por", updatable = false, length = 255)
    private String criadoPor;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
    }

}
