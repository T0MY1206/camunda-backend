package com.tto.workflow.api.v1.camunda.externaltask;

import com.tto.workflow.api.v1.dto.camunda.ExternalTaskDto;
import com.tto.workflow.service.CamundaExternalTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/external-task")
@Tag(name = "ExternalTask", description = "Paridad REST: tareas externas")
public class ExternalTaskController {

  private final CamundaExternalTaskService externalTaskService;

  public ExternalTaskController(CamundaExternalTaskService externalTaskService) {
    this.externalTaskService = externalTaskService;
  }

  @GetMapping
  @Operation(summary = "Listar tareas externas")
  public List<ExternalTaskDto> list(
      @RequestParam(required = false) String topicName,
      @RequestParam(required = false) String workerId,
      @RequestParam(required = false) String processInstanceId,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return externalTaskService.list(topicName, workerId, processInstanceId, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Tarea externa por id")
  public ExternalTaskDto get(@PathVariable String id) {
    return externalTaskService.get(id);
  }
}
