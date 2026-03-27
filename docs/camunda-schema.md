# Esquema de base de datos Camunda 7.24.0 (PostgreSQL)

Este documento describe las tablas del motor de procesos, sus columnas, relaciones explicitas (FK), indices por defecto del DDL oficial y los indices adicionales definidos en `extra-indexes.sql`.

**Origen del DDL:** scripts `activiti.postgres.create.*.sql` empaquetados en `camunda-engine-7.24.0.jar`. Orden de aplicacion: `identity.sql` y luego `engine.sql`; opcionalmente `extra-indexes.sql`.

## Convenciones de nombres


| Prefijo    | Significado |
| ---------- | ----------- |
| `ACT_RE_*` | Repository  |
| `ACT_RU_*` | Runtime     |
| `ACT_HI_*` | History     |
| `ACT_ID_*` | Identity    |
| `ACT_GE_*` | General     |


## Indices adicionales (rendimiento)

Definidos en [extra-indexes.sql](../src/main/resources/db/camunda/7.24.0/postgres/extra-indexes.sql). Complementan consultas con millones de filas; cada indice suma coste de escritura. Validar con `EXPLAIN ANALYZE`.

## `ACT_GE_BYTEARRAY`

### Para que se usa

Almacenamiento de blobs (definiciones BPMN serializadas, variables grandes, stacks de error, adjuntos).

### Uso conjunto con otras tablas

- FK: ACT_FK_BYTEARR_DEPL: DEPLOYMENT_ID_ -> ACT_RE_DEPLOYMENT(ID_)
- Referenciada por: ACT_RU_VARIABLE (via ACT_FK_VAR_BYTEARRAY); ACT_RU_JOB (via ACT_FK_JOB_EXCEPTION); ACT_RU_EXT_TASK (via ACT_FK_EXT_TASK_ERROR_DETAILS).

### Columnas


| Columna (definicion en DDL)      |
| -------------------------------- |
| `ID_ varchar(64)`                |
| `REV_ integer`                   |
| `NAME_ varchar(255)`             |
| `DEPLOYMENT_ID_ varchar(64)`     |
| `BYTES_ bytea`                   |
| `GENERATED_ boolean`             |
| `TENANT_ID_ varchar(64)`         |
| `TYPE_ integer`                  |
| `CREATE_TIME_ timestamp`         |
| `ROOT_PROC_INST_ID_ varchar(64)` |
| `REMOVAL_TIME_ timestamp`        |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_BYTEAR_DEPL`** (`DEPLOYMENT_ID_`): Recursos binarios ligados a un despliegue (FK y listados por deployment).
- `**ACT_IDX_BYTEARRAY_ROOT_PI**` (`ROOT_PROC_INST_ID_`): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_BYTEARRAY_RM_TIME**` (`REMOVAL_TIME_`): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.
- `**ACT_IDX_BYTEARRAY_NAME**` (`NAME`_): Busqueda de entradas de byte array por nombre de recurso.

## `ACT_GE_PROPERTY`

### Para que se usa

Propiedades globales del motor (version de esquema, locks, contadores de id, etc.).

### Uso conjunto con otras tablas

- Ver columnas `*_ID_` para enlaces logicos con otras entidades del mismo dominio (GE/RE/RU/HI).

### Columnas


| Columna (definicion en DDL) |
| --------------------------- |
| `NAME_ varchar(64)`         |
| `VALUE_ varchar(300)`       |
| `REV_ integer`              |


### Indices por defecto (DDL oficial)

- (Ningun `CREATE INDEX` explicito en el DDL para esta tabla; la PK sigue indexada).

## `ACT_GE_SCHEMA_LOG`

### Para que se usa

Registro de versiones de esquema aplicadas al arrancar o migrar la base.

### Uso conjunto con otras tablas

- Ver columnas `*_ID_` para enlaces logicos con otras entidades del mismo dominio (GE/RE/RU/HI).

### Columnas


| Columna (definicion en DDL) |
| --------------------------- |
| `ID_ varchar(64)`           |
| `TIMESTAMP_ timestamp`      |
| `VERSION_ varchar(255)`     |


### Indices por defecto (DDL oficial)

- (Ningun `CREATE INDEX` explicito en el DDL para esta tabla; la PK sigue indexada).

## `ACT_HI_ACTINST`

### Para que se usa

Instancias de actividad historicas (una fila por token/actividad completada o abierta en historia).

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID_` (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)          |
| ------------------------------------ |
| `ID_ varchar(64) not null`           |
| `PARENT_ACT_INST_ID_ varchar(64)`    |
| `PROC_DEF_KEY_ varchar(255)`         |
| `PROC_DEF_ID_ varchar(64) not null`  |
| `ROOT_PROC_INST_ID_ varchar(64)`     |
| `PROC_INST_ID_ varchar(64) not null` |
| `EXECUTION_ID_ varchar(64) not null` |
| `ACT_ID_ varchar(255) not null`      |
| `TASK_ID_ varchar(64)`               |
| `CALL_PROC_INST_ID_ varchar(64)`     |
| `CALL_CASE_INST_ID_ varchar(64)`     |
| `ACT_NAME_ varchar(255)`             |
| `ACT_TYPE_ varchar(255) not null`    |
| `ASSIGNEE_ varchar(255)`             |
| `START_TIME_ timestamp not null`     |
| `END_TIME_ timestamp`                |
| `DURATION_ bigint`                   |
| `ACT_INST_STATE_ integer`            |
| `SEQUENCE_COUNTER_ bigint`           |
| `TENANT_ID_ varchar(64)`             |
| `REMOVAL_TIME_ timestamp`            |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_ACTINST_ROOT_PI**` (`ROOT_PROC_INST_ID_`): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_HI_ACT_INST_START_END**` (`START_TIME_, END_TIME_`): Optimiza consultas que filtran o ordenan por (START_TIME_, END_TIME_) en `ACT_HI_ACTINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_ACT_INST_END**` (`END_TIME`_): Optimiza consultas que filtran o ordenan por (END_TIME_) en `ACT_HI_ACTINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_ACT_INST_PROCINST**` (`PROC_INST_ID_, ACT_ID`_): Optimiza consultas que filtran o ordenan por (PROC_INST_ID_, ACT_ID_) en `ACT_HI_ACTINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_ACT_INST_COMP**` (`EXECUTION_ID_, ACT_ID_, END_TIME_, ID`_): Indice compuesto para compensacion / estado de instancia de actividad.
- `**ACT_IDX_HI_ACT_INST_STATS**` (`PROC_DEF_ID_, PROC_INST_ID_, ACT_ID_, END_TIME_, ACT_INST_STATE_`): Consultas de estadisticas de actividad (Cockpit/reports).
- `**ACT_IDX_HI_ACT_INST_TENANT_ID**` (`TENANT_ID_`): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_HI_ACT_INST_PROC_DEF_KEY**` (`PROC_DEF_KEY_`): Filtros por clave logica de definicion (sin version).
- `**ACT_IDX_HI_AI_PDEFID_END_TIME**` (`PROC_DEF_ID_, END_TIME_`): Informes por fin de actividad/proceso; instancias completadas.
- `**ACT_IDX_HI_ACT_INST_RM_TIME**` (`REMOVAL_TIME_`): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.

## `ACT_HI_ATTACHMENT`

### Para que se usa

Adjuntos historicos.

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID`_ (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)      |
| -------------------------------- |
| `ID_ varchar(64) not null`       |
| `REV_ integer`                   |
| `USER_ID_ varchar(255)`          |
| `NAME_ varchar(255)`             |
| `DESCRIPTION_ varchar(4000)`     |
| `TYPE_ varchar(255)`             |
| `TASK_ID_ varchar(64)`           |
| `ROOT_PROC_INST_ID_ varchar(64)` |
| `PROC_INST_ID_ varchar(64)`      |
| `URL_ varchar(4000)`             |
| `CONTENT_ID_ varchar(64)`        |
| `TENANT_ID_ varchar(64)`         |
| `CREATE_TIME_ timestamp`         |
| `REMOVAL_TIME_ timestamp`        |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_ATTACHMENT_CONTENT**` (`CONTENT_ID_`): Adjuntos por tarea o instancia.
- `**ACT_IDX_HI_ATTACHMENT_ROOT_PI**` (`ROOT_PROC_INST_ID_`): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_HI_ATTACHMENT_PROCINST**` (`PROC_INST_ID_`): Adjuntos por tarea o instancia.
- `**ACT_IDX_HI_ATTACHMENT_TASK**` (`TASK_ID_`): Adjuntos por tarea o instancia.
- `**ACT_IDX_HI_ATTACHMENT_RM_TIME**` (`REMOVAL_TIME_`): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.

## `ACT_HI_BATCH`

### Para que se usa

Historial de lotes completados.

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID`_ (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)       |
| --------------------------------- |
| `ID_ varchar(64) not null`        |
| `TYPE_ varchar(255)`              |
| `TOTAL_JOBS_ integer`             |
| `JOBS_PER_SEED_ integer`          |
| `INVOCATIONS_PER_JOB_ integer`    |
| `SEED_JOB_DEF_ID_ varchar(64)`    |
| `MONITOR_JOB_DEF_ID_ varchar(64)` |
| `BATCH_JOB_DEF_ID_ varchar(64)`   |
| `TENANT_ID_ varchar(64)`          |
| `CREATE_USER_ID_ varchar(255)`    |
| `START_TIME_ timestamp not null`  |
| `END_TIME_ timestamp`             |
| `REMOVAL_TIME_ timestamp`         |
| `EXEC_START_TIME_ timestamp`      |


### Indices por defecto (DDL oficial)

- `**ACT_HI_BAT_RM_TIME**` (`REMOVAL_TIME_`): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.

## `ACT_HI_CASEACTINST`

### Para que se usa

Actividades de caso historicas.

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID`_ (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)          |
| ------------------------------------ |
| `ID_ varchar(64) not null`           |
| `PARENT_ACT_INST_ID_ varchar(64)`    |
| `CASE_DEF_ID_ varchar(64) not null`  |
| `CASE_INST_ID_ varchar(64) not null` |
| `CASE_ACT_ID_ varchar(255) not null` |
| `TASK_ID_ varchar(64)`               |
| `CALL_PROC_INST_ID_ varchar(64)`     |
| `CALL_CASE_INST_ID_ varchar(64)`     |
| `CASE_ACT_NAME_ varchar(255)`        |
| `CASE_ACT_TYPE_ varchar(255)`        |
| `CREATE_TIME_ timestamp not null`    |
| `END_TIME_ timestamp`                |
| `DURATION_ bigint`                   |
| `STATE_ integer`                     |
| `REQUIRED_ boolean`                  |
| `TENANT_ID_ varchar(64)`             |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_CAS_A_I_CREATE**` (`CREATE_TIME_`): Optimiza consultas que filtran o ordenan por (CREATE_TIME_) en `ACT_HI_CASEACTINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_CAS_A_I_END**` (`END_TIME`_): Optimiza consultas que filtran o ordenan por (END_TIME_) en `ACT_HI_CASEACTINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_CAS_A_I_COMP**` (`CASE_ACT_ID_, END_TIME_, ID`_): Indice compuesto para compensacion / estado de instancia de actividad.
- `**ACT_IDX_HI_CAS_A_I_TENANT_ID**` (`TENANT_ID_`): Aislamiento multi-tenant en consultas de API y webapps.

## `ACT_HI_CASEINST`

### Para que se usa

