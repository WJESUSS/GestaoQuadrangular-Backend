package com.gestaoigrejaemcelula.demo.aplication.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
public class RelatorioRequestDTO {
    @NotNull private Long celulaId;
    @NotNull private LocalDate dataReuniao;
    private String estudo;
    private List<Long> membrosPresentesIds;
    private List<VisitantePresencaDTO> visitantesPresentes;
    private Integer quantidadeVisitantes = 0;
    private List<MembroAusenteDTO> membrosAusentes;
}