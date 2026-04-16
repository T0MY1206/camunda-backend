package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Task")
public record TaskDto(
    String id,
    String name,
    String assignee,
    String owner,
    String createTime,
    String dueDate,
    Integer priority,
    String delegationState,
    String processInstanceId,
    String executionId,
    String processDefinitionId,
    String taskDefinitionKey,
    String tenantId,
    Boolean suspended) {}
