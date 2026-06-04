package com.gestaoigrejaemcelula.demo.aplication.dto;

import jakarta.validation.constraints.NotNull;

public class TransferirMembroDTO {
    @NotNull private Long membroId;
    @NotNull private Long novaCelulaId;

    public Long getMembroId() { return membroId; }
    public void setMembroId(Long membroId) { this.membroId = membroId; }
    public Long getNovaCelulaId() { return novaCelulaId; }
    public void setNovaCelulaId(Long novaCelulaId) { this.novaCelulaId = novaCelulaId; }
}
