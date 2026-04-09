package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HistoricTaskInstance")
public record HistoricTaskInstanceDto(
    String id,
    String taskDefinitionKey,
    String processDefinitionId,
    String processInstanceId,
    String executionId,
    String name,
    String assignee,
    String startTime,
    String endTime,
    Long duration,
    String tenantId,
    String removalTime) {}
