package com.tto.workflow.api.v1.camunda.message;

import com.tto.workflow.api.v1.dto.camunda.MessageCorrelationDto;
import com.tto.workflow.service.CamundaMessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/message")
@Tag(name = "Message", description = "Paridad REST: correlación de mensajes")
public class MessageController {

  private final CamundaMessageService messageService;

  public MessageController(CamundaMessageService messageService) {
    this.messageService = messageService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Correlacionar mensaje con instancia de proceso")
  public void correlate(@RequestBody MessageCorrelationDto body) {
    messageService.correlate(body);
  }
}
