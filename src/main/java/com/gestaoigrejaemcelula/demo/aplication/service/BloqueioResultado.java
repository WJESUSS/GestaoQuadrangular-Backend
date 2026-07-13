
package com.gestaoigrejaemcelula.demo.aplication.service;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class BloqueioResultado {
    private final boolean localOk;
    private final boolean metaOk;
    private final String mensagem;

    public static BloqueioResultado sucesso() {
        return new BloqueioResultado(true, true, "Operação realizada com sucesso.");
    }

    public static BloqueioResultado apenasLocal() {
        return new BloqueioResultado(true, false,
                "Operação salva localmente, mas não foi possível sincronizar com a Meta. " +
                        "Isso ocorre quando o número não enviou mensagem nas últimas 24h. " +
                        "O efeito na Meta pode levar até 24h.");
    }
}