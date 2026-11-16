-- V2: datos iniciales de pacientes (el primero coincide con el usuario paciente@gmail.com de auth-service)
INSERT INTO pacientes
    (rut, nombre, apellido, email, telefono, fecha_nacimiento, sexo_biologico, identidad_genero,
     peso, estatura, cintura, alergias, enfermedades, patron_alimenticio)
VALUES
    ('12.345.678-5', 'Ana', 'Perez Gonzalez', 'paciente@gmail.com', '+56912345678', '1995-08-20',
     'Femenino', 'Mujer', 68.50, 165.00, 80.00, 'Alergia a los frutos secos', NULL, 'Omnivoro'),
    ('15.678.234-1', 'Matias', 'Fuentes Rojas', 'matias.fuentes@nutrivida.cl', '+56987654321', '1990-09-30',
     'Masculino', 'Hombre', 82.30, 178.00, 91.50, NULL, 'Hipertension', 'Vegetariano'),
    ('18.901.456-7', 'Valentina', 'Soto Araya', 'valentina.soto@nutrivida.cl', '+56911223344', '2000-01-25',
     'Femenino', 'Mujer', 55.00, 160.00, 68.00, 'Intolerancia a la lactosa', NULL, 'Vegano'),
    ('9.876.543-3', 'Camilo', 'Rojas Munoz', 'camilo.rojas@nutrivida.cl', '+56955667788', '1982-03-14',
     'Masculino', 'Hombre', 91.80, 172.00, 102.00, NULL, 'Diabetes tipo 2', 'Omnivoro');
