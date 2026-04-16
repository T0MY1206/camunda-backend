package com.tto.workflow.api.v1.camunda.jobdefinition;

import com.tto.workflow.api.v1.dto.camunda.JobDefinitionDto;
import com.tto.workflow.service.CamundaJobDefinitionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/job-definition")
@Tag(name = "JobDefinition", description = "Paridad REST: definiciones de job")
public class JobDefinitionController {

  private final CamundaJobDefinitionService jobDefinitionService;

  public JobDefinitionController(CamundaJobDefinitionService jobDefinitionService) {
    this.jobDefinitionService = jobDefinitionService;
  }

  @GetMapping
  @Operation(summary = "Listar definiciones de job")
  public List<JobDefinitionDto> list(
      @RequestParam(required = false) String processDefinitionId,
      @RequestParam(required = false) String jobType,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return jobDefinitionService.list(processDefinitionId, jobType, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Definición de job por id")
  public JobDefinitionDto get(@PathVariable String id) {
    return jobDefinitionService.get(id);
  }
}
