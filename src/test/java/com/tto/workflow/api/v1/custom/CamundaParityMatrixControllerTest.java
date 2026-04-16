package com.tto.workflow.api.v1.custom;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.tto.workflow.service.CamundaParityMatrixService;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CamundaParityMatrixController.class)
class CamundaParityMatrixControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockBean private CamundaParityMatrixService matrixService;

  @Test
  void returnsParityMatrixSummary() throws Exception {
    when(matrixService.buildSummary())
        .thenReturn(
            Map.of(
                "source", "camunda-engine-rest-openapi 7.24.0",
                "parityPrefix", "/api/v1/camunda",
                "totalOperations", 100));

    mockMvc
        .perform(get("/api/v1/custom/camunda-parity/matrix").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.source").value("camunda-engine-rest-openapi 7.24.0"))
        .andExpect(jsonPath("$.parityPrefix").value("/api/v1/camunda"))
        .andExpect(jsonPath("$.totalOperations").value(100));
  }
}
