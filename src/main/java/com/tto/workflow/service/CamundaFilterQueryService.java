package com.tto.workflow.service;

import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.FilterService;
import org.camunda.bpm.engine.filter.Filter;
import org.camunda.bpm.engine.filter.FilterQuery;
import org.springframework.stereotype.Service;

@Service
public class CamundaFilterQueryService {

  private final FilterService filterService;

  public CamundaFilterQueryService(FilterService filterService) {
    this.filterService = filterService;
  }

  public List<Map<String, String>> list(Integer firstResult, Integer maxResults) {
    FilterQuery q = filterService.createFilterQuery().orderByName().asc();
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(this::toBrief).toList();
  }

  private Map<String, String> toBrief(Filter f) {
    return Map.of(
        "id", f.getId() != null ? f.getId() : "",
        "name", f.getName() != null ? f.getName() : "",
        "resourceType", f.getResourceType() != null ? f.getResourceType() : "");
  }
}
