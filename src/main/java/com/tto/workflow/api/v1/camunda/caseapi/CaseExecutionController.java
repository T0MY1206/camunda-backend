package com.tto.workflow.api.v1.camunda.caseapi;

import com.tto.workflow.service.CamundaCaseService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/case-execution")
@Tag(name = "CaseExecution", description = "Paridad REST: CMMN (ejecuciones de caso)")
public class CaseExecutionController {

  private final CamundaCaseService caseService;

  public CaseExecutionController(CamundaCaseService caseService) {
    this.caseService = caseService;
  }

  @GetMapping
  @Operation(summary = "Listar ejecuciones de caso")
  public List<Map<String, String>> list(
      @RequestParam(required = false) String caseInstanceId,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return caseService.listCaseExecutions(caseInstanceId, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Ejecución de caso por id")
  public Map<String, String> get(@PathVariable String id) {
    return caseService.getCaseExecution(id);
  }
}
