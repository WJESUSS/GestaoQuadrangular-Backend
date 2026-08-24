package com.gestaoigrejaemcelula.demo.aplication.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

/**
 * Enviado pela Secretaria para autorizar a membresia de um convertido
 * após o batismo nesta igreja.
 */
@Getter
@Setter
public class AutorizarMembresiaDTO {

    /** Data do batismo realizado nesta igreja */
    @NotNull(message = "Data do batismo é obrigatória")
    private LocalDate dataBatismo;
}
