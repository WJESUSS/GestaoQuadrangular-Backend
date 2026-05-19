package com.gestaoigrejaemcelula.demo.domain.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "encontros_missao70")
public class EncontroMissao70 {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDate dataEncontro;

    /** Semana calculada automaticamente pelo service: 1, 2, 3 ou 4 */
    @Column(nullable = false)
    private int numeroSemana;

    private String observacoes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "missao70_id", nullable = false)
    @JsonIgnoreProperties({"encontros", "visitantes", "celula", "hibernateLazyInitializer"})
    private Missao70 missao70;

    @OneToMany(mappedBy = "encontro", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DecisaoMissao70> decisoes = new ArrayList<>();

    // --- Getters e Setters ---

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public LocalDate getDataEncontro() { return dataEncontro; }
    public void setDataEncontro(LocalDate dataEncontro) { this.dataEncontro = dataEncontro; }

    public int getNumeroSemana() { return numeroSemana; }
    public void setNumeroSemana(int numeroSemana) { this.numeroSemana = numeroSemana; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public Missao70 getMissao70() { return missao70; }
    public void setMissao70(Missao70 missao70) { this.missao70 = missao70; }

    public List<DecisaoMissao70> getDecisoes() { return decisoes; }
    public void setDecisoes(List<DecisaoMissao70> decisoes) { this.decisoes = decisoes; }
}