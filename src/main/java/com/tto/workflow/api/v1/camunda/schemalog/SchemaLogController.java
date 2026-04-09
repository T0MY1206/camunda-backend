package com.tto.workflow.api.v1.camunda.schemalog;

import com.tto.workflow.api.v1.dto.camunda.SchemaLogDto;
import com.tto.workflow.service.CamundaSchemaLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/schema-log")
@Tag(name = "SchemaLog", description = "Paridad REST: log de versiones de esquema (JPA ACT_GE_SCHEMA_LOG)")
public class SchemaLogController {

  private final CamundaSchemaLogService schemaLogService;

  public SchemaLogController(CamundaSchemaLogService schemaLogService) {
    this.schemaLogService = schemaLogService;
  }

  @GetMapping
  @Operation(summary = "Listar entradas de log de esquema")
  public List<SchemaLogDto> list() {
    return schemaLogService.list();
  }
}
