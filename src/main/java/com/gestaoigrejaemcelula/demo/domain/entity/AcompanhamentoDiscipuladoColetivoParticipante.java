package com.gestaoigrejaemcelula.demo.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

import java.time.LocalDateTime;

@Entity
@Table(
        name = "acompanhamento_discipulado_coletivo_participante",
        uniqueConstraints = @UniqueConstraint(
                name = "uk_acomp_colet_participante",
                columnNames = {"discipulado_coletivo_id", "membro_id"}
        )
)
public class AcompanhamentoDiscipuladoColetivoParticipante {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "discipulado_coletivo_id", nullable = false)
    private AcompanhamentoDiscipuladoColetivo discipulado;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membro_id", nullable = false)
    private Membro membro;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public AcompanhamentoDiscipuladoColetivo getDiscipulado() { return discipulado; }
    public void setDiscipulado(AcompanhamentoDiscipuladoColetivo discipulado) { this.discipulado = discipulado; }

    public Membro getMembro() { return membro; }
    public void setMembro(Membro membro) { this.membro = membro; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }
}
