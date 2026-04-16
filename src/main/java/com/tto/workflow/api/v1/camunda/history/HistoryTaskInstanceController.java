package com.tto.workflow.api.v1.camunda.history;

import com.tto.workflow.api.v1.dto.camunda.HistoricTaskInstanceDto;
import com.tto.workflow.service.CamundaHistoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/history/task-instance")
@Tag(name = "HistoryTaskInstance", description = "Paridad REST: historial de tareas")
public class HistoryTaskInstanceController {

  private final CamundaHistoryQueryService historyQueryService;

  public HistoryTaskInstanceController(CamundaHistoryQueryService historyQueryService) {
    this.historyQueryService = historyQueryService;
  }

  @GetMapping
  @Operation(summary = "Listar tareas históricas")
  public List<HistoricTaskInstanceDto> list(
      @RequestParam(required = false) String processInstanceId,
      @RequestParam(required = false) String taskDefinitionKey,
      @RequestParam(required = false) String assignee,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return historyQueryService.listTaskInstances(
        processInstanceId, taskDefinitionKey, assignee, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Tarea histórica por id")
  public HistoricTaskInstanceDto get(@PathVariable String id) {
    return historyQueryService.getTaskInstance(id);
  }
}
