package com.gestaoigrejaemcelula.demo.domain.enums;

import com.fasterxml.jackson.annotation.JsonCreator;

import java.text.Normalizer;
import java.util.Locale;

public enum TipoArrolamento {
    PROFISSAO_DE_FE,
    TRANSFERENCIA,
    ACLAMACAO;

    @JsonCreator
    public static TipoArrolamento fromString(String valor) {
        if (valor == null || valor.isBlank()) {
            return null;
        }
        String normalizado = Normalizer.normalize(valor.trim().toUpperCase(Locale.ROOT), Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replace(' ', '_')
                .replace('-', '_');
        for (TipoArrolamento tipo : values()) {
            if (tipo.name().equals(normalizado)) {
                return tipo;
            }
        }
        throw new IllegalArgumentException(
                "Valor inválido para TipoArrolamento: " + valor + ". Aceitos: PROFISSAO_DE_FE, TRANSFERENCIA, ACLAMACAO");
    }
}
