package com.tto.workflow.service;

import com.tto.workflow.api.v1.common.CamundaNotFoundException;
import com.tto.workflow.api.v1.dto.camunda.CompleteTaskDto;
import com.tto.workflow.api.v1.dto.camunda.TaskDto;
import com.tto.workflow.api.v1.dto.camunda.VariableValueDto;
import com.tto.workflow.api.v1.mapper.CamundaDtoMapper;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.TaskService;
import org.camunda.bpm.engine.task.Task;
import org.camunda.bpm.engine.task.TaskQuery;
import org.camunda.bpm.engine.variable.VariableValue;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CamundaTaskService {

  private final TaskService taskService;
  private final CamundaDtoMapper mapper;

  public CamundaTaskService(TaskService taskService, CamundaDtoMapper mapper) {
    this.taskService = taskService;
    this.mapper = mapper;
  }

  public List<TaskDto> list(
      String assignee,
      String assigneeLike,
      String processInstanceId,
      String processDefinitionKey,
      String tenantId,
      Integer firstResult,
      Integer maxResults) {
    TaskQuery q = taskService.createTaskQuery().orderByTaskCreateTime().desc();
    if (assignee != null && !assignee.isBlank()) {
      q.taskAssignee(assignee);
    }
    if (assigneeLike != null && !assigneeLike.isBlank()) {
      q.taskAssigneeLike("%" + assigneeLike + "%");
    }
    if (processInstanceId != null && !processInstanceId.isBlank()) {
      q.processInstanceId(processInstanceId);
    }
    if (processDefinitionKey != null && !processDefinitionKey.isBlank()) {
      q.processDefinitionKey(processDefinitionKey);
    }
    if (tenantId != null && !tenantId.isBlank()) {
      q.tenantIdIn(tenantId);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toTaskDto).toList();
  }

  public TaskDto get(String id) {
    Task t = taskService.createTaskQuery().taskId(id).singleResult();
    if (t == null) {
      throw new CamundaNotFoundException("Task " + id + " does not exist");
    }
    return mapper.toTaskDto(t);
  }

  @Transactional
  public void complete(String id, CompleteTaskDto body) {
    Task t = taskService.createTaskQuery().taskId(id).singleResult();
    if (t == null) {
      throw new CamundaNotFoundException("Task " + id + " does not exist");
    }
    if (body != null && body.variables() != null && !body.variables().isEmpty()) {
      var vars = toVariableMap(body.variables());
      taskService.complete(id, vars);
    } else {
      taskService.complete(id);
    }
  }

  private org.camunda.bpm.engine.variable.VariableMap toVariableMap(Map<String, VariableValueDto> variables) {
    org.camunda.bpm.engine.variable.VariableMap map = Variables.createVariables();
    for (var e : variables.entrySet()) {
      VariableValueDto v = e.getValue();
      if (v == null) {
        continue;
      }
      map.putValue(e.getKey(), toVariableValue(v));
    }
    return map;
  }

  private VariableValue<?> toVariableValue(VariableValueDto v) {
    String type = v.type() != null ? v.type() : "String";
    return switch (type) {
      case "Integer" -> Variables.integerValue(v.value() != null ? ((Number) v.value()).intValue() : null);
      case "Long" -> Variables.longValue(v.value() != null ? ((Number) v.value()).longValue() : null);
      case "Double" -> Variables.doubleValue(v.value() != null ? ((Number) v.value()).doubleValue() : null);
      case "Boolean" -> Variables.booleanValue((Boolean) v.value());
      case "Short" -> Variables.shortValue(v.value() != null ? ((Number) v.value()).shortValue() : null);
      case "Date" -> Variables.dateValue(
          v.value() instanceof java.util.Date d ? d : null);
      case "Object" -> Variables.objectValue(v.value());
      default -> Variables.stringValue(v.value() != null ? v.value().toString() : null);
    };
  }
}