Instancia de caso historica.

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID_` (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)              |
| ---------------------------------------- |
| `ID_ varchar(64) not null`               |
| `CASE_INST_ID_ varchar(64) not null`     |
| `BUSINESS_KEY_ varchar(255)`             |
| `CASE_DEF_ID_ varchar(64) not null`      |
| `CREATE_TIME_ timestamp not null`        |
| `CLOSE_TIME_ timestamp`                  |
| `DURATION_ bigint`                       |
| `STATE_ integer`                         |
| `CREATE_USER_ID_ varchar(255)`           |
| `SUPER_CASE_INSTANCE_ID_ varchar(64)`    |
| `SUPER_PROCESS_INSTANCE_ID_ varchar(64)` |
| `TENANT_ID_ varchar(64)`                 |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_CAS_I_CLOSE**` (`CLOSE_TIME_`): Optimiza consultas que filtran o ordenan por (CLOSE_TIME_) en `ACT_HI_CASEINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_CAS_I_BUSKEY**` (`BUSINESS_KEY`_): Optimiza consultas que filtran o ordenan por (BUSINESS_KEY_) en `ACT_HI_CASEINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_CAS_I_TENANT_ID**` (`TENANT_ID`_): Aislamiento multi-tenant en consultas de API y webapps.

## `ACT_HI_COMMENT`

### Para que se usa

Comentarios en tareas o instancias.

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID_` (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)       |
| --------------------------------- |
| `ID_ varchar(64) not null`        |
| `TYPE_ varchar(255)`              |
| `TIME_ timestamp not null`        |
| `USER_ID_ varchar(255)`           |
| `TASK_ID_ varchar(64)`            |
| `ROOT_PROC_INST_ID_ varchar(64)`  |
| `PROC_INST_ID_ varchar(64)`       |
| `ACTION_ varchar(255)`            |
| `MESSAGE_ varchar(4000)`          |
| `FULL_MSG_ bytea`                 |
| `TENANT_ID_ varchar(64)`          |
| `REMOVAL_TIME_ timestamp`         |
| `REV_ integer not null default 1` |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_COMMENT_TASK**` (`TASK_ID_`): Comentarios por tarea o proceso.
- `**ACT_IDX_HI_COMMENT_ROOT_PI**` (`ROOT_PROC_INST_ID_`): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_HI_COMMENT_PROCINST**` (`PROC_INST_ID_`): Comentarios por tarea o proceso.
- `**ACT_IDX_HI_COMMENT_RM_TIME**` (`REMOVAL_TIME_`): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.

## `ACT_HI_DEC_IN`

### Para que se usa

Entradas de decision historicas.

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID`_ (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)         |
| ----------------------------------- |
| `ID_ varchar(64) NOT NULL`          |
| `DEC_INST_ID_ varchar(64) NOT NULL` |
| `CLAUSE_ID_ varchar(64)`            |
| `CLAUSE_NAME_ varchar(255)`         |
| `VAR_TYPE_ varchar(100)`            |
| `BYTEARRAY_ID_ varchar(64)`         |
| `DOUBLE_ double precision`          |
| `LONG_ bigint`                      |
| `TEXT_ varchar(4000)`               |
| `TEXT2_ varchar(4000)`              |
| `TENANT_ID_ varchar(64)`            |
| `CREATE_TIME_ timestamp`            |
| `ROOT_PROC_INST_ID_ varchar(64)`    |
| `REMOVAL_TIME_ timestamp`           |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_DEC_IN_INST**` (`DEC_INST_ID_`): Optimiza consultas que filtran o ordenan por (DEC_INST_ID_) en `ACT_HI_DEC_IN` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_DEC_IN_CLAUSE**` (`DEC_INST_ID_, CLAUSE_ID`_): Optimiza consultas que filtran o ordenan por (DEC_INST_ID_, CLAUSE_ID_) en `ACT_HI_DEC_IN` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_DEC_IN_ROOT_PI**` (`ROOT_PROC_INST_ID`_): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_HI_DEC_IN_RM_TIME**` (`REMOVAL_TIME_`): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.

## `ACT_HI_DEC_OUT`

### Para que se usa

Salidas de decision historicas.

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID`_ (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)         |
| ----------------------------------- |
| `ID_ varchar(64) NOT NULL`          |
| `DEC_INST_ID_ varchar(64) NOT NULL` |
| `CLAUSE_ID_ varchar(64)`            |
| `CLAUSE_NAME_ varchar(255)`         |
| `RULE_ID_ varchar(64)`              |
| `RULE_ORDER_ integer`               |
| `VAR_NAME_ varchar(255)`            |
| `VAR_TYPE_ varchar(100)`            |
| `BYTEARRAY_ID_ varchar(64)`         |
| `DOUBLE_ double precision`          |
| `LONG_ bigint`                      |
| `TEXT_ varchar(4000)`               |
| `TEXT2_ varchar(4000)`              |
| `TENANT_ID_ varchar(64)`            |
| `CREATE_TIME_ timestamp`            |
| `ROOT_PROC_INST_ID_ varchar(64)`    |
| `REMOVAL_TIME_ timestamp`           |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_DEC_OUT_INST**` (`DEC_INST_ID_`): Optimiza consultas que filtran o ordenan por (DEC_INST_ID_) en `ACT_HI_DEC_OUT` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_DEC_OUT_RULE**` (`RULE_ORDER_, CLAUSE_ID`_): Optimiza consultas que filtran o ordenan por (RULE_ORDER_, CLAUSE_ID_) en `ACT_HI_DEC_OUT` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_DEC_OUT_ROOT_PI**` (`ROOT_PROC_INST_ID`_): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_HI_DEC_OUT_RM_TIME**` (`REMOVAL_TIME_`): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.

## `ACT_HI_DECINST`

### Para que se usa

Instancias de decision DMN evaluadas.

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID`_ (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)          |
| ------------------------------------ |
| `ID_ varchar(64) NOT NULL`           |
| `DEC_DEF_ID_ varchar(64) NOT NULL`   |
| `DEC_DEF_KEY_ varchar(255) NOT NULL` |
| `DEC_DEF_NAME_ varchar(255)`         |
| `PROC_DEF_KEY_ varchar(255)`         |
| `PROC_DEF_ID_ varchar(64)`           |
| `PROC_INST_ID_ varchar(64)`          |
| `CASE_DEF_KEY_ varchar(255)`         |
| `CASE_DEF_ID_ varchar(64)`           |
| `CASE_INST_ID_ varchar(64)`          |
| `ACT_INST_ID_ varchar(64)`           |
| `ACT_ID_ varchar(255)`               |
| `EVAL_TIME_ timestamp not null`      |
| `REMOVAL_TIME_ timestamp`            |
| `COLLECT_VALUE_ double precision`    |
| `USER_ID_ varchar(255)`              |
| `ROOT_DEC_INST_ID_ varchar(64)`      |
| `ROOT_PROC_INST_ID_ varchar(64)`     |
| `DEC_REQ_ID_ varchar(64)`            |
| `DEC_REQ_KEY_ varchar(255)`          |
| `TENANT_ID_ varchar(64)`             |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_DEC_INST_ID**` (`DEC_DEF_ID_`): Optimiza consultas que filtran o ordenan por (DEC_DEF_ID_) en `ACT_HI_DECINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_DEC_INST_KEY**` (`DEC_DEF_KEY`_): Optimiza consultas que filtran o ordenan por (DEC_DEF_KEY_) en `ACT_HI_DECINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_DEC_INST_PI**` (`PROC_INST_ID`_): Optimiza consultas que filtran o ordenan por (PROC_INST_ID_) en `ACT_HI_DECINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_DEC_INST_CI**` (`CASE_INST_ID`_): Optimiza consultas que filtran o ordenan por (CASE_INST_ID_) en `ACT_HI_DECINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_DEC_INST_ACT**` (`ACT_ID`_): Optimiza consultas que filtran o ordenan por (ACT_ID_) en `ACT_HI_DECINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_DEC_INST_ACT_INST**` (`ACT_INST_ID`_): Optimiza consultas que filtran o ordenan por (ACT_INST_ID_) en `ACT_HI_DECINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_DEC_INST_TIME**` (`EVAL_TIME`_): Optimiza consultas que filtran o ordenan por (EVAL_TIME_) en `ACT_HI_DECINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_DEC_INST_TENANT_ID**` (`TENANT_ID`_): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_HI_DEC_INST_ROOT_ID**` (`ROOT_DEC_INST_ID_`): Optimiza consultas que filtran o ordenan por (ROOT_DEC_INST_ID_) en `ACT_HI_DECINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_DEC_INST_REQ_ID**` (`DEC_REQ_ID`_): Optimiza consultas que filtran o ordenan por (DEC_REQ_ID_) en `ACT_HI_DECINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_DEC_INST_REQ_KEY**` (`DEC_REQ_KEY`_): Optimiza consultas que filtran o ordenan por (DEC_REQ_KEY_) en `ACT_HI_DECINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_DEC_INST_ROOT_PI**` (`ROOT_PROC_INST_ID`_): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_HI_DEC_INST_RM_TIME**` (`REMOVAL_TIME_`): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.

## `ACT_HI_DETAIL`

### Para que se usa

Detalle de actualizacion de variables (audit trail fino).

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID`_ (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)      |
| -------------------------------- |
| `ID_ varchar(64) not null`       |
| `TYPE_ varchar(255) not null`    |
| `PROC_DEF_KEY_ varchar(255)`     |
| `PROC_DEF_ID_ varchar(64)`       |
| `ROOT_PROC_INST_ID_ varchar(64)` |
| `PROC_INST_ID_ varchar(64)`      |
| `EXECUTION_ID_ varchar(64)`      |
| `CASE_DEF_KEY_ varchar(255)`     |
| `CASE_DEF_ID_ varchar(64)`       |
| `CASE_INST_ID_ varchar(64)`      |
| `CASE_EXECUTION_ID_ varchar(64)` |
| `TASK_ID_ varchar(64)`           |
| `ACT_INST_ID_ varchar(64)`       |
| `VAR_INST_ID_ varchar(64)`       |
| `NAME_ varchar(255) not null`    |
| `VAR_TYPE_ varchar(64)`          |
| `REV_ integer`                   |
| `TIME_ timestamp not null`       |
| `BYTEARRAY_ID_ varchar(64)`      |
| `DOUBLE_ double precision`       |
| `LONG_ bigint`                   |
| `TEXT_ varchar(4000)`            |
| `TEXT2_ varchar(4000)`           |
| `SEQUENCE_COUNTER_ bigint`       |
| `TENANT_ID_ varchar(64)`         |
| `OPERATION_ID_ varchar(64)`      |
| `REMOVAL_TIME_ timestamp`        |
| `INITIAL_ boolean`               |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_DETAIL_ROOT_PI**` (`ROOT_PROC_INST_ID_`): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_HI_DETAIL_PROC_INST**` (`PROC_INST_ID_`): Detalle fino de cambios (variables, propiedades).
- `**ACT_IDX_HI_DETAIL_ACT_INST**` (`ACT_INST_ID_`): Detalle fino de cambios (variables, propiedades).
- `**ACT_IDX_HI_DETAIL_CASE_INST**` (`CASE_INST_ID_`): Detalle fino de cambios (variables, propiedades).
- `**ACT_IDX_HI_DETAIL_CASE_EXEC**` (`CASE_EXECUTION_ID_`): Detalle fino de cambios (variables, propiedades).
- `**ACT_IDX_HI_DETAIL_TIME**` (`TIME_`): Detalle fino de cambios (variables, propiedades).
- `**ACT_IDX_HI_DETAIL_NAME**` (`NAME_`): Detalle fino de cambios (variables, propiedades).
- `**ACT_IDX_HI_DETAIL_TASK_ID**` (`TASK_ID_`): Acceso a variables, identity links o comentarios ligados a una tarea.
- `**ACT_IDX_HI_DETAIL_TENANT_ID**` (`TENANT_ID_`): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_HI_DETAIL_PROC_DEF_KEY**` (`PROC_DEF_KEY_`): Filtros por clave logica de definicion (sin version).
- `**ACT_IDX_HI_DETAIL_BYTEAR**` (`BYTEARRAY_ID_`): Detalle fino de cambios (variables, propiedades).
- `**ACT_IDX_HI_DETAIL_RM_TIME**` (`REMOVAL_TIME_`): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.
- `**ACT_IDX_HI_DETAIL_TASK_BYTEAR**` (`BYTEARRAY_ID_, TASK_ID`_): Detalle fino de cambios (variables, propiedades).
- `**ACT_IDX_HI_DETAIL_VAR_INST_ID**` (`VAR_INST_ID_`): Enlace al registro de variable historica en detalles.

## `ACT_HI_EXT_TASK_LOG`

### Para que se usa

Historial de ciclo de vida de external tasks.

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID_` (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)           |
| ------------------------------------- |
| `ID_ varchar(64) not null`            |
| `TIMESTAMP_ timestamp not null`       |
| `EXT_TASK_ID_ varchar(64) not null`   |
| `RETRIES_ integer`                    |
| `TOPIC_NAME_ varchar(255)`            |
| `WORKER_ID_ varchar(255)`             |
| `PRIORITY_ bigint not null default 0` |
| `ERROR_MSG_ varchar(4000)`            |
| `ERROR_DETAILS_ID_ varchar(64)`       |
| `ACT_ID_ varchar(255)`                |
| `ACT_INST_ID_ varchar(64)`            |
| `EXECUTION_ID_ varchar(64)`           |
| `PROC_INST_ID_ varchar(64)`           |
| `ROOT_PROC_INST_ID_ varchar(64)`      |
| `PROC_DEF_ID_ varchar(64)`            |
| `PROC_DEF_KEY_ varchar(255)`          |
| `TENANT_ID_ varchar(64)`              |
| `STATE_ integer`                      |
| `REMOVAL_TIME_ timestamp`             |


