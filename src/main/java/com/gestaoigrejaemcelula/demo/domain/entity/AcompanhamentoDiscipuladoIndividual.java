package com.gestaoigrejaemcelula.demo.domain.entity;

import com.gestaoigrejaemcelula.demo.domain.enums.StatusAcompanhamentoDiscipulado;
import com.gestaoigrejaemcelula.demo.domain.enums.TipoEstudoDiscipulado;
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
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "acompanhamento_discipulado_individual", indexes = {
        @Index(name = "idx_acomp_ind_membro_semana", columnList = "membro_id, semana_inicio"),
        @Index(name = "idx_acomp_ind_celula_data", columnList = "celula_id, data"),
        @Index(name = "idx_acomp_ind_lider_data", columnList = "lider_id, data")
})
public class AcompanhamentoDiscipuladoIndividual {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "membro_id", nullable = false)
    private Membro membro;

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

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @Column(length = 255)
    private String local;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private StatusAcompanhamentoDiscipulado status = StatusAcompanhamentoDiscipulado.CONCLUIDO;

    @Column(name = "semana_inicio", nullable = false)
    private LocalDate semanaInicio;

    @Column(name = "semana_fim", nullable = false)
    private LocalDate semanaFim;

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

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Membro getMembro() { return membro; }
    public void setMembro(Membro membro) { this.membro = membro; }

    public Usuario getLider() { return lider; }
    public void setLider(Usuario lider) { this.lider = lider; }

    public Celula getCelula() { return celula; }
    public void setCelula(Celula celula) { this.celula = celula; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public LocalTime getHorario() { return horario; }
    public void setHorario(LocalTime horario) { this.horario = horario; }

    public TipoEstudoDiscipulado getTipoEstudo() { return tipoEstudo; }
    public void setTipoEstudo(TipoEstudoDiscipulado tipoEstudo) { this.tipoEstudo = tipoEstudo; }

    public String getTipoEstudoOutro() { return tipoEstudoOutro; }
    public void setTipoEstudoOutro(String tipoEstudoOutro) { this.tipoEstudoOutro = tipoEstudoOutro; }

    public String getTema() { return tema; }
    public void setTema(String tema) { this.tema = tema; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public String getLocal() { return local; }
    public void setLocal(String local) { this.local = local; }

    public StatusAcompanhamentoDiscipulado getStatus() { return status; }
    public void setStatus(StatusAcompanhamentoDiscipulado status) { this.status = status; }

    public LocalDate getSemanaInicio() { return semanaInicio; }
    public void setSemanaInicio(LocalDate semanaInicio) { this.semanaInicio = semanaInicio; }

    public LocalDate getSemanaFim() { return semanaFim; }
    public void setSemanaFim(LocalDate semanaFim) { this.semanaFim = semanaFim; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public void setCriadoEm(LocalDateTime criadoEm) { this.criadoEm = criadoEm; }

    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
    public void setAtualizadoEm(LocalDateTime atualizadoEm) { this.atualizadoEm = atualizadoEm; }

    public String getCriadoPor() { return criadoPor; }
    public void setCriadoPor(String criadoPor) { this.criadoPor = criadoPor; }
}
