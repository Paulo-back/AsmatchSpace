-- V9__lembrete_template_instancias.sql

-- Renomeia a tabela atual para ser o template
ALTER TABLE lembretes RENAME TO lembrete_templates;

-- Adiciona campos de recorrência ao template
ALTER TABLE lembrete_templates
    ADD COLUMN tipo_recorrencia VARCHAR(20) NOT NULL DEFAULT 'NENHUMA',
    ADD COLUMN dias_semana      VARCHAR(20),
    ADD COLUMN data_fim         DATE;

-- Renomeia 'data' para 'data_inicio' (semântica correta no template)
ALTER TABLE lembrete_templates RENAME COLUMN data TO data_inicio;

-- Remove 'concluido' do template (não faz sentido aqui — vai para a instância)
ALTER TABLE lembrete_templates DROP COLUMN concluido;

-- Cria tabela de instâncias
CREATE TABLE lembrete_instancias (
    id                  BIGSERIAL PRIMARY KEY,
    template_id         BIGINT NOT NULL REFERENCES lembrete_templates(id) ON DELETE CASCADE,
    data_instancia      DATE NOT NULL,
    horario_efetivo     TIME NOT NULL,
    status              VARCHAR(20) NOT NULL DEFAULT 'PENDENTE',
    notificacao_enviada BOOLEAN NOT NULL DEFAULT FALSE,
    UNIQUE (template_id, data_instancia)
);

-- Migra os registros existentes: cada lembrete antigo vira uma instância de hoje
INSERT INTO lembrete_instancias (template_id, data_instancia, horario_efetivo, status)
SELECT id, data_inicio, horario, 'PENDENTE'
FROM lembrete_templates;