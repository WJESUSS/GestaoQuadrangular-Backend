package com.gestaoigrejaemcelula.demo.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "encontros_casa_de_paz")
public class EncontroCasaDePaz {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataEncontro;

    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "casa_de_paz_id", nullable = false)
    @JsonIgnoreProperties({"encontros", "visitantes", "celula", "hibernateLazyInitializer"})
    private CasaDePaz casaDePaz;

    @OneToMany(mappedBy = "encontro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DecisaoEncontro> decisoes = new ArrayList<>();

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public LocalDate getDataEncontro() { return dataEncontro; }
    public void setDataEncontro(LocalDate dataEncontro) { this.dataEncontro = dataEncontro; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public CasaDePaz getCasaDePaz() { return casaDePaz; }
    public void setCasaDePaz(CasaDePaz casaDePaz) { this.casaDePaz = casaDePaz; }
    public List<DecisaoEncontro> getDecisoes() { return decisoes; }
    public void setDecisoes(List<DecisaoEncontro> decisoes) { this.decisoes = decisoes; }
}