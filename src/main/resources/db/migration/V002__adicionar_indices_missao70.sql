CREATE INDEX IF NOT EXISTS idx_missao70_celula_id ON missao70(celula_id);
CREATE INDEX IF NOT EXISTS idx_missao70_status ON missao70(status);
CREATE INDEX IF NOT EXISTS idx_missao70_data_inicio ON missao70(dataInicio);
CREATE INDEX IF NOT EXISTS idx_encontros_missao70_id ON encontros_missao70(missao70_id);
CREATE INDEX IF NOT EXISTS idx_decisoes_encontro_id ON decisoes_missao70(encontro_missao70_id);
