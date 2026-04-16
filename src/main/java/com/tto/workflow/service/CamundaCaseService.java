package com.tto.workflow.service;

import com.tto.workflow.api.v1.common.CamundaNotFoundException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.camunda.bpm.engine.CaseService;
import org.camunda.bpm.engine.runtime.CaseExecution;
import org.camunda.bpm.engine.runtime.CaseExecutionQuery;
import org.springframework.stereotype.Service;

/**
 * CMMN: consultas básicas de ejecuciones de caso cuando el motor tiene definiciones CMMN desplegadas.
 */
@Service
public class CamundaCaseService {

  private final CaseService caseService;

  public CamundaCaseService(CaseService caseService) {
    this.caseService = caseService;
  }

  public List<Map<String, String>> listCaseExecutions(String caseInstanceId, Integer firstResult, Integer maxResults) {
    CaseExecutionQuery q = caseService.createCaseExecutionQuery().orderByCaseExecutionId().asc();
    if (caseInstanceId != null && !caseInstanceId.isBlank()) {
      q.caseInstanceId(caseInstanceId);
    }
    int first = firstResult != null ? firstResult : 0;
    int max = maxResults != null ? maxResults : 10;
    return q.listPage(first, max).stream().map(this::toBrief).toList();
  }

  public Map<String, String> getCaseExecution(String id) {
    CaseExecution ce = caseService.createCaseExecutionQuery().caseExecutionId(id).singleResult();
    if (ce == null) {
      throw new CamundaNotFoundException("Case execution " + id + " does not exist");
    }
    return toBrief(ce);
  }

  private Map<String, String> toBrief(CaseExecution ce) {
    Map<String, String> m = new HashMap<>();
    m.put("id", ce.getId());
    m.put("caseInstanceId", ce.getCaseInstanceId() != null ? ce.getCaseInstanceId() : "");
    m.put("activityId", ce.getActivityId() != null ? ce.getActivityId() : "");
    return m;
  }
}
