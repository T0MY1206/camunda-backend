# camunda-backend

Spring Boot + Camunda 7.24 (motor embebido + Cockpit/Tasklist Webapps). APIs REST propias en Spring MVC (sin `camunda-bpm-spring-boot-starter-rest`). Base de datos: **PostgreSQL**.

## APIs y documentación

- **Paridad Camunda (REST 7.24 orientativa):** prefijo `GET/POST/DELETE ... /api/v1/camunda/**` (despliegues, definiciones, instancias, tareas, historial, jobs, identidad, DMN, etc.).
- **Extensiones propias:** prefijo `/api/v1/custom/**` (ejemplo: `GET /api/v1/custom/status`).
- **OpenAPI / Swagger UI:** SpringDoc — tras arrancar, documentación en `/v3/api-docs` y UI en `/swagger-ui.html` (grupos **camunda-parity** y **custom**).
- Guía narrativa de endpoints y flujos: [docs/api-endpoints-guia.md](docs/api-endpoints-guia.md).

## Requisitos

- Java 25
- PostgreSQL accesible desde la aplicación (host, puerto, base y credenciales según tu entorno)

### Contenedor Docker que se detiene al instante (antes funcionaba)

Si usas **`postgres:latest`** (PostgreSQL 18+) con un **volumen antiguo** montado en `/var/lib/postgresql/data`, el contenedor **sale con código 1** y en los logs aparece un error sobre *"18+, these Docker images are configured to store database data..."* y *"data in /var/lib/postgresql/data (unused mount/volume)"*.

**Causa:** la imagen nueva no acepta ese layout de datos de una versión anterior sin migrar con `pg_upgrade`.

**Opciones (desarrollo, datos prescindibles):**

1. Eliminar el contenedor y el volumen conflictivo, y volver a crear con una imagen **LTS estable** (recomendado) y volumen nuevo:

   ```powershell
   docker stop postgres 2>$null; docker rm postgres
   docker volume rm postgres_data
   docker run -d --name postgres -e POSTGRES_PASSWORD=sa -p 5432:5432 -v postgres_data:/var/lib/postgresql/data postgres:16-alpine
   ```

2. **Sin borrar datos:** fija la imagen a la **misma versión mayor** con la que se creó el volumen (p. ej. `postgres:15` o `postgres:16`) en lugar de `latest`.

3. **Mantener PG 18+:** sigue la guía oficial de la imagen para montar el volumen en `/var/lib/postgresql` (no solo en `data`) y migrar datos; es más laborioso.

Evita `postgres:latest` en proyectos si tienes volúmenes persistentes antiguos; usa una etiqueta fija (`postgres:16-alpine`, etc.).

## Base de datos

1. **Conexión:** configura URL, usuario y contraseña en `src/main/resources/application.yaml` o mediante variables de entorno (ver sección siguiente).

2. Crear el esquema Camunda **7.24.0** (orden recomendado):

   - `src/main/resources/db/camunda/7.24.0/postgres/identity.sql`
   - `src/main/resources/db/camunda/7.24.0/postgres/engine.sql`
   - (opcional) `src/main/resources/db/camunda/7.24.0/postgres/extra-indexes.sql`

   Ejecución con `psql`, DBeaver u otra herramienta contra la misma base definida en el JDBC URL.

3. Arrancar la aplicación: por defecto `database-schema-update: true` (el motor crea/actualiza tablas). Para esquema solo vía SQL manual, usa el perfil `manual-schema` (ver más abajo).

## Variables de entorno (conexión)

Puedes sobreescribir la configuración sin tocar el YAML en el repo:

| Variable | Ejemplo |
|----------|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://host:5432/nombre_base` |
| `SPRING_DATASOURCE_USERNAME` | usuario |
| `SPRING_DATASOURCE_PASSWORD` | contraseña |

## Documentación del modelo de datos

Descripción de tablas, relaciones, columnas e índices: [docs/camunda-schema.md](docs/camunda-schema.md).

Para regenerar ese documento tras cambiar los SQL:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/Generate-CamundaSchemaDoc.ps1
```

## Configuración

Ver `src/main/resources/application.yaml` (datasource y usuario admin de Camunda).

### Si el arranque falla al conectar o al iniciar Camunda

1. **PostgreSQL accesible:** misma máquina → `localhost` y el puerto publicado (suele ser `5432`). Comprueba usuario, contraseña y nombre de base en el JDBC URL.
2. **SSL local:** el URL incluye `?sslmode=disable` para desarrollo; en servidores con SSL obligatorio, quita eso y configura el cliente.
3. **Esquema vacío:** con `camunda.bpm.database.schema-update: true` (por defecto), el motor crea o actualiza las tablas al arrancar. Si prefieres crear solo con SQL manual, ejecuta los scripts y arranca con perfil `manual-schema`:
   ```text
   -- Ejemplo: variable de entorno
   SPRING_PROFILES_ACTIVE=manual-schema
   ```
4. **Errores útiles:** revisa la consola o el log por `ProcessEngineException`, `Connection refused` o `password authentication failed`.

### `Connection refused` a `localhost:5432` (o `127.0.0.1:5432`)

Eso significa que **en ese momento no hay ningún PostgreSQL escuchando** en ese host y puerto. No es un fallo de Camunda en sí.

1. **Contenedor Docker:** comprueba que esté **en ejecución** y que el puerto esté publicado:
   ```bash
   docker ps
   ```
   Deberías ver algo como `0.0.0.0:5432->5432/tcp` (si el puerto del host es otro, por ejemplo `5433`, el JDBC debe usar ese puerto: `jdbc:postgresql://127.0.0.1:5433/postgres`).

2. **Arrancar el contenedor** si está parado: `docker start <nombre>` o el comando que uses habitualmente.

3. **Sincronizar conexion y arranque:** levanta la base **antes** de ejecutar la aplicación en IntelliJ.

4. **Variable de entorno:** puedes sobrescribir sin tocar el YAML en la configuración de ejecución de IntelliJ:
   - `SPRING_DATASOURCE_URL=jdbc:postgresql://127.0.0.1:PUERTO/postgres?sslmode=disable`

El `application.yaml` usa por defecto `127.0.0.1` en lugar de `localhost` para reducir problemas en Windows.
