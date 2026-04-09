package com.tto.workflow.api.v1.camunda.telemetry;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/telemetry")
@Tag(name = "Telemetry", description = "Telemetría (placeholder; en EE consultar documentación Camunda)")
public class TelemetryController {

  @GetMapping
  @Operation(summary = "Endpoint placeholder para telemetría")
  public Map<String, String> telemetry() {
    return Map.of(
        "note",
        "La telemetría detallada depende de la edición Camunda; use configuración del motor o Cockpit.");
  }
}
