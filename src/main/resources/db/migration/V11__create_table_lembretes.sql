CREATE TABLE IF NOT EXISTS lembretes (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    titulo VARCHAR(255) NOT NULL,
    horario VARCHAR(10) NOT NULL,
    data VARCHAR(20) NOT NULL,
    concluido BOOLEAN DEFAULT FALSE,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_lembrete_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);
