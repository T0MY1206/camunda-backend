package com.tto.workflow.api.v1.camunda.history;

import com.tto.workflow.api.v1.dto.camunda.HistoricActivityInstanceDto;
import com.tto.workflow.service.CamundaHistoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/history/activity-instance")
@Tag(name = "HistoryActivityInstance", description = "Paridad REST: historial de actividades")
public class HistoryActivityInstanceController {

  private final CamundaHistoryQueryService historyQueryService;

  public HistoryActivityInstanceController(CamundaHistoryQueryService historyQueryService) {
    this.historyQueryService = historyQueryService;
  }

  @GetMapping
  @Operation(summary = "Listar instancias de actividad históricas")
  public List<HistoricActivityInstanceDto> list(
      @RequestParam(required = false) String processInstanceId,
      @RequestParam(required = false) String activityId,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return historyQueryService.listActivityInstances(processInstanceId, activityId, firstResult, maxResults);
  }
}
