package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ExternalTask")
public record ExternalTaskDto(
    String id,
    String topicName,
    String workerId,
    String activityId,
    String executionId,
    String processInstanceId,
    String processDefinitionId,
    String tenantId,
    Integer retries,
    String errorMessage,
    Boolean suspended,
    Integer priority) {}
