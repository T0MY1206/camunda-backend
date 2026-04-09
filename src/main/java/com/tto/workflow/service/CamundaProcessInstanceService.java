package com.tto.workflow.service;

import com.tto.workflow.api.v1.common.CamundaNotFoundException;
import com.tto.workflow.api.v1.dto.camunda.ProcessInstanceDto;
import com.tto.workflow.api.v1.dto.camunda.StartProcessInstanceDto;
import com.tto.workflow.api.v1.dto.camunda.VariableValueDto;
import com.tto.workflow.api.v1.mapper.CamundaDtoMapper;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.runtime.ProcessInstanceQuery;
import org.camunda.bpm.engine.variable.VariableValue;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CamundaProcessInstanceService {

  private final RuntimeService runtimeService;
  private final CamundaDtoMapper mapper;

  public CamundaProcessInstanceService(RuntimeService runtimeService, CamundaDtoMapper mapper) {
    this.runtimeService = runtimeService;
    this.mapper = mapper;
  }

  public List<ProcessInstanceDto> list(
      String processDefinitionKey,
      String businessKey,
      String tenantId,
      Boolean suspended,
      Integer firstResult,
      Integer maxResults) {
    ProcessInstanceQuery q = runtimeService.createProcessInstanceQuery().orderByProcessInstanceId().asc();
    if (processDefinitionKey != null && !processDefinitionKey.isBlank()) {
      q.processDefinitionKey(processDefinitionKey);
    }
    if (businessKey != null && !businessKey.isBlank()) {
      q.processInstanceBusinessKey(businessKey);
    }
    if (tenantId != null && !tenantId.isBlank()) {
      q.tenantIdIn(tenantId);
    }
    if (Boolean.TRUE.equals(suspended)) {
      q.suspended();
    } else if (Boolean.FALSE.equals(suspended)) {
      q.active();
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toProcessInstanceDto).toList();
  }

  public ProcessInstanceDto get(String id) {
    ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
    if (pi == null) {
      throw new CamundaNotFoundException("Process instance " + id + " does not exist");
    }
    return mapper.toProcessInstanceDto(pi);
  }

  @Transactional
  public ProcessInstanceDto startByProcessDefinitionId(String processDefinitionId, StartProcessInstanceDto body) {
    var vars = buildVariables(body != null ? body.variables() : null);
    String businessKey = body != null ? body.businessKey() : null;
    ProcessInstance pi =
        runtimeService.startProcessInstanceById(processDefinitionId, businessKey, vars);
    return mapper.toProcessInstanceDto(pi);
  }

  @Transactional
  public ProcessInstanceDto startByProcessDefinitionKey(String key, String tenantId, StartProcessInstanceDto body) {
    var vars = buildVariables(body != null ? body.variables() : null);
    String businessKey = body != null ? body.businessKey() : null;
    ProcessInstance pi;
    if (tenantId != null && !tenantId.isBlank()) {
      pi =
          runtimeService
              .createProcessInstanceByKey(key)
              .tenantId(tenantId)
              .businessKey(businessKey)
              .setVariables(vars)
              .execute();
    } else {
      pi = runtimeService.startProcessInstanceByKey(key, businessKey, vars);
    }
    return mapper.toProcessInstanceDto(pi);
  }

  @Transactional
  public void delete(String id, String reason) {
    ProcessInstance pi = runtimeService.createProcessInstanceQuery().processInstanceId(id).singleResult();
    if (pi == null) {
      throw new CamundaNotFoundException("Process instance " + id + " does not exist");
    }
    runtimeService.deleteProcessInstance(id, reason != null ? reason : "deleted via API");
  }

  private org.camunda.bpm.engine.variable.VariableMap buildVariables(Map<String, VariableValueDto> variables) {
    org.camunda.bpm.engine.variable.VariableMap map = Variables.createVariables();
    if (variables == null) {
      return map;
    }
    for (var e : variables.entrySet()) {
      VariableValueDto v = e.getValue();
      if (v == null) {
        continue;
      }
      VariableValue<?> vv = toVariableValue(v);
      map.putValue(e.getKey(), vv);
    }
    return map;
  }

  @SuppressWarnings("unchecked")
  private VariableValue<?> toVariableValue(VariableValueDto v) {
    String type = v.type() != null ? v.type() : "String";
    return switch (type) {
      case "Integer" -> Variables.integerValue((Integer) v.value());
      case "Long" -> Variables.longValue(v.value() != null ? ((Number) v.value()).longValue() : null);
      case "Double" -> Variables.doubleValue(v.value() != null ? ((Number) v.value()).doubleValue() : null);
      case "Boolean" -> Variables.booleanValue((Boolean) v.value());
      case "Short" -> Variables.shortValue(v.value() != null ? ((Number) v.value()).shortValue() : null);
      case "Date" -> Variables.dateValue(v.value() instanceof Date d ? d : null);
      case "Object" -> Variables.objectValue(v.value());
      default -> Variables.stringValue(v.value() != null ? v.value().toString() : null);
    };
  }
}
