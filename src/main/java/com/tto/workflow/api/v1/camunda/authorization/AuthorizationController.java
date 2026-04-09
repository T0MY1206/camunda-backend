package com.tto.workflow.api.v1.camunda.authorization;

import com.tto.workflow.service.CamundaAuthorizationQueryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/authorization")
@Tag(name = "Authorization", description = "Paridad REST: autorizaciones")
public class AuthorizationController {

  private final CamundaAuthorizationQueryService authorizationQueryService;

  public AuthorizationController(CamundaAuthorizationQueryService authorizationQueryService) {
    this.authorizationQueryService = authorizationQueryService;
  }

  @GetMapping
  @Operation(summary = "Listar autorizaciones (resumen)")
  public List<Map<String, String>> list(
      @RequestParam(required = false) Integer firstResult, @RequestParam(required = false) Integer maxResults) {
    return authorizationQueryService.list(firstResult, maxResults);
  }
}
