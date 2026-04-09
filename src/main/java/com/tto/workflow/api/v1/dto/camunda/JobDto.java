package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Job")
public record JobDto(
    String id,
    String jobDefinitionId,
    String processInstanceId,
    String executionId,
    String processDefinitionId,
    String deploymentId,
    String createTime,
    String dueDate,
    Integer retries,
    Integer priority,
    String exceptionMessage,
    String failedActivityId,
    String tenantId,
    Boolean suspended) {}
