CREATE TABLE pacientes (
                           id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
                           rut                VARCHAR(12)  NOT NULL UNIQUE,
                           nombre             VARCHAR(100) NOT NULL,
                           apellido           VARCHAR(100) NOT NULL,
                           email              VARCHAR(150) NOT NULL UNIQUE,
                           telefono           VARCHAR(12)  NOT NULL,
                           fecha_nacimiento   DATE         NOT NULL,
                           sexo_biologico     VARCHAR(30)  NOT NULL,
                           identidad_genero   VARCHAR(30)  NOT NULL,
                           peso               DECIMAL(5,2) NOT NULL,
                           estatura           DECIMAL(5,2) NOT NULL,
                           cintura            DECIMAL(5,2) NOT NULL,
                           alergias           VARCHAR(255) NULL,
                           enfermedades       VARCHAR(255) NULL,
                           patron_alimenticio VARCHAR(50)  NOT NULL
);
