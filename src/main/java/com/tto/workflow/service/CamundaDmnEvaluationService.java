package com.tto.workflow.service;

import java.util.Map;
import org.camunda.bpm.dmn.engine.DmnDecisionTableResult;
import org.camunda.bpm.engine.DecisionService;
import org.springframework.stereotype.Service;

@Service
public class CamundaDmnEvaluationService {

  private final DecisionService decisionService;

  public CamundaDmnEvaluationService(DecisionService decisionService) {
    this.decisionService = decisionService;
  }

  public Map<String, Object> evaluateByKey(String key, Map<String, Object> variables) {
    DmnDecisionTableResult result =
        decisionService.evaluateDecisionTableByKey(key, variables != null ? variables : Map.of());
    return Map.of("resultList", result.getResultList());
  }
}
