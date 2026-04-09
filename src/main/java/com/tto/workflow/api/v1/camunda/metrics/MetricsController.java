package com.tto.workflow.api.v1.camunda.metrics;

import com.tto.workflow.service.CamundaEngineInfoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/metrics")
@Tag(name = "Metrics", description = "Paridad REST: métricas de tablas (conteos)")
public class MetricsController {

  private final CamundaEngineInfoService engineInfoService;

  public MetricsController(CamundaEngineInfoService engineInfoService) {
    this.engineInfoService = engineInfoService;
  }

  @GetMapping
  @Operation(summary = "Conteos de filas por tabla (opcionalmente una tabla)")
  public Map<String, Long> metrics(@RequestParam(required = false) String name) {
    return engineInfoService.metrics(name);
  }
}
