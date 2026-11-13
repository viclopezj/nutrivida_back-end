CREATE TABLE usuarios (
                          id       BIGINT AUTO_INCREMENT PRIMARY KEY,
                          email    VARCHAR(150) NOT NULL UNIQUE,
                          password VARCHAR(100) NOT NULL,
                          rol      VARCHAR(20)  NOT NULL,
                          CONSTRAINT chk_usuario_rol CHECK (rol IN ('ADMIN', 'PACIENTE'))
);