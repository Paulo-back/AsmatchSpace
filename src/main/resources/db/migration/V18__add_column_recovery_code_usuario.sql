
ALTER TABLE usuarios
    ADD COLUMN codigo_recuperacao VARCHAR(6),
    ADD COLUMN codigo_expiracao TIMESTAMP;

    UPDATE clientes c
    SET email = u.login
    FROM usuarios u
    WHERE u.id = c.usuario_id
      AND c.email IS DISTINCT FROM u.login;