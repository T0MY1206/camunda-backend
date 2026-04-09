package com.tto.workflow.service;

import com.tto.workflow.api.v1.dto.camunda.EventSubscriptionDto;
import com.tto.workflow.api.v1.mapper.CamundaDtoMapper;
import java.util.List;
import org.camunda.bpm.engine.RuntimeService;
import org.camunda.bpm.engine.runtime.EventSubscription;
import org.camunda.bpm.engine.runtime.EventSubscriptionQuery;
import org.springframework.stereotype.Service;

@Service
public class CamundaEventSubscriptionService {

  private final RuntimeService runtimeService;
  private final CamundaDtoMapper mapper;

  public CamundaEventSubscriptionService(RuntimeService runtimeService, CamundaDtoMapper mapper) {
    this.runtimeService = runtimeService;
    this.mapper = mapper;
  }

  public List<EventSubscriptionDto> list(
      String processInstanceId,
      String executionId,
      String eventType,
      String eventName,
      Integer firstResult,
      Integer maxResults) {
    EventSubscriptionQuery q = runtimeService.createEventSubscriptionQuery().orderById().asc();
    if (processInstanceId != null && !processInstanceId.isBlank()) {
      q.processInstanceId(processInstanceId);
    }
    if (executionId != null && !executionId.isBlank()) {
      q.executionId(executionId);
    }
    if (eventType != null && !eventType.isBlank()) {
      q.eventType(eventType);
    }
    if (eventName != null && !eventName.isBlank()) {
      q.eventName(eventName);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toEventSubscriptionDto).toList();
  }

  public EventSubscriptionDto get(String id) {
    EventSubscription es = runtimeService.createEventSubscriptionQuery().eventSubscriptionId(id).singleResult();
    if (es == null) {
      throw new com.tto.workflow.api.v1.common.CamundaNotFoundException("Event subscription " + id + " does not exist");
    }
    return mapper.toEventSubscriptionDto(es);
  }
}
