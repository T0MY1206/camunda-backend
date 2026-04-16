package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Completar tarea con variables opcionales")
public record CompleteTaskDto(
    @Schema(description = "Variables locales al completar") Map<String, VariableValueDto> variables) {}
