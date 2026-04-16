package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HistoricActivityInstance")
public record HistoricActivityInstanceDto(
    String id,
    String activityId,
    String activityName,
    String activityType,
    String processDefinitionId,
    String processInstanceId,
    String executionId,
    String assignee,
    String startTime,
    String endTime,
    Long duration,
    String tenantId,
    String removalTime) {}
