package com.tto.workflow.service;

import com.tto.workflow.api.v1.dto.camunda.SchemaLogDto;
import com.tto.workflow.domain.repository.ActGeSchemaLogRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CamundaSchemaLogService {

  private final ActGeSchemaLogRepository schemaLogRepository;

  public CamundaSchemaLogService(ActGeSchemaLogRepository schemaLogRepository) {
    this.schemaLogRepository = schemaLogRepository;
  }

  public List<SchemaLogDto> list() {
    return schemaLogRepository.findAll().stream()
        .map(
            e ->
                new SchemaLogDto(
                    e.getId(),
                    e.getTimestamp() != null ? e.getTimestamp().toString() : null,
                    e.getVersion()))
        .toList();
  }
}
