package com.tto.workflow.api.v1.camunda.execution;

import com.tto.workflow.api.v1.dto.camunda.ExecutionDto;
import com.tto.workflow.service.CamundaExecutionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/execution")
@Tag(name = "Execution", description = "Paridad REST: ejecuciones")
public class ExecutionController {

  private final CamundaExecutionService executionService;

  public ExecutionController(CamundaExecutionService executionService) {
    this.executionService = executionService;
  }

  @GetMapping
  @Operation(summary = "Listar ejecuciones")
  public List<ExecutionDto> list(
      @RequestParam(required = false) String processInstanceId,
      @RequestParam(required = false) String activityId,
      @RequestParam(required = false) String tenantId,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return executionService.list(processInstanceId, activityId, tenantId, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Ejecución por id")
  public ExecutionDto get(@PathVariable String id) {
    return executionService.get(id);
  }
}
