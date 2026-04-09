package com.tto.workflow.api.v1.camunda.processdefinition;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tto.workflow.api.v1.dto.camunda.ProcessDefinitionDto;
import com.tto.workflow.service.CamundaProcessDefinitionService;
import com.tto.workflow.service.CamundaProcessInstanceService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = ProcessDefinitionController.class)
class ProcessDefinitionControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CamundaProcessDefinitionService processDefinitionService;

  @MockBean private CamundaProcessInstanceService processInstanceService;

  @Test
  void listDefinitions() throws Exception {
    when(processDefinitionService.list(any(), any(), any(), any(), any(), any(), any(), any(), any()))
        .thenReturn(
            List.of(
                new ProcessDefinitionDto(
                    "pd1",
                    "loan",
                    null,
                    null,
                    "Loan",
                    1,
                    "loan.bpmn",
                    "dep1",
                    null,
                    false,
                    null,
                    null,
                    null,
                    true)));

    mockMvc
        .perform(get("/api/v1/camunda/process-definition").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].key").value("loan"));
  }
}
