# NutriVida - Back-end (Microservicios)

Back-end de la clínica de nutrición **NutriVida**, construido como **3 microservicios independientes** con Spring Boot, cada uno con su propia base de datos MySQL y documentado con Swagger.

## Datos del curso

| | |
| :--- | :--- |
| **Sección** | 003D |
| **Asignatura** | Desarrollo Fullstack II |
| **Docente** | Victor Andrade |
| **Grupo** | 12 |
| **Integrantes** | Vicente Lopez e Igor Rojas |

---

## Contexto

NutriVida es una clínica de nutrición y dietética fundada en 2016 en Temuco, Región de La Araucanía, con 4 nutricionistas. Este back-end es la base de datos y la API que reemplazará los datos estáticos del front-end React (productos, pacientes y pedidos).

> **Nota:** esta primera versión no usa Docker, Eureka, Config Server ni API Gateway. Los servicios se levantan directamente y se acceden por su puerto. Las relaciones entre servicios (Feign) quedan para próximas semanas.

---

## Tecnologías

- Java 25
- Spring Boot 4.0.6 (Web MVC, Data JPA, Validation)
- Flyway (migraciones de base de datos)
- MySQL
- Lombok
- springdoc-openapi 3.0.2 (Swagger UI)
- Maven (`mvnw`)

---

## Microservicios

| Servicio | Puerto | Base de datos | Ruta base | Swagger UI |
| :--- | :---: | :--- | :--- | :--- |
| `pacientes-service` | 8081 | `nv_bd_pacientes` | `/api/v1/pacientes` | http://localhost:8081/swagger-ui.html |
| `productos-service` | 8082 | `nv_bd_productos` | `/api/v1/productos` | http://localhost:8082/swagger-ui.html |
| `pedidos-service` | 8083 | `nv_bd_pedidos` | `/api/v1/pedidos` | http://localhost:8083/swagger-ui.html |

Cada servicio tiene su propia base de datos y **no hay relaciones entre ellas**: `pedidos` guarda `id_paciente` e `id_producto` como números simples, sin llave foránea ni consulta entre servicios.

### Estructura de cada servicio

```
<servicio>/src/main/java/cl/duoc/<servicio>/
├── <Servicio>Application.java    Clase principal con @OpenAPIDefinition
├── model/                        Entidad JPA con validaciones
├── repository/                   Interfaz JpaRepository
├── service/                      CRUD y reglas de negocio
├── controller/                   Endpoints REST con anotaciones Swagger
└── exception/                    ErrorResponse, GlobalExceptionHandler y excepciones propias
src/main/resources/
├── application.yml
└── db/migration/V1__creacion_tabla_<nombre>.sql
```

---

## Endpoints (CRUD)

Los 3 servicios exponen los mismos 5 endpoints (ejemplo con `productos`):

| Método | Ruta | Respuesta |
| :--- | :--- | :--- |
| `GET` | `/api/v1/productos` | 200 con la lista |
| `GET` | `/api/v1/productos/{id}` | 200 o 404 |
| `POST` | `/api/v1/productos` | 201 con el registro creado |
| `PUT` | `/api/v1/productos/{id}` | 200 o 404 |
| `DELETE` | `/api/v1/productos/{id}` | 204 |

Las validaciones fallidas responden **400** con el formato:

```json
{
  "status": 400,
  "error": "Error de validacion",
  "mensaje": "[Precio no puede ser negativo.]",
  "fechaHora": "2026-11-10T09:00:00"
}
```

## Reglas de negocio

| Servicio | Regla |
| :--- | :--- |
| Pacientes | El `rut` y el `email` no pueden repetirse. |
| Productos | Si no se envía `destacado` queda en `false`; si no se envía `activo` queda en `true`. |
| Pedidos | `total` debe ser igual a `cantidad × precioUnitario`. `estado` solo acepta `pendiente` o `pagado` (por defecto `pendiente`). Si no se envía `fechaPedido` se usa la fecha y hora actual. |

---

## Instrucciones de instalación y ejecución

### Requisitos previos

- JDK 25
- MySQL corriendo en local (puerto 3306 por defecto)
- Maven o el `mvnw` incluido en cada servicio
- Un cliente SQL (IntelliJ Database, MySQL Workbench o DBeaver)

### Paso 1: Crear las bases de datos

