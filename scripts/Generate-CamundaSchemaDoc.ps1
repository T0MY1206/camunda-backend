# Genera docs/camunda-schema.md a partir de los DDL en src/main/resources/db/camunda/7.24.0/postgres/
# Ejecutar desde la raiz del repo: powershell -File scripts/Generate-CamundaSchemaDoc.ps1
$ErrorActionPreference = "Stop"
$root = Split-Path $PSScriptRoot -Parent
$sqlDir = Join-Path $root "src/main/resources/db/camunda/7.24.0/postgres"
$outFile = Join-Path $root "docs/camunda-schema.md"

function Get-TablePurpose {
  param([string]$name)
  $h = @{
    'ACT_GE_PROPERTY'        = 'Propiedades globales del motor (version de esquema, locks, contadores de id, etc.).'
    'ACT_GE_BYTEARRAY'       = 'Almacenamiento de blobs (definiciones BPMN serializadas, variables grandes, stacks de error, adjuntos).'
    'ACT_GE_SCHEMA_LOG'      = 'Registro de versiones de esquema aplicadas al arrancar o migrar la base.'
    'ACT_RE_DEPLOYMENT'      = 'Unidad de despliegue: agrupa recursos BPMN/DMN/CMMN subidos al motor.'
    'ACT_RE_PROCDEF'         = 'Definicion de proceso BPMN versionada (clave, version, suspension, tenant).'
    'ACT_RE_CAMFORMDEF'      = 'Definiciones de formularios embebidos asociados al despliegue.'
    'ACT_RU_EXECUTION'       = 'Ejecucion runtime (token BPMN): arbol de instancia, actividad actual, parent/super, business key.'
    'ACT_RU_TASK'            = 'Tareas de usuario abiertas (Tasklist): asignatario, prioridad, due date, enlaces a ejecucion e instancia.'
    'ACT_RU_VARIABLE'        = 'Variables de proceso/tarea/caso en runtime (valor inline o referencia a byte array).'
    'ACT_RU_IDENTITYLINK'    = 'Participantes de tareas o iniciadores/candidatos (usuarios/grupos) en runtime.'
    'ACT_RU_EVENT_SUBSCR'    = 'Suscripciones a eventos (mensaje, senal, compensacion, condicional) pendientes.'
    'ACT_RU_JOB'             = 'Jobs asincronos (timers, async continuation, mensajes) para el Job Executor.'
    'ACT_RU_JOBDEF'          = 'Definiciones de job asociadas a actividades o procesos.'
    'ACT_RU_INCIDENT'        = 'Incidentes tecnicos (fallos de job, errores de conector) enlazados a ejecucion o job.'
    'ACT_RU_AUTHORIZATION'   = 'Autorizaciones granulares (ACL) para recursos del motor y webapps.'
    'ACT_RU_FILTER'          = 'Filtros guardados de Tasklist (queries reutilizables).'
    'ACT_RU_METER_LOG'       = 'Metricas internas de uso (contadores por nombre y ventana temporal).'
    'ACT_RU_TASK_METER_LOG'  = 'Metricas anonimizadas de asignacion de tareas.'
    'ACT_RU_EXT_TASK'        = 'Tareas externas (workers): topic, lock, prioridad, reintentos.'
    'ACT_RU_BATCH'           = 'Metadatos de operaciones por lotes (modificacion masiva, migracion, etc.).'
    'ACT_HI_PROCINST'        = 'Instancia de proceso historica: inicio, fin, business key, definicion, removal time.'
    'ACT_HI_ACTINST'         = 'Instancias de actividad historicas (una fila por token/actividad completada o abierta en historia).'
    'ACT_HI_TASKINST'        = 'Tareas historicas (abiertas y cerradas segun nivel de historia).'
    'ACT_HI_VARINST'         = 'Variables historicas (valor final o serie de cambios segun configuracion).'
    'ACT_HI_DETAIL'          = 'Detalle de actualizacion de variables (audit trail fino).'
    'ACT_HI_IDENTITYLINK'    = 'Historial de identity links en tareas/procesos.'
    'ACT_HI_COMMENT'         = 'Comentarios en tareas o instancias.'
    'ACT_HI_ATTACHMENT'      = 'Adjuntos historicos.'
    'ACT_HI_OP_LOG'          = 'Log de operaciones de usuario (cockpit/tasklist) para auditoria.'
    'ACT_HI_INCIDENT'        = 'Incidentes historicos.'
    'ACT_HI_JOB_LOG'         = 'Historial de ejecucion de jobs (exito/fallo, mensajes).'
    'ACT_HI_BATCH'           = 'Historial de lotes completados.'
    'ACT_HI_EXT_TASK_LOG'    = 'Historial de ciclo de vida de external tasks.'
    'ACT_RE_CASE_DEF'        = 'Definicion de caso CMMN versionada.'
    'ACT_RU_CASE_EXECUTION'  = 'Ejecucion de caso CMMN (arbol de case execution).'
    'ACT_RU_CASE_SENTRY_PART' = 'Partes de centinelas CMMN (condiciones de activacion).'
    'ACT_HI_CASEINST'        = 'Instancia de caso historica.'
    'ACT_HI_CASEACTINST'     = 'Actividades de caso historicas.'
    'ACT_RE_DECISION_DEF'    = 'Definicion DMN desplegada.'
    'ACT_RE_DECISION_REQ_DEF' = 'Requerimientos de decision DMN (DRD).'
    'ACT_HI_DECINST'         = 'Instancias de decision DMN evaluadas.'
    'ACT_HI_DEC_IN'          = 'Entradas de decision historicas.'
    'ACT_HI_DEC_OUT'         = 'Salidas de decision historicas.'
    'ACT_ID_GROUP'           = 'Grupos de usuarios del identity service embebido.'
    'ACT_ID_USER'            = 'Usuarios (credenciales si se usa el proveedor embebido).'
    'ACT_ID_MEMBERSHIP'      = 'Relacion usuario-grupo.'
    'ACT_ID_INFO'            = 'Metadatos clave-valor por usuario (p. ej. preferencias).'
    'ACT_ID_TENANT'          = 'Tenants del identity service.'
    'ACT_ID_TENANT_MEMBER'   = 'Miembros de tenant (usuarios o grupos).'
  }
  if ($h.ContainsKey($name)) { return $h[$name] }
  if ($name -match '^ACT_RE_') { return 'Repositorio: definiciones y metadatos de despliegue relativamente estaticos.' }
  if ($name -match '^ACT_RU_') { return 'Runtime: estado vivo del motor; se elimina o archiva al completar instancias.' }
  if ($name -match '^ACT_HI_') { return 'Historial: datos para auditoria e informes; alto volumen; sin FK entre tablas HI por diseno.' }
  if ($name -match '^ACT_ID_') { return 'Identidad: usuarios, grupos y permisos embebidos (opcional si se usa otro IdP).' }
  if ($name -match '^ACT_GE_') { return 'General: datos transversales (propiedades, blobs, log de esquema).' }
  return 'Tabla del modelo relacional del motor Camunda.'
}

