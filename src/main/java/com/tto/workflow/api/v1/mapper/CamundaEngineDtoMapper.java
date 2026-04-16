package com.tto.workflow.api.v1.mapper;

import com.tto.workflow.api.v1.dto.camunda.DecisionDefinitionDto;
import com.tto.workflow.api.v1.dto.camunda.ExternalTaskDto;
import com.tto.workflow.api.v1.dto.camunda.GroupDto;
import com.tto.workflow.api.v1.dto.camunda.IncidentDto;
import com.tto.workflow.api.v1.dto.camunda.JobDefinitionDto;
import com.tto.workflow.api.v1.dto.camunda.JobDto;
import com.tto.workflow.api.v1.dto.camunda.UserDto;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.repository.DecisionDefinition;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.engine.management.JobDefinition;
import org.camunda.bpm.engine.runtime.Job;
import org.springframework.stereotype.Component;

@Component
public class CamundaEngineDtoMapper {

  private static final DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

  private String fmt(Date d) {
    if (d == null) {
      return null;
    }
    return d.toInstant().atOffset(ZoneOffset.UTC).format(ISO);
  }

  public ExternalTaskDto toExternalTaskDto(ExternalTask t) {
    return new ExternalTaskDto(
        t.getId(),
        t.getTopicName(),
        t.getWorkerId(),
        t.getActivityId(),
        t.getExecutionId(),
        t.getProcessInstanceId(),
        t.getProcessDefinitionId(),
        t.getTenantId(),
        t.getRetries(),
        t.getErrorMessage(),
        t.isSuspended(),
        (int) Math.min(Math.max(t.getPriority(), Integer.MIN_VALUE), Integer.MAX_VALUE));
  }

  public IncidentDto toIncidentDto(Incident i) {
    return new IncidentDto(
        i.getId(),
        i.getIncidentType(),
        i.getIncidentMessage(),
        i.getExecutionId(),
        i.getActivityId(),
        i.getProcessInstanceId(),
        i.getProcessDefinitionId(),
        i.getTenantId(),
        i.getJobDefinitionId(),
        i.getFailedActivityId(),
        i.getCauseIncidentId(),
        i.getRootCauseIncidentId(),
        i.getConfiguration(),
        fmt(i.getIncidentTimestamp()));
  }

  public JobDto toJobDto(Job j) {
    return new JobDto(
        j.getId(),
        j.getJobDefinitionId(),
        j.getProcessInstanceId(),
        j.getExecutionId(),
        j.getProcessDefinitionId(),
        j.getDeploymentId(),
        fmt(j.getCreateTime()),
        fmt(j.getDuedate()),
        j.getRetries(),
        (int) Math.min(Math.max(j.getPriority(), Integer.MIN_VALUE), Integer.MAX_VALUE),
        j.getExceptionMessage(),
        j.getFailedActivityId(),
        j.getTenantId(),
        j.isSuspended());
  }

  public JobDefinitionDto toJobDefinitionDto(JobDefinition jd) {
    return new JobDefinitionDto(
        jd.getId(),
        jd.getProcessDefinitionId(),
        jd.getProcessDefinitionKey(),
        jd.getJobType(),
        jd.getJobConfiguration(),
        jd.getActivityId(),
        jd.isSuspended(),
        jd.getOverridingJobPriority() == null ? null : jd.getOverridingJobPriority().intValue(),
        jd.getTenantId(),
        jd.getDeploymentId());
  }

  public UserDto toUserDto(User u) {
    return new UserDto(u.getId(), u.getFirstName(), u.getLastName(), u.getEmail());
  }

  public GroupDto toGroupDto(Group g) {
    return new GroupDto(g.getId(), g.getName(), g.getType());
  }

  public DecisionDefinitionDto toDecisionDefinitionDto(DecisionDefinition d) {
    return new DecisionDefinitionDto(
        d.getId(),
        d.getKey(),
        d.getName(),
        d.getVersion(),
        d.getDeploymentId(),
        d.getTenantId(),
        d.getResourceName(),
        d.getDiagramResourceName(),
        Boolean.FALSE);
  }
}
