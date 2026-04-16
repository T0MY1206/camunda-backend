package com.tto.workflow.api.v1.camunda.eventsubscription;

import com.tto.workflow.api.v1.dto.camunda.EventSubscriptionDto;
import com.tto.workflow.service.CamundaEventSubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/event-subscription")
@Tag(name = "EventSubscription", description = "Paridad REST: suscripciones a eventos")
public class EventSubscriptionController {

  private final CamundaEventSubscriptionService eventSubscriptionService;

  public EventSubscriptionController(CamundaEventSubscriptionService eventSubscriptionService) {
    this.eventSubscriptionService = eventSubscriptionService;
  }

  @GetMapping
  @Operation(summary = "Listar suscripciones")
  public List<EventSubscriptionDto> list(
      @RequestParam(required = false) String processInstanceId,
      @RequestParam(required = false) String executionId,
      @RequestParam(required = false) String eventType,
      @RequestParam(required = false) String eventName,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return eventSubscriptionService.list(
        processInstanceId, executionId, eventType, eventName, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Suscripción por id")
  public EventSubscriptionDto get(@PathVariable String id) {
    return eventSubscriptionService.get(id);
  }
}
