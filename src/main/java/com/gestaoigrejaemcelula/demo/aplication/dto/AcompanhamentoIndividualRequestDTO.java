package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.TipoEstudoDiscipulado;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class AcompanhamentoIndividualRequestDTO {

    @NotNull(message = "Membro é obrigatório")
    private Long membroId;

    @NotNull(message = "Data do discipulado é obrigatória")
    @PastOrPresent(message = "Data do discipulado não pode ser futura")
    private LocalDate data;

    @NotNull(message = "Horário do discipulado é obrigatório")
    private LocalTime horario;

    @NotNull(message = "Tipo de estudo é obrigatório")
    private TipoEstudoDiscipulado tipoEstudo;

    @Size(max = 120, message = "Descrição do tipo de estudo deve ter no máximo 120 caracteres")
    private String tipoEstudoOutro;

    @NotBlank(message = "Tema estudado é obrigatório")
    @Size(max = 255, message = "Tema deve ter no máximo 255 caracteres")
    private String tema;

    @Size(max = 2000, message = "Observações devem ter no máximo 2000 caracteres")
    private String observacoes;

    @Size(max = 255, message = "Local deve ter no máximo 255 caracteres")
    private String local;
}
