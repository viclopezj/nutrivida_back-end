Creación de bases de datos
Script SQL
sql
CREATE DATABASE IF NOT EXISTS nv_bd_pacientes;
CREATE DATABASE IF NOT EXISTS nv_bd_productos;
CREATE DATABASE IF NOT EXISTS nv_bd_pedidos;

Verificar:

sql
SHOW DATABASES LIKE 'nv_bd%';

Por terminal:

bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS nv_bd_pacientes; CREATE DATABASE IF NOT EXISTS nv_bd_productos; CREATE DATABASE IF NOT EXISTS nv_bd_pedidos;"

Si necesitas empezar de cero (borra todo, incluidas las tablas y el historial de Flyway):

sql
DROP DATABASE IF EXISTS nv_bd_pacientes;
DROP DATABASE IF EXISTS nv_bd_productos;
DROP DATABASE IF EXISTS nv_bd_pedidos;

Después vuelve a ejecutar los CREATE DATABASE y reinicia los servicios para que Flyway recree las tablas.

Enlaces de Swagger
Servicio	Swagger UI	JSON de la API
pacientes-service	http://localhost:8081/swagger-ui.html	http://localhost:8081/v3/api-docs
productos-service	http://localhost:8082/swagger-ui.html	http://localhost:8082/v3/api-docs
pedidos-service	http://localhost:8083/swagger-ui.html	http://localhost:8083/v3/api-docs

Endpoints directos:

http://localhost:8081/api/v1/pacientes
http://localhost:8082/api/v1/productos
http://localhost:8083/api/v1/pedidos
Verificación en MySQL (Flyway)

MySQL no tiene enlace web, se revisa con consultas en tu cliente SQL.

Tablas creadas:

sql
SHOW TABLES FROM nv_bd_pacientes;
SHOW TABLES FROM nv_bd_productos;
SHOW TABLES FROM nv_bd_pedidos;

Historial de migraciones de Flyway:

sql
SELECT installed_rank, version, description, script, success
FROM nv_bd_pacientes.flyway_schema_history;

SELECT installed_rank, version, description, script, success
FROM nv_bd_productos.flyway_schema_history;

SELECT installed_rank, version, description, script, success
FROM nv_bd_pedidos.flyway_schema_history;

Cada una debe mostrar version = 1 y success = 1.

Datos guardados:

sql
SELECT * FROM nv_bd_pacientes.pacientes;
SELECT * FROM nv_bd_productos.productos;
SELECT * FROM nv_bd_pedidos.pedidos;
