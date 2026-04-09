package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "DecisionDefinition")
public record DecisionDefinitionDto(
    String id,
    String key,
    String name,
    Integer version,
    String deploymentId,
    String tenantId,
    String resource,
    String diagram,
    Boolean suspended) {}
