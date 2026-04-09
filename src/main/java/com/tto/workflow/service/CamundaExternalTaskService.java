package com.tto.workflow.service;

import com.tto.workflow.api.v1.common.CamundaNotFoundException;
import com.tto.workflow.api.v1.dto.camunda.ExternalTaskDto;
import com.tto.workflow.api.v1.mapper.CamundaEngineDtoMapper;
import java.util.List;
import org.camunda.bpm.engine.ExternalTaskService;
import org.camunda.bpm.engine.externaltask.ExternalTask;
import org.camunda.bpm.engine.externaltask.ExternalTaskQuery;
import org.springframework.stereotype.Service;

@Service
public class CamundaExternalTaskService {

  private final ExternalTaskService externalTaskService;
  private final CamundaEngineDtoMapper mapper;

  public CamundaExternalTaskService(ExternalTaskService externalTaskService, CamundaEngineDtoMapper mapper) {
    this.externalTaskService = externalTaskService;
    this.mapper = mapper;
  }

  public List<ExternalTaskDto> list(
      String topicName, String workerId, String processInstanceId, Integer firstResult, Integer maxResults) {
    ExternalTaskQuery q = externalTaskService.createExternalTaskQuery().orderById().asc();
    if (topicName != null && !topicName.isBlank()) {
      q.topicName(topicName);
    }
    if (workerId != null && !workerId.isBlank()) {
      q.workerId(workerId);
    }
    if (processInstanceId != null && !processInstanceId.isBlank()) {
      q.processInstanceId(processInstanceId);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toExternalTaskDto).toList();
  }

  public ExternalTaskDto get(String id) {
    ExternalTask t = externalTaskService.createExternalTaskQuery().externalTaskId(id).singleResult();
    if (t == null) {
      throw new CamundaNotFoundException("External task " + id + " does not exist");
    }
    return mapper.toExternalTaskDto(t);
  }
}
