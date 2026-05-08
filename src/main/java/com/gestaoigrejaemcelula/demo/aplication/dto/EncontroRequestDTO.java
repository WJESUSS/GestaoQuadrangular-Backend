package com.gestaoigrejaemcelula.demo.aplication.dto;


import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;

import java.time.LocalDate;
import java.util.List;

public class EncontroRequestDTO {
    private LocalDate dataEncontro;
    private String observacoes;
    private List<DecisaoDTO> decisoes;

    public static class DecisaoDTO {
        private Long visitanteId;
        private DecisaoEspiritual tipoDecisao;
        public Long getVisitanteId() { return visitanteId; }
        public void setVisitanteId(Long visitanteId) { this.visitanteId = visitanteId; }
        public DecisaoEspiritual getTipoDecisao() { return tipoDecisao; }
        public void setTipoDecisao(DecisaoEspiritual tipoDecisao) { this.tipoDecisao = tipoDecisao; }
    }

    public LocalDate getDataEncontro() { return dataEncontro; }
    public void setDataEncontro(LocalDate dataEncontro) { this.dataEncontro = dataEncontro; }
    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }
    public List<DecisaoDTO> getDecisoes() { return decisoes; }
    public void setDecisoes(List<DecisaoDTO> decisoes) { this.decisoes = decisoes; }
}