package com.tto.workflow.api.v1.dto.camunda;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;
import java.util.Map;

@Schema(name = "ProcessInstance")
public record ProcessInstanceDto(
    String id,
    String definitionId,
    String businessKey,
    String caseInstanceId,
    Boolean ended,
    Boolean suspended,
    String tenantId,
    List<Map<String, String>> links) {}
