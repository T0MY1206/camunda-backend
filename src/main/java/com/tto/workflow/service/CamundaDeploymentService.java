package com.tto.workflow.service;

import com.tto.workflow.api.v1.common.CamundaNotFoundException;
import com.tto.workflow.api.v1.dto.camunda.DeploymentDto;
import com.tto.workflow.api.v1.mapper.CamundaDtoMapper;
import java.util.List;
import org.camunda.bpm.engine.RepositoryService;
import org.camunda.bpm.engine.repository.Deployment;
import org.camunda.bpm.engine.repository.DeploymentQuery;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CamundaDeploymentService {

  private final RepositoryService repositoryService;
  private final CamundaDtoMapper mapper;

  public CamundaDeploymentService(RepositoryService repositoryService, CamundaDtoMapper mapper) {
    this.repositoryService = repositoryService;
    this.mapper = mapper;
  }

  public List<DeploymentDto> list(
      String name, String nameLike, String source, String tenantId, Integer firstResult, Integer maxResults) {
    DeploymentQuery q = repositoryService.createDeploymentQuery().orderByDeploymentTime().desc();
    if (name != null && !name.isBlank()) {
      q.deploymentName(name);
    }
    if (nameLike != null && !nameLike.isBlank()) {
      q.deploymentNameLike("%" + nameLike + "%");
    }
    if (source != null && !source.isBlank()) {
      q.deploymentSource(source);
    }
    if (tenantId != null && !tenantId.isBlank()) {
      q.tenantIdIn(tenantId);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toDeploymentDto).toList();
  }

  public DeploymentDto get(String id) {
    Deployment d =
        repositoryService
            .createDeploymentQuery()
            .deploymentId(id)
            .singleResult();
    if (d == null) {
      throw new CamundaNotFoundException("Deployment " + id + " does not exist");
    }
    return mapper.toDeploymentDto(d);
  }

  @Transactional
  public void delete(String id, boolean cascade) {
    Deployment d = repositoryService.createDeploymentQuery().deploymentId(id).singleResult();
    if (d == null) {
      throw new CamundaNotFoundException("Deployment " + id + " does not exist");
    }
    repositoryService.deleteDeployment(id, cascade);
  }
}