function Get-IndexComment {
  param([string]$iname, [string]$table, [string]$cols)
  if ($cols -match 'DEPLOYMENT_ID') { return 'Recursos binarios ligados a un despliegue (FK y listados por deployment).' }
  if ($cols -match '^NAME_' -and $table -eq 'ACT_GE_BYTEARRAY') { return 'Busqueda de entradas de byte array por nombre de recurso.' }
  if ($iname -match 'BYTEAR_DEPL') { return 'Recursos binarios ligados a un despliegue (FK y listados por deployment).' }
  $patterns = [ordered]@{
    'ROOT_PI'                  = 'Filas por instancia de proceso raiz (arbol, historia, cleanup por removal time).'
    'RM_TIME'                  = 'Historificacion y borrado programado (history cleanup) por REMOVAL_TIME_.'
    'TENANT_ID'                = 'Aislamiento multi-tenant en consultas de API y webapps.'
    'BUSINESS_KEY'             = 'Busqueda de instancias por clave de negocio.'
    'PROC_DEF_KEY'             = 'Filtros por clave logica de definicion (sin version).'
    'PROC_DEF_ID'              = 'Joins y filtros por id de definicion desplegada (con version).'
    'PROC_INST_ID'             = 'Navegacion por instancia de proceso (runtime o historia).'
    'EXECUTION_ID'             = 'Enlace al token de ejecucion BPMN concreto.'
    'TASK_ID'                  = 'Acceso a variables, identity links o comentarios ligados a una tarea.'
    'ASSIGNEE'                 = 'Bandejas por asignatario (Tasklist, REST task query).'
    'OWNER_'                   = 'Filtrado por propietario de tarea.'
    'CREATE_TIME'              = 'Ordenacion o ventanas temporales por creacion.'
    'LAST_UPDATED'             = 'Ordenacion por ultima actualizacion de tarea.'
    'DUE_DATE'                 = 'Tareas ordenadas o filtradas por vencimiento.'
    'PRIORITY'                 = 'Ordenacion por prioridad de tarea o external task.'
    'START_TIME'               = 'Informes e intervalos por inicio de actividad o proceso.'
    'END_TIME'                 = 'Informes por fin de actividad/proceso; instancias completadas.'
    'BYTEARRAY_ID'             = 'Resolucion de valores grandes o adjuntos almacenados en ACT_GE_BYTEARRAY.'
    'JOB_DEF'                  = 'Jobs o logs asociados a una definicion de job concreta.'
    'HANDLER_TYPE'             = 'Seleccion de jobs por tipo de handler (async, timer, etc.).'
    'TOPIC_NAME'               = 'Colas de external task por topic (workers).'
    'LOCK_EXP_TIME'            = 'Reclamacion y expiracion de locks en external tasks.'
    'RESOURCE_ID'              = 'Autorizaciones por id de recurso (incl. comodines).'
    'GROUP_ID'                 = 'Resolucion de membership y permisos de grupo.'
    'USER_ID'                  = 'Resolucion por usuario (identity links, op log).'
    'EVENT_NAME'               = 'Suscripciones y correlacion por nombre de evento.'
    'CONFIGURATION_'           = 'Correlacion de eventos (p. ej. mensaje) por configuracion.'
    'CASE_INST_ID'             = 'Navegacion CMMN por instancia de caso.'
    'CASE_EXECUTION_ID'        = 'Variables o detalle ligados a ejecucion de caso.'
    'METER_LOG'                = 'Agregacion de metricas por nombre/tiempo/reporter.'
    'TASK_METER_LOG'           = 'Metricas de tareas (asignacion) por tiempo.'
    'TIMESTAMP'                = 'Orden temporal de entradas de log (operacion, identity link historico).'
    'VAR_INST_ID'              = 'Enlace al registro de variable historica en detalles.'
    'ACT_INST_ID'              = 'Correlacion con instancia de actividad historica.'
    'OPERATION_TYPE'           = 'Filtrado de op log por tipo de operacion.'
    'ENTITY_TYPE'              = 'Filtrado de op log por entidad afectada.'
    'ERROR_DETAILS'            = 'Enlace a stack o detalle de error en external task.'
    'RESTARTED_PROC_INST'      = 'Seguimiento de reinicios de instancia.'
    'STATS'                    = 'Consultas de estadisticas de actividad (Cockpit/reports).'
    'COMP'                     = 'Indice compuesto para compensacion / estado de instancia de actividad.'
    'PROCVAR_NAME_TYPE'        = 'Busqueda de variables historicas por nombre y tipo dentro de instancia.'
    'VAR_PI_NAME_TYPE'         = 'Clave compuesta instancia + nombre + tipo para variables historicas.'
    'EX_STACK'                 = 'Traza de excepcion persistida en byte array.'
    'BATCH'                    = 'Operaciones por lote (seed/monitor/batch jobs).'
    'SEED_JOB_DEF'             = 'Job semilla de batch.'
    'MONITOR_JOB_DEF'          = 'Job monitor de batch.'
    'SENTRY'                   = 'Evaluacion de partes de centinela CMMN.'
    'DECISION'                 = 'Instancias y entradas/salidas DMN historicas.'
    'EXT_TASK_LOG'             = 'Historial de eventos de external task.'
    'ATTACHMENT'               = 'Adjuntos por tarea o instancia.'
    'COMMENT'                  = 'Comentarios por tarea o proceso.'
    'IDENT_LNK'                = 'Identity links historicos (candidatos, participantes).'
    'INCIDENT'                 = 'Incidentes runtime o historicos por proceso/ejecucion.'
    'OP_LOG'                   = 'Log de operaciones de usuario.'
    'JOB_LOG'                  = 'Historial de intentos de job.'
    'DETAIL'                   = 'Detalle fino de cambios (variables, propiedades).'
    'CASE_EXE'                 = 'Ejecucion de caso CMMN (arbol, definicion).'
    'DEPLOYMENT_ID'            = 'Recursos ligados a un despliegue concreto.'
    'VERSION_TAG'              = 'Busqueda de definiciones por etiqueta de version.'
    'NAME_'                    = 'Filtrado por nombre logico (recurso, metrica, detalle).'
  }
  foreach ($key in $patterns.Keys) {
    if ($iname -match $key) { return $patterns[$key] }
  }
  return "Optimiza consultas que filtran o ordenan por ($cols) en ``$table`` (motor, APIs REST o webapps)."
}

