package com.tto.workflow.api.v1.mapper;

import com.tto.workflow.api.v1.dto.camunda.DeploymentDto;
import com.tto.workflow.api.v1.dto.camunda.EventSubscriptionDto;
import com.tto.workflow.api.v1.dto.camunda.ExecutionDto;
import com.tto.workflow.api.v1.dto.camunda.ProcessDefinitionDto;
import com.tto.workflow.api.v1.dto.camunda.ProcessInstanceDto;
import com.tto.workflow.api.v1.dto.camunda.TaskDto;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.Execution;
import org.camunda.bpm.engine.runtime.ProcessInstance;
import org.camunda.bpm.engine.task.Task;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CamundaDtoMapper {

  DateTimeFormatter ISO = DateTimeFormatter.ISO_OFFSET_DATE_TIME;

  @Mapping(target = "deploymentTime", expression = "java(formatDate(deployment.getDeploymentTime()))")
  DeploymentDto toDeploymentDto(Deployment deployment);

  @Mapping(target = "resource", source = "resourceName")
  @Mapping(target = "diagram", source = "diagramResourceName")
  @Mapping(target = "startableInTasklist", source = "startableInTasklist")
  ProcessDefinitionDto toProcessDefinitionDto(ProcessDefinition def);

  @Mapping(target = "links", expression = "java(emptyLinks())")
  @Mapping(target = "caseInstanceId", source = "caseInstanceId")
  @Mapping(target = "ended", source = "ended")
  @Mapping(target = "suspended", source = "suspended")
  ProcessInstanceDto toProcessInstanceDto(ProcessInstance pi);

  @Mapping(target = "activityId", source = "activityId")
  ExecutionDto toExecutionDto(Execution e);

  @Mapping(target = "createTime", expression = "java(formatDate(task.getCreateTime()))")
  @Mapping(target = "dueDate", expression = "java(formatDate(task.getDueDate()))")
  TaskDto toTaskDto(Task task);

  @Mapping(target = "configuration", ignore = true)
  @Mapping(target = "created", expression = "java(formatDate(es.getCreated()))")
  EventSubscriptionDto toEventSubscriptionDto(EventSubscription es);

  default List<Map<String, String>> emptyLinks() {
    return List.of();
  }

  default String formatDate(Date date) {
    if (date == null) {
      return null;
    }
    return date.toInstant().atOffset(ZoneOffset.UTC).format(ISO);
  }
}
