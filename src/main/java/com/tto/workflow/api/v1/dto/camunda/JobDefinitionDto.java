package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "JobDefinition")
public record JobDefinitionDto(
    String id,
    String processDefinitionId,
    String processDefinitionKey,
    String jobType,
    String jobConfiguration,
    String activityId,
    Boolean suspended,
    Integer overridingJobPriority,
    String tenantId,
    String deploymentId) {}
