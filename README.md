# NutriVida - Back-end (Microservicios)

Back-end de la clínica de nutrición **NutriVida**, construido como **4 microservicios independientes** con Spring Boot, cada uno con su propia base de datos MySQL, autenticación con **JWT** y documentación en **Swagger** con enlaces **HATEOAS** en los GET.
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

NutriVida es una clínica de nutrición y dietética fundada en 2016 en Temuco, Región de La Araucanía, con 4 nutricionistas. Este back-end es la base de datos y la API que reemplazará los datos estáticos del front-end React (productos, pacientes, pedidos y usuarios).

> **Nota:** esta versión no usa Docker, Eureka, Config Server ni API Gateway. Los servicios se levantan directamente y se acceden por su puerto. Las relaciones entre servicios (Feign) quedan para próximas semanas.

---

## Tecnologías

- Java 25
- Spring Boot 4.1.2 (Web MVC, Data JPA, Validation, Security, HATEOAS)
- JWT con jjwt 0.12.6
- Flyway (migraciones de base de datos)
- MySQL
- Lombok
- springdoc-openapi 3.0.2 (Swagger UI)
- JUnit 5 y DataFaker <!-- COMPLETAR: ajustar según lo que use finalmente el testing -->
- Maven (`mvnw`)

---

## Microservicios

| Servicio | Puerto | Base de datos | Ruta base | Swagger UI |
| :--- | :---: | :--- | :--- | :--- |
| `pacientes-service` | 8081 | `nv_bd_pacientes` | `/api/v1/pacientes` | http://localhost:8081/swagger-ui.html |
| `productos-service` | 8082 | `nv_bd_productos` | `/api/v1/productos` | http://localhost:8082/swagger-ui.html |
| `pedidos-service` | 8083 | `nv_bd_pedidos` | `/api/v1/pedidos` | http://localhost:8083/swagger-ui.html |
| `auth-service` | 8084 | `nv_bd_auth` | `/api/v1/auth` | http://localhost:8084/swagger-ui.html |

Cada servicio tiene su propia base de datos y **no hay relaciones entre ellas**: `pedidos` guarda `id_paciente` e `id_producto` como números simples, sin llave foránea ni consulta entre servicios. Los enlaces HATEOAS de un pedido apuntan a su paciente y su producto, pero no se hace ninguna llamada entre servicios.

### Estructura de cada servicio

```
<servicio>/src/main/java/cl/duoc/<servicio>/
├── <Servicio>Application.java    Clase principal con @OpenAPIDefinition y @SecurityScheme
├── model/                        Entidad JPA con validaciones y ejemplos de Swagger
├── repository/                   Interfaz JpaRepository
├── service/                      CRUD y reglas de negocio
├── controller/                   Endpoints REST con anotaciones Swagger y HATEOAS
├── security/                     JwtUtil, JwtFilter y SecurityConfig
└── exception/                    ErrorResponse, GlobalExceptionHandler y excepciones propias
src/main/resources/
├── application.yml
└── db/migration/                 Migraciones Flyway (V1, V2...)
```

`auth-service` agrega las carpetas `dto/` (LoginRequest, RegistroRequest, LoginResponse, UsuarioResponse) y `config/` (usuarios iniciales).


---

## Autenticación con JWT

### Cómo funciona

1. El usuario hace login en `auth-service` (`POST /api/v1/auth/login`) con su correo y contraseña.
2. `auth-service` comprueba la contraseña contra el hash BCrypt guardado y devuelve un **token JWT** firmado, con una duración de **1 hora**. El token lleva dentro el correo (`sub`) y el rol (`rol`).
3. En los demás servicios, el cliente envía el token en la cabecera `Authorization: Bearer <token>`.
4. Cada servicio valida la **firma** del token con la misma clave secreta (`jwt.secret`) y lee el rol. **No consulta a `auth-service`**.

| Resultado | Cuándo ocurre |
| :--- | :--- |
| **401 Unauthorized** | No se envía token, o es falso o está expirado |
| **403 Forbidden** | El token es válido pero el rol no tiene permiso |

### Roles y permisos

| Rol | Descripción |
| :--- | :--- |
| `ADMIN` | Administrador de la clínica |
| `PACIENTE` | Paciente. Es el único rol que se puede crear desde el registro |
| `SECRETARIA` | Secretaria de la clínica. Tendrá permisos sobre las citas cuando exista ese servicio |

| Servicio | GET | POST, PUT, DELETE |
| :--- | :---: | :---: |
| `productos-service` | Cualquier usuario autenticado | Solo `ADMIN` |
| `pacientes-service` | Cualquier usuario autenticado | Cualquier usuario autenticado |
| `pedidos-service` | Cualquier usuario autenticado | Cualquier usuario autenticado |
| `auth-service` | `/login` y `/register` son públicos | |

### Usuarios de prueba

Se crean automáticamente al arrancar `auth-service` (son los mismos del front-end más la secretaria). Son datos ficticios solo para desarrollo.

