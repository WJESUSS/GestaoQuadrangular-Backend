package com.gestaoigrejaemcelula.demo.aplication.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LancamentoTesourariaDTO {
    @NotBlank private String membroNome;
    @NotNull @Positive private BigDecimal valorDizimo;
    private BigDecimal valorOferta;
    private String tipoOferta;
    @NotNull private LocalDate dataLancamento;

    public BigDecimal getValorDizimo() { return valorDizimo; }
    public void setValorDizimo(BigDecimal valorDizimo) { this.valorDizimo = valorDizimo; }
    public BigDecimal getValorOferta() { return valorOferta; }
    public void setValorOferta(BigDecimal valorOferta) { this.valorOferta = valorOferta; }
    public String getTipoOferta() { return tipoOferta; }
    public void setTipoOferta(String tipoOferta) { this.tipoOferta = tipoOferta; }
    public LocalDate getDataLancamento() { return dataLancamento; }
    public void setDataLancamento(LocalDate dataLancamento) { this.dataLancamento = dataLancamento; }
    public String getMembroNome() { return membroNome; }
    public void setMembroNome(String membroNome) { this.membroNome = membroNome; }
}