function Parse-CreateTables {
  param([string]$sql)
  $tables = @{}
  $rx = [regex]'(?is)create\s+table\s+(\w+)\s*\((.*?)\)\s*;'
  foreach ($m in $rx.Matches($sql)) {
    $tname = $m.Groups[1].Value
    $body = $m.Groups[2].Value
    $cols = New-Object System.Collections.Generic.List[string]
    foreach ($line in ($body -split "`n")) {
      $trim = $line.Trim()
      if ($trim -match '^(primary key|constraint|unique)') { continue }
      if ($trim -eq "" -or $trim.StartsWith("--")) { continue }
      if ($trim -match '^(\w+)\s') { $cols.Add($trim.TrimEnd(',')) }
    }
    $tables[$tname] = $cols
  }
  $tables
}

function Parse-Indexes {
  param([string]$sql)
  $byTable = @{}
  $rx = [regex]'(?is)create\s+(unique\s+)?index\s+(\w+)\s+on\s+(\w+)\s*\(([^)]+)\)\s*;'
  foreach ($m in $rx.Matches($sql)) {
    $uniq = $m.Groups[1].Value -ne ""
    $iname = $m.Groups[2].Value
    $tname = $m.Groups[3].Value
    $cols = ($m.Groups[4].Value -replace '\s+', ' ').Trim()
    if (-not $byTable.ContainsKey($tname)) { $byTable[$tname] = New-Object System.Collections.Generic.List[hashtable] }
    $byTable[$tname].Add(@{ Name = $iname; Unique = $uniq; Columns = $cols })
  }
  $byTable
}

