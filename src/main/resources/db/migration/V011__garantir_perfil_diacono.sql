ALTER TABLE usuarios DROP CONSTRAINT IF EXISTS usuarios_perfil_check;

ALTER TABLE usuarios ADD CONSTRAINT usuarios_perfil_check
    CHECK (perfil IN (
        'ADMIN',
        'PASTOR',
        'LIDER_CELULA',
        'SUPERINTENDENTE',
        'SECRETARIO',
        'TESOUREIRO',
        'DIACONO'
    ));
