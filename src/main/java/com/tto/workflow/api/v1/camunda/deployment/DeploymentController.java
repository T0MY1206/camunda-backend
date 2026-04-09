package com.tto.workflow.api.v1.camunda.deployment;

import com.tto.workflow.api.v1.dto.camunda.DeploymentDto;
import com.tto.workflow.service.CamundaDeploymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/deployment")
@Tag(name = "Deployment", description = "Paridad REST: despliegues")
public class DeploymentController {

  private final CamundaDeploymentService deploymentService;

  public DeploymentController(CamundaDeploymentService deploymentService) {
    this.deploymentService = deploymentService;
  }

  @GetMapping
  @Operation(summary = "Listar despliegues")
  public List<DeploymentDto> list(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) String nameLike,
      @RequestParam(required = false) String source,
      @RequestParam(required = false) String tenantId,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return deploymentService.list(name, nameLike, source, tenantId, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Obtener despliegue por id")
  public DeploymentDto get(@PathVariable String id) {
    return deploymentService.get(id);
  }

  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  @Operation(summary = "Eliminar despliegue")
  public void delete(@PathVariable String id, @RequestParam(defaultValue = "false") boolean cascade) {
    deploymentService.delete(id, cascade);
  }
}
