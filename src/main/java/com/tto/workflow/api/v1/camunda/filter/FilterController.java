package com.tto.workflow.api.v1.camunda.filter;

import com.tto.workflow.service.CamundaFilterQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/filter")
@Tag(name = "Filter", description = "Paridad REST: filtros guardados")
public class FilterController {

  private final CamundaFilterQueryService filterQueryService;

  public FilterController(CamundaFilterQueryService filterQueryService) {
    this.filterQueryService = filterQueryService;
  }

  @GetMapping
  @Operation(summary = "Listar filtros (resumen)")
  public List<Map<String, String>> list(
      @RequestParam(required = false) Integer firstResult, @RequestParam(required = false) Integer maxResults) {
    return filterQueryService.list(firstResult, maxResults);
  }
}