### Indices por defecto (DDL oficial)

- `**ACT_HI_EXT_TASK_LOG_ROOT_PI**` (`ROOT_PROC_INST_ID_`): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_HI_EXT_TASK_LOG_PROCINST**` (`PROC_INST_ID_`): Historial de eventos de external task.
- `**ACT_HI_EXT_TASK_LOG_PROCDEF**` (`PROC_DEF_ID_`): Historial de eventos de external task.
- `**ACT_HI_EXT_TASK_LOG_PROC_DEF_KEY**` (`PROC_DEF_KEY_`): Filtros por clave logica de definicion (sin version).
- `**ACT_HI_EXT_TASK_LOG_TENANT_ID**` (`TENANT_ID_`): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_HI_EXTTASKLOG_ERRORDET**` (`ERROR_DETAILS_ID_`): Optimiza consultas que filtran o ordenan por (ERROR_DETAILS_ID_) en `ACT_HI_EXT_TASK_LOG` (motor, APIs REST o webapps).
- `**ACT_HI_EXT_TASK_LOG_RM_TIME**` (`REMOVAL_TIME`_): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.

## `ACT_HI_IDENTITYLINK`

### Para que se usa

Historial de identity links en tareas/procesos.

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID`_ (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)      |
| -------------------------------- |
| `ID_ varchar(64) not null`       |
| `TIMESTAMP_ timestamp not null`  |
| `TYPE_ varchar(255)`             |
| `USER_ID_ varchar(255)`          |
| `GROUP_ID_ varchar(255)`         |
| `TASK_ID_ varchar(64)`           |
| `ROOT_PROC_INST_ID_ varchar(64)` |
| `PROC_DEF_ID_ varchar(64)`       |
| `OPERATION_TYPE_ varchar(64)`    |
| `ASSIGNER_ID_ varchar(64)`       |
| `PROC_DEF_KEY_ varchar(255)`     |
| `TENANT_ID_ varchar(64)`         |
| `REMOVAL_TIME_ timestamp`        |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_IDENT_LNK_ROOT_PI**` (`ROOT_PROC_INST_ID_`): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_HI_IDENT_LNK_USER**` (`USER_ID_`): Identity links historicos (candidatos, participantes).
- `**ACT_IDX_HI_IDENT_LNK_GROUP**` (`GROUP_ID_`): Identity links historicos (candidatos, participantes).
- `**ACT_IDX_HI_IDENT_LNK_TENANT_ID**` (`TENANT_ID_`): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_HI_IDENT_LNK_PROC_DEF_KEY**` (`PROC_DEF_KEY_`): Filtros por clave logica de definicion (sin version).
- `**ACT_IDX_HI_IDENT_LINK_TASK**` (`TASK_ID_`): Optimiza consultas que filtran o ordenan por (TASK_ID_) en `ACT_HI_IDENTITYLINK` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_IDENT_LINK_RM_TIME**` (`REMOVAL_TIME`_): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.
- `**ACT_IDX_HI_IDENT_LNK_TIMESTAMP**` (`TIMESTAMP`_): Orden temporal de entradas de log (operacion, identity link historico).

## `ACT_HI_INCIDENT`

### Para que se usa

Incidentes historicos.

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID_` (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)            |
| -------------------------------------- |
| `ID_ varchar(64) not null`             |
| `PROC_DEF_KEY_ varchar(255)`           |
| `PROC_DEF_ID_ varchar(64)`             |
| `ROOT_PROC_INST_ID_ varchar(64)`       |
| `PROC_INST_ID_ varchar(64)`            |
| `EXECUTION_ID_ varchar(64)`            |
| `CREATE_TIME_ timestamp not null`      |
| `END_TIME_ timestamp`                  |
| `INCIDENT_MSG_ varchar(4000)`          |
| `INCIDENT_TYPE_ varchar(255) not null` |
| `ACTIVITY_ID_ varchar(255)`            |
| `FAILED_ACTIVITY_ID_ varchar(255)`     |
| `CAUSE_INCIDENT_ID_ varchar(64)`       |
| `ROOT_CAUSE_INCIDENT_ID_ varchar(64)`  |
| `CONFIGURATION_ varchar(255)`          |
| `HISTORY_CONFIGURATION_ varchar(255)`  |
| `INCIDENT_STATE_ integer`              |
| `TENANT_ID_ varchar(64)`               |
| `JOB_DEF_ID_ varchar(64)`              |
| `ANNOTATION_ varchar(4000)`            |
| `REMOVAL_TIME_ timestamp`              |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_INCIDENT_TENANT_ID**` (`TENANT_ID_`): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_HI_INCIDENT_PROC_DEF_KEY**` (`PROC_DEF_KEY_`): Filtros por clave logica de definicion (sin version).
- `**ACT_IDX_HI_INCIDENT_ROOT_PI**` (`ROOT_PROC_INST_ID_`): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_HI_INCIDENT_PROCINST**` (`PROC_INST_ID_`): Incidentes runtime o historicos por proceso/ejecucion.
- `**ACT_IDX_HI_INCIDENT_RM_TIME**` (`REMOVAL_TIME_`): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.
- `**ACT_IDX_HI_INCIDENT_CREATE_TIME**` (`CREATE_TIME`_): Ordenacion o ventanas temporales por creacion.
- `**ACT_IDX_HI_INCIDENT_END_TIME**` (`END_TIME_`): Informes por fin de actividad/proceso; instancias completadas.

## `ACT_HI_JOB_LOG`

### Para que se usa

Historial de ejecucion de jobs (exito/fallo, mensajes).

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID_` (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)               |
| ----------------------------------------- |
| `ID_ varchar(64) not null`                |
| `TIMESTAMP_ timestamp not null`           |
| `JOB_ID_ varchar(64) not null`            |
| `JOB_DUEDATE_ timestamp`                  |
| `JOB_RETRIES_ integer`                    |
| `JOB_PRIORITY_ bigint NOT NULL DEFAULT 0` |
| `JOB_EXCEPTION_MSG_ varchar(4000)`        |
| `JOB_EXCEPTION_STACK_ID_ varchar(64)`     |
| `JOB_STATE_ integer`                      |
| `JOB_DEF_ID_ varchar(64)`                 |
| `JOB_DEF_TYPE_ varchar(255)`              |
| `JOB_DEF_CONFIGURATION_ varchar(255)`     |
| `ACT_ID_ varchar(255)`                    |
| `FAILED_ACT_ID_ varchar(255)`             |
| `EXECUTION_ID_ varchar(64)`               |
| `ROOT_PROC_INST_ID_ varchar(64)`          |
| `PROCESS_INSTANCE_ID_ varchar(64)`        |
| `PROCESS_DEF_ID_ varchar(64)`             |
| `PROCESS_DEF_KEY_ varchar(255)`           |
| `DEPLOYMENT_ID_ varchar(64)`              |
| `SEQUENCE_COUNTER_ bigint`                |
| `TENANT_ID_ varchar(64)`                  |
| `HOSTNAME_ varchar(255)`                  |
| `REMOVAL_TIME_ timestamp`                 |
| `BATCH_ID_ varchar(64)`                   |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_JOB_LOG_ROOT_PI**` (`ROOT_PROC_INST_ID_`): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_HI_JOB_LOG_PROCINST**` (`PROCESS_INSTANCE_ID_`): Historial de intentos de job.
- `**ACT_IDX_HI_JOB_LOG_PROCDEF**` (`PROCESS_DEF_ID_`): Historial de intentos de job.
- `**ACT_IDX_HI_JOB_LOG_TENANT_ID**` (`TENANT_ID_`): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_HI_JOB_LOG_JOB_DEF_ID**` (`JOB_DEF_ID_`): Jobs o logs asociados a una definicion de job concreta.
- `**ACT_IDX_HI_JOB_LOG_PROC_DEF_KEY**` (`PROCESS_DEF_KEY_`): Filtros por clave logica de definicion (sin version).
- `**ACT_IDX_HI_JOB_LOG_EX_STACK**` (`JOB_EXCEPTION_STACK_ID_`): Traza de excepcion persistida en byte array.
- `**ACT_IDX_HI_JOB_LOG_RM_TIME**` (`REMOVAL_TIME_`): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.
- `**ACT_IDX_HI_JOB_LOG_JOB_CONF**` (`JOB_DEF_CONFIGURATION`_): Historial de intentos de job.

## `ACT_HI_OP_LOG`

### Para que se usa

Log de operaciones de usuario (cockpit/tasklist) para auditoria.

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID_` (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)      |
| -------------------------------- |
| `ID_ varchar(64) not null`       |
| `DEPLOYMENT_ID_ varchar(64)`     |
| `PROC_DEF_ID_ varchar(64)`       |
| `PROC_DEF_KEY_ varchar(255)`     |
| `ROOT_PROC_INST_ID_ varchar(64)` |
| `PROC_INST_ID_ varchar(64)`      |
| `EXECUTION_ID_ varchar(64)`      |
| `CASE_DEF_ID_ varchar(64)`       |
| `CASE_INST_ID_ varchar(64)`      |
| `CASE_EXECUTION_ID_ varchar(64)` |
| `TASK_ID_ varchar(64)`           |
| `JOB_ID_ varchar(64)`            |
| `JOB_DEF_ID_ varchar(64)`        |
| `BATCH_ID_ varchar(64)`          |
| `USER_ID_ varchar(255)`          |
| `TIMESTAMP_ timestamp not null`  |
| `OPERATION_TYPE_ varchar(64)`    |
| `OPERATION_ID_ varchar(64)`      |
| `ENTITY_TYPE_ varchar(30)`       |
| `PROPERTY_ varchar(64)`          |
| `ORG_VALUE_ varchar(4000)`       |
| `NEW_VALUE_ varchar(4000)`       |
| `TENANT_ID_ varchar(64)`         |
| `REMOVAL_TIME_ timestamp`        |
| `CATEGORY_ varchar(64)`          |
| `EXTERNAL_TASK_ID_ varchar(64)`  |
| `ANNOTATION_ varchar(4000)`      |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_OP_LOG_ROOT_PI**` (`ROOT_PROC_INST_ID_`): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_HI_OP_LOG_PROCINST**` (`PROC_INST_ID_`): Log de operaciones de usuario.
- `**ACT_IDX_HI_OP_LOG_PROCDEF**` (`PROC_DEF_ID_`): Log de operaciones de usuario.
- `**ACT_IDX_HI_OP_LOG_TASK**` (`TASK_ID_`): Log de operaciones de usuario.
- `**ACT_IDX_HI_OP_LOG_RM_TIME**` (`REMOVAL_TIME_`): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.
- `**ACT_IDX_HI_OP_LOG_TIMESTAMP**` (`TIMESTAMP`_): Orden temporal de entradas de log (operacion, identity link historico).
- `**ACT_IDX_HI_OP_LOG_USER_ID**` (`USER_ID_`): Resolucion por usuario (identity links, op log).
- `**ACT_IDX_HI_OP_LOG_OP_TYPE**` (`OPERATION_TYPE_`): Log de operaciones de usuario.
- `**ACT_IDX_HI_OP_LOG_ENTITY_TYPE**` (`ENTITY_TYPE_`): Filtrado de op log por entidad afectada.

