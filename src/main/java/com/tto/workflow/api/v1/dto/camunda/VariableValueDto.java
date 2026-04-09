package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Valor tipado (estilo Camunda REST)")
public record VariableValueDto(
    @Schema(description = "Valor serializable") Object value,
    @Schema(description = "Tipo: String, Integer, Boolean, Double, Long, Short, Date, Object") String type) {}
