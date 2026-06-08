package com.gestaoigrejaemcelula.demo.domain.entity;

import com.gestaoigrejaemcelula.demo.domain.entity.Celula;
import com.gestaoigrejaemcelula.demo.domain.entity.Membro;
import com.gestaoigrejaemcelula.demo.domain.entity.Visitante;
import com.gestaoigrejaemcelula.demo.domain.enums.MotivoNaoRealizacaoCelula;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Getter
@Setter
@Table(indexes = {
    @Index(name = "idx_relatorio_celula_id", columnList = "celula_id"),
    @Index(name = "idx_relatorio_data_reuniao", columnList = "dataReuniao")
})
public class Relatorio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    private Celula celula;

    private LocalDate dataReuniao;

    @Column(columnDefinition = "TEXT")
    private String estudo;

    private Integer quantidadeVisitantes = 0;
// Na entidade Relatorio existente, adicione:

    @Column(nullable = true)
    private Boolean realizada = true; // true = célula ocorreu, false = não ocorreu

    public MotivoNaoRealizacaoCelula getMotivoNaoRealizacao() {
        return motivoNaoRealizacao;
    }

    public void setMotivoNaoRealizacao(MotivoNaoRealizacaoCelula motivoNaoRealizacao) {
        this.motivoNaoRealizacao = motivoNaoRealizacao;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Celula getCelula() {
        return celula;
    }

    public void setCelula(Celula celula) {
        this.celula = celula;
    }

    public LocalDate getDataReuniao() {
        return dataReuniao;
    }

    public void setDataReuniao(LocalDate dataReuniao) {
        this.dataReuniao = dataReuniao;
    }

    public String getEstudo() {
        return estudo;
    }

    public void setEstudo(String estudo) {
        this.estudo = estudo;
    }

    public Integer getQuantidadeVisitantes() {
        return quantidadeVisitantes;
    }

    public void setQuantidadeVisitantes(Integer quantidadeVisitantes) {
        this.quantidadeVisitantes = quantidadeVisitantes;
    }

    public Boolean getRealizada() {
        return realizada;
    }

    public void setRealizada(Boolean realizada) {
        this.realizada = realizada;
    }

    public List<Membro> getPresentes() {
        return presentes;
    }

    public void setPresentes(List<Membro> presentes) {
        this.presentes = presentes;
    }

    public List<Visitante> getVisitantesPresentes() {
        return visitantesPresentes;
    }

    public void setVisitantesPresentes(List<Visitante> visitantesPresentes) {
        this.visitantesPresentes = visitantesPresentes;
    }

    public LocalDateTime getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(LocalDateTime dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    @Enumerated(EnumType.STRING)
    @Column(nullable = true)
    private MotivoNaoRealizacaoCelula motivoNaoRealizacao;
    @ManyToMany
    @JoinTable(
            name = "relatorio_membros_presenca",
            joinColumns = @JoinColumn(name = "relatorio_id"),
            inverseJoinColumns = @JoinColumn(name = "membro_id")
    )
    private List<Membro> presentes = new java.util.ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "relatorio_visitantes_presenca",
            joinColumns = @JoinColumn(name = "relatorio_id"),
            inverseJoinColumns = @JoinColumn(name = "visitante_id")
    )
    private List<Visitante> visitantesPresentes = new java.util.ArrayList<>();

    private LocalDateTime dataCadastro = LocalDateTime.now();

    public int getQuantidadeMembros() {
        return presentes.size();
    }

    public int getQuantidadeVisitantesCadastrados() {
        return visitantesPresentes.size();
    }

    public int getTotalVisitantes() {
        return getQuantidadeVisitantesCadastrados() + quantidadeVisitantes;
    }

    public int getTotalPresentes() {
        return getQuantidadeMembros() + getTotalVisitantes();
    }


}
