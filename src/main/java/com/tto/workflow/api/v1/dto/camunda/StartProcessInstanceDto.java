package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

@Schema(description = "Inicio de instancia (variables opcionales tipo Camunda REST).")
public record StartProcessInstanceDto(
    @Schema(description = "Clave de negocio opcional") String businessKey,
    @Schema(
            description =
                "Mapa nombre -> { value, type } con type: String, Integer, Boolean, Double, Long, Short, Date, Object")
    Map<String, VariableValueDto> variables) {}
