package com.tto.workflow.service;

import com.tto.workflow.api.v1.common.CamundaNotFoundException;
import com.tto.workflow.api.v1.dto.camunda.ProcessDefinitionDto;
import com.tto.workflow.api.v1.mapper.CamundaDtoMapper;
import java.util.List;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.ProcessDefinition;
import org.camunda.bpm.engine.repository.ProcessDefinitionQuery;
import org.springframework.stereotype.Service;

@Service
public class CamundaProcessDefinitionService {

  private final RepositoryService repositoryService;
  private final CamundaDtoMapper mapper;

  public CamundaProcessDefinitionService(RepositoryService repositoryService, CamundaDtoMapper mapper) {
    this.repositoryService = repositoryService;
    this.mapper = mapper;
  }

  public List<ProcessDefinitionDto> list(
      String key,
      String keyLike,
      String name,
      String nameLike,
      String tenantId,
      Boolean latestVersion,
      Boolean suspended,
      Integer firstResult,
      Integer maxResults) {
    ProcessDefinitionQuery q = repositoryService.createProcessDefinitionQuery().orderByProcessDefinitionKey().asc();
    if (Boolean.TRUE.equals(latestVersion)) {
      q.latestVersion();
    }
    if (key != null && !key.isBlank()) {
      q.processDefinitionKey(key);
    }
    if (keyLike != null && !keyLike.isBlank()) {
      q.processDefinitionKeyLike("%" + keyLike + "%");
    }
    if (name != null && !name.isBlank()) {
      q.processDefinitionName(name);
    }
    if (nameLike != null && !nameLike.isBlank()) {
      q.processDefinitionNameLike("%" + nameLike + "%");
    }
    if (tenantId != null && !tenantId.isBlank()) {
      q.tenantIdIn(tenantId);
    }
    if (Boolean.TRUE.equals(suspended)) {
      q.suspended();
    } else if (Boolean.FALSE.equals(suspended)) {
      q.active();
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toProcessDefinitionDto).toList();
  }

  public ProcessDefinitionDto getById(String id) {
    ProcessDefinition def = repositoryService.getProcessDefinition(id);
    if (def == null) {
      throw new CamundaNotFoundException("Process definition " + id + " does not exist");
    }
    return mapper.toProcessDefinitionDto(def);
  }

  public ProcessDefinitionDto getByKeyAndTenant(String key, String tenantId) {
    ProcessDefinitionQuery q = repositoryService.createProcessDefinitionQuery().processDefinitionKey(key).latestVersion();
    if (tenantId != null && !tenantId.isBlank()) {
      q.tenantIdIn(tenantId);
    }
    ProcessDefinition def = q.singleResult();
    if (def == null) {
      throw new CamundaNotFoundException("No process definition for key " + key);
    }
    return mapper.toProcessDefinitionDto(def);
  }
}
