package com.tto.workflow.service;

import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.AuthorizationService;
import org.camunda.bpm.engine.authorization.Authorization;
import org.camunda.bpm.engine.authorization.AuthorizationQuery;
import org.springframework.stereotype.Service;

@Service
public class CamundaAuthorizationQueryService {

  private final AuthorizationService authorizationService;

  public CamundaAuthorizationQueryService(AuthorizationService authorizationService) {
    this.authorizationService = authorizationService;
  }

  public List<Map<String, String>> list(Integer firstResult, Integer maxResults) {
    AuthorizationQuery q = authorizationService.createAuthorizationQuery();
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(this::toBrief).toList();
  }

  private Map<String, String> toBrief(Authorization a) {
    return Map.of(
        "id", a.getId() != null ? a.getId() : "",
        "type",
        String.valueOf(a.getAuthorizationType()),
        "resourceType", String.valueOf(a.getResourceType()));
  }
}
