package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Execution")
public record ExecutionDto(
    String id,
    String processInstanceId,
    String parentId,
    String processDefinitionId,
    String tenantId,
    Boolean suspended,
    String activityId) {}
