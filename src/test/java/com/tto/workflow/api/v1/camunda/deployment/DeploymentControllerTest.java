package com.tto.workflow.api.v1.camunda.deployment;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tto.workflow.api.v1.dto.camunda.DeploymentDto;
import com.tto.workflow.service.CamundaDeploymentService;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = DeploymentController.class)
class DeploymentControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CamundaDeploymentService deploymentService;

  @Test
  void listReturnsJson() throws Exception {
    when(deploymentService.list(any(), any(), any(), any(), any(), any()))
        .thenReturn(
            List.of(
                new DeploymentDto(
                    "d1", "demo", "2025-01-01T00:00:00Z", "process application", null)));

    mockMvc
        .perform(get("/api/v1/camunda/deployment").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$[0].id").value("d1"));
  }

  @Test
  void getById() throws Exception {
    when(deploymentService.get("d1"))
        .thenReturn(new DeploymentDto("d1", "demo", "2025-01-01T00:00:00Z", "process application", null));

    mockMvc
        .perform(get("/api/v1/camunda/deployment/d1").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id").value("d1"));
  }
}