## `ACT_HI_PROCINST`

### Para que se usa

Instancia de proceso historica: inicio, fin, business key, definicion, removal time.

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID_` (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)              |
| ---------------------------------------- |
| `ID_ varchar(64) not null`               |
| `PROC_INST_ID_ varchar(64) not null`     |
| `BUSINESS_KEY_ varchar(255)`             |
| `PROC_DEF_KEY_ varchar(255)`             |
| `PROC_DEF_ID_ varchar(64) not null`      |
| `START_TIME_ timestamp not null`         |
| `END_TIME_ timestamp`                    |
| `REMOVAL_TIME_ timestamp`                |
| `DURATION_ bigint`                       |
| `START_USER_ID_ varchar(255)`            |
| `START_ACT_ID_ varchar(255)`             |
| `END_ACT_ID_ varchar(255)`               |
| `SUPER_PROCESS_INSTANCE_ID_ varchar(64)` |
| `ROOT_PROC_INST_ID_ varchar(64)`         |
| `SUPER_CASE_INSTANCE_ID_ varchar(64)`    |
| `CASE_INST_ID_ varchar(64)`              |
| `DELETE_REASON_ varchar(4000)`           |
| `TENANT_ID_ varchar(64)`                 |
| `STATE_ varchar(255)`                    |
| `RESTARTED_PROC_INST_ID_ varchar(64)`    |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_PRO_INST_END**` (`END_TIME_`): Optimiza consultas que filtran o ordenan por (END_TIME_) en `ACT_HI_PROCINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_PRO_I_BUSKEY**` (`BUSINESS_KEY`_): Optimiza consultas que filtran o ordenan por (BUSINESS_KEY_) en `ACT_HI_PROCINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_PRO_INST_TENANT_ID**` (`TENANT_ID`_): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_HI_PRO_INST_PROC_DEF_KEY**` (`PROC_DEF_KEY_`): Filtros por clave logica de definicion (sin version).
- `**ACT_IDX_HI_PRO_INST_PROC_TIME**` (`START_TIME_, END_TIME_`): Optimiza consultas que filtran o ordenan por (START_TIME_, END_TIME_) en `ACT_HI_PROCINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_PI_PDEFID_END_TIME**` (`PROC_DEF_ID_, END_TIME`_): Informes por fin de actividad/proceso; instancias completadas.
- `**ACT_IDX_HI_PRO_INST_ROOT_PI**` (`ROOT_PROC_INST_ID_`): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_HI_PRO_INST_RM_TIME**` (`REMOVAL_TIME_`): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.
- `**ACT_IDX_HI_PRO_RST_PRO_INST_ID**` (`RESTARTED_PROC_INST_ID`_): Optimiza consultas que filtran o ordenan por (RESTARTED_PROC_INST_ID_) en `ACT_HI_PROCINST` (motor, APIs REST o webapps).

## `ACT_HI_TASKINST`

### Para que se usa

Tareas historicas (abiertas y cerradas segun nivel de historia).

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID`_ (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)      |
| -------------------------------- |
| `ID_ varchar(64) not null`       |
| `TASK_DEF_KEY_ varchar(255)`     |
| `PROC_DEF_KEY_ varchar(255)`     |
| `PROC_DEF_ID_ varchar(64)`       |
| `ROOT_PROC_INST_ID_ varchar(64)` |
| `PROC_INST_ID_ varchar(64)`      |
| `EXECUTION_ID_ varchar(64)`      |
| `CASE_DEF_KEY_ varchar(255)`     |
| `CASE_DEF_ID_ varchar(64)`       |
| `CASE_INST_ID_ varchar(64)`      |
| `CASE_EXECUTION_ID_ varchar(64)` |
| `ACT_INST_ID_ varchar(64)`       |
| `NAME_ varchar(255)`             |
| `PARENT_TASK_ID_ varchar(64)`    |
| `DESCRIPTION_ varchar(4000)`     |
| `OWNER_ varchar(255)`            |
| `ASSIGNEE_ varchar(255)`         |
| `START_TIME_ timestamp not null` |
| `END_TIME_ timestamp`            |
| `DURATION_ bigint`               |
| `DELETE_REASON_ varchar(4000)`   |
| `PRIORITY_ integer`              |
| `DUE_DATE_ timestamp`            |
| `FOLLOW_UP_DATE_ timestamp`      |
| `TENANT_ID_ varchar(64)`         |
| `REMOVAL_TIME_ timestamp`        |
| `TASK_STATE_ varchar(64)`        |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_TASKINST_ROOT_PI**` (`ROOT_PROC_INST_ID_`): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_HI_TASK_INST_TENANT_ID**` (`TENANT_ID_`): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_HI_TASK_INST_PROC_DEF_KEY**` (`PROC_DEF_KEY_`): Filtros por clave logica de definicion (sin version).
- `**ACT_IDX_HI_TASKINST_PROCINST**` (`PROC_INST_ID_`): Optimiza consultas que filtran o ordenan por (PROC_INST_ID_) en `ACT_HI_TASKINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_TASKINSTID_PROCINST**` (`ID_,PROC_INST_ID`_): Optimiza consultas que filtran o ordenan por (ID_,PROC_INST_ID_) en `ACT_HI_TASKINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_TASK_INST_RM_TIME**` (`REMOVAL_TIME`_): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.
- `**ACT_IDX_HI_TASK_INST_START**` (`START_TIME`_): Optimiza consultas que filtran o ordenan por (START_TIME_) en `ACT_HI_TASKINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_TASK_INST_END**` (`END_TIME`_): Optimiza consultas que filtran o ordenan por (END_TIME_) en `ACT_HI_TASKINST` (motor, APIs REST o webapps).

## `ACT_HI_VARINST`

### Para que se usa

Variables historicas (valor final o serie de cambios segun configuracion).

### Uso conjunto con otras tablas

- Historial: relaciones logicas con `ACT_HI_PROCINST`, `ACT_RU_*` / `ACT_RE_*` por columnas `*_ID`_ (sin FK en el DDL).

### Columnas


| Columna (definicion en DDL)      |
| -------------------------------- |
| `ID_ varchar(64) not null`       |
| `PROC_DEF_KEY_ varchar(255)`     |
| `PROC_DEF_ID_ varchar(64)`       |
| `ROOT_PROC_INST_ID_ varchar(64)` |
| `PROC_INST_ID_ varchar(64)`      |
| `EXECUTION_ID_ varchar(64)`      |
| `ACT_INST_ID_ varchar(64)`       |
| `CASE_DEF_KEY_ varchar(255)`     |
| `CASE_DEF_ID_ varchar(64)`       |
| `CASE_INST_ID_ varchar(64)`      |
| `CASE_EXECUTION_ID_ varchar(64)` |
| `TASK_ID_ varchar(64)`           |
| `NAME_ varchar(255) not null`    |
| `VAR_TYPE_ varchar(100)`         |
| `CREATE_TIME_ timestamp`         |
| `REV_ integer`                   |
| `BYTEARRAY_ID_ varchar(64)`      |
| `DOUBLE_ double precision`       |
| `LONG_ bigint`                   |
| `TEXT_ varchar(4000)`            |
| `TEXT2_ varchar(4000)`           |
| `TENANT_ID_ varchar(64)`         |
| `STATE_ varchar(20)`             |
| `REMOVAL_TIME_ timestamp`        |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_HI_VARINST_ROOT_PI**` (`ROOT_PROC_INST_ID_`): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_HI_PROCVAR_PROC_INST**` (`PROC_INST_ID_`): Optimiza consultas que filtran o ordenan por (PROC_INST_ID_) en `ACT_HI_VARINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_PROCVAR_NAME_TYPE**` (`NAME_, VAR_TYPE`_): Busqueda de variables historicas por nombre y tipo dentro de instancia.
- `**ACT_IDX_HI_CASEVAR_CASE_INST**` (`CASE_INST_ID_`): Optimiza consultas que filtran o ordenan por (CASE_INST_ID_) en `ACT_HI_VARINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_VAR_INST_TENANT_ID**` (`TENANT_ID`_): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_HI_VAR_INST_PROC_DEF_KEY**` (`PROC_DEF_KEY_`): Filtros por clave logica de definicion (sin version).
- `**ACT_IDX_HI_VARINST_BYTEAR**` (`BYTEARRAY_ID_`): Optimiza consultas que filtran o ordenan por (BYTEARRAY_ID_) en `ACT_HI_VARINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_VARINST_RM_TIME**` (`REMOVAL_TIME`_): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.
- `**ACT_IDX_HI_VAR_PI_NAME_TYPE**` (`PROC_INST_ID_, NAME_, VAR_TYPE`_): Clave compuesta instancia + nombre + tipo para variables historicas.
- `**ACT_IDX_HI_VARINST_NAME**` (`NAME_`): Optimiza consultas que filtran o ordenan por (NAME_) en `ACT_HI_VARINST` (motor, APIs REST o webapps).
- `**ACT_IDX_HI_VARINST_ACT_INST_ID**` (`ACT_INST_ID`_): Correlacion con instancia de actividad historica.

## `ACT_ID_GROUP`

### Para que se usa

Grupos de usuarios del identity service embebido.

### Uso conjunto con otras tablas

- Referenciada por: ACT_ID_MEMBERSHIP (via ACT_FK_MEMB_GROUP); ACT_ID_TENANT_MEMBER (via ACT_FK_TENANT_MEMB_GROUP).

### Columnas


| Columna (definicion en DDL) |
| --------------------------- |
| `ID_ varchar(64)`           |
| `REV_ integer`              |
| `NAME_ varchar(255)`        |
| `TYPE_ varchar(255)`        |


### Indices por defecto (DDL oficial)

- (Ningun `CREATE INDEX` explicito en el DDL para esta tabla; la PK sigue indexada).

## `ACT_ID_INFO`

### Para que se usa

Metadatos clave-valor por usuario (p. ej. preferencias).

### Uso conjunto con otras tablas

- Ver columnas `*_ID`_ para enlaces logicos con otras entidades del mismo dominio (GE/RE/RU/HI).

### Columnas


| Columna (definicion en DDL) |
| --------------------------- |
| `ID_ varchar(64)`           |
| `REV_ integer`              |
| `USER_ID_ varchar(64)`      |
| `TYPE_ varchar(64)`         |
| `KEY_ varchar(255)`         |
| `VALUE_ varchar(255)`       |
| `PASSWORD_ bytea`           |
| `PARENT_ID_ varchar(255)`   |


### Indices por defecto (DDL oficial)

- (Ningun `CREATE INDEX` explicito en el DDL para esta tabla; la PK sigue indexada).

## `ACT_ID_MEMBERSHIP`

### Para que se usa

Relacion usuario-grupo.

### Uso conjunto con otras tablas

- FK: ACT_FK_MEMB_GROUP: GROUP_ID_ -> ACT_ID_GROUP(ID_)
- FK: ACT_FK_MEMB_USER: USER_ID_ -> ACT_ID_USER(ID_)

### Columnas


| Columna (definicion en DDL) |
| --------------------------- |
| `USER_ID_ varchar(64)`      |
| `GROUP_ID_ varchar(64)`     |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_MEMB_GROUP**` (`GROUP_ID`_): Optimiza consultas que filtran o ordenan por (GROUP_ID_) en `ACT_ID_MEMBERSHIP` (motor, APIs REST o webapps).
- `**ACT_IDX_MEMB_USER**` (`USER_ID`_): Optimiza consultas que filtran o ordenan por (USER_ID_) en `ACT_ID_MEMBERSHIP` (motor, APIs REST o webapps).

## `ACT_ID_TENANT`

### Para que se usa

Tenants del identity service.

### Uso conjunto con otras tablas

