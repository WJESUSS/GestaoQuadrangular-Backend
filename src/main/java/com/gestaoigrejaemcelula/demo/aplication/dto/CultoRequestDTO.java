package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.TipoCulto;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
public class CultoRequestDTO {

    @NotNull(message = "Data é obrigatória")
    private LocalDate data;

    private TipoCulto tipoCulto;

    @NotBlank(message = "Horário é obrigatório")
    private String horario;

    @NotBlank(message = "Texto pregado é obrigatório")
    private String textoPregado;

    @NotBlank(message = "Pregador é obrigatório")
    private String pregador;

    @Min(value = 0, message = "Quantidade de membros deve ser ≥ 0")
    private Integer quantidadeMembros = 0;

    @Min(value = 0, message = "Visitantes/simpatizantes deve ser ≥ 0")
    private Integer visitantesSimpatizantes = 0;

    @Min(value = 0, message = "Total de crianças deve ser ≥ 0")
    private Integer totalCriancas = 0;

    @Min(value = 0, message = "Quantidade de diáconos deve ser ≥ 0")
    private Integer quantidadeDiaconos = 0;

    private Boolean campanhaAtiva = false;

    private String nomeCampanha;

    private String observacoes;
}
