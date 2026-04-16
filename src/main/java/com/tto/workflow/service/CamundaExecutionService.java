package com.tto.workflow.service;

import com.tto.workflow.api.v1.common.CamundaNotFoundException;
import com.tto.workflow.api.v1.dto.camunda.ExecutionDto;
import com.tto.workflow.api.v1.mapper.CamundaDtoMapper;
import java.util.List;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ExecutionQuery;
import org.springframework.stereotype.Service;

@Service
public class CamundaExecutionService {

  private final RuntimeService runtimeService;
  private final CamundaDtoMapper mapper;

  public CamundaExecutionService(RuntimeService runtimeService, CamundaDtoMapper mapper) {
    this.runtimeService = runtimeService;
    this.mapper = mapper;
  }

  public List<ExecutionDto> list(
      String processInstanceId, String activityId, String tenantId, Integer firstResult, Integer maxResults) {
    ExecutionQuery q = runtimeService.createExecutionQuery().orderByProcessInstanceId().asc();
    if (processInstanceId != null && !processInstanceId.isBlank()) {
      q.processInstanceId(processInstanceId);
    }
    if (activityId != null && !activityId.isBlank()) {
      q.activityId(activityId);
    }
    if (tenantId != null && !tenantId.isBlank()) {
      q.tenantIdIn(tenantId);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toExecutionDto).toList();
  }

  public ExecutionDto get(String id) {
    Execution e = runtimeService.createExecutionQuery().executionId(id).singleResult();
    if (e == null) {
      throw new CamundaNotFoundException("Execution " + id + " does not exist");
    }
    return mapper.toExecutionDto(e);
  }
}
