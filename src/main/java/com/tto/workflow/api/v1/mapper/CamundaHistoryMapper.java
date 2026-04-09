package com.tto.workflow.api.v1.mapper;

import com.tto.workflow.api.v1.dto.camunda.HistoricActivityInstanceDto;
import com.tto.workflow.api.v1.dto.camunda.HistoricDetailDto;
import com.tto.workflow.api.v1.dto.camunda.HistoricProcessInstanceDto;
import com.tto.workflow.api.v1.dto.camunda.HistoricTaskInstanceDto;
import com.tto.workflow.api.v1.dto.camunda.HistoricVariableInstanceDto;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.camunda.bpm.engine.history.HistoricActivityInstance;
import org.camunda.bpm.engine.history.HistoricDetail;
import org.camunda.bpm.engine.history.HistoricFormProperty;
import org.camunda.bpm.engine.history.HistoricProcessInstance;
import org.camunda.bpm.engine.history.HistoricVariableUpdate;
import org.camunda.bpm.engine.history.HistoricTaskInstance;
import org.camunda.bpm.engine.history.HistoricVariableInstance;
import org.springframework.stereotype.Component;

@Component
public class CamundaHistoryMapper {

  private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

  private String fmt(Date d) {
    if (d == null) {
      return null;
    }
    return d.toInstant().atOffset(ZoneOffset.UTC).format(ISO);
  }

  public HistoricProcessInstanceDto toHistoricProcessInstanceDto(HistoricProcessInstance h) {
    return new HistoricProcessInstanceDto(
        h.getId(),
        h.getProcessDefinitionId(),
        h.getProcessDefinitionKey(),
        h.getBusinessKey(),
        fmt(h.getStartTime()),
        fmt(h.getEndTime()),
        h.getDurationInMillis(),
        h.getState(),
        h.getTenantId(),
        fmt(h.getRemovalTime()));
  }

  public HistoricTaskInstanceDto toHistoricTaskInstanceDto(HistoricTaskInstance t) {
    return new HistoricTaskInstanceDto(
        t.getId(),
        t.getTaskDefinitionKey(),
        t.getProcessDefinitionId(),
        t.getProcessInstanceId(),
        t.getExecutionId(),
        t.getName(),
        t.getAssignee(),
        fmt(t.getStartTime()),
        fmt(t.getEndTime()),
        t.getDurationInMillis(),
        t.getTenantId(),
        fmt(t.getRemovalTime()));
  }

  public HistoricActivityInstanceDto toHistoricActivityInstanceDto(HistoricActivityInstance a) {
    return new HistoricActivityInstanceDto(
        a.getId(),
        a.getActivityId(),
        a.getActivityName(),
        a.getActivityType(),
        a.getProcessDefinitionId(),
        a.getProcessInstanceId(),
        a.getExecutionId(),
        a.getAssignee(),
        fmt(a.getStartTime()),
        fmt(a.getEndTime()),
        a.getDurationInMillis(),
        a.getTenantId(),
        fmt(a.getRemovalTime()));
  }

  public HistoricVariableInstanceDto toHistoricVariableInstanceDto(HistoricVariableInstance v) {
    Object val = v.getValue();
    return new HistoricVariableInstanceDto(
        v.getId(),
        v.getName(),
        v.getProcessInstanceId(),
        v.getActivityInstanceId(),
        v.getExecutionId(),
        v.getTaskId(),
        v.getTypeName(),
        val,
        fmt(v.getCreateTime()),
        fmt(v.getRemovalTime()));
  }

  public HistoricDetailDto toHistoricDetailDto(HistoricDetail d) {
    return new HistoricDetailDto(
        d.getId(),
        historicDetailKind(d),
        d.getProcessInstanceId(),
        d.getActivityInstanceId(),
        d.getExecutionId(),
        d.getTaskId(),
        fmt(d.getTime()),
        d.getTenantId(),
        fmt(d.getRemovalTime()),
        historicDetailSequence(d));
  }

  private static String historicDetailKind(HistoricDetail d) {
    if (d instanceof HistoricVariableUpdate) {
      return "variableUpdate";
    }
    if (d instanceof HistoricFormProperty) {
      return "formProperty";
    }
    return "historicDetail";
  }

  private static String historicDetailSequence(HistoricDetail d) {
    if (d instanceof HistoricVariableUpdate u) {
      return String.valueOf(u.getRevision());
    }
    return null;
  }
}
