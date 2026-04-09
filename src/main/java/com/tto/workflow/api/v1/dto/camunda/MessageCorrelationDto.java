package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Correlación de mensaje (paridad simplificada)")
public record MessageCorrelationDto(
    @Schema(description = "Nombre del mensaje") String messageName,
    @Schema(description = "Clave de negocio opcional") String businessKey,
    @Schema(description = "Variables opcionales") Map<String, VariableValueDto> variables) {}
