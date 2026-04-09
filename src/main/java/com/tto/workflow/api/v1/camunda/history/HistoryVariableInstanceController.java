package com.tto.workflow.api.v1.camunda.history;

import com.tto.workflow.api.v1.dto.camunda.HistoricVariableInstanceDto;
import com.tto.workflow.service.CamundaHistoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/history/variable-instance")
@Tag(name = "HistoryVariableInstance", description = "Paridad REST: variables históricas")
public class HistoryVariableInstanceController {

  private final CamundaHistoryQueryService historyQueryService;

  public HistoryVariableInstanceController(CamundaHistoryQueryService historyQueryService) {
    this.historyQueryService = historyQueryService;
  }

  @GetMapping
  @Operation(summary = "Listar variables históricas")
  public List<HistoricVariableInstanceDto> list(
      @RequestParam(required = false) String processInstanceId,
      @RequestParam(required = false) String variableName,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return historyQueryService.listVariableInstances(processInstanceId, variableName, firstResult, maxResults);
  }
}
