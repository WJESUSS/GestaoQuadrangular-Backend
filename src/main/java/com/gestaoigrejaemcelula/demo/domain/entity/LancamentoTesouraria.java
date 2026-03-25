package com.gestaoigrejaemcelula.demo.domain.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "lancamento_tesouraria")
public class LancamentoTesouraria {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String membroNome;

    private BigDecimal valorDizimo;

    private BigDecimal valorOferta;

    private String tipoOferta; // BRONZE, PRATA, OURO

    private LocalDate dataLancamento;

    // Getters e Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getMembroNome() {
        return membroNome;
    }

    public void setMembroNome(String membroNome) {
        this.membroNome = membroNome;
    }

    public BigDecimal getValorDizimo() { return valorDizimo; }
    public void setValorDizimo(BigDecimal valorDizimo) { this.valorDizimo = valorDizimo; }

    public BigDecimal getValorOferta() { return valorOferta; }
    public void setValorOferta(BigDecimal valorOferta) { this.valorOferta = valorOferta; }

    public String getTipoOferta() { return tipoOferta; }
    public void setTipoOferta(String tipoOferta) { this.tipoOferta = tipoOferta; }

    public LocalDate getDataLancamento() { return dataLancamento; }
    public void setDataLancamento(LocalDate dataLancamento) { this.dataLancamento = dataLancamento; }
}
