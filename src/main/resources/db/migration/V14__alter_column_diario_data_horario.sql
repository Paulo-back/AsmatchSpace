ALTER TABLE diario_sintomas
    ALTER COLUMN data    TYPE DATE USING data::DATE,
    ALTER COLUMN horario TYPE TIME USING horario::TIME;