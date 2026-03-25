package com.gestaoigrejaemcelula.demo.aplication.dto;

public class PastorMetricasDTO {

    private Long totalMembros;
    private Long novosMembrosMes;
    private Long naoAcompanhados;
    private Long celulasAtivas; // <--- ADICIONE ESTE CAMPO

    public PastorMetricasDTO() {}

    public PastorMetricasDTO(Long totalMembros, Long novosMembrosMes, Long naoAcompanhados, Long celulasAtivas) {
        this.totalMembros = totalMembros;
        this.novosMembrosMes = novosMembrosMes;
        this.naoAcompanhados = naoAcompanhados;
        this.celulasAtivas = celulasAtivas; // <--- ADICIONE AQUI
    }

    // Getters e Setters
    public Long getTotalMembros() { return totalMembros; }
    public void setTotalMembros(Long totalMembros) { this.totalMembros = totalMembros; }

    public Long getNovosMembrosMes() { return novosMembrosMes; }
    public void setNovosMembrosMes(Long novosMembrosMes) { this.novosMembrosMes = novosMembrosMes; }

    public Long getNaoAcompanhados() { return naoAcompanhados; }
    public void setNaoAcompanhados(Long naoAcompanhados) { this.naoAcompanhados = naoAcompanhados; }

    public Long getCelulasAtivas() { return celulasAtivas; } // <--- ADICIONE ESTE GETTER
    public void setCelulasAtivas(Long celulasAtivas) { this.celulasAtivas = celulasAtivas; }
}