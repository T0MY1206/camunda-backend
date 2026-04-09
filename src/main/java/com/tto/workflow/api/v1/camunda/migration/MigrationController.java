package com.tto.workflow.api.v1.camunda.migration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/migration")
@Tag(name = "Migration", description = "Migración de instancias BPMN (placeholder)")
public class MigrationController {

  @GetMapping
  @Operation(summary = "Información sobre migración de procesos")
  public Map<String, String> info() {
    return Map.of(
        "note",
        "Para planes de migración use RuntimeService.createMigrationPlanBuilder() o la API REST oficial extendida.");
  }
}
