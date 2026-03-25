package com.gestaoigrejaemcelula.demo.aplication.dto;


import java.math.BigDecimal;
import java.time.LocalDate;

public class LancamentoTesourariaDTO {
    private String membroNome;
    private BigDecimal valorDizimo;
    private BigDecimal valorOferta;
    private String tipoOferta; // BRONZE, PRATA, OURO
    private LocalDate dataLancamento;

    public BigDecimal getValorDizimo() {
        return valorDizimo;
    }

    public void setValorDizimo(BigDecimal valorDizimo) {
        this.valorDizimo = valorDizimo;
    }

    public BigDecimal getValorOferta() {
        return valorOferta;
    }

    public void setValorOferta(BigDecimal valorOferta) {
        this.valorOferta = valorOferta;
    }

    public String getTipoOferta() {
        return tipoOferta;
    }

    public void setTipoOferta(String tipoOferta) {
        this.tipoOferta = tipoOferta;
    }

    public LocalDate getDataLancamento() {
        return dataLancamento;
    }

    public void setDataLancamento(LocalDate dataLancamento) {
        this.dataLancamento = dataLancamento;
    }

    // getters e setters
    public String getMembroNome() { return membroNome; }
    public void setMembroNome(String membroNome) { this.membroNome = membroNome; }
}


