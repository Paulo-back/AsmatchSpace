CREATE TABLE IF NOT EXISTS diario_sintomas (
    id BIGSERIAL PRIMARY KEY,
    cliente_id BIGINT NOT NULL,
    data VARCHAR(20) NOT NULL,
    horario VARCHAR(10) NOT NULL,
    intensidade VARCHAR(255),
    descricao TEXT,
    data_cadastro TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_diario_cliente
        FOREIGN KEY (cliente_id) REFERENCES clientes(id) ON DELETE CASCADE
);