function Parse-ForeignKeys {
  param([string]$sql)
  $list = New-Object System.Collections.Generic.List[hashtable]
  $rx = [regex]'(?is)alter\s+table\s+(\w+)\s+add\s+constraint\s+(\w+)\s+foreign\s+key\s*\(([^)]+)\)\s*references\s+(\w+)\s*\(([^)]+)\)'
  foreach ($m in $rx.Matches($sql)) {
    $list.Add(@{
      FromTable = $m.Groups[1].Value
      Name      = $m.Groups[2].Value
      FromCols  = $m.Groups[3].Value.Trim()
      ToTable   = $m.Groups[4].Value
      ToCols    = $m.Groups[5].Value.Trim()
    })
  }
  $list
}

$identitySql = Get-Content (Join-Path $sqlDir "identity.sql") -Raw -Encoding UTF8
$engineSql = Get-Content (Join-Path $sqlDir "engine.sql") -Raw -Encoding UTF8
$extraSql = ""
if (Test-Path (Join-Path $sqlDir "extra-indexes.sql")) {
  $extraSql = Get-Content (Join-Path $sqlDir "extra-indexes.sql") -Raw -Encoding UTF8
}

$allSql = $identitySql + "`n" + $engineSql
$tables = Parse-CreateTables $allSql
$fks = Parse-ForeignKeys $allSql
$idxDefault = Parse-Indexes $allSql
$idxExtra = Parse-Indexes $extraSql

$fkByFrom = @{}
foreach ($fk in $fks) {
  if (-not $fkByFrom.ContainsKey($fk.FromTable)) { $fkByFrom[$fk.FromTable] = New-Object System.Collections.Generic.List[string] }
  [void]$fkByFrom[$fk.FromTable].Add("$($fk.Name): $($fk.FromCols) -> $($fk.ToTable)($($fk.ToCols))")
}

