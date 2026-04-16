package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(name = "User")
public record UserDto(String id, String firstName, String lastName, String email) {}
