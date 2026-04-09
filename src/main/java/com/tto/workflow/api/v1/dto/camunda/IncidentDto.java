package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Incident")
public record IncidentDto(
    String id,
    String incidentType,
    String message,
    String executionId,
    String activityId,
    String processInstanceId,
    String processDefinitionId,
    String tenantId,
    String jobDefinitionId,
    String failedActivityId,
    String causeIncidentId,
    String rootCauseIncidentId,
    String configuration,
    String incidentTimestamp) {}
