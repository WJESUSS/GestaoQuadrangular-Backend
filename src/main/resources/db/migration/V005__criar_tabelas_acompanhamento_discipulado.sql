-- ============================================================
-- V005: Módulo de Acompanhamento de Discipulado
-- Tabelas novas e independentes (não altera tabelas existentes)
-- ============================================================

CREATE TABLE IF NOT EXISTS acompanhamento_discipulado_individual (
    id                BIGSERIAL PRIMARY KEY,
    membro_id         BIGINT       NOT NULL,
    lider_id          BIGINT       NOT NULL,
    celula_id         BIGINT       NOT NULL,
    data              DATE         NOT NULL,
    horario           TIME         NOT NULL,
    tipo_estudo       VARCHAR(40)  NOT NULL,
    tipo_estudo_outro VARCHAR(120),
    tema              VARCHAR(255) NOT NULL,
    observacoes       TEXT,
    local             VARCHAR(255),
    status            VARCHAR(20)  NOT NULL DEFAULT 'CONCLUIDO',
    semana_inicio     DATE         NOT NULL,
    semana_fim        DATE         NOT NULL,
    criado_em         TIMESTAMP    NOT NULL,
    atualizado_em     TIMESTAMP,
    criado_por        VARCHAR(255),
    CONSTRAINT fk_acomp_ind_membro FOREIGN KEY (membro_id) REFERENCES membros (id),
    CONSTRAINT fk_acomp_ind_lider  FOREIGN KEY (lider_id)  REFERENCES usuarios (id),
    CONSTRAINT fk_acomp_ind_celula FOREIGN KEY (celula_id) REFERENCES celulas (id)
);

CREATE INDEX IF NOT EXISTS idx_acomp_ind_membro_semana ON acompanhamento_discipulado_individual (membro_id, semana_inicio);
CREATE INDEX IF NOT EXISTS idx_acomp_ind_celula_data   ON acompanhamento_discipulado_individual (celula_id, data);
CREATE INDEX IF NOT EXISTS idx_acomp_ind_lider_data    ON acompanhamento_discipulado_individual (lider_id, data);

CREATE TABLE IF NOT EXISTS acompanhamento_discipulado_coletivo (
    id                BIGSERIAL PRIMARY KEY,
    lider_id          BIGINT       NOT NULL,
    celula_id         BIGINT       NOT NULL,
    data              DATE         NOT NULL,
    horario           TIME         NOT NULL,
    tipo_estudo       VARCHAR(40)  NOT NULL,
    tipo_estudo_outro VARCHAR(120),
    tema              VARCHAR(255) NOT NULL,
    local             VARCHAR(255),
    observacoes       TEXT,
    status            VARCHAR(20)  NOT NULL DEFAULT 'CONCLUIDO',
    criado_em         TIMESTAMP    NOT NULL,
    atualizado_em     TIMESTAMP,
    criado_por        VARCHAR(255),
    CONSTRAINT fk_acomp_col_lider  FOREIGN KEY (lider_id)  REFERENCES usuarios (id),
    CONSTRAINT fk_acomp_col_celula FOREIGN KEY (celula_id) REFERENCES celulas (id)
);

CREATE INDEX IF NOT EXISTS idx_acomp_col_celula_data ON acompanhamento_discipulado_coletivo (celula_id, data);
CREATE INDEX IF NOT EXISTS idx_acomp_col_lider_data  ON acompanhamento_discipulado_coletivo (lider_id, data);

CREATE TABLE IF NOT EXISTS acompanhamento_discipulado_coletivo_participante (
    id                       BIGSERIAL PRIMARY KEY,
    discipulado_coletivo_id  BIGINT    NOT NULL,
    membro_id                BIGINT    NOT NULL,
    criado_em                TIMESTAMP NOT NULL,
    CONSTRAINT uk_acomp_colet_participante UNIQUE (discipulado_coletivo_id, membro_id),
    CONSTRAINT fk_acomp_part_discipulado FOREIGN KEY (discipulado_coletivo_id)
        REFERENCES acompanhamento_discipulado_coletivo (id),
    CONSTRAINT fk_acomp_part_membro FOREIGN KEY (membro_id) REFERENCES membros (id)
);
