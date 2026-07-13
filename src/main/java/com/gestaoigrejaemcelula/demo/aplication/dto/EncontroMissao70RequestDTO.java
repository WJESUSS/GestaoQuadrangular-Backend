package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.DecisaoEspiritual;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * O número da semana NÃO é informado pelo frontend.
 * O sistema avança automaticamente: Semana 1 → 2 → 3 → 4.
 */
public class EncontroMissao70RequestDTO {

    private LocalDate dataEncontro;
    private LocalTime horaEncontro;
    private String observacoes;
    private List<DecisaoDTO> decisoes;

    /** NOVO: ids dos visitantes marcados como presentes neste culto específico */
    private List<Long> visitantesPresentesIds;

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

    public LocalTime getHoraEncontro() { return horaEncontro; }
    public void setHoraEncontro(LocalTime horaEncontro) { this.horaEncontro = horaEncontro; }

    public String getObservacoes() { return observacoes; }
    public void setObservacoes(String observacoes) { this.observacoes = observacoes; }

    public List<DecisaoDTO> getDecisoes() { return decisoes; }
    public void setDecisoes(List<DecisaoDTO> decisoes) { this.decisoes = decisoes; }

    public List<Long> getVisitantesPresentesIds() { return visitantesPresentesIds; }
    public void setVisitantesPresentesIds(List<Long> visitantesPresentesIds) { this.visitantesPresentesIds = visitantesPresentesIds; }
}