package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.JustificativaFalta;

public class MembroAusenteDTO {

    private Long membroId;
    private JustificativaFalta justificativa;

    public MembroAusenteDTO() {}

    public MembroAusenteDTO(Long membroId, JustificativaFalta justificativa) {
        this.membroId = membroId;
        this.justificativa = justificativa;
    }

    public Long getMembroId() { return membroId; }
    public void setMembroId(Long membroId) { this.membroId = membroId; }

    public JustificativaFalta getJustificativa() { return justificativa; }
    public void setJustificativa(JustificativaFalta justificativa) { this.justificativa = justificativa; }
}