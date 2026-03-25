package com.gestaoigrejaemcelula.demo.domain.entity;



import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "historico_celula")

public class HistoricoCelula {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "membro_id", nullable = false)
    private Membro membro;

    @ManyToOne
    @JoinColumn(name = "celula_id", nullable = false)
    private Celula celula;

    @Column(nullable = false)
    private LocalDate dataEntrada;

    private LocalDate dataSaida;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Membro getMembro() {
        return membro;
    }

    public void setMembro(Membro membro) {
        this.membro = membro;
    }

    public Celula getCelula() {
        return celula;
    }

    public void setCelula(Celula celula) {
        this.celula = celula;
    }

    public LocalDate getDataEntrada() {
        return dataEntrada;
    }

    public void setDataEntrada(LocalDate dataEntrada) {
        this.dataEntrada = dataEntrada;
    }

    public LocalDate getDataSaida() {
        return dataSaida;
    }

    public void setDataSaida(LocalDate dataSaida) {
        this.dataSaida = dataSaida;
    }
// getters e setters
}

