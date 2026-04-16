package com.tto.workflow.api.v1.camunda.decision;

import com.tto.workflow.api.v1.dto.camunda.DecisionDefinitionDto;
import com.tto.workflow.service.CamundaDecisionService;
import com.tto.workflow.service.CamundaDmnEvaluationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/decision-definition")
@Tag(name = "DecisionDefinition", description = "Paridad REST: definiciones DMN")
public class DecisionDefinitionController {

  private final CamundaDecisionService decisionService;
  private final CamundaDmnEvaluationService dmnEvaluationService;

  public DecisionDefinitionController(
      CamundaDecisionService decisionService, CamundaDmnEvaluationService dmnEvaluationService) {
    this.decisionService = decisionService;
    this.dmnEvaluationService = dmnEvaluationService;
  }

  @GetMapping
  @Operation(summary = "Listar definiciones de decisión")
  public List<DecisionDefinitionDto> list(
      @RequestParam(required = false) String key,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String tenantId,
      @RequestParam(required = false) Boolean latestVersion,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return decisionService.list(key, name, tenantId, latestVersion, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Definición de decisión por id")
  public DecisionDefinitionDto get(@PathVariable String id) {
    return decisionService.get(id);
  }

  @PostMapping("/evaluate")
  @Operation(summary = "Evaluar tabla DMN por clave (variables en cuerpo JSON)")
  public Map<String, Object> evaluate(
      @RequestParam String key, @RequestBody(required = false) Map<String, Object> variables) {
    return dmnEvaluationService.evaluateByKey(key, variables);
  }
}
