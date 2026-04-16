-- Índices adicionales para alto volumen (Camunda 7.24 / PostgreSQL)
-- Ejecutar DESPUÉS de identity.sql y engine.sql.
-- No sustituyen los índices oficiales; complementan patrones típicos de consulta.
-- Revisar en producción con EXPLAIN ANALYZE y estadísticas reales.

-- ACT_RU_TASK: listados por definición de tarea + asignatario (Tasklist / REST frecuente)
CREATE INDEX IF NOT EXISTS ACT_IDX_EXTRA_TASK_DEF_ASSIGNEE ON ACT_RU_TASK (TASK_DEF_KEY_, ASSIGNEE_);

-- ACT_RU_TASK: ordenación por prioridad y fecha de vencimiento (bandejas ordenadas)
CREATE INDEX IF NOT EXISTS ACT_IDX_EXTRA_TASK_PRIO_DUE ON ACT_RU_TASK (PRIORITY_, DUE_DATE_);

-- ACT_RU_TASK: filtro por instancia + tarea abierta (seguimiento por proceso)
CREATE INDEX IF NOT EXISTS ACT_IDX_EXTRA_TASK_PROC_EXEC ON ACT_RU_TASK (PROC_INST_ID_, EXECUTION_ID_);

-- ACT_HI_TASKINST: historial por asignatario y cierre (reportes, auditoría)
CREATE INDEX IF NOT EXISTS ACT_IDX_EXTRA_HI_TASK_ASSIGNEE_END ON ACT_HI_TASKINST (ASSIGNEE_, END_TIME_);

-- ACT_HI_TASKINST: historial por clave de definición de proceso y tiempo de fin
CREATE INDEX IF NOT EXISTS ACT_IDX_EXTRA_HI_TASK_PDEFKEY_END ON ACT_HI_TASKINST (PROC_DEF_KEY_, END_TIME_);

-- ACT_HI_TASKINST: definición de proceso + instancia (trazabilidad)
CREATE INDEX IF NOT EXISTS ACT_IDX_EXTRA_HI_TASK_PROCDEF_PROCINST ON ACT_HI_TASKINST (PROC_DEF_ID_, PROC_INST_ID_);

-- ACT_HI_VARINST: lectura de variables por instancia y nombre (consultas masivas)
CREATE INDEX IF NOT EXISTS ACT_IDX_EXTRA_HI_VAR_PROC_NAME ON ACT_HI_VARINST (PROC_INST_ID_, NAME_);

-- ACT_HI_VARINST: variables por actividad (correlación con actividad histórica)
CREATE INDEX IF NOT EXISTS ACT_IDX_EXTRA_HI_VAR_ACT_NAME ON ACT_HI_VARINST (ACT_INST_ID_, NAME_);

-- ACT_RU_VARIABLE: variables runtime por instancia y nombre (evita escaneos en procesos con muchas variables)
CREATE INDEX IF NOT EXISTS ACT_IDX_EXTRA_RU_VAR_PROC_NAME ON ACT_RU_VARIABLE (PROC_INST_ID_, NAME_);

-- ACT_HI_ACTINST: actividades por proceso y tiempo de fin (informes por proceso)
CREATE INDEX IF NOT EXISTS ACT_IDX_EXTRA_HI_ACT_PROC_END ON ACT_HI_ACTINST (PROC_INST_ID_, END_TIME_);

-- ACT_HI_PROCINST: búsqueda por clave de definición y rango temporal de inicio
CREATE INDEX IF NOT EXISTS ACT_IDX_EXTRA_HI_PI_KEY_START ON ACT_HI_PROCINST (PROC_DEF_KEY_, START_TIME_);

-- ACT_RU_EXT_TASK: colas de external task por prioridad y bloqueo (workers)
CREATE INDEX IF NOT EXISTS ACT_IDX_EXTRA_EXT_TASK_TOPIC_PRIO ON ACT_RU_EXT_TASK (TOPIC_NAME_, PRIORITY_, LOCK_EXP_TIME_);
