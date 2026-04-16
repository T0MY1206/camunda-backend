package com.tto.workflow.api.v1.camunda.processdefinition;

import com.tto.workflow.api.v1.dto.camunda.ProcessDefinitionDto;
import com.tto.workflow.api.v1.dto.camunda.ProcessInstanceDto;
import com.tto.workflow.api.v1.dto.camunda.StartProcessInstanceDto;
import com.tto.workflow.service.CamundaProcessDefinitionService;
import com.tto.workflow.service.CamundaProcessInstanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/process-definition")
@Tag(name = "ProcessDefinition", description = "Paridad REST: definiciones de proceso")
public class ProcessDefinitionController {

  private final CamundaProcessDefinitionService processDefinitionService;
  private final CamundaProcessInstanceService processInstanceService;

  public ProcessDefinitionController(
      CamundaProcessDefinitionService processDefinitionService,
      CamundaProcessInstanceService processInstanceService) {
    this.processDefinitionService = processDefinitionService;
    this.processInstanceService = processInstanceService;
  }

  @GetMapping
  @Operation(summary = "Listar definiciones de proceso")
  public List<ProcessDefinitionDto> list(
      @RequestParam(required = false) String key,
      @RequestParam(required = false) String keyLike,
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String nameLike,
      @RequestParam(required = false) String tenantId,
      @RequestParam(required = false) Boolean latestVersion,
      @RequestParam(required = false) Boolean suspended,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return processDefinitionService.list(
        key, keyLike, name, nameLike, tenantId, latestVersion, suspended, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Definición por id")
  public ProcessDefinitionDto getById(@PathVariable String id) {
    return processDefinitionService.getById(id);
  }

  @GetMapping("/key/{key}")
  @Operation(summary = "Última versión por clave (tenant opcional)")
  public ProcessDefinitionDto getByKey(
      @PathVariable String key, @RequestParam(required = false) String tenantId) {
    return processDefinitionService.getByKeyAndTenant(key, tenantId);
  }

  @PostMapping("/{id}/start")
  @Operation(summary = "Iniciar instancia por id de definición (paridad REST)")
  public ProcessInstanceDto start(
      @PathVariable String id, @RequestBody(required = false) StartProcessInstanceDto body) {
    return processInstanceService.startByProcessDefinitionId(id, body);
  }
}
