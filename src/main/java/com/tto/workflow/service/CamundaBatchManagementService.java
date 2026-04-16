package com.tto.workflow.service;

import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.ManagementService;
import org.camunda.bpm.engine.batch.Batch;
import org.camunda.bpm.engine.batch.BatchQuery;
import org.springframework.stereotype.Service;

@Service
public class CamundaBatchManagementService {

  private final ManagementService managementService;

  public CamundaBatchManagementService(ManagementService managementService) {
    this.managementService = managementService;
  }

  public List<Map<String, String>> listBatches(Integer firstResult, Integer maxResults) {
    BatchQuery q = managementService.createBatchQuery().orderById().asc();
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(this::toBrief).toList();
  }

  private Map<String, String> toBrief(Batch b) {
    return Map.of(
        "id", b.getId(),
        "type", b.getType() != null ? b.getType() : "",
        "jobsCreated", String.valueOf(b.getJobsCreated()));
  }
}
