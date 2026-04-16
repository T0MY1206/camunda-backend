package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "SchemaLog")
public record SchemaLogDto(String id, String timestamp, String version) {}