| Rol | Correo | Contraseña |
| :--- | :--- | :--- |
| `ADMIN` | `admin@duocuc.cl` | `Admin1234` |
| `PACIENTE` | `paciente@gmail.com` | `Paciente1234` |
| `SECRETARIA` | `secretaria@duocuc.cl` | `Secretaria1234` |

Las contraseñas se guardan con **BCrypt**; nunca en texto plano.

### Endpoints de `auth-service`

| Método | Ruta | Descripción | Respuestas |
| :--- | :--- | :--- | :--- |
| `POST` | `/api/v1/auth/login` | Devuelve el token JWT | 200, 400, 401 |
| `POST` | `/api/v1/auth/register` | Crea un usuario con rol `PACIENTE` | 201, 400 |

El registro valida lo mismo que el front: correo de `@gmail.com`, `@duocuc.cl` o `@outlook.com` (sin repetirse), y contraseña de 8 a 72 caracteres con una mayúscula, una minúscula y un número.

**Ejemplo de login:**

```json
{
  "email": "admin@duocuc.cl",
  "password": "Admin1234"
}
```

**Respuesta:**

```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "tipo": "Bearer",
  "rol": "ADMIN"
}
```

### Clave secreta

Los **4 servicios deben tener exactamente el mismo `jwt.secret`**, de mínimo 32 caracteres. Por defecto se usa el valor del `application.yml`, y se puede cambiar con la variable de entorno `JWT_SECRET` (en los 4 servicios):

```yaml
jwt:
  secret: ${JWT_SECRET:nutrivida-clave-secreta-para-jwt-2026-minimo-32-caracteres}
  expiracion-ms: 3600000
```

> La clave por defecto es solo para desarrollo. En un entorno real no debe estar escrita en el código.

---

## Endpoints (CRUD)

Los servicios `pacientes`, `productos` y `pedidos` exponen los mismos 5 endpoints (ejemplo con `productos`):

| Método | Ruta | Respuesta | Rol requerido |
| :--- | :--- | :--- | :--- |
| `GET` | `/api/v1/productos` | 200 con la lista | Autenticado |
| `GET` | `/api/v1/productos/{id}` | 200 o 404 | Autenticado |
| `POST` | `/api/v1/productos` | 201 con el registro creado | `ADMIN` |
| `PUT` | `/api/v1/productos/{id}` | 200 o 404 | `ADMIN` |
| `DELETE` | `/api/v1/productos/{id}` | 204 | `ADMIN` |

Las validaciones fallidas responden **400** con el formato:

```json
{
  "status": 400,
  "error": "Error de validacion",
  "mensaje": "[Precio no puede ser negativo.]",
  "fechaHora": "2026-11-10T09:00:00"
}
```

### HATEOAS en los GET

Los GET devuelven el recurso con enlaces (`_links`) en formato HAL (`application/hal+json`).

**GET `/api/v1/productos/1`:**

```json
{
  "id": 1,
  "nombre": "Aceite de Oliva Extra Virgen 500ml",
  "descripcion": "Aceite de oliva extra virgen prensado en frio.",
  "precio": 7990,
  "categoria": "Despensa",
  "destacado": false,
  "activo": true,
  "_links": {
    "self": { "href": "http://localhost:8082/api/v1/productos/1" },
    "productos": { "href": "http://localhost:8082/api/v1/productos" }
  }
}
```

**GET `/api/v1/pedidos/1`** incluye además enlaces al paciente y al producto del pedido:

```json
"_links" {
  "self": { "href": "http://localhost:8083/api/v1/pedidos/1" },
  "pedidos": { "href": "http://localhost:8083/api/v1/pedidos" },
  "paciente": { "href": "http://localhost:8081/api/v1/pacientes/1" },
  "producto": { "href": "http://localhost:8082/api/v1/productos/1" }
}
```

El listado (`GET /api/v1/productos`) entrega una colección en `_embedded` con los `_links` de cada elemento y un `self` general.

## Reglas de negocio

| Servicio | Regla |
| :--- | :--- |
| Auth | El correo no puede repetirse. El registro siempre crea rol `PACIENTE`. Login con datos incorrectos responde 401 sin indicar si el correo existe. |
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
CREATE DATABASE IF NOT EXISTS nv_bd_auth;
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

> En `auth-service` puede aparecer el aviso `Using generated security password`. Es solo informativo y se puede ignorar.

### Paso 5: Verificar

**Swagger UI:**

- http://localhost:8081/swagger-ui.html (pacientes)
- http://localhost:8082/swagger-ui.html (productos)
- http://localhost:8083/swagger-ui.html (pedidos)
- http://localhost:8084/swagger-ui.html (auth)

**Tablas y migraciones en MySQL:**

```sql
SHOW TABLES FROM nv_bd_pacientes;
SHOW TABLES FROM nv_bd_productos;
SHOW TABLES FROM nv_bd_pedidos;
SHOW TABLES FROM nv_bd_auth;

SELECT installed_rank, version, description, script, success
FROM nv_bd_pacientes.flyway_schema_history;
```

