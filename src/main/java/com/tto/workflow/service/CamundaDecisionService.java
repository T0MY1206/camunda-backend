package com.tto.workflow.service;

import com.tto.workflow.api.v1.common.CamundaNotFoundException;
import com.tto.workflow.api.v1.dto.camunda.DecisionDefinitionDto;
import com.tto.workflow.api.v1.mapper.CamundaEngineDtoMapper;
import java.util.List;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.DecisionDefinition;
import org.camunda.bpm.engine.repository.DecisionDefinitionQuery;
import org.springframework.stereotype.Service;

@Service
public class CamundaDecisionService {

  private final RepositoryService repositoryService;
  private final CamundaEngineDtoMapper mapper;

  public CamundaDecisionService(RepositoryService repositoryService, CamundaEngineDtoMapper mapper) {
    this.repositoryService = repositoryService;
    this.mapper = mapper;
  }

  public List<DecisionDefinitionDto> list(
      String key, String name, String tenantId, Boolean latestVersion, Integer firstResult, Integer maxResults) {
    DecisionDefinitionQuery q = repositoryService.createDecisionDefinitionQuery().orderByDecisionDefinitionKey().asc();
    if (Boolean.TRUE.equals(latestVersion)) {
      q.latestVersion();
    }
    if (key != null && !key.isBlank()) {
      q.decisionDefinitionKey(key);
    }
    if (name != null && !name.isBlank()) {
      q.decisionDefinitionName(name);
    }
    if (tenantId != null && !tenantId.isBlank()) {
      q.tenantIdIn(tenantId);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toDecisionDefinitionDto).toList();
  }

  public DecisionDefinitionDto get(String id) {
    DecisionDefinition d = repositoryService.getDecisionDefinition(id);
    if (d == null) {
      throw new CamundaNotFoundException("Decision definition " + id + " does not exist");
    }
    return mapper.toDecisionDefinitionDto(d);
  }
}
