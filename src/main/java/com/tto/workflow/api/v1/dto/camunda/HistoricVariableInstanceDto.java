package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HistoricVariableInstance")
public record HistoricVariableInstanceDto(
    String id,
    String name,
    String processInstanceId,
    String activityInstanceId,
    String executionId,
    String taskId,
    String type,
    Object value,
    String createTime,
    String removalTime) {}
