package com.tto.workflow.service;

import com.tto.workflow.api.v1.common.CamundaNotFoundException;
import com.tto.workflow.api.v1.dto.camunda.JobDto;
import com.tto.workflow.api.v1.mapper.CamundaEngineDtoMapper;
import java.util.List;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.runtime.Job;
import org.camunda.bpm.engine.runtime.JobQuery;
import org.springframework.stereotype.Service;

@Service
public class CamundaJobService {

  private final ManagementService managementService;
  private final CamundaEngineDtoMapper mapper;

  public CamundaJobService(ManagementService managementService, CamundaEngineDtoMapper mapper) {
    this.managementService = managementService;
    this.mapper = mapper;
  }

  public List<JobDto> list(String processInstanceId, String jobDefinitionId, Integer firstResult, Integer maxResults) {
    JobQuery q = managementService.createJobQuery().orderByJobId().asc();
    if (processInstanceId != null && !processInstanceId.isBlank()) {
      q.processInstanceId(processInstanceId);
    }
    if (jobDefinitionId != null && !jobDefinitionId.isBlank()) {
      q.jobDefinitionId(jobDefinitionId);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toJobDto).toList();
  }

  public JobDto get(String id) {
    Job j = managementService.createJobQuery().jobId(id).singleResult();
    if (j == null) {
      throw new CamundaNotFoundException("Job " + id + " does not exist");
    }
    return mapper.toJobDto(j);
  }
}
