-- V2: pedidos de ejemplo. id_paciente e id_producto apuntan a los datos iniciales de
-- pacientes-service (1 a 4) y productos-service (1 a 6). total = cantidad x precio_unitario.
INSERT INTO pedidos (id_paciente, id_producto, cantidad, precio_unitario, total, estado, fecha_pedido)
VALUES
    (1, 1, 2, 7990, 15980, 'pagado',    '2026-11-02 10:15:00'),
    (1, 3, 1, 12990, 12990, 'pendiente', '2026-11-05 16:40:00'),
    (2, 2, 3, 5490, 16470, 'pagado',    '2026-11-06 09:05:00'),
    (3, 5, 1, 9990, 9990, 'pendiente',  '2026-11-09 12:30:00'),
    (4, 4, 2, 3490, 6980, 'pagado',     '2026-11-10 18:20:00');
