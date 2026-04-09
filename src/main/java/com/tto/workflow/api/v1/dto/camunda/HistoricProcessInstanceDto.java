package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "HistoricProcessInstance")
public record HistoricProcessInstanceDto(
    String id,
    String processDefinitionId,
    String processDefinitionKey,
    String businessKey,
    String startTime,
    String endTime,
    Long duration,
    String state,
    String tenantId,
    String removalTime) {}
