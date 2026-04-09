package com.tto.workflow.service;

import com.tto.workflow.api.v1.common.CamundaNotFoundException;
import com.tto.workflow.api.v1.dto.camunda.GroupDto;
import com.tto.workflow.api.v1.dto.camunda.UserDto;
import com.tto.workflow.api.v1.mapper.CamundaEngineDtoMapper;
import java.util.List;
import org.camunda.bpm.engine.IdentityService;
import org.camunda.bpm.engine.identity.Group;
import org.camunda.bpm.engine.identity.GroupQuery;
import org.camunda.bpm.engine.identity.User;
import org.camunda.bpm.engine.identity.UserQuery;
import org.springframework.stereotype.Service;

@Service
public class CamundaIdentityService {

  private final IdentityService identityService;
  private final CamundaEngineDtoMapper mapper;

  public CamundaIdentityService(IdentityService identityService, CamundaEngineDtoMapper mapper) {
    this.identityService = identityService;
    this.mapper = mapper;
  }

  public List<UserDto> listUsers(String firstName, String lastName, Integer firstResult, Integer maxResults) {
    UserQuery q = identityService.createUserQuery().orderByUserId().asc();
    if (firstName != null && !firstName.isBlank()) {
      q.userFirstName(firstName);
    }
    if (lastName != null && !lastName.isBlank()) {
      q.userLastName(lastName);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toUserDto).toList();
  }

  public UserDto getUser(String id) {
    User u = identityService.createUserQuery().userId(id).singleResult();
    if (u == null) {
      throw new CamundaNotFoundException("User " + id + " does not exist");
    }
    return mapper.toUserDto(u);
  }

  public List<GroupDto> listGroups(String name, Integer firstResult, Integer maxResults) {
    GroupQuery q = identityService.createGroupQuery().orderByGroupId().asc();
    if (name != null && !name.isBlank()) {
      q.groupName(name);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(mapper::toGroupDto).toList();
  }

  public GroupDto getGroup(String id) {
    Group g = identityService.createGroupQuery().groupId(id).singleResult();
    if (g == null) {
      throw new CamundaNotFoundException("Group " + id + " does not exist");
    }
    return mapper.toGroupDto(g);
  }
}
