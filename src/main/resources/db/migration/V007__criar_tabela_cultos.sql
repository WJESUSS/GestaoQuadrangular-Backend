CREATE TABLE cultos (
    id                      UUID            PRIMARY KEY,
    data                    DATE            NOT NULL,
    tipo_culto              VARCHAR(50)     NOT NULL,
    texto_pregado           TEXT            NOT NULL,
    pregador                VARCHAR(255)    NOT NULL,
    quantidade_membros      INTEGER         NOT NULL DEFAULT 0,
    visitantes_simpatizantes INTEGER        NOT NULL DEFAULT 0,
    total_criancas          INTEGER         NOT NULL DEFAULT 0,
    quantidade_diaconos     INTEGER         NOT NULL DEFAULT 0,
    total_geral             INTEGER         NOT NULL DEFAULT 0,
    campanha_ativa          BOOLEAN         NOT NULL DEFAULT FALSE,
    nome_campanha           VARCHAR(255),
    observacoes             TEXT,
    registrado_por_id       BIGINT          NOT NULL REFERENCES usuarios(id),
    criado_em               TIMESTAMP       NOT NULL DEFAULT NOW(),
    atualizado_em           TIMESTAMP
);

CREATE INDEX idx_cultos_data ON cultos(data);
CREATE INDEX idx_cultos_tipo ON cultos(tipo_culto);
CREATE INDEX idx_cultos_registrado_por ON cultos(registrado_por_id);
CREATE INDEX idx_cultos_data_tipo ON cultos(data, tipo_culto);
CREATE UNIQUE INDEX idx_cultos_data_tipo_unique ON cultos(data, tipo_culto);