- Referenciada por: ACT_ID_TENANT_MEMBER (via ACT_FK_TENANT_MEMB).

### Columnas


| Columna (definicion en DDL) |
| --------------------------- |
| `ID_ varchar(64)`           |
| `REV_ integer`              |
| `NAME_ varchar(255)`        |


### Indices por defecto (DDL oficial)

- (Ningun `CREATE INDEX` explicito en el DDL para esta tabla; la PK sigue indexada).

## `ACT_ID_TENANT_MEMBER`

### Para que se usa

Miembros de tenant (usuarios o grupos).

### Uso conjunto con otras tablas

- FK: ACT_FK_TENANT_MEMB: TENANT_ID_ -> ACT_ID_TENANT(ID_)
- FK: ACT_FK_TENANT_MEMB_USER: USER_ID_ -> ACT_ID_USER(ID_)
- FK: ACT_FK_TENANT_MEMB_GROUP: GROUP_ID_ -> ACT_ID_GROUP(ID_)

### Columnas


| Columna (definicion en DDL)       |
| --------------------------------- |
| `ID_ varchar(64) not null`        |
| `TENANT_ID_ varchar(64) not null` |
| `USER_ID_ varchar(64)`            |
| `GROUP_ID_ varchar(64)`           |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_TENANT_MEMB**` (`TENANT_ID`_): Optimiza consultas que filtran o ordenan por (TENANT_ID_) en `ACT_ID_TENANT_MEMBER` (motor, APIs REST o webapps).
- `**ACT_IDX_TENANT_MEMB_USER**` (`USER_ID`_): Optimiza consultas que filtran o ordenan por (USER_ID_) en `ACT_ID_TENANT_MEMBER` (motor, APIs REST o webapps).
- `**ACT_IDX_TENANT_MEMB_GROUP**` (`GROUP_ID`_): Optimiza consultas que filtran o ordenan por (GROUP_ID_) en `ACT_ID_TENANT_MEMBER` (motor, APIs REST o webapps).

## `ACT_ID_USER`

### Para que se usa

Usuarios (credenciales si se usa el proveedor embebido).

### Uso conjunto con otras tablas

- Referenciada por: ACT_ID_MEMBERSHIP (via ACT_FK_MEMB_USER); ACT_ID_TENANT_MEMBER (via ACT_FK_TENANT_MEMB_USER).

### Columnas


| Columna (definicion en DDL) |
| --------------------------- |
| `ID_ varchar(64)`           |
| `REV_ integer`              |
| `FIRST_ varchar(255)`       |
| `LAST_ varchar(255)`        |
| `EMAIL_ varchar(255)`       |
| `PWD_ varchar(255)`         |
| `SALT_ varchar(255)`        |
| `LOCK_EXP_TIME_ timestamp`  |
| `ATTEMPTS_ integer`         |
| `PICTURE_ID_ varchar(64)`   |


### Indices por defecto (DDL oficial)

- (Ningun `CREATE INDEX` explicito en el DDL para esta tabla; la PK sigue indexada).

## `ACT_RE_CAMFORMDEF`

### Para que se usa

Definiciones de formularios embebidos asociados al despliegue.

### Uso conjunto con otras tablas

- Ver columnas `*_ID`_ para enlaces logicos con otras entidades del mismo dominio (GE/RE/RU/HI).

### Columnas


| Columna (definicion en DDL)    |
| ------------------------------ |
| `ID_ varchar(64) NOT NULL`     |
| `REV_ integer`                 |
| `KEY_ varchar(255) NOT NULL`   |
| `VERSION_ integer NOT NULL`    |
| `DEPLOYMENT_ID_ varchar(64)`   |
| `RESOURCE_NAME_ varchar(4000)` |
| `TENANT_ID_ varchar(64)`       |


### Indices por defecto (DDL oficial)

- (Ningun `CREATE INDEX` explicito en el DDL para esta tabla; la PK sigue indexada).

## `ACT_RE_CASE_DEF`

### Para que se usa

Definicion de caso CMMN versionada.

### Uso conjunto con otras tablas

- Referenciada por: ACT_RU_CASE_EXECUTION (via ACT_FK_CASE_EXE_CASE_DEF); ACT_RU_TASK (via ACT_FK_TASK_CASE_DEF).

### Columnas


| Columna (definicion en DDL)         |
| ----------------------------------- |
| `ID_ varchar(64) NOT NULL`          |
| `REV_ integer`                      |
| `CATEGORY_ varchar(255)`            |
| `NAME_ varchar(255)`                |
| `KEY_ varchar(255) NOT NULL`        |
| `VERSION_ integer NOT NULL`         |
| `DEPLOYMENT_ID_ varchar(64)`        |
| `RESOURCE_NAME_ varchar(4000)`      |
| `DGRM_RESOURCE_NAME_ varchar(4000)` |
| `TENANT_ID_ varchar(64)`            |
| `HISTORY_TTL_ integer`              |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_CASE_DEF_TENANT_ID**` (`TENANT_ID`_): Aislamiento multi-tenant en consultas de API y webapps.

## `ACT_RE_DECISION_DEF`

### Para que se usa

Definicion DMN desplegada.

### Uso conjunto con otras tablas

- FK: ACT_FK_DEC_REQ: DEC_REQ_ID_ -> ACT_RE_DECISION_REQ_DEF(ID_)

### Columnas


| Columna (definicion en DDL)         |
| ----------------------------------- |
| `ID_ varchar(64) NOT NULL`          |
| `REV_ integer`                      |
| `CATEGORY_ varchar(255)`            |
| `NAME_ varchar(255)`                |
| `KEY_ varchar(255) NOT NULL`        |
| `VERSION_ integer NOT NULL`         |
| `DEPLOYMENT_ID_ varchar(64)`        |
| `RESOURCE_NAME_ varchar(4000)`      |
| `DGRM_RESOURCE_NAME_ varchar(4000)` |
| `DEC_REQ_ID_ varchar(64)`           |
| `DEC_REQ_KEY_ varchar(255)`         |
| `TENANT_ID_ varchar(64)`            |
| `HISTORY_TTL_ integer`              |
| `VERSION_TAG_ varchar(64)`          |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_DEC_DEF_TENANT_ID**` (`TENANT_ID`_): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_DEC_DEF_REQ_ID**` (`DEC_REQ_ID_`): Optimiza consultas que filtran o ordenan por (DEC_REQ_ID_) en `ACT_RE_DECISION_DEF` (motor, APIs REST o webapps).

## `ACT_RE_DECISION_REQ_DEF`

### Para que se usa

Requerimientos de decision DMN (DRD).

### Uso conjunto con otras tablas

- Referenciada por: ACT_RE_DECISION_DEF (via ACT_FK_DEC_REQ).

### Columnas


| Columna (definicion en DDL)         |
| ----------------------------------- |
| `ID_ varchar(64) NOT NULL`          |
| `REV_ integer`                      |
| `CATEGORY_ varchar(255)`            |
| `NAME_ varchar(255)`                |
| `KEY_ varchar(255) NOT NULL`        |
| `VERSION_ integer NOT NULL`         |
| `DEPLOYMENT_ID_ varchar(64)`        |
| `RESOURCE_NAME_ varchar(4000)`      |
| `DGRM_RESOURCE_NAME_ varchar(4000)` |
| `TENANT_ID_ varchar(64)`            |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_DEC_REQ_DEF_TENANT_ID**` (`TENANT_ID`_): Aislamiento multi-tenant en consultas de API y webapps.

## `ACT_RE_DEPLOYMENT`

### Para que se usa

Unidad de despliegue: agrupa recursos BPMN/DMN/CMMN subidos al motor.

### Uso conjunto con otras tablas

- Referenciada por: ACT_GE_BYTEARRAY (via ACT_FK_BYTEARR_DEPL).

### Columnas


| Columna (definicion en DDL) |
| --------------------------- |
| `ID_ varchar(64)`           |
| `NAME_ varchar(255)`        |
| `DEPLOY_TIME_ timestamp`    |
| `SOURCE_ varchar(255)`      |
| `TENANT_ID_ varchar(64)`    |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_DEPLOYMENT_NAME**` (`NAME`_): Optimiza consultas que filtran o ordenan por (NAME_) en `ACT_RE_DEPLOYMENT` (motor, APIs REST o webapps).
- `**ACT_IDX_DEPLOYMENT_TENANT_ID**` (`TENANT_ID`_): Aislamiento multi-tenant en consultas de API y webapps.

## `ACT_RE_PROCDEF`

### Para que se usa

Definicion de proceso BPMN versionada (clave, version, suspension, tenant).

### Uso conjunto con otras tablas

- Referenciada por: ACT_RU_EXECUTION (via ACT_FK_EXE_PROCDEF); ACT_RU_IDENTITYLINK (via ACT_FK_ATHRZ_PROCEDEF); ACT_RU_TASK (via ACT_FK_TASK_PROCDEF); ACT_RU_INCIDENT (via ACT_FK_INC_PROCDEF).

### Columnas


| Columna (definicion en DDL)                |
| ------------------------------------------ |
| `ID_ varchar(64) NOT NULL`                 |
| `REV_ integer`                             |
| `CATEGORY_ varchar(255)`                   |
| `NAME_ varchar(255)`                       |
| `KEY_ varchar(255) NOT NULL`               |
| `VERSION_ integer NOT NULL`                |
| `DEPLOYMENT_ID_ varchar(64)`               |
| `RESOURCE_NAME_ varchar(4000)`             |
| `DGRM_RESOURCE_NAME_ varchar(4000)`        |
| `HAS_START_FORM_KEY_ boolean`              |
| `SUSPENSION_STATE_ integer`                |
| `TENANT_ID_ varchar(64)`                   |
| `VERSION_TAG_ varchar(64)`                 |
| `HISTORY_TTL_ integer`                     |
| `STARTABLE_ boolean NOT NULL default TRUE` |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_PROCDEF_DEPLOYMENT_ID**` (`DEPLOYMENT_ID`_): Recursos binarios ligados a un despliegue (FK y listados por deployment).
- `**ACT_IDX_PROCDEF_TENANT_ID**` (`TENANT_ID_`): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_PROCDEF_VER_TAG**` (`VERSION_TAG_`): Optimiza consultas que filtran o ordenan por (VERSION_TAG_) en `ACT_RE_PROCDEF` (motor, APIs REST o webapps).

## `ACT_RU_AUTHORIZATION`

### Para que se usa

Autorizaciones granulares (ACL) para recursos del motor y webapps.

### Uso conjunto con otras tablas

- Ver columnas `*_ID`_ para enlaces logicos con otras entidades del mismo dominio (GE/RE/RU/HI).

### Columnas


| Columna (definicion en DDL)       |
| --------------------------------- |
| `ID_ varchar(64) not null`        |
| `REV_ integer not null`           |
| `TYPE_ integer not null`          |
| `GROUP_ID_ varchar(255)`          |
| `USER_ID_ varchar(255)`           |
| `RESOURCE_TYPE_ integer not null` |
| `RESOURCE_ID_ varchar(255)`       |
| `PERMS_ integer`                  |
| `REMOVAL_TIME_ timestamp`         |
| `ROOT_PROC_INST_ID_ varchar(64)`  |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_AUTH_GROUP_ID**` (`GROUP_ID_`): Resolucion de membership y permisos de grupo.
- `**ACT_IDX_AUTH_RESOURCE_ID**` (`RESOURCE_ID_`): Autorizaciones por id de recurso (incl. comodines).
- `**ACT_IDX_AUTH_ROOT_PI**` (`ROOT_PROC_INST_ID_`): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_AUTH_RM_TIME**` (`REMOVAL_TIME_`): Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.

## `ACT_RU_BATCH`

### Para que se usa

Metadatos de operaciones por lotes (modificacion masiva, migracion, etc.).

### Uso conjunto con otras tablas

