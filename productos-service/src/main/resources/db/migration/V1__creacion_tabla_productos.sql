CREATE TABLE productos (
                           id          BIGINT AUTO_INCREMENT PRIMARY KEY,
                           nombre      VARCHAR(120) NOT NULL,
                           descripcion TEXT         NOT NULL,
                           precio      INT          NOT NULL,
                           categoria   VARCHAR(30)  NOT NULL,
                           destacado   BOOLEAN      NOT NULL DEFAULT FALSE,
                           activo      BOOLEAN      NOT NULL DEFAULT TRUE,
                           CONSTRAINT chk_producto_precio CHECK (precio >= 0)
);
