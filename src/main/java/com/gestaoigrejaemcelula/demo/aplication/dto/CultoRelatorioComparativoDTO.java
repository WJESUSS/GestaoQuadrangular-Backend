package com.gestaoigrejaemcelula.demo.aplication.dto;

import com.gestaoigrejaemcelula.demo.domain.enums.TipoCulto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CultoRelatorioComparativoDTO {
    private LocalDate data;
    private TipoCulto tipoCulto;
    private String tipoCultoDescricao;
    private Integer totalGeral;
    private Integer quantidadeMembros;
    private Integer visitantesSimpatizantes;
    private Integer totalCriancas;
    private Integer quantidadeDiaconos;

    public CultoRelatorioComparativoDTO(LocalDate data, TipoCulto tipoCulto,
                                         Integer totalGeral, Integer quantidadeMembros,
                                         Integer visitantesSimpatizantes, Integer totalCriancas,
                                         Integer quantidadeDiaconos) {
        this.data = data;
        this.tipoCulto = tipoCulto;
        this.tipoCultoDescricao = tipoCulto != null ? tipoCulto.getDescricao() : null;
        this.totalGeral = totalGeral;
        this.quantidadeMembros = quantidadeMembros;
        this.visitantesSimpatizantes = visitantesSimpatizantes;
        this.totalCriancas = totalCriancas;
        this.quantidadeDiaconos = quantidadeDiaconos;
    }
}