$sb = New-Object System.Text.StringBuilder
[void]$sb.AppendLine("# Esquema de base de datos Camunda 7.24.0 (PostgreSQL)")
[void]$sb.AppendLine("")
[void]$sb.AppendLine("Este documento describe las tablas del motor de procesos, sus columnas, relaciones explicitas (FK), indices por defecto del DDL oficial y los indices adicionales definidos en ``extra-indexes.sql``.")
[void]$sb.AppendLine("")
[void]$sb.AppendLine("**Origen del DDL:** scripts ``activiti.postgres.create.*.sql`` empaquetados en ``camunda-engine-7.24.0.jar``. Orden de aplicacion: ``identity.sql`` y luego ``engine.sql``; opcionalmente ``extra-indexes.sql``.")
[void]$sb.AppendLine("")
[void]$sb.AppendLine("## Convenciones de nombres")
[void]$sb.AppendLine("")
[void]$sb.AppendLine("| Prefijo | Significado |")
[void]$sb.AppendLine("|--------|-------------|")
[void]$sb.AppendLine("| ``ACT_RE_*`` | Repository |")
[void]$sb.AppendLine("| ``ACT_RU_*`` | Runtime |")
[void]$sb.AppendLine("| ``ACT_HI_*`` | History |")
[void]$sb.AppendLine("| ``ACT_ID_*`` | Identity |")
[void]$sb.AppendLine("| ``ACT_GE_*`` | General |")
[void]$sb.AppendLine("")
[void]$sb.AppendLine("## Indices adicionales (rendimiento)")
[void]$sb.AppendLine("")
[void]$sb.AppendLine("Definidos en [extra-indexes.sql](../src/main/resources/db/camunda/7.24.0/postgres/extra-indexes.sql). Complementan consultas con millones de filas; cada indice suma coste de escritura. Validar con ``EXPLAIN ANALYZE``.")
[void]$sb.AppendLine("")

foreach ($t in ($idxExtra.Keys | Sort-Object)) {
  foreach ($ix in $idxExtra[$t]) {
    $desc = Get-IndexComment -iname $ix.Name -table $t -cols $ix.Columns
    [void]$sb.AppendLine("- **``$($ix.Name)``** en ``$t`` (``$($ix.Columns)``): $desc")
  }
}
[void]$sb.AppendLine("")

$tableNames = $tables.Keys | Sort-Object
foreach ($t in $tableNames) {
  [void]$sb.AppendLine("## ``$t``")
  [void]$sb.AppendLine("")
  [void]$sb.AppendLine("### Para que se usa")
  [void]$sb.AppendLine("")
  [void]$sb.AppendLine((Get-TablePurpose $t))
  [void]$sb.AppendLine("")
  [void]$sb.AppendLine("### Uso conjunto con otras tablas")
  [void]$sb.AppendLine("")
  if ($fkByFrom.ContainsKey($t)) {
    foreach ($line in $fkByFrom[$t]) {
      [void]$sb.AppendLine("- FK: $line")
    }
  }
  $refsTo = @()
  foreach ($fk in $fks) {
    if ($fk.ToTable -eq $t) { $refsTo += "$($fk.FromTable) (via $($fk.Name))" }
  }
  if ($refsTo.Count -gt 0) {
    [void]$sb.AppendLine("- Referenciada por: $($refsTo -join '; ').")
  }
  if (-not $fkByFrom.ContainsKey($t) -and $refsTo.Count -eq 0) {
    if ($t -match "^ACT_HI_") {
      [void]$sb.AppendLine("- Historial: relaciones logicas con ``ACT_HI_PROCINST``, ``ACT_RU_*`` / ``ACT_RE_*`` por columnas ``*_ID_`` (sin FK en el DDL).")
    } else {
      [void]$sb.AppendLine("- Ver columnas ``*_ID_`` para enlaces logicos con otras entidades del mismo dominio (GE/RE/RU/HI).")
    }
  }
  [void]$sb.AppendLine("")
  [void]$sb.AppendLine("### Columnas")
  [void]$sb.AppendLine("")
  [void]$sb.AppendLine("| Columna (definicion en DDL) |")
  [void]$sb.AppendLine("|----------------------------|")
  foreach ($c in $tables[$t]) {
    $esc = $c -replace '\|', '\|'
    [void]$sb.AppendLine("| ``$esc`` |")
  }
  [void]$sb.AppendLine("")
  [void]$sb.AppendLine("### Indices por defecto (DDL oficial)")
  [void]$sb.AppendLine("")
  if ($idxDefault.ContainsKey($t)) {
    foreach ($ix in $idxDefault[$t]) {
      $u = if ($ix.Unique) { "unico " } else { "" }
      $idc = Get-IndexComment -iname $ix.Name -table $t -cols $ix.Columns
      [void]$sb.AppendLine("- **``$($ix.Name)``** ($u``$($ix.Columns)``): $idc")
    }
  } else {
    [void]$sb.AppendLine("- (Ningun ``CREATE INDEX`` explicito en el DDL para esta tabla; la PK sigue indexada).")
  }
  [void]$sb.AppendLine("")
}

# UTF-8 con BOM para mejor compatibilidad con visores en Windows
$utf8Bom = New-Object System.Text.UTF8Encoding $true
[System.IO.File]::WriteAllText($outFile, $sb.ToString(), $utf8Bom)
Write-Host "Written $outFile"
