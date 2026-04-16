package com.tto.workflow.api.v1.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import io.swagger.v3.oas.annotations.media.Schema;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Schema(description = "Error alineado al estilo Camunda REST (type, message, code).")
public record ApiError(
    @Schema(description = "Tipo de error o excepción") String type,
    @Schema(description = "Mensaje legible") String message,
    @Schema(description = "Código opcional de negocio o motor") Integer code) {

  public static ApiError of(String type, String message) {
    return new ApiError(type, message, null);
  }

  public static ApiError of(String type, String message, Integer code) {
    return new ApiError(type, message, code);
  }
}