- FK: ACT_FK_BATCH_SEED_JOB_DEF: SEED_JOB_DEF_ID_ -> ACT_RU_JOBDEF(ID_)
- FK: ACT_FK_BATCH_MONITOR_JOB_DEF: MONITOR_JOB_DEF_ID_ -> ACT_RU_JOBDEF(ID_)
- FK: ACT_FK_BATCH_JOB_DEF: BATCH_JOB_DEF_ID_ -> ACT_RU_JOBDEF(ID_)
- Referenciada por: ACT_RU_VARIABLE (via ACT_FK_VAR_BATCH).

### Columnas


| Columna (definicion en DDL)       |
| --------------------------------- |
| `ID_ varchar(64) not null`        |
| `REV_ integer not null`           |
| `TYPE_ varchar(255)`              |
| `TOTAL_JOBS_ integer`             |
| `JOBS_CREATED_ integer`           |
| `JOBS_PER_SEED_ integer`          |
| `INVOCATIONS_PER_JOB_ integer`    |
| `SEED_JOB_DEF_ID_ varchar(64)`    |
| `BATCH_JOB_DEF_ID_ varchar(64)`   |
| `MONITOR_JOB_DEF_ID_ varchar(64)` |
| `SUSPENSION_STATE_ integer`       |
| `CONFIGURATION_ varchar(255)`     |
| `TENANT_ID_ varchar(64)`          |
| `CREATE_USER_ID_ varchar(255)`    |
| `START_TIME_ timestamp`           |
| `EXEC_START_TIME_ timestamp`      |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_BATCH_SEED_JOB_DEF**` (`SEED_JOB_DEF_ID`_): Jobs o logs asociados a una definicion de job concreta.
- `**ACT_IDX_BATCH_MONITOR_JOB_DEF**` (`MONITOR_JOB_DEF_ID_`): Jobs o logs asociados a una definicion de job concreta.
- `**ACT_IDX_BATCH_JOB_DEF**` (`BATCH_JOB_DEF_ID_`): Jobs o logs asociados a una definicion de job concreta.

## `ACT_RU_CASE_EXECUTION`

### Para que se usa

Ejecucion de caso CMMN (arbol de case execution).

### Uso conjunto con otras tablas

- FK: ACT_FK_CASE_EXE_CASE_INST: CASE_INST_ID_ -> ACT_RU_CASE_EXECUTION(ID_)
- FK: ACT_FK_CASE_EXE_PARENT: PARENT_ID_ -> ACT_RU_CASE_EXECUTION(ID_)
- FK: ACT_FK_CASE_EXE_CASE_DEF: CASE_DEF_ID_ -> ACT_RE_CASE_DEF(ID_)
- Referenciada por: ACT_RU_CASE_EXECUTION (via ACT_FK_CASE_EXE_CASE_INST); ACT_RU_CASE_EXECUTION (via ACT_FK_CASE_EXE_PARENT); ACT_RU_VARIABLE (via ACT_FK_VAR_CASE_EXE); ACT_RU_VARIABLE (via ACT_FK_VAR_CASE_INST); ACT_RU_TASK (via ACT_FK_TASK_CASE_EXE); ACT_RU_CASE_SENTRY_PART (via ACT_FK_CASE_SENTRY_CASE_INST); ACT_RU_CASE_SENTRY_PART (via ACT_FK_CASE_SENTRY_CASE_EXEC).

### Columnas


| Columna (definicion en DDL)    |
| ------------------------------ |
| `ID_ varchar(64) NOT NULL`     |
| `REV_ integer`                 |
| `CASE_INST_ID_ varchar(64)`    |
| `SUPER_CASE_EXEC_ varchar(64)` |
| `SUPER_EXEC_ varchar(64)`      |
| `BUSINESS_KEY_ varchar(255)`   |
| `PARENT_ID_ varchar(64)`       |
| `CASE_DEF_ID_ varchar(64)`     |
| `ACT_ID_ varchar(255)`         |
| `PREV_STATE_ integer`          |
| `CURRENT_STATE_ integer`       |
| `REQUIRED_ boolean`            |
| `TENANT_ID_ varchar(64)`       |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_CASE_EXEC_BUSKEY**` (`BUSINESS_KEY`_): Ejecucion de caso CMMN (arbol, definicion).
- `**ACT_IDX_CASE_EXE_CASE_INST**` (`CASE_INST_ID_`): Ejecucion de caso CMMN (arbol, definicion).
- `**ACT_IDX_CASE_EXE_PARENT**` (`PARENT_ID_`): Ejecucion de caso CMMN (arbol, definicion).
- `**ACT_IDX_CASE_EXE_CASE_DEF**` (`CASE_DEF_ID_`): Ejecucion de caso CMMN (arbol, definicion).
- `**ACT_IDX_CASE_EXEC_TENANT_ID**` (`TENANT_ID_`): Aislamiento multi-tenant en consultas de API y webapps.

## `ACT_RU_CASE_SENTRY_PART`

### Para que se usa

Partes de centinelas CMMN (condiciones de activacion).

### Uso conjunto con otras tablas

- FK: ACT_FK_CASE_SENTRY_CASE_INST: CASE_INST_ID_ -> ACT_RU_CASE_EXECUTION(ID_)
- FK: ACT_FK_CASE_SENTRY_CASE_EXEC: CASE_EXEC_ID_ -> ACT_RU_CASE_EXECUTION(ID_)

### Columnas


| Columna (definicion en DDL)        |
| ---------------------------------- |
| `ID_ varchar(64) NOT NULL`         |
| `REV_ integer`                     |
| `CASE_INST_ID_ varchar(64)`        |
| `CASE_EXEC_ID_ varchar(64)`        |
| `SENTRY_ID_ varchar(255)`          |
| `TYPE_ varchar(255)`               |
| `SOURCE_CASE_EXEC_ID_ varchar(64)` |
| `STANDARD_EVENT_ varchar(255)`     |
| `SOURCE_ varchar(255)`             |
| `VARIABLE_EVENT_ varchar(255)`     |
| `VARIABLE_NAME_ varchar(255)`      |
| `SATISFIED_ boolean`               |
| `TENANT_ID_ varchar(64)`           |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_CASE_SENTRY_CASE_INST**` (`CASE_INST_ID`_): Evaluacion de partes de centinela CMMN.
- `**ACT_IDX_CASE_SENTRY_CASE_EXEC**` (`CASE_EXEC_ID_`): Evaluacion de partes de centinela CMMN.

## `ACT_RU_EVENT_SUBSCR`

### Para que se usa

Suscripciones a eventos (mensaje, senal, compensacion, condicional) pendientes.

### Uso conjunto con otras tablas

- FK: ACT_FK_EVENT_EXEC: EXECUTION_ID_ -> ACT_RU_EXECUTION(ID_)

### Columnas


| Columna (definicion en DDL)         |
| ----------------------------------- |
| `ID_ varchar(64) not null`          |
| `REV_ integer`                      |
| `EVENT_TYPE_ varchar(255) not null` |
| `EVENT_NAME_ varchar(255)`          |
| `EXECUTION_ID_ varchar(64)`         |
| `PROC_INST_ID_ varchar(64)`         |
| `ACTIVITY_ID_ varchar(255)`         |
| `CONFIGURATION_ varchar(255)`       |
| `CREATED_ timestamp not null`       |
| `TENANT_ID_ varchar(64)`            |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_EVENT_SUBSCR_CONFIG_**` (`CONFIGURATION`_): Optimiza consultas que filtran o ordenan por (CONFIGURATION_) en `ACT_RU_EVENT_SUBSCR` (motor, APIs REST o webapps).
- `**ACT_IDX_EVENT_SUBSCR_TENANT_ID**` (`TENANT_ID`_): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_EVENT_SUBSCR**` (`EXECUTION_ID_`): Optimiza consultas que filtran o ordenan por (EXECUTION_ID_) en `ACT_RU_EVENT_SUBSCR` (motor, APIs REST o webapps).
- `**ACT_IDX_EVENT_SUBSCR_EVT_NAME**` (`EVENT_NAME`_): Optimiza consultas que filtran o ordenan por (EVENT_NAME_) en `ACT_RU_EVENT_SUBSCR` (motor, APIs REST o webapps).

## `ACT_RU_EXECUTION`

### Para que se usa

Ejecucion runtime (token BPMN): arbol de instancia, actividad actual, parent/super, business key.

### Uso conjunto con otras tablas

- FK: ACT_FK_EXE_PROCINST: PROC_INST_ID_ -> ACT_RU_EXECUTION(ID_)
- FK: ACT_FK_EXE_PARENT: PARENT_ID_ -> ACT_RU_EXECUTION(ID_)
- FK: ACT_FK_EXE_SUPER: SUPER_EXEC_ -> ACT_RU_EXECUTION(ID_)
- FK: ACT_FK_EXE_PROCDEF: PROC_DEF_ID_ -> ACT_RE_PROCDEF(ID_)
- Referenciada por: ACT_RU_EXECUTION (via ACT_FK_EXE_PROCINST); ACT_RU_EXECUTION (via ACT_FK_EXE_PARENT); ACT_RU_EXECUTION (via ACT_FK_EXE_SUPER); ACT_RU_TASK (via ACT_FK_TASK_EXE); ACT_RU_TASK (via ACT_FK_TASK_PROCINST); ACT_RU_VARIABLE (via ACT_FK_VAR_EXE); ACT_RU_VARIABLE (via ACT_FK_VAR_PROCINST); ACT_RU_EVENT_SUBSCR (via ACT_FK_EVENT_EXEC); ACT_RU_INCIDENT (via ACT_FK_INC_EXE); ACT_RU_INCIDENT (via ACT_FK_INC_PROCINST); ACT_RU_EXT_TASK (via ACT_FK_EXT_TASK_EXE).

### Columnas


| Columna (definicion en DDL)      |
| -------------------------------- |
| `ID_ varchar(64)`                |
| `REV_ integer`                   |
| `ROOT_PROC_INST_ID_ varchar(64)` |
| `PROC_INST_ID_ varchar(64)`      |
| `BUSINESS_KEY_ varchar(255)`     |
| `PARENT_ID_ varchar(64)`         |
| `PROC_DEF_ID_ varchar(64)`       |
| `SUPER_EXEC_ varchar(64)`        |
| `SUPER_CASE_EXEC_ varchar(64)`   |
| `CASE_INST_ID_ varchar(64)`      |
| `ACT_ID_ varchar(255)`           |
| `ACT_INST_ID_ varchar(64)`       |
| `IS_ACTIVE_ boolean`             |
| `IS_CONCURRENT_ boolean`         |
| `IS_SCOPE_ boolean`              |
| `IS_EVENT_SCOPE_ boolean`        |
| `SUSPENSION_STATE_ integer`      |
| `CACHED_ENT_STATE_ integer`      |
| `SEQUENCE_COUNTER_ bigint`       |
| `TENANT_ID_ varchar(64)`         |
| `PROC_DEF_KEY_ varchar(255)`     |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_EXE_ROOT_PI**` (`ROOT_PROC_INST_ID`_): Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).
- `**ACT_IDX_EXEC_BUSKEY**` (`BUSINESS_KEY_`): Optimiza consultas que filtran o ordenan por (BUSINESS_KEY_) en `ACT_RU_EXECUTION` (motor, APIs REST o webapps).
- `**ACT_IDX_EXEC_TENANT_ID**` (`TENANT_ID`_): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_EXE_PROCINST**` (`PROC_INST_ID_`): Optimiza consultas que filtran o ordenan por (PROC_INST_ID_) en `ACT_RU_EXECUTION` (motor, APIs REST o webapps).
- `**ACT_IDX_EXE_PARENT**` (`PARENT_ID`_): Optimiza consultas que filtran o ordenan por (PARENT_ID_) en `ACT_RU_EXECUTION` (motor, APIs REST o webapps).
- `**ACT_IDX_EXE_SUPER**` (`SUPER_EXEC`_): Optimiza consultas que filtran o ordenan por (SUPER_EXEC_) en `ACT_RU_EXECUTION` (motor, APIs REST o webapps).
- `**ACT_IDX_EXE_PROCDEF**` (`PROC_DEF_ID`_): Optimiza consultas que filtran o ordenan por (PROC_DEF_ID_) en `ACT_RU_EXECUTION` (motor, APIs REST o webapps).

## `ACT_RU_EXT_TASK`

### Para que se usa

Tareas externas (workers): topic, lock, prioridad, reintentos.

### Uso conjunto con otras tablas

- FK: ACT_FK_EXT_TASK_EXE: EXECUTION_ID_ -> ACT_RU_EXECUTION(ID_)
- FK: ACT_FK_EXT_TASK_ERROR_DETAILS: ERROR_DETAILS_ID_ -> ACT_GE_BYTEARRAY(ID_)

### Columnas


| Columna (definicion en DDL)           |
| ------------------------------------- |
| `ID_ varchar(64) not null`            |
| `REV_ integer not null`               |
| `WORKER_ID_ varchar(255)`             |
| `TOPIC_NAME_ varchar(255)`            |
| `RETRIES_ integer`                    |
| `ERROR_MSG_ varchar(4000)`            |
| `ERROR_DETAILS_ID_ varchar(64)`       |
| `LOCK_EXP_TIME_ timestamp`            |
| `CREATE_TIME_ timestamp`              |
| `SUSPENSION_STATE_ integer`           |
| `EXECUTION_ID_ varchar(64)`           |
| `PROC_INST_ID_ varchar(64)`           |
| `PROC_DEF_ID_ varchar(64)`            |
| `PROC_DEF_KEY_ varchar(255)`          |
| `ACT_ID_ varchar(255)`                |
| `ACT_INST_ID_ varchar(64)`            |
| `TENANT_ID_ varchar(64)`              |
| `PRIORITY_ bigint NOT NULL DEFAULT 0` |
| `LAST_FAILURE_LOG_ID_ varchar(64)`    |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_EXT_TASK_TOPIC**` (`TOPIC_NAME`_): Optimiza consultas que filtran o ordenan por (TOPIC_NAME_) en `ACT_RU_EXT_TASK` (motor, APIs REST o webapps).
- `**ACT_IDX_EXT_TASK_TENANT_ID**` (`TENANT_ID`_): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_EXT_TASK_PRIORITY**` (`PRIORITY_`): Ordenacion por prioridad de tarea o external task.
- `**ACT_IDX_EXT_TASK_ERR_DETAILS**` (`ERROR_DETAILS_ID_`): Detalle fino de cambios (variables, propiedades).
- `**ACT_IDX_EXT_TASK_EXEC**` (`EXECUTION_ID_`): Optimiza consultas que filtran o ordenan por (EXECUTION_ID_) en `ACT_RU_EXT_TASK` (motor, APIs REST o webapps).

## `ACT_RU_FILTER`

### Para que se usa

Filtros guardados de Tasklist (queries reutilizables).

### Uso conjunto con otras tablas

- Ver columnas `*_ID`_ para enlaces logicos con otras entidades del mismo dominio (GE/RE/RU/HI).

### Columnas


| Columna (definicion en DDL)            |
| -------------------------------------- |
| `ID_ varchar(64) not null`             |
| `REV_ integer not null`                |
| `RESOURCE_TYPE_ varchar(255) not null` |
| `NAME_ varchar(255) not null`          |
| `OWNER_ varchar(255)`                  |
| `QUERY_ TEXT not null`                 |
| `PROPERTIES_ TEXT`                     |


### Indices por defecto (DDL oficial)

- (Ningun `CREATE INDEX` explicito en el DDL para esta tabla; la PK sigue indexada).

## `ACT_RU_IDENTITYLINK`

### Para que se usa

Participantes de tareas o iniciadores/candidatos (usuarios/grupos) en runtime.

### Uso conjunto con otras tablas

- FK: ACT_FK_TSKASS_TASK: TASK_ID_ -> ACT_RU_TASK(ID_)
- FK: ACT_FK_ATHRZ_PROCEDEF: PROC_DEF_ID_ -> ACT_RE_PROCDEF(ID_)

### Columnas


| Columna (definicion en DDL) |
| --------------------------- |
| `ID_ varchar(64)`           |
| `REV_ integer`              |
| `GROUP_ID_ varchar(255)`    |
| `TYPE_ varchar(255)`        |
| `USER_ID_ varchar(255)`     |
| `TASK_ID_ varchar(64)`      |
| `PROC_DEF_ID_ varchar (64)` |
| `TENANT_ID_ varchar(64)`    |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_IDENT_LNK_USER**` (`USER_ID`_): Identity links historicos (candidatos, participantes).
- `**ACT_IDX_IDENT_LNK_GROUP**` (`GROUP_ID_`): Identity links historicos (candidatos, participantes).
- `**ACT_IDX_TSKASS_TASK**` (`TASK_ID_`): Optimiza consultas que filtran o ordenan por (TASK_ID_) en `ACT_RU_IDENTITYLINK` (motor, APIs REST o webapps).
- `**ACT_IDX_ATHRZ_PROCEDEF**` (`PROC_DEF_ID`_): Optimiza consultas que filtran o ordenan por (PROC_DEF_ID_) en `ACT_RU_IDENTITYLINK` (motor, APIs REST o webapps).

