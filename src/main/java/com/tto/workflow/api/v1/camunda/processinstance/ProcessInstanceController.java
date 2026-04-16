package com.tto.workflow.api.v1.camunda.processinstance;

import com.tto.workflow.api.v1.dto.camunda.ProcessInstanceDto;
import com.tto.workflow.api.v1.dto.camunda.StartProcessInstanceDto;
import com.tto.workflow.service.CamundaProcessInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/process-instance")
@Tag(name = "ProcessInstance", description = "Paridad REST: instancias de proceso")
public class ProcessInstanceController {

  private final CamundaProcessInstanceService processInstanceService;

  public ProcessInstanceController(CamundaProcessInstanceService processInstanceService) {
    this.processInstanceService = processInstanceService;
  }

  @GetMapping
  @Operation(summary = "Listar instancias en ejecución")
  public List<ProcessInstanceDto> list(
      @RequestParam(required = false) String processDefinitionKey,
      @RequestParam(required = false) String businessKey,
      @RequestParam(required = false) String tenantId,
      @RequestParam(required = false) Boolean suspended,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return processInstanceService.list(
        processDefinitionKey, businessKey, tenantId, suspended, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Instancia por id")
  public ProcessInstanceDto get(@PathVariable String id) {
    return processInstanceService.get(id);
  }

  @PostMapping("/start")
  @Operation(summary = "Iniciar por id de definición")
  public ProcessInstanceDto startByDefinitionId(
      @RequestParam String processDefinitionId, @RequestBody(required = false) StartProcessInstanceDto body) {
    return processInstanceService.startByProcessDefinitionId(processDefinitionId, body);
  }

  @PostMapping("/start/key/{key}")
  @Operation(summary = "Iniciar por clave de definición")
  public ProcessInstanceDto startByKey(
      @PathVariable String key,
      @RequestParam(required = false) String tenantId,
      @RequestBody(required = false) StartProcessInstanceDto body) {
    return processInstanceService.startByProcessDefinitionKey(key, tenantId, body);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Eliminar instancia (borrado en runtime)")
  public void delete(@PathVariable String id, @RequestParam(required = false) String reason) {
    processInstanceService.delete(id, reason);
  }
}
