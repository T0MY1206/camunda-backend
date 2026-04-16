package com.tto.workflow.api.v1.camunda.historycleanup;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/history/cleanup")
@Tag(name = "HistoryCleanup", description = "Limpieza de historial (placeholder)")
public class HistoryCleanupController {

  @GetMapping
  @Operation(summary = "Información sobre limpieza de historial")
  public Map<String, String> info() {
    return Map.of(
        "note",
        "Configure history cleanup vía motor (camunda.bpm) o HistoryService; operaciones destructivas no se exponen aquí por defecto.");
  }
}
