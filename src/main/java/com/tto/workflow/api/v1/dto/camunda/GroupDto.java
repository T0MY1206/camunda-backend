package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Group")
public record GroupDto(String id, String name, String type) {}
