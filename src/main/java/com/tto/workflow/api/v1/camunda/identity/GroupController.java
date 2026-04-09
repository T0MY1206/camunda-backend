package com.tto.workflow.api.v1.camunda.identity;

import com.tto.workflow.api.v1.dto.camunda.GroupDto;
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
@RequestMapping("/api/v1/camunda/group")
@Tag(name = "Group", description = "Paridad REST: grupos (identity)")
public class GroupController {

  private final CamundaIdentityService identityService;

  public GroupController(CamundaIdentityService identityService) {
    this.identityService = identityService;
  }

  @GetMapping
  @Operation(summary = "Listar grupos")
  public List<GroupDto> list(
      @RequestParam(required = false) String name,
      @RequestParam(required = false) Integer firstResult,
      @RequestParam(required = false) Integer maxResults) {
    return identityService.listGroups(name, firstResult, maxResults);
  }

  @GetMapping("/{id}")
  @Operation(summary = "Grupo por id")
  public GroupDto get(@PathVariable String id) {
    return identityService.getGroup(id);
  }
}
