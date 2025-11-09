ALTER TABLE clientes ADD COLUMN usuario_id BIGINT;

ALTER TABLE clientes
ADD CONSTRAINT fk_cliente_usuario
FOREIGN KEY (usuario_id)
REFERENCES usuarios(id);
