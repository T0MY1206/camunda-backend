package com.tto.workflow.api.v1.camunda.incident;

import com.tto.workflow.api.v1.dto.camunda.IncidentDto;
import com.tto.workflow.service.CamundaIncidentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/incident")
@Tag(name = "Incident", description = "Paridad REST: incidencias")
public class IncidentController {

  private final CamundaIncidentService incidentService;

  public IncidentController(CamundaIncidentService incidentService) {
    this.incidentService = incidentService;
  }

  @GetMapping
  @Operation(summary = "Listar incidencias")
  public List<IncidentDto> list(
      @RequestParam(required = false) String processInstanceId,
      @RequestParam(required = false) String incidentType,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return incidentService.list(processInstanceId, incidentType, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Incidencia por id")
  public IncidentDto get(@PathVariable String id) {
    return incidentService.get(id);
  }
}
