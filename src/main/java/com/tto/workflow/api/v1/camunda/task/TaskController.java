package com.tto.workflow.api.v1.camunda.task;

import com.tto.workflow.api.v1.dto.camunda.CompleteTaskDto;
import com.tto.workflow.api.v1.dto.camunda.TaskDto;
import com.tto.workflow.service.CamundaTaskService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/task")
@Tag(name = "Task", description = "Paridad REST: tareas")
public class TaskController {

  private final CamundaTaskService taskService;

  public TaskController(CamundaTaskService taskService) {
    this.taskService = taskService;
  }

  @GetMapping
  @Operation(summary = "Listar tareas")
  public List<TaskDto> list(
      @RequestParam(required = false) String assignee,
      @RequestParam(required = false) String assigneeLike,
      @RequestParam(required = false) String processInstanceId,
      @RequestParam(required = false) String processDefinitionKey,
      @RequestParam(required = false) String tenantId,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return taskService.list(
        assignee, assigneeLike, processInstanceId, processDefinitionKey, tenantId, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Tarea por id")
  public TaskDto get(@PathVariable String id) {
    return taskService.get(id);
  }

  @PostMapping("/{id}/complete")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Completar tarea")
  public void complete(@PathVariable String id, @RequestBody(required = false) CompleteTaskDto body) {
    taskService.complete(id, body);
  }
}
