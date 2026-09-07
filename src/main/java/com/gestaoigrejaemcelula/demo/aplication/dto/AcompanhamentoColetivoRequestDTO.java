package com.gestaoigrejaemcelula.demo.aplication.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

@Data
@EqualsAndHashCode(callSuper = true)
public class AcompanhamentoColetivoRequestDTO extends AcompanhamentoRequestDTO {

    @NotNull(message = "Informe os membros presentes")
    private List<Long> participantesIds;
}
