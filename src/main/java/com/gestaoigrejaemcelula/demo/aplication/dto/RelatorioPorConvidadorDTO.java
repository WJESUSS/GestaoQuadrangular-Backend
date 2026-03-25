package com.gestaoigrejaemcelula.demo.aplication.dto;

public class RelatorioPorConvidadorDTO {

    private String nomeConvidador;
    private Long total;

    public RelatorioPorConvidadorDTO(String s, Long aLong) {
    }

    public String getNomeConvidador() {
        return nomeConvidador;
    }

    public void setNomeConvidador(String nomeConvidador) {
        this.nomeConvidador = nomeConvidador;
    }

    public Long getTotal() {
        return total;
    }

    public void setTotal(Long total) {
        this.total = total;
    }
// construtor, getters
}
