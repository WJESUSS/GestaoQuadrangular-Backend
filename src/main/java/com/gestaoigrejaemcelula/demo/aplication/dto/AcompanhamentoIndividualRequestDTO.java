package com.gestaoigrejaemcelula.demo.aplication.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class AcompanhamentoIndividualRequestDTO extends AcompanhamentoRequestDTO {

    @NotNull(message = "Membro é obrigatório")
    private Long membroId;
}
