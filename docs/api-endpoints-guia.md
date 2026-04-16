# Guía de endpoints REST

Este backend expone dos superficies documentadas en Swagger (SpringDoc OpenAPI 3), agrupadas en **API Camunda (paridad)** (`/api/v1/camunda/**`) y **APIs personalizadas** (`/api/v1/custom/**`). La paridad con la [REST oficial 7.24](https://docs.camunda.org/rest/camunda-bpm-platform/7.24/) es orientativa: rutas y DTOs siguen el mismo espíritu; los detalles de query params pueden diferir.

## Paridad — flujos típicos

### Desplegar y ejecutar un proceso (BPMN)

1. Despliega un BAR/ZIP con el motor (herramientas habituales) o usa los endpoints de despliegue si los habilitáis con multipart en el futuro.
2. `GET /api/v1/camunda/process-definition` — lista o filtra por `key`, `latestVersion=true`.
3. `POST /api/v1/camunda/process-definition/{id}/start` — cuerpo JSON opcional:
   ```json
   {
     "businessKey": "bk-1",
     "variables": {
       "customerId": { "value": "42", "type": "String" }
     }
   }
   ```
4. `GET /api/v1/camunda/task?processInstanceId=...` — tareas abiertas.
5. `POST /api/v1/camunda/task/{id}/complete` — completar con variables opcionales.

### Consultar historial

- Instancias terminadas: `GET /api/v1/camunda/history/process-instance?finished=true`
- Tareas históricas: `GET /api/v1/camunda/history/task-instance?processInstanceId=...`
- Actividades: `GET /api/v1/camunda/history/activity-instance?processInstanceId=...`
- Variables históricas: `GET /api/v1/camunda/history/variable-instance?processInstanceId=...`
- Detalle (auditoría): `GET /api/v1/camunda/history/detail?processInstanceId=...`

### DMN

- Definiciones: `GET /api/v1/camunda/decision-definition`
- Evaluación por clave: `POST /api/v1/camunda/decision-definition/evaluate?key=miDecision` con cuerpo JSON de variables de entrada (mapa plano `nombre -> valor`).

### Motor, métricas y esquema

- `GET /api/v1/camunda/engine` — nombre y versión del motor.
- `GET /api/v1/camunda/engine/schema-version` — propiedad `camunda.schema.version` en `ACT_GE_PROPERTY` (si existe).
- `GET /api/v1/camunda/metrics` — conteos de filas en tablas clave (opcional `?name=ACT_RU_TASK`).
- `GET /api/v1/camunda/schema-log` — filas de `ACT_GE_SCHEMA_LOG` (vía JPA).

### Jobs, incidencias, tareas externas

- Jobs: `GET /api/v1/camunda/job`, `GET /api/v1/camunda/job-definition`
- Incidencias: `GET /api/v1/camunda/incident`
- External tasks: `GET /api/v1/camunda/external-task`

### Identidad y autorización

- Usuarios: `GET /api/v1/camunda/user`, `GET /api/v1/camunda/user/{id}`
- Grupos: `GET /api/v1/camunda/group`
- Autorizaciones (resumen): `GET /api/v1/camunda/authorization`
- Filtros guardados: `GET /api/v1/camunda/filter`

### Mensajes y suscripciones

- Correlación de mensaje: `POST /api/v1/camunda/message` con `{ "messageName": "...", "businessKey": "...", "variables": { } }`
- Suscripciones: `GET /api/v1/camunda/event-subscription`

### CMMN, lotes, migración, limpieza, telemetría

- Casos (CMMN): `GET /api/v1/camunda/case-execution`
- Lotes: `GET /api/v1/camunda/batch`
- Migración / limpieza de historial: `GET /api/v1/camunda/migration`, `GET /api/v1/camunda/history/cleanup` (información; operaciones destructivas no se exponen por defecto).
- Telemetría: `GET /api/v1/camunda/telemetry` (placeholder).

## APIs personalizadas

- `GET /api/v1/custom/status` — comprobación simple del grupo **custom** (extensión de producto).

## Errores

Las respuestas de error siguen el modelo `ApiError`: `type`, `message`, `code` (opcional), alineado al estilo Camunda REST.

## Modelo de datos

Referencia de tablas: [camunda-schema.md](camunda-schema.md).
