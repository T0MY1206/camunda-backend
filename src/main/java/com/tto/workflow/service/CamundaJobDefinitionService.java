package com.tto.workflow.service;

import com.tto.workflow.api.v1.common.CamundaNotFoundException;
import com.tto.workflow.api.v1.dto.camunda.JobDefinitionDto;
import com.tto.workflow.api.v1.mapper.CamundaEngineDtoMapper;
import java.util.List;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.management.JobDefinition;
import org.camunda.bpm.engine.management.JobDefinitionQuery;
import org.springframework.stereotype.Service;

@Service
public class CamundaJobDefinitionService {

  private final ManagementService managementService;
  private final CamundaEngineDtoMapper mapper;

  public CamundaJobDefinitionService(ManagementService managementService, CamundaEngineDtoMapper mapper) {
    this.managementService = managementService;
    this.mapper = mapper;
  }

  public List<JobDefinitionDto> list(
      String processDefinitionId, String jobType, Integer firstResult, Integer maxResults) {
    JobDefinitionQuery q = managementService.createJobDefinitionQuery().orderByJobDefinitionId().asc();
    if (processDefinitionId != null && !processDefinitionId.isBlank()) {
      q.processDefinitionId(processDefinitionId);
    }
    if (jobType != null && !jobType.isBlank()) {
      q.jobType(jobType);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toJobDefinitionDto).toList();
  }

  public JobDefinitionDto get(String id) {
    JobDefinition jd = managementService.createJobDefinitionQuery().jobDefinitionId(id).singleResult();
    if (jd == null) {
      throw new CamundaNotFoundException("Job definition " + id + " does not exist");
    }
    return mapper.toJobDefinitionDto(jd);
  }
}
