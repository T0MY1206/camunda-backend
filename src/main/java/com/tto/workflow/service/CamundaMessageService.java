package com.tto.workflow.service;

import com.tto.workflow.api.v1.dto.camunda.MessageCorrelationDto;
import com.tto.workflow.api.v1.dto.camunda.VariableValueDto;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.MessageCorrelationBuilder;
import org.camunda.bpm.engine.variable.VariableValue;
import org.camunda.bpm.engine.variable.Variables;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CamundaMessageService {

  private final RuntimeService runtimeService;

  public CamundaMessageService(RuntimeService runtimeService) {
    this.runtimeService = runtimeService;
  }

  @Transactional
  public void correlate(MessageCorrelationDto dto) {
    if (dto == null || dto.messageName() == null || dto.messageName().isBlank()) {
      throw new IllegalArgumentException("messageName is required");
    }
    MessageCorrelationBuilder b = runtimeService.createMessageCorrelation(dto.messageName());
    if (dto.businessKey() != null && !dto.businessKey().isBlank()) {
      b.processInstanceBusinessKey(dto.businessKey());
    }
    if (dto.variables() != null && !dto.variables().isEmpty()) {
      for (var e : dto.variables().entrySet()) {
        VariableValueDto v = e.getValue();
        if (v == null) {
          continue;
        }
        b.setVariable(e.getKey(), toVariableValue(v));
      }
    }
    b.correlate();
  }

  private VariableValue<?> toVariableValue(VariableValueDto v) {
    String type = v.type() != null ? v.type() : "String";
    return switch (type) {
      case "Integer" -> Variables.integerValue(v.value() != null ? ((Number) v.value()).intValue() : null);
      case "Long" -> Variables.longValue(v.value() != null ? ((Number) v.value()).longValue() : null);
      case "Double" -> Variables.doubleValue(v.value() != null ? ((Number) v.value()).doubleValue() : null);
      case "Boolean" -> Variables.booleanValue((Boolean) v.value());
      case "Short" -> Variables.shortValue(v.value() != null ? ((Number) v.value()).shortValue() : null);
      case "Date" -> Variables.dateValue(v.value() instanceof java.util.Date d ? d : null);
      case "Object" -> Variables.objectValue(v.value());
      default -> Variables.stringValue(v.value() != null ? v.value().toString() : null);
    };
  }
}
