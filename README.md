# camunda-backend

Backend basado en Spring Boot + Camunda Platform 7.24 con base de datos PostgreSQL y enfoque de paridad REST bajo `/api/v1/camunda`.

## Estado actual del proyecto

- Motor Camunda 7.24 embebido con webapps (Cockpit / Tasklist / Admin).
- API REST oficial de Camunda habilitada internamente en `/engine-rest`.
- Capa de paridad activa: todas las rutas `/api/v1/camunda/**` se enrutan al contrato de Camunda 7.24.
- OpenAPI principal en `/v3/api-docs`.
- OpenAPI de paridad Camunda (rebaseado a `/api/v1/camunda`) en `/v3/api-docs/camunda-parity`.
- Endpoints personalizados en `/api/v1/custom/**`.

## Stack técnico

- Java 25
- Spring Boot 3.5.5
- Camunda Platform 7.24.0
- PostgreSQL
- Springdoc OpenAPI / Swagger UI
- MapStruct

## Endpoints de documentación

- Swagger UI: `http://127.0.0.1:18080/swagger-ui.html`
- OpenAPI general: `http://127.0.0.1:18080/v3/api-docs`
- OpenAPI paridad Camunda: `http://127.0.0.1:18080/v3/api-docs/camunda-parity`

## Configuración de base de datos

La app usa por defecto:

- URL: `jdbc:postgresql://127.0.0.1:5432/postgres?sslmode=disable`
- Usuario: `postgres`
- Password: `sa`

Variables de entorno soportadas:

| Variable | Ejemplo |
|----------|---------|
| `SPRING_DATASOURCE_URL` | `jdbc:postgresql://127.0.0.1:55432/camunda_run?sslmode=disable` |
| `SPRING_DATASOURCE_USERNAME` | `camunda` |
| `SPRING_DATASOURCE_PASSWORD` | `camunda` |
| `SERVER_PORT` | `18080` |

## Arranque rápido (Windows + Docker)

1) Levantar PostgreSQL:

```powershell
docker rm -f camunda-postgres-run 2>$null
docker run -d --name camunda-postgres-run `
  -e POSTGRES_DB=camunda_run `
  -e POSTGRES_USER=camunda `
  -e POSTGRES_PASSWORD=camunda `
  -p 55432:5432 postgres:16
```

2) Ejecutar la app:

```powershell
$env:SPRING_DATASOURCE_URL="jdbc:postgresql://127.0.0.1:55432/camunda_run?sslmode=disable"
$env:SPRING_DATASOURCE_USERNAME="camunda"
$env:SPRING_DATASOURCE_PASSWORD="camunda"
$env:SERVER_PORT="18080"
$env:JAVA_TOOL_OPTIONS="-Duser.timezone=UTC"
java -jar target/camundaV1-1.0.0-SNAPSHOT.jar
```

## Construcción y pruebas

Build local (si tienes Maven instalado):

```bash
mvn clean package
```

Build con Maven en Docker:

```powershell
docker run --rm -v "c:/Proyectos/camunda-backend:/workspace" -w /workspace `
  eclipse-temurin:25-jdk bash -lc "apt-get update -o Acquire::Retries=5 -qq && apt-get install -y -qq maven > /dev/null && mvn clean package -DskipTests"
```

Tests:

```bash
mvn test
```

## Esquema SQL y documentación de base

- Scripts SQL Camunda 7.24 para PostgreSQL:
  - `src/main/resources/db/camunda/7.24.0/postgres/identity.sql`
  - `src/main/resources/db/camunda/7.24.0/postgres/engine.sql`
  - `src/main/resources/db/camunda/7.24.0/postgres/extra-indexes.sql`
- Documento de esquema: `docs/camunda-schema.md`
- Generación del documento:

```powershell
powershell -ExecutionPolicy Bypass -File scripts/Generate-CamundaSchemaDoc.ps1
```

## Notas operativas

- Si quieres controlar el esquema solo por SQL manual, usa el perfil `manual-schema` (`camunda.bpm.database.schema-update=false`).
- Evita `postgres:latest` para no romper volúmenes por cambios de versión mayor; usa tags fijos (`postgres:16`, `postgres:16-alpine`, etc.).
- El forwarding de paridad se controla con `camunda.parity.forward-enabled` (actualmente en `true`).

## Propuesta para GitHub

### Descripción del repositorio

`Spring Boot backend with Camunda 7.24 REST parity under /api/v1/camunda, PostgreSQL support, Swagger/OpenAPI docs, and custom workflow endpoints.`

### Topics sugeridos

- `camunda`
- `camunda-platform`
- `camunda-7`
- `spring-boot`
- `java`
- `postgresql`
- `workflow`
- `bpmn`
- `rest-api`
- `openapi`
