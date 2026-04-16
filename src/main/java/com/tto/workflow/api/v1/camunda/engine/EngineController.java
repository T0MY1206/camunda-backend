package com.tto.workflow.api.v1.camunda.engine;

import com.tto.workflow.service.CamundaEngineInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/engine")
@Tag(name = "Engine", description = "Paridad REST: información del motor")
public class EngineController {

  private final CamundaEngineInfoService engineInfoService;

  public EngineController(CamundaEngineInfoService engineInfoService) {
    this.engineInfoService = engineInfoService;
  }

  @GetMapping
  @Operation(summary = "Información del motor embebido")
  public Map<String, Object> info() {
    return engineInfoService.engineInfo();
  }

  @GetMapping("/schema-version")
  @Operation(summary = "Versión de esquema (ACT_GE_PROPERTY)")
  public Map<String, String> schemaVersion() {
    return Map.of("schemaVersion", engineInfoService.schemaVersion());
  }
}
