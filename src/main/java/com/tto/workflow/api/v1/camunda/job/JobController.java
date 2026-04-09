package com.tto.workflow.api.v1.camunda.job;

import com.tto.workflow.api.v1.dto.camunda.JobDto;
import com.tto.workflow.service.CamundaJobService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/job")
@Tag(name = "Job", description = "Paridad REST: jobs asíncronos")
public class JobController {

  private final CamundaJobService jobService;

  public JobController(CamundaJobService jobService) {
    this.jobService = jobService;
  }

  @GetMapping
  @Operation(summary = "Listar jobs")
  public List<JobDto> list(
      @RequestParam(required = false) String processInstanceId,
      @RequestParam(required = false) String jobDefinitionId,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return jobService.list(processInstanceId, jobDefinitionId, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Job por id")
  public JobDto get(@PathVariable String id) {
    return jobService.get(id);
  }
}
