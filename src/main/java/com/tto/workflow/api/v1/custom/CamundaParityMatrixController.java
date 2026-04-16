package com.tto.workflow.api.v1.custom;

import com.tto.workflow.service.CamundaParityMatrixService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/custom/camunda-parity")
@Tag(name = "CamundaParity", description = "Diagnóstico de cobertura de paridad Camunda 7.24")
public class CamundaParityMatrixController {

  private final CamundaParityMatrixService matrixService;

  public CamundaParityMatrixController(CamundaParityMatrixService matrixService) {
    this.matrixService = matrixService;
  }

  @GetMapping(value = "/matrix", produces = MediaType.APPLICATION_JSON_VALUE)
  @Operation(summary = "Resumen de matriz endpoint-a-endpoint basado en OpenAPI oficial 7.24")
  public Map<String, Object> matrix() {
    return matrixService.buildSummary();
  }
}