Cada base debe mostrar su tabla (`pacientes`, `productos`, `pedidos`, `usuarios`) y `flyway_schema_history` con todas las versiones en `success = 1`.

**Usuarios y contraseñas cifradas:**

```sql
SELECT id, email, LEFT(password, 7) AS hash, rol FROM nv_bd_auth.usuarios;
```

Los hashes deben comenzar con `$2a$10$`, nunca en texto plano.

---

## Cómo probar con Swagger (flujo con JWT)

1. Abrir http://localhost:8084/swagger-ui.html y ejecutar `POST /api/v1/auth/login` con un usuario de prueba.
2. Copiar el valor de `token` de la respuesta.
3. Abrir el Swagger de otro servicio (por ejemplo http://localhost:8082/swagger-ui.html) y pulsar **Authorize**.
4. Pegar **solo el token** (sin escribir la palabra `Bearer`) y confirmar.
5. Probar los endpoints. El mismo token sirve para los 4 servicios.

**Recorrido de prueba sugerido:**

| Paso | Acción | Resultado esperado |
| :---: | :--- | :--- |
| 1 | `GET /productos` sin token | 401 |
| 2 | Login como paciente, Authorize, `GET /productos` | 200 con `_links` |
| 3 | Con el token de paciente, `POST /productos` | 403 |
| 4 | Login como admin, Authorize, `POST /productos` | 201 |
| 5 | Modificar una letra del token y repetir | 401 |
| 6 | `POST /auth/register` con un correo nuevo | 201 con rol `PACIENTE` |
| 7 | Repetir el registro | 400 por correo repetido |

---

## Ejemplos para probar en Swagger

**POST /api/v1/auth/register**

```json
{
  "email": "nuevo@gmail.com",
  "password": "Clave1234"
}
```

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

**POST /api/v1/productos** (requiere token de `ADMIN`)

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

Pruebas de error esperadas (400): repetir el mismo paciente (RUT repetido), un producto con `precio` negativo, un pedido con `total` distinto de `cantidad × precioUnitario` y un registro con correo `@hotmail.com`.
---

## Migraciones con Flyway

Las migraciones están en `src/main/resources/db/migration/` de cada servicio y **no se modifican una vez aplicadas**: los cambios se hacen con una versión nueva (`V2`, `V3`...).

<!-- COMPLETAR: listar las migraciones de cada servicio, por ejemplo:

| Servicio | Migración | Contenido |
| :--- | :--- | :--- |
| pacientes-service | V1__creacion_tabla_pacientes.sql | Crea la tabla pacientes |
| pacientes-service | V2__insercion_pacientes.sql | Datos de prueba |
...
-->

En `auth-service` los usuarios iniciales no están en una migración porque sus contraseñas necesitan el hash BCrypt: los crea la clase `DatosIniciales` al arrancar, y solo inserta los que no existen.

---

## Pruebas

<!-- COMPLETAR: pruebas con JUnit y DataFaker. Indicar:
     - qué se prueba en cada servicio (services y controllers)
     - cómo se ejecutan: ./mvnw test
     - si necesitan MySQL corriendo (los contextLoads la necesitan) -->

---

## Posibles problemas


| Mensaje o síntoma | Causa |
| :--- | :--- |
| `Unknown database` | Falta ejecutar el script de creación de las bases de datos. |
| `Access denied for user` | Usuario o contraseña incorrectos en el `application.yml`. |
| `Port already in use` | Otro servicio ya usa ese puerto. |
| `Schema-validation: wrong column type` | La entidad y el SQL de la migración no coinciden. |
| `Schema-validation: missing table [usuarios]` | La migración de `auth-service` no está en `db/migration/`. |
| Swagger no carga | Revisar la dependencia springdoc 3.0.2 y la ruta `/swagger-ui.html`. |
| 401 aunque el token sea válido | El `jwt.secret` no es idéntico en los 4 servicios, o el token expiró (dura 1 hora). |
| 401 al pulsar Authorize | Se escribió la palabra `Bearer` junto al token: debe pegarse solo el token. |
| 403 en `POST`, `PUT` o `DELETE` de productos | El usuario no tiene rol `ADMIN`. |
| Los GET no muestran `_links` | Falta la dependencia `spring-boot-starter-hateoas` o no se recargó Maven. |

---

## Trabajo futuro

- Servicio de citas, con permisos para `SECRETARIA` y `ADMIN`.
- Nuevas tablas y servicios mediante migraciones Flyway: nutricionistas, categorías, detalle de pedido y datos de salud de la cita.
- Restringir el `DELETE` de pacientes y pedidos al rol `ADMIN`.
- Eureka, Config Server y API Gateway (los enlaces HATEOAS de pedidos pasarán a usar la URL del gateway).
- Comunicación entre servicios con Feign.
- Mover `jwt.secret` fuera del código (variable de entorno o Config Server).
- Conexión del front-end React con estos servicios.
- Separar nombre y apellido en el formulario de registro del front-end.

---

## Autores

- Vicente Lopez - vic.lopez@duocuc.cl
- Igor Rojas - igo.rojas@duocuc.cl
