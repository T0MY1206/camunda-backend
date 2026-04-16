package com.tto.workflow.api.v1.camunda.history;

import com.tto.workflow.api.v1.dto.camunda.HistoricDetailDto;
import com.tto.workflow.service.CamundaHistoryQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/history/detail")
@Tag(name = "HistoryDetail", description = "Paridad REST: detalle histórico (auditoría)")
public class HistoryDetailController {

  private final CamundaHistoryQueryService historyQueryService;

  public HistoryDetailController(CamundaHistoryQueryService historyQueryService) {
    this.historyQueryService = historyQueryService;
  }

  @GetMapping
  @Operation(summary = "Listar detalles históricos")
  public List<HistoricDetailDto> list(
      @RequestParam(required = false) String processInstanceId,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return historyQueryService.listDetails(processInstanceId, firstResult, maxResults);
  }
}
