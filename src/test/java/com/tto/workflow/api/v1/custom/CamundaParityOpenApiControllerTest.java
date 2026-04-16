package com.tto.workflow.api.v1.custom;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(controllers = CamundaParityOpenApiController.class)
class CamundaParityOpenApiControllerTest {

  @Autowired private MockMvc mockMvc;

  @Test
  void servesOfficialOpenApiRebasedToParityPrefix() throws Exception {
    mockMvc
        .perform(get("/v3/api-docs/camunda-parity").accept(MediaType.APPLICATION_JSON))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.openapi").value("3.0.2"))
        .andExpect(jsonPath("$.servers[0].url").value("/api/v1/camunda"))
        .andExpect(jsonPath("$.x-camunda-source-openapi-version").value("7.24.0"));
  }
}
