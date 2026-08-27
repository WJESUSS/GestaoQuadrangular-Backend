package com.gestaoigrejaemcelula.demo.domain.entity;

import com.gestaoigrejaemcelula.demo.domain.enums.TipoCulto;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "cultos", indexes = {
        @Index(name = "idx_cultos_data", columnList = "data"),
        @Index(name = "idx_cultos_tipo", columnList = "tipo_culto"),
        @Index(name = "idx_cultos_registrado_por", columnList = "registrado_por_id"),
        @Index(name = "idx_cultos_data_tipo", columnList = "data, tipo_culto, horario")
})
public class Culto {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private LocalDate data;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_culto", nullable = false)
    private TipoCulto tipoCulto;

    @Column(nullable = false, length = 5)
    private String horario;

    @Column(name = "texto_pregado", nullable = false)
    private String textoPregado;

    @Column(nullable = false)
    private String pregador;

    @Column(name = "quantidade_membros", nullable = false)
    private Integer quantidadeMembros = 0;

    @Column(name = "visitantes_simpatizantes", nullable = false)
    private Integer visitantesSimpatizantes = 0;

    @Column(name = "total_criancas", nullable = false)
    private Integer totalCriancas = 0;

    @Column(name = "quantidade_diaconos", nullable = false)
    private Integer quantidadeDiaconos = 0;

    @Column(name = "total_geral", nullable = false)
    private Integer totalGeral = 0;

    @Column(name = "campanha_ativa", nullable = false)
    private Boolean campanhaAtiva = false;

    @Column(name = "nome_campanha")
    private String nomeCampanha;

    @Column(columnDefinition = "TEXT")
    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "registrado_por_id", nullable = false)
    private Usuario registradoPor;

    @Column(name = "criado_em", nullable = false, updatable = false)
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    private LocalDateTime atualizadoEm;

    @PrePersist
    public void prePersist() {
        this.criadoEm = LocalDateTime.now();
        this.atualizadoEm = LocalDateTime.now();
        calcularTotalGeral();
    }

    @PreUpdate
    public void preUpdate() {
        this.atualizadoEm = LocalDateTime.now();
        calcularTotalGeral();
    }

    public void calcularTotalGeral() {
        this.totalGeral = this.quantidadeMembros
                + this.visitantesSimpatizantes
                + this.totalCriancas
                + this.quantidadeDiaconos;
    }

    // GETTERS E SETTERS

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public LocalDate getData() { return data; }
    public void setData(LocalDate data) { this.data = data; }

    public TipoCulto getTipoCulto() { return tipoCulto; }
    public void setTipoCulto(TipoCulto tipoCulto) { this.tipoCulto = tipoCulto; }

    public String getHorario() { return horario; }
    public void setHorario(String horario) { this.horario = horario; }

    public String getTextoPregado() { return textoPregado; }
    public void setTextoPregado(String textoPregado) { this.textoPregado = textoPregado; }

    public String getPregador() { return pregador; }
    public void setPregador(String pregador) { this.pregador = pregador; }

    public Integer getQuantidadeMembros() { return quantidadeMembros; }
    public void setQuantidadeMembros(Integer quantidadeMembros) { this.quantidadeMembros = quantidadeMembros; }

    public Integer getVisitantesSimpatizantes() { return visitantesSimpatizantes; }
    public void setVisitantesSimpatizantes(Integer visitantesSimpatizantes) { this.visitantesSimpatizantes = visitantesSimpatizantes; }

    public Integer getTotalCriancas() { return totalCriancas; }
    public void setTotalCriancas(Integer totalCriancas) { this.totalCriancas = totalCriancas; }

    public Integer getQuantidadeDiaconos() { return quantidadeDiaconos; }
    public void setQuantidadeDiaconos(Integer quantidadeDiaconos) { this.quantidadeDiaconos = quantidadeDiaconos; }

    public Integer getTotalGeral() { return totalGeral; }
    public void setTotalGeral(Integer totalGeral) { this.totalGeral = totalGeral; }

    public Boolean getCampanhaAtiva() { return campanhaAtiva; }
    public void setCampanhaAtiva(Boolean campanhaAtiva) { this.campanhaAtiva = campanhaAtiva; }

    public String getNomeCampanha() { return nomeCampanha; }
    public void setNomeCampanha(String nomeCampanha) { this.nomeCampanha = nomeCampanha; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Usuario getRegistradoPor() { return registradoPor; }
    public void setRegistradoPor(Usuario registradoPor) { this.registradoPor = registradoPor; }

    public LocalDateTime getCriadoEm() { return criadoEm; }
    public LocalDateTime getAtualizadoEm() { return atualizadoEm; }
}
