package com.gestaoigrejaemcelula.demo.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;

import jakarta.persistence.*;

@Entity
@Table(name = "decisoes_encontro")
public class DecisaoEncontro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DecisaoEspiritual tipoDecisao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visitante_id", nullable = false)
    @JsonIgnoreProperties({"celula", "hibernateLazyInitializer"})
    private Visitante visitante;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "encontro_id", nullable = false)
    @JsonIgnoreProperties({"decisoes", "casaDePaz", "hibernateLazyInitializer"})
    private EncontroCasaDePaz encontro;

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public DecisaoEspiritual getTipoDecisao() { return tipoDecisao; }
    public void setTipoDecisao(DecisaoEspiritual tipoDecisao) { this.tipoDecisao = tipoDecisao; }
    public Visitante getVisitante() { return visitante; }
    public void setVisitante(Visitante visitante) { this.visitante = visitante; }
    public EncontroCasaDePaz getEncontro() { return encontro; }
    public void setEncontro(EncontroCasaDePaz encontro) { this.encontro = encontro; }
}