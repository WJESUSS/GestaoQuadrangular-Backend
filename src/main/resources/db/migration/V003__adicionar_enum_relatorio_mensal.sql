-- Adiciona o novo valor ao enum de notificações
ALTER TABLE notificacoes DROP CONSTRAINT IF EXISTS notificacoes_tipo_check;

ALTER TABLE notificacoes ADD CONSTRAINT notificacoes_tipo_check
    CHECK (tipo IN (
        'MULTIPLICACAO_CELULA',
        'APROVACAO_SOLICITACAO',
        'REJEICAO_SOLICITACAO',
        'AVISO_GERAL',
        'RELATORIO_PENDENTE',
        'LEMBRETE_REUNIAO',
        'SOLICITACAO_ALTERACAO',
        'RELATORIO_MENSAL_COMPLETO'
    ));
