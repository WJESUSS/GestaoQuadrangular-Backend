DO $$
BEGIN
    IF NOT EXISTS (SELECT 1 FROM information_schema.columns WHERE table_name = 'cultos' AND column_name = 'horario') THEN
        ALTER TABLE cultos ADD COLUMN horario VARCHAR(5) NOT NULL DEFAULT '00:00';
    END IF;
END $$;

UPDATE cultos SET horario = '00:00' WHERE horario IS NULL;

ALTER TABLE cultos ALTER COLUMN horario SET NOT NULL;
ALTER TABLE cultos ALTER COLUMN horario SET DEFAULT '00:00';

DROP INDEX IF EXISTS idx_cultos_data_tipo_unique;

CREATE UNIQUE INDEX IF NOT EXISTS idx_cultos_data_tipo_horario_unique ON cultos(data, tipo_culto, horario);
