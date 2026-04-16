package com.tto.workflow.service;

import com.tto.workflow.api.v1.common.CamundaNotFoundException;
import com.tto.workflow.api.v1.dto.camunda.IncidentDto;
import com.tto.workflow.api.v1.mapper.CamundaEngineDtoMapper;
import java.util.List;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.Incident;
import org.camunda.bpm.engine.runtime.IncidentQuery;
import org.springframework.stereotype.Service;

@Service
public class CamundaIncidentService {

  private final RuntimeService runtimeService;
  private final CamundaEngineDtoMapper mapper;

  public CamundaIncidentService(RuntimeService runtimeService, CamundaEngineDtoMapper mapper) {
    this.runtimeService = runtimeService;
    this.mapper = mapper;
  }

  public List<IncidentDto> list(String processInstanceId, String incidentType, Integer firstResult, Integer maxResults) {
    IncidentQuery q = runtimeService.createIncidentQuery().orderByIncidentTimestamp().desc();
    if (processInstanceId != null && !processInstanceId.isBlank()) {
      q.processInstanceId(processInstanceId);
    }
    if (incidentType != null && !incidentType.isBlank()) {
      q.incidentType(incidentType);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toIncidentDto).toList();
  }

  public IncidentDto get(String id) {
    Incident i = runtimeService.createIncidentQuery().incidentId(id).singleResult();
    if (i == null) {
      throw new CamundaNotFoundException("Incident " + id + " does not exist");
    }
    return mapper.toIncidentDto(i);
  }
}
