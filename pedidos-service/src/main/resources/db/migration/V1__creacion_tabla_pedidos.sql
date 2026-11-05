CREATE TABLE pedidos (
                         id              BIGINT AUTO_INCREMENT PRIMARY KEY,
                         id_paciente     BIGINT      NOT NULL,
                         id_producto     BIGINT      NOT NULL,
                         cantidad        INT         NOT NULL,
                         precio_unitario INT         NOT NULL,
                         total           INT         NOT NULL,
                         estado          VARCHAR(20) NOT NULL DEFAULT 'pagado',
                         fecha_pedido    DATETIME    NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         CONSTRAINT chk_pedido_cantidad CHECK (cantidad > 0)
);
