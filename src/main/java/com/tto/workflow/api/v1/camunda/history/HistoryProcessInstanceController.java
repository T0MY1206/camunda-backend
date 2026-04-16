package com.tto.workflow.api.v1.camunda.history;

import com.tto.workflow.api.v1.dto.camunda.HistoricProcessInstanceDto;
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
@RequestMapping("/api/v1/camunda/history/process-instance")
@Tag(name = "HistoryProcessInstance", description = "Paridad REST: historial de instancias de proceso")
public class HistoryProcessInstanceController {

  private final CamundaHistoryQueryService historyQueryService;

  public HistoryProcessInstanceController(CamundaHistoryQueryService historyQueryService) {
    this.historyQueryService = historyQueryService;
  }

  @GetMapping
  @Operation(summary = "Listar instancias históricas")
  public List<HistoricProcessInstanceDto> list(
      @RequestParam(required = false) String processDefinitionKey,
      @RequestParam(required = false) String processInstanceId,
      @RequestParam(required = false) String businessKey,
      @RequestParam(required = false) Boolean finished,
      @RequestParam(required = false) String tenantId,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return historyQueryService.listProcessInstances(
        processDefinitionKey, processInstanceId, businessKey, finished, tenantId, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Instancia histórica por id")
  public HistoricProcessInstanceDto get(@PathVariable String id) {
    return historyQueryService.getProcessInstance(id);
  }
}
