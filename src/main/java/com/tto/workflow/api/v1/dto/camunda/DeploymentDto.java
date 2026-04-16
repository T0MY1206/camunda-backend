package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "Deployment")
public record DeploymentDto(
    @Schema(description = "Id del despliegue") String id,
    @Schema(description = "Nombre") String name,
    @Schema(description = "Fecha ISO8601") String deploymentTime,
    @Schema(description = "Origen") String source,
    @Schema(description = "Tenant") String tenantId) {}
