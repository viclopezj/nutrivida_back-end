-- V3: indice para busquedas por apellido (rut y email ya tienen indice por su UNIQUE)
CREATE INDEX idx_pacientes_apellido ON pacientes (apellido);