## `ACT_RU_INCIDENT`

### Para que se usa

Incidentes tecnicos (fallos de job, errores de conector) enlazados a ejecucion o job.

### Uso conjunto con otras tablas

- FK: ACT_FK_INC_EXE: EXECUTION_ID_ -> ACT_RU_EXECUTION(ID_)
- FK: ACT_FK_INC_PROCINST: PROC_INST_ID_ -> ACT_RU_EXECUTION(ID_)
- FK: ACT_FK_INC_PROCDEF: PROC_DEF_ID_ -> ACT_RE_PROCDEF(ID_)
- FK: ACT_FK_INC_CAUSE: CAUSE_INCIDENT_ID_ -> ACT_RU_INCIDENT(ID_)
- FK: ACT_FK_INC_RCAUSE: ROOT_CAUSE_INCIDENT_ID_ -> ACT_RU_INCIDENT(ID_)
- FK: ACT_FK_INC_JOB_DEF: JOB_DEF_ID_ -> ACT_RU_JOBDEF(ID_)
- Referenciada por: ACT_RU_INCIDENT (via ACT_FK_INC_CAUSE); ACT_RU_INCIDENT (via ACT_FK_INC_RCAUSE).

### Columnas


| Columna (definicion en DDL)              |
| ---------------------------------------- |
| `ID_ varchar(64) not null`               |
| `REV_ integer not null`                  |
| `INCIDENT_TIMESTAMP_ timestamp not null` |
| `INCIDENT_MSG_ varchar(4000)`            |
| `INCIDENT_TYPE_ varchar(255) not null`   |
| `EXECUTION_ID_ varchar(64)`              |
| `ACTIVITY_ID_ varchar(255)`              |
| `FAILED_ACTIVITY_ID_ varchar(255)`       |
| `PROC_INST_ID_ varchar(64)`              |
| `PROC_DEF_ID_ varchar(64)`               |
| `CAUSE_INCIDENT_ID_ varchar(64)`         |
| `ROOT_CAUSE_INCIDENT_ID_ varchar(64)`    |
| `CONFIGURATION_ varchar(255)`            |
| `TENANT_ID_ varchar(64)`                 |
| `JOB_DEF_ID_ varchar(64)`                |
| `ANNOTATION_ varchar(4000)`              |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_INC_CONFIGURATION**` (`CONFIGURATION`_): Optimiza consultas que filtran o ordenan por (CONFIGURATION_) en `ACT_RU_INCIDENT` (motor, APIs REST o webapps).
- `**ACT_IDX_INC_TENANT_ID**` (`TENANT_ID`_): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_INC_JOB_DEF**` (`JOB_DEF_ID_`): Jobs o logs asociados a una definicion de job concreta.
- `**ACT_IDX_INC_CAUSEINCID**` (`CAUSE_INCIDENT_ID_`): Optimiza consultas que filtran o ordenan por (CAUSE_INCIDENT_ID_) en `ACT_RU_INCIDENT` (motor, APIs REST o webapps).
- `**ACT_IDX_INC_EXID**` (`EXECUTION_ID`_): Optimiza consultas que filtran o ordenan por (EXECUTION_ID_) en `ACT_RU_INCIDENT` (motor, APIs REST o webapps).
- `**ACT_IDX_INC_PROCDEFID**` (`PROC_DEF_ID`_): Optimiza consultas que filtran o ordenan por (PROC_DEF_ID_) en `ACT_RU_INCIDENT` (motor, APIs REST o webapps).
- `**ACT_IDX_INC_PROCINSTID**` (`PROC_INST_ID`_): Optimiza consultas que filtran o ordenan por (PROC_INST_ID_) en `ACT_RU_INCIDENT` (motor, APIs REST o webapps).
- `**ACT_IDX_INC_ROOTCAUSEINCID**` (`ROOT_CAUSE_INCIDENT_ID`_): Optimiza consultas que filtran o ordenan por (ROOT_CAUSE_INCIDENT_ID_) en `ACT_RU_INCIDENT` (motor, APIs REST o webapps).

## `ACT_RU_JOB`

### Para que se usa

Jobs asincronos (timers, async continuation, mensajes) para el Job Executor.

### Uso conjunto con otras tablas

- FK: ACT_FK_JOB_EXCEPTION: EXCEPTION_STACK_ID_ -> ACT_GE_BYTEARRAY(ID_)

### Columnas


| Columna (definicion en DDL)                    |
| ---------------------------------------------- |
| `ID_ varchar(64) NOT NULL`                     |
| `REV_ integer`                                 |
| `TYPE_ varchar(255) NOT NULL`                  |
| `LOCK_EXP_TIME_ timestamp`                     |
| `LOCK_OWNER_ varchar(255)`                     |
| `EXCLUSIVE_ boolean`                           |
| `EXECUTION_ID_ varchar(64)`                    |
| `ROOT_PROC_INST_ID_ varchar(64)`               |
| `PROCESS_INSTANCE_ID_ varchar(64)`             |
| `PROCESS_DEF_ID_ varchar(64)`                  |
| `PROCESS_DEF_KEY_ varchar(255)`                |
| `RETRIES_ integer`                             |
| `EXCEPTION_STACK_ID_ varchar(64)`              |
| `EXCEPTION_MSG_ varchar(4000)`                 |
| `FAILED_ACT_ID_ varchar(255)`                  |
| `DUEDATE_ timestamp`                           |
| `REPEAT_ varchar(255)`                         |
| `REPEAT_OFFSET_ bigint DEFAULT 0`              |
| `HANDLER_TYPE_ varchar(255)`                   |
| `HANDLER_CFG_ varchar(4000)`                   |
| `DEPLOYMENT_ID_ varchar(64)`                   |
| `SUSPENSION_STATE_ integer NOT NULL DEFAULT 1` |
| `JOB_DEF_ID_ varchar(64)`                      |
| `PRIORITY_ bigint NOT NULL DEFAULT 0`          |
| `SEQUENCE_COUNTER_ bigint`                     |
| `TENANT_ID_ varchar(64)`                       |
| `CREATE_TIME_ timestamp`                       |
| `LAST_FAILURE_LOG_ID_ varchar(64)`             |
| `BATCH_ID_ varchar(64)`                        |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_JOB_EXECUTION_ID**` (`EXECUTION_ID`_): Enlace al token de ejecucion BPMN concreto.
- `**ACT_IDX_JOB_HANDLER**` (`HANDLER_TYPE_,HANDLER_CFG_`): Optimiza consultas que filtran o ordenan por (HANDLER_TYPE_,HANDLER_CFG_) en `ACT_RU_JOB` (motor, APIs REST o webapps).
- `**ACT_IDX_JOB_PROCINST**` (`PROCESS_INSTANCE_ID`_): Optimiza consultas que filtran o ordenan por (PROCESS_INSTANCE_ID_) en `ACT_RU_JOB` (motor, APIs REST o webapps).
- `**ACT_IDX_JOB_ROOT_PROCINST**` (`ROOT_PROC_INST_ID`_): Optimiza consultas que filtran o ordenan por (ROOT_PROC_INST_ID_) en `ACT_RU_JOB` (motor, APIs REST o webapps).
- `**ACT_IDX_JOB_TENANT_ID**` (`TENANT_ID`_): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_JOB_JOB_DEF_ID**` (`JOB_DEF_ID_`): Jobs o logs asociados a una definicion de job concreta.
- `**ACT_IDX_JOB_EXCEPTION**` (`EXCEPTION_STACK_ID_`): Optimiza consultas que filtran o ordenan por (EXCEPTION_STACK_ID_) en `ACT_RU_JOB` (motor, APIs REST o webapps).
- `**ACT_IDX_JOB_HANDLER_TYPE**` (`HANDLER_TYPE`_): Seleccion de jobs por tipo de handler (async, timer, etc.).

