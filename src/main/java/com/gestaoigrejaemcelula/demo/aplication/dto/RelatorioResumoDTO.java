package com.gestaoigrejaemcelula.demo.aplication.dto;

import java.time.LocalDate;
import java.util.List;

public class RelatorioResumoDTO {

    private LocalDate inicio;
    private LocalDate fim;

    private Integer totalCelulas;
    private Integer totalMembros;
    private Integer totalVisitantes;

    private List<RelatorioResponseDTO> relatorios;

    public LocalDate getInicio() {
        return inicio;
    }

    public void setInicio(LocalDate inicio) {
        this.inicio = inicio;
    }

    public LocalDate getFim() {
        return fim;
    }

    public void setFim(LocalDate fim) {
        this.fim = fim;
    }

    public Integer getTotalCelulas() {
        return totalCelulas;
    }

    public void setTotalCelulas(Integer totalCelulas) {
        this.totalCelulas = totalCelulas;
    }

    public Integer getTotalMembros() {
        return totalMembros;
    }

    public void setTotalMembros(Integer totalMembros) {
        this.totalMembros = totalMembros;
    }

    public Integer getTotalVisitantes() {
        return totalVisitantes;
    }

    public void setTotalVisitantes(Integer totalVisitantes) {
        this.totalVisitantes = totalVisitantes;
    }

    public List<RelatorioResponseDTO> getRelatorios() {
        return relatorios;
    }

    public void setRelatorios(List<RelatorioResponseDTO> relatorios) {
        this.relatorios = relatorios;
    }
// getters e setters
}

