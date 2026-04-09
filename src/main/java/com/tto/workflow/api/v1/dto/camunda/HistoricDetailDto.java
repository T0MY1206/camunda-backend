package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HistoricDetail")
public record HistoricDetailDto(
    String id,
    String type,
    String processInstanceId,
    String activityInstanceId,
    String executionId,
    String taskId,
    String time,
    String tenantId,
    String removalTime,
    String sequenceCounter) {}
