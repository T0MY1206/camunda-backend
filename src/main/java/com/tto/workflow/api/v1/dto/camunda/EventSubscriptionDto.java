package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "EventSubscription")
public record EventSubscriptionDto(
    String id,
    String eventType,
    String eventName,
    String executionId,
    String processInstanceId,
    String activityId,
    String configuration,
    String created,
    String tenantId) {}
