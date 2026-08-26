package com.gestaoigrejaemcelula.demo.domain.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "ranking_finalizado", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"mes_ano", "celula_id"})
})
public class RankingFinalizado {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "mes_ano", nullable = false, length = 7)
    private String mesAno;

    @Column(name = "celula_id", nullable = false)
    private Long celulaId;

    @Column(name = "nome_celula", nullable = false)
    private String nomeCelula;

    private String lider;

    @Column(name = "presenca_media")
    private Integer presencaMedia;

    private Integer visitantes;
    private Integer consolidados;
    private Integer batismos;

    private Boolean multiplicou;

    @Column(name = "aceitou_jesus")
    private Integer aceitouJesus;

    @Column(name = "deseja_batismo")
    private Integer desejaBatismo;

    private Integer reconciliou;

    @Column(name = "pontos_discipulado")
    private Integer pontosDiscipulado;

    @Column(name = "quantidade_discipulados")
    private Integer quantidadeDiscipulados;

    @Column(name = "pontos_cultos")
    private Integer pontosCultos;

    private Integer pontuacao;
    private Integer posicao;

    @Column(name = "data_finalizacao", nullable = false)
    private LocalDateTime dataFinalizacao;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMesAno() { return mesAno; }
    public void setMesAno(String mesAno) { this.mesAno = mesAno; }

    public Long getCelulaId() { return celulaId; }
    public void setCelulaId(Long celulaId) { this.celulaId = celulaId; }

    public String getNomeCelula() { return nomeCelula; }
    public void setNomeCelula(String nomeCelula) { this.nomeCelula = nomeCelula; }

    public String getLider() { return lider; }
    public void setLider(String lider) { this.lider = lider; }

    public Integer getPresencaMedia() { return presencaMedia; }
    public void setPresencaMedia(Integer presencaMedia) { this.presencaMedia = presencaMedia; }

    public Integer getVisitantes() { return visitantes; }
    public void setVisitantes(Integer visitantes) { this.visitantes = visitantes; }

    public Integer getConsolidados() { return consolidados; }
    public void setConsolidados(Integer consolidados) { this.consolidados = consolidados; }

    public Integer getBatismos() { return batismos; }
    public void setBatismos(Integer batismos) { this.batismos = batismos; }

    public Boolean getMultiplicou() { return multiplicou; }
    public void setMultiplicou(Boolean multiplicou) { this.multiplicou = multiplicou; }

    public Integer getAceitouJesus() { return aceitouJesus; }
    public void setAceitouJesus(Integer aceitouJesus) { this.aceitouJesus = aceitouJesus; }

    public Integer getDesejaBatismo() { return desejaBatismo; }
    public void setDesejaBatismo(Integer desejaBatismo) { this.desejaBatismo = desejaBatismo; }

    public Integer getReconciliou() { return reconciliou; }
    public void setReconciliou(Integer reconciliou) { this.reconciliou = reconciliou; }

    public Integer getPontosDiscipulado() { return pontosDiscipulado; }
    public void setPontosDiscipulado(Integer pontosDiscipulado) { this.pontosDiscipulado = pontosDiscipulado; }

    public Integer getQuantidadeDiscipulados() { return quantidadeDiscipulados; }
    public void setQuantidadeDiscipulados(Integer quantidadeDiscipulados) { this.quantidadeDiscipulados = quantidadeDiscipulados; }

    public Integer getPontosCultos() { return pontosCultos; }
    public void setPontosCultos(Integer pontosCultos) { this.pontosCultos = pontosCultos; }

    public Integer getPontuacao() { return pontuacao; }
    public void setPontuacao(Integer pontuacao) { this.pontuacao = pontuacao; }

    public Integer getPosicao() { return posicao; }
    public void setPosicao(Integer posicao) { this.posicao = posicao; }

    public LocalDateTime getDataFinalizacao() { return dataFinalizacao; }
    public void setDataFinalizacao(LocalDateTime dataFinalizacao) { this.dataFinalizacao = dataFinalizacao; }
}
