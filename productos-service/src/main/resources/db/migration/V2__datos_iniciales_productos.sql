-- V2: catalogo inicial de productos (los ids 1 a 6 se usan en los pedidos de ejemplo de pedidos-service)
INSERT INTO productos (nombre, descripcion, precio, categoria, destacado, activo)
VALUES
    ('Aceite de Oliva Extra Virgen 500ml',
     'Aceite de oliva extra virgen prensado en frio, ideal para ensaladas y cocina diaria.',
     7990, 'Despensa', FALSE, TRUE),
    ('Granola Sin Azucar 400g',
     'Granola de avena, almendras y semillas, endulzada solo con fruta deshidratada.',
     5490, 'Despensa', FALSE, TRUE),
    ('Proteina Vegetal 900g',
     'Proteina de arveja y arroz sabor vainilla, sin lactosa y sin gluten.',
     12990, 'Suplementos', TRUE, TRUE),
    ('Mix de Frutos Secos 200g',
     'Mezcla de nueces, almendras y arandanos sin sal agregada.',
     3490, 'Snacks', FALSE, TRUE),
    ('Omega 3 60 capsulas',
     'Suplemento de aceite de pescado con EPA y DHA.',
     9990, 'Suplementos', TRUE, TRUE),
    ('Te Verde Organico 20 bolsitas',
     'Te verde organico en bolsitas individuales.',
     2990, 'Bebidas', FALSE, TRUE);
