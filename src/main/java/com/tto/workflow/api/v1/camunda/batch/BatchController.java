package com.tto.workflow.api.v1.camunda.batch;

import com.tto.workflow.service.CamundaBatchManagementService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/batch")
@Tag(name = "Batch", description = "Paridad REST: lotes (batch)")
public class BatchController {

  private final CamundaBatchManagementService batchManagementService;

  public BatchController(CamundaBatchManagementService batchManagementService) {
    this.batchManagementService = batchManagementService;
  }

  @GetMapping
  @Operation(summary = "Listar lotes")
  public List<Map<String, String>> list(
      @RequestParam(required = false) Integer firstResult, @RequestParam(required = false) Integer maxResults) {
    return batchManagementService.listBatches(firstResult, maxResults);
  }
}
