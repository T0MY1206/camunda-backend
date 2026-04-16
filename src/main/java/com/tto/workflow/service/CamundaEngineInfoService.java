package com.tto.workflow.service;

import com.tto.workflow.domain.entity.ActGeProperty;
import com.tto.workflow.domain.repository.ActGePropertyRepository;
import java.util.HashMap;
import java.util.Map;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.ProcessEngine;
import org.springframework.stereotype.Service;

@Service
public class CamundaEngineInfoService {

  private final ProcessEngine processEngine;
  private final ManagementService managementService;
  private final ActGePropertyRepository propertyRepository;

  public CamundaEngineInfoService(
      ProcessEngine processEngine,
      ManagementService managementService,
      ActGePropertyRepository propertyRepository) {
    this.processEngine = processEngine;
    this.managementService = managementService;
    this.propertyRepository = propertyRepository;
  }

  public Map<String, Object> engineInfo() {
    Map<String, Object> m = new HashMap<>();
    m.put("name", processEngine.getName());
    m.put("version", ProcessEngine.VERSION);
    return m;
  }

  public Map<String, Long> metrics(String tableName) {
    Map<String, Long> tableCount = managementService.getTableCount();
    if (tableName != null && !tableName.isBlank()) {
      return Map.of(tableName, tableCount.getOrDefault(tableName, 0L));
    }
    return Map.of(
        "ACT_RU_EXECUTION", tableCount.getOrDefault("ACT_RU_EXECUTION", 0L),
        "ACT_RU_TASK", tableCount.getOrDefault("ACT_RU_TASK", 0L),
        "ACT_HI_PROCINST", tableCount.getOrDefault("ACT_HI_PROCINST", 0L));
  }

  public String schemaVersion() {
    return propertyRepository.findById("camunda.schema.version").map(ActGeProperty::getValue).orElse("unknown");
  }
}
