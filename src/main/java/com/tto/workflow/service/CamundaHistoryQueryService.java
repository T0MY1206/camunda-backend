package com.tto.workflow.service;

import com.tto.workflow.api.v1.common.CamundaNotFoundException;
import com.tto.workflow.api.v1.dto.camunda.HistoricActivityInstanceDto;
import com.tto.workflow.api.v1.dto.camunda.HistoricDetailDto;
import com.tto.workflow.api.v1.dto.camunda.HistoricProcessInstanceDto;
import com.tto.workflow.api.v1.dto.camunda.HistoricTaskInstanceDto;
import com.tto.workflow.api.v1.dto.camunda.HistoricVariableInstanceDto;
import com.tto.workflow.api.v1.mapper.CamundaHistoryMapper;
import java.util.List;
import org.camunda.bpm.engine.HistoryService;
import org.camunda.bpm.engine.history.HistoricActivityInstanceQuery;
import org.camunda.bpm.engine.history.HistoricDetailQuery;
import org.camunda.bpm.engine.history.HistoricProcessInstanceQuery;
import org.camunda.bpm.engine.history.HistoricTaskInstanceQuery;
import org.camunda.bpm.engine.history.HistoricVariableInstanceQuery;
import org.springframework.stereotype.Service;

@Service
public class CamundaHistoryQueryService {

  private final HistoryService historyService;
  private final CamundaHistoryMapper mapper;

  public CamundaHistoryQueryService(HistoryService historyService, CamundaHistoryMapper mapper) {
    this.historyService = historyService;
    this.mapper = mapper;
  }

  public List<HistoricProcessInstanceDto> listProcessInstances(
      String processDefinitionKey,
      String processInstanceId,
      String businessKey,
      Boolean finished,
      String tenantId,
      Integer firstResult,
      Integer maxResults) {
    HistoricProcessInstanceQuery q =
        historyService.createHistoricProcessInstanceQuery().orderByProcessInstanceStartTime().desc();
    if (processDefinitionKey != null && !processDefinitionKey.isBlank()) {
      q.processDefinitionKey(processDefinitionKey);
    }
    if (processInstanceId != null && !processInstanceId.isBlank()) {
      q.processInstanceId(processInstanceId);
    }
    if (businessKey != null && !businessKey.isBlank()) {
      q.processInstanceBusinessKey(businessKey);
    }
    if (tenantId != null && !tenantId.isBlank()) {
      q.tenantIdIn(tenantId);
    }
    if (Boolean.TRUE.equals(finished)) {
      q.completed();
    } else if (Boolean.FALSE.equals(finished)) {
      q.unfinished();
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toHistoricProcessInstanceDto).toList();
  }

  public HistoricProcessInstanceDto getProcessInstance(String id) {
    var h = historyService.createHistoricProcessInstanceQuery().processInstanceId(id).singleResult();
    if (h == null) {
      throw new CamundaNotFoundException("Historic process instance " + id + " does not exist");
    }
    return mapper.toHistoricProcessInstanceDto(h);
  }

  public List<HistoricTaskInstanceDto> listTaskInstances(
      String processInstanceId, String taskDefinitionKey, String assignee, Integer firstResult, Integer maxResults) {
    HistoricTaskInstanceQuery q = historyService.createHistoricTaskInstanceQuery().orderByHistoricTaskInstanceStartTime().desc();
    if (processInstanceId != null && !processInstanceId.isBlank()) {
      q.processInstanceId(processInstanceId);
    }
    if (taskDefinitionKey != null && !taskDefinitionKey.isBlank()) {
      q.taskDefinitionKey(taskDefinitionKey);
    }
    if (assignee != null && !assignee.isBlank()) {
      q.taskAssignee(assignee);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toHistoricTaskInstanceDto).toList();
  }

  public HistoricTaskInstanceDto getTaskInstance(String id) {
    var t = historyService.createHistoricTaskInstanceQuery().taskId(id).singleResult();
    if (t == null) {
      throw new CamundaNotFoundException("Historic task instance " + id + " does not exist");
    }
    return mapper.toHistoricTaskInstanceDto(t);
  }

  public List<HistoricActivityInstanceDto> listActivityInstances(
      String processInstanceId, String activityId, Integer firstResult, Integer maxResults) {
    HistoricActivityInstanceQuery q =
        historyService.createHistoricActivityInstanceQuery().orderByHistoricActivityInstanceStartTime().asc();
    if (processInstanceId != null && !processInstanceId.isBlank()) {
      q.processInstanceId(processInstanceId);
    }
    if (activityId != null && !activityId.isBlank()) {
      q.activityId(activityId);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toHistoricActivityInstanceDto).toList();
  }

  public List<HistoricVariableInstanceDto> listVariableInstances(
      String processInstanceId, String variableName, Integer firstResult, Integer maxResults) {
    HistoricVariableInstanceQuery q =
        historyService.createHistoricVariableInstanceQuery().orderByVariableName().asc();
    if (processInstanceId != null && !processInstanceId.isBlank()) {
      q.processInstanceId(processInstanceId);
    }
    if (variableName != null && !variableName.isBlank()) {
      q.variableName(variableName);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toHistoricVariableInstanceDto).toList();
  }

  public List<HistoricDetailDto> listDetails(String processInstanceId, Integer firstResult, Integer maxResults) {
    HistoricDetailQuery q = historyService.createHistoricDetailQuery().orderByTime().asc();
    if (processInstanceId != null && !processInstanceId.isBlank()) {
      q.processInstanceId(processInstanceId);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toHistoricDetailDto).toList();
  }
}
