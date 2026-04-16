package com.tto.workflow.api.v1.camunda.identity;

import com.tto.workflow.api.v1.dto.camunda.UserDto;
import com.tto.workflow.service.CamundaIdentityService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/camunda/user")
@Tag(name = "User", description = "Paridad REST: usuarios (identity)")
public class UserController {

  private final CamundaIdentityService identityService;

  public UserController(CamundaIdentityService identityService) {
    this.identityService = identityService;
  }

  @GetMapping
  @Operation(summary = "Listar usuarios")
  public List<UserDto> list(
      @RequestParam(required = false) String firstName,
      @RequestParam(required = false) String lastName,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return identityService.listUsers(firstName, lastName, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Usuario por id")
  public UserDto get(@PathVariable String id) {
    return identityService.getUser(id);
  }
}