Ejecutar en MySQL como `root`:

```sql
CREATE DATABASE IF NOT EXISTS nv_bd_pacientes;
CREATE DATABASE IF NOT EXISTS nv_bd_productos;
CREATE DATABASE IF NOT EXISTS nv_bd_pedidos;
```

Las tablas **no** se crean a mano: las crea Flyway con las migraciones `V1` al arrancar cada servicio.

### Paso 2: Revisar la conexión

En el `application.yml` de cada servicio se asume:

```yaml
datasource:
  url: jdbc:mysql://localhost:3306/nv_bd_<nombre>
  username: root
  password:
```

Si tu MySQL usa otro puerto o tiene contraseña en `root`, ajusta la URL y el campo `password` en los 3 archivos.

### Paso 3: Compilar

Dentro de cada carpeta de servicio:

```bash
./mvnw clean package -DskipTests
```

### Paso 4: Levantar los servicios

El orden es indiferente. Desde IntelliJ ejecutando cada clase `*Application`, o por terminal dentro de cada carpeta:

```bash
./mvnw spring-boot:run
```

En la consola debe aparecer `Started ...Application` y el aviso de que Flyway aplicó la migración.

### Paso 5: Verificar

**Swagger UI:**

- http://localhost:8081/swagger-ui.html (pacientes)
- http://localhost:8082/swagger-ui.html (productos)
- http://localhost:8083/swagger-ui.html (pedidos)

**Tablas y migraciones en MySQL:**

```sql
SHOW TABLES FROM nv_bd_pacientes;
SHOW TABLES FROM nv_bd_productos;
SHOW TABLES FROM nv_bd_pedidos;

SELECT installed_rank, version, description, script, success
FROM nv_bd_pacientes.flyway_schema_history;
```

Cada base debe mostrar su tabla (`pacientes`, `productos`, `pedidos`) y `flyway_schema_history` con `version = 1` y `success = 1`.

---

## Ejemplos para probar en Swagger

**POST /api/v1/pacientes**

```json
{
  "rut": "12.345.678-9",
  "nombre": "Ana",
  "apellido": "Pérez González",
  "email": "paciente@gmail.com",
  "telefono": "+56912345678",
  "fechaNacimiento": "1995-08-20",
  "sexoBiologico": "Femenino",
  "identidadGenero": "Mujer",
  "peso": 68.5,
  "estatura": 165,
  "cintura": 80,
  "alergias": "Alergia a los frutos secos",
  "enfermedades": null,
  "patronAlimenticio": "Omnívoro"
}
```

**POST /api/v1/productos**

```json
{
  "nombre": "Aceite de Oliva Extra Virgen 500ml",
  "descripcion": "Aceite de oliva extra virgen prensado en frío, ideal para ensaladas y cocina diaria.",
  "precio": 7990,
  "categoria": "Despensa"
}
```

**POST /api/v1/pedidos**

```json
{
  "idPaciente": 1,
  "idProducto": 1,
  "cantidad": 2,
  "precioUnitario": 7990,
  "total": 15980
}
```

Pruebas de error esperadas (400): repetir el mismo paciente (RUT repetido), un producto con `precio` negativo y un pedido con `total` distinto de `cantidad × precioUnitario`.

---

## Problemas frecuentes

| Mensaje | Causa |
| :--- | :--- |
| `Unknown database` | Falta ejecutar el script de creación de las bases de datos. |
| `Access denied for user` | Usuario o contraseña incorrectos en el `application.yml`. |
| `Port already in use` | Otro servicio ya usa ese puerto. |
| `Schema-validation: wrong column type` | La entidad y el SQL de la migración no coinciden. |
| Swagger no carga | Revisar la dependencia springdoc 3.0.2 y la ruta `/swagger-ui.html`. |

---

## Trabajo futuro

- Nuevas tablas y servicios mediante migraciones Flyway `V2`, `V3`...: citas, nutricionistas, categorías, detalle de pedido, usuarios con contraseña y rol.
- Eureka, Config Server y API Gateway.
- Comunicación entre servicios con Feign.
- Conexión del front-end React con estos servicios.
- Separar nombre y apellido en el formulario de registro del front-end.

---

## Autores

- Vicente Lopez - vic.lopez@duocuc.cl
- Igor Rojas - igo.rojas@duocuc.cl
