-- V3: indices para consultar pedidos por paciente y por estado
CREATE INDEX idx_pedidos_id_paciente ON pedidos (id_paciente);
CREATE INDEX idx_pedidos_estado ON pedidos (estado);
