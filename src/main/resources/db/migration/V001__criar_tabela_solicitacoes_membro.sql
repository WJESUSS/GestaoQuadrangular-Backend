CREATE TABLE solicitacoes_membro (
    id                  BIGSERIAL       PRIMARY KEY,
    status              VARCHAR(50)     NOT NULL DEFAULT 'PENDENTE',
    data_solicitacao    TIMESTAMP       NOT NULL DEFAULT NOW(),
    data_decisao        TIMESTAMP,
    lider_id            BIGINT          REFERENCES usuarios(id),
    celula_id           BIGINT          REFERENCES celulas(id),
    secretario_id       BIGINT          REFERENCES usuarios(id),
    motivo_rejeicao     TEXT,
    membro_criado_id    BIGINT,

    nome                VARCHAR(255)    NOT NULL,
    telefone            VARCHAR(255),
    email               VARCHAR(255),
    cpf                 VARCHAR(14),
    rg                  VARCHAR(20),
    estado_civil        VARCHAR(50),
    data_nascimento     DATE,
    data_conversao      DATE,
    data_batismo        DATE,
    nome_mae            VARCHAR(255),
    nome_pai            VARCHAR(255),
    nome_conjuge        VARCHAR(255),
    naturalidade        VARCHAR(255),
    grau_escolaridade   VARCHAR(255),
    curso               VARCHAR(255),
    profissao           VARCHAR(255),
    endereco            VARCHAR(255),
    numero              VARCHAR(255),
    bairro              VARCHAR(255),
    cidade              VARCHAR(255),
    cep                 VARCHAR(9),
    uf                  VARCHAR(2),
    pertence_outra_religiao  BOOLEAN,
    qual_religiao            VARCHAR(255),
    batizado_nas_aguas       BOOLEAN,
    data_batizado_nas_aguas  DATE,
    igreja_batizado_nas_aguas VARCHAR(255),
    batizado_espirito_santo  BOOLEAN,
    tipo_arrolamento         VARCHAR(50),
    jurisdicao_arrolamento   VARCHAR(255),
    arrolado_por             VARCHAR(255),
    observacoes              TEXT
);

CREATE INDEX idx_solicitacao_status ON solicitacoes_membro(status);
CREATE INDEX idx_solicitacao_celula ON solicitacoes_membro(celula_id);

ALTER TABLE registro_webhook ADD COLUMN texto_mensagem TEXT;
ALTER TABLE registro_webhook ADD COLUMN tipo_mensagem VARCHAR(50);

ALTER TABLE missao70 ADD COLUMN motivo_cancelamento VARCHAR(50);
ALTER TABLE missao70 ADD COLUMN observacao_cancelamento VARCHAR(500);




