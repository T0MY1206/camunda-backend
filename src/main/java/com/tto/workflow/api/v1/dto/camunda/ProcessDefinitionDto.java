package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "ProcessDefinition")
public record ProcessDefinitionDto(
    String id,
    String key,
    String category,
    String description,
    String name,
    Integer version,
    String resource,
    String deploymentId,
    String diagram,
    Boolean suspended,
    String tenantId,
    String versionTag,
    Integer historyTimeToLive,
    Boolean startableInTasklist) {}
