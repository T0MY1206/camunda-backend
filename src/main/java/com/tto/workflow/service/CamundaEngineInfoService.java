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
    if (tableName != null && !tableName.isBlank()) {
      return Map.of(tableName, managementService.getTableCount(tableName));
    }
    return Map.of(
        "ACT_RU_EXECUTION", managementService.getTableCount("ACT_RU_EXECUTION"),
        "ACT_RU_TASK", managementService.getTableCount("ACT_RU_TASK"),
        "ACT_HI_PROCINST", managementService.getTableCount("ACT_HI_PROCINST"));
  }

  public String schemaVersion() {
    return propertyRepository.findById("camunda.schema.version").map(ActGeProperty::getValue).orElse("unknown");
  }
}