## `ACT_RU_JOBDEF`

### Para que se usa

Definiciones de job asociadas a actividades o procesos.

### Uso conjunto con otras tablas

- Referenciada por: ACT_RU_INCIDENT (via ACT_FK_INC_JOB_DEF); ACT_RU_BATCH (via ACT_FK_BATCH_SEED_JOB_DEF); ACT_RU_BATCH (via ACT_FK_BATCH_MONITOR_JOB_DEF); ACT_RU_BATCH (via ACT_FK_BATCH_JOB_DEF).

### Columnas


| Columna (definicion en DDL)       |
| --------------------------------- |
| `ID_ varchar(64) NOT NULL`        |
| `REV_ integer`                    |
| `PROC_DEF_ID_ varchar(64)`        |
| `PROC_DEF_KEY_ varchar(255)`      |
| `ACT_ID_ varchar(255)`            |
| `JOB_TYPE_ varchar(255) NOT NULL` |
| `JOB_CONFIGURATION_ varchar(255)` |
| `SUSPENSION_STATE_ integer`       |
| `JOB_PRIORITY_ bigint`            |
| `TENANT_ID_ varchar(64)`          |
| `DEPLOYMENT_ID_ varchar(64)`      |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_JOBDEF_TENANT_ID**` (`TENANT_ID`_): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_JOBDEF_PROC_DEF_ID**` (`PROC_DEF_ID_`): Joins y filtros por id de definicion desplegada (con version).

## `ACT_RU_METER_LOG`

### Para que se usa

Metricas internas de uso (contadores por nombre y ventana temporal).

### Uso conjunto con otras tablas

- Ver columnas `*_ID_` para enlaces logicos con otras entidades del mismo dominio (GE/RE/RU/HI).

### Columnas


| Columna (definicion en DDL)      |
| -------------------------------- |
| `ID_ varchar(64) not null`       |
| `NAME_ varchar(64) not null`     |
| `REPORTER_ varchar(255)`         |
| `VALUE_ bigint`                  |
| `TIMESTAMP_ timestamp`           |
| `MILLISECONDS_ bigint DEFAULT 0` |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_METER_LOG_MS**` (`MILLISECONDS_`): Agregacion de metricas por nombre/tiempo/reporter.
- `**ACT_IDX_METER_LOG_NAME_MS**` (`NAME_, MILLISECONDS_`): Agregacion de metricas por nombre/tiempo/reporter.
- `**ACT_IDX_METER_LOG_REPORT**` (`NAME_, REPORTER_, MILLISECONDS_`): Agregacion de metricas por nombre/tiempo/reporter.
- `**ACT_IDX_METER_LOG_TIME**` (`TIMESTAMP_`): Agregacion de metricas por nombre/tiempo/reporter.
- `**ACT_IDX_METER_LOG**` (`NAME_, TIMESTAMP_`): Agregacion de metricas por nombre/tiempo/reporter.

## `ACT_RU_TASK`

### Para que se usa

Tareas de usuario abiertas (Tasklist): asignatario, prioridad, due date, enlaces a ejecucion e instancia.

### Uso conjunto con otras tablas

- FK: ACT_FK_TASK_EXE: EXECUTION_ID_ -> ACT_RU_EXECUTION(ID_)
- FK: ACT_FK_TASK_PROCINST: PROC_INST_ID_ -> ACT_RU_EXECUTION(ID_)
- FK: ACT_FK_TASK_PROCDEF: PROC_DEF_ID_ -> ACT_RE_PROCDEF(ID_)
- FK: ACT_FK_TASK_CASE_EXE: CASE_EXECUTION_ID_ -> ACT_RU_CASE_EXECUTION(ID_)
- FK: ACT_FK_TASK_CASE_DEF: CASE_DEF_ID_ -> ACT_RE_CASE_DEF(ID_)
- Referenciada por: ACT_RU_IDENTITYLINK (via ACT_FK_TSKASS_TASK).

### Columnas


| Columna (definicion en DDL)      |
| -------------------------------- |
| `ID_ varchar(64)`                |
| `REV_ integer`                   |
| `EXECUTION_ID_ varchar(64)`      |
| `PROC_INST_ID_ varchar(64)`      |
| `PROC_DEF_ID_ varchar(64)`       |
| `CASE_EXECUTION_ID_ varchar(64)` |
| `CASE_INST_ID_ varchar(64)`      |
| `CASE_DEF_ID_ varchar(64)`       |
| `NAME_ varchar(255)`             |
| `PARENT_TASK_ID_ varchar(64)`    |
| `DESCRIPTION_ varchar(4000)`     |
| `TASK_DEF_KEY_ varchar(255)`     |
| `OWNER_ varchar(255)`            |
| `ASSIGNEE_ varchar(255)`         |
| `DELEGATION_ varchar(64)`        |
| `PRIORITY_ integer`              |
| `CREATE_TIME_ timestamp`         |
| `LAST_UPDATED_ timestamp`        |
| `DUE_DATE_ timestamp`            |
| `FOLLOW_UP_DATE_ timestamp`      |
| `SUSPENSION_STATE_ integer`      |
| `TENANT_ID_ varchar(64)`         |
| `TASK_STATE_ varchar(64)`        |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_TASK_CREATE**` (`CREATE_TIME`_): Optimiza consultas que filtran o ordenan por (CREATE_TIME_) en `ACT_RU_TASK` (motor, APIs REST o webapps).
- `**ACT_IDX_TASK_LAST_UPDATED**` (`LAST_UPDATED`_): Ordenacion por ultima actualizacion de tarea.
- `**ACT_IDX_TASK_ASSIGNEE**` (`ASSIGNEE_`): Bandejas por asignatario (Tasklist, REST task query).
- `**ACT_IDX_TASK_OWNER**` (`OWNER_`): Optimiza consultas que filtran o ordenan por (OWNER_) en `ACT_RU_TASK` (motor, APIs REST o webapps).
- `**ACT_IDX_TASK_TENANT_ID**` (`TENANT_ID`_): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_TASK_EXEC**` (`EXECUTION_ID_`): Optimiza consultas que filtran o ordenan por (EXECUTION_ID_) en `ACT_RU_TASK` (motor, APIs REST o webapps).
- `**ACT_IDX_TASK_PROCINST**` (`PROC_INST_ID`_): Optimiza consultas que filtran o ordenan por (PROC_INST_ID_) en `ACT_RU_TASK` (motor, APIs REST o webapps).
- `**ACT_IDX_TASK_PROCDEF**` (`PROC_DEF_ID`_): Optimiza consultas que filtran o ordenan por (PROC_DEF_ID_) en `ACT_RU_TASK` (motor, APIs REST o webapps).
- `**ACT_IDX_TASK_CASE_EXEC**` (`CASE_EXECUTION_ID`_): Ejecucion de caso CMMN (arbol, definicion).
- `**ACT_IDX_TASK_CASE_DEF_ID**` (`CASE_DEF_ID_`): Optimiza consultas que filtran o ordenan por (CASE_DEF_ID_) en `ACT_RU_TASK` (motor, APIs REST o webapps).

## `ACT_RU_TASK_METER_LOG`

### Para que se usa

Metricas anonimizadas de asignacion de tareas.

### Uso conjunto con otras tablas

- Ver columnas `*_ID`_ para enlaces logicos con otras entidades del mismo dominio (GE/RE/RU/HI).

### Columnas


| Columna (definicion en DDL) |
| --------------------------- |
| `ID_ varchar(64) not null`  |
| `ASSIGNEE_HASH_ bigint`     |
| `TIMESTAMP_ timestamp`      |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_TASK_METER_LOG_TIME**` (`TIMESTAMP_`): Agregacion de metricas por nombre/tiempo/reporter.

## `ACT_RU_VARIABLE`

### Para que se usa

Variables de proceso/tarea/caso en runtime (valor inline o referencia a byte array).

### Uso conjunto con otras tablas

- FK: ACT_FK_VAR_EXE: EXECUTION_ID_ -> ACT_RU_EXECUTION(ID_)
- FK: ACT_FK_VAR_PROCINST: PROC_INST_ID_ -> ACT_RU_EXECUTION(ID_)
- FK: ACT_FK_VAR_BYTEARRAY: BYTEARRAY_ID_ -> ACT_GE_BYTEARRAY(ID_)
- FK: ACT_FK_VAR_BATCH: BATCH_ID_ -> ACT_RU_BATCH(ID_)
- FK: ACT_FK_VAR_CASE_EXE: CASE_EXECUTION_ID_ -> ACT_RU_CASE_EXECUTION(ID_)
- FK: ACT_FK_VAR_CASE_INST: CASE_INST_ID_ -> ACT_RU_CASE_EXECUTION(ID_)

### Columnas


| Columna (definicion en DDL)      |
| -------------------------------- |
| `ID_ varchar(64) not null`       |
| `REV_ integer`                   |
| `TYPE_ varchar(255) not null`    |
| `NAME_ varchar(255) not null`    |
| `EXECUTION_ID_ varchar(64)`      |
| `PROC_INST_ID_ varchar(64)`      |
| `PROC_DEF_ID_ varchar(64)`       |
| `CASE_EXECUTION_ID_ varchar(64)` |
| `CASE_INST_ID_ varchar(64)`      |
| `TASK_ID_ varchar(64)`           |
| `BATCH_ID_ varchar(64)`          |
| `BYTEARRAY_ID_ varchar(64)`      |
| `DOUBLE_ double precision`       |
| `LONG_ bigint`                   |
| `TEXT_ varchar(4000)`            |
| `TEXT2_ varchar(4000)`           |
| `VAR_SCOPE_ varchar(64)`         |
| `SEQUENCE_COUNTER_ bigint`       |
| `IS_CONCURRENT_LOCAL_ boolean`   |
| `TENANT_ID_ varchar(64)`         |


### Indices por defecto (DDL oficial)

- `**ACT_IDX_VARIABLE_TASK_ID**` (`TASK_ID`_): Acceso a variables, identity links o comentarios ligados a una tarea.
- `**ACT_IDX_VARIABLE_TENANT_ID**` (`TENANT_ID_`): Aislamiento multi-tenant en consultas de API y webapps.
- `**ACT_IDX_VARIABLE_TASK_NAME_TYPE**` (`TASK_ID_, NAME_, TYPE_`): Filtrado por nombre logico (recurso, metrica, detalle).
- `**ACT_IDX_VAR_EXE**` (`EXECUTION_ID_`): Optimiza consultas que filtran o ordenan por (EXECUTION_ID_) en `ACT_RU_VARIABLE` (motor, APIs REST o webapps).
- `**ACT_IDX_VAR_PROCINST**` (`PROC_INST_ID`_): Optimiza consultas que filtran o ordenan por (PROC_INST_ID_) en `ACT_RU_VARIABLE` (motor, APIs REST o webapps).
- `**ACT_IDX_VAR_BYTEARRAY**` (`BYTEARRAY_ID`_): Optimiza consultas que filtran o ordenan por (BYTEARRAY_ID_) en `ACT_RU_VARIABLE` (motor, APIs REST o webapps).
- `**ACT_IDX_BATCH_ID**` (`BATCH_ID`_): Operaciones por lote (seed/monitor/batch jobs).
- `**ACT_IDX_VAR_CASE_EXE**` (`CASE_EXECUTION_ID_`): Ejecucion de caso CMMN (arbol, definicion).
- `**ACT_IDX_VAR_CASE_INST_ID**` (`CASE_INST_ID_`): Navegacion CMMN por instancia de caso.

