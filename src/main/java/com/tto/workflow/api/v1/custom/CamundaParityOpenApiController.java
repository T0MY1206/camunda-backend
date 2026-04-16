package com.tto.workflow.api.v1.custom;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.swagger.v3.oas.annotations.Hidden;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@Hidden
@RestController
public class CamundaParityOpenApiController {

  private final ObjectMapper objectMapper;

  public CamundaParityOpenApiController(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  @GetMapping(value = "/v3/api-docs/camunda-parity", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<JsonNode> camundaParityOpenApi() throws IOException {
    try (InputStream stream = getClass().getClassLoader().getResourceAsStream("openapi.json")) {
      if (stream == null) {
        return ResponseEntity.internalServerError().build();
      }

      ObjectNode root = (ObjectNode) objectMapper.readTree(stream);
      ArrayNode servers = objectMapper.createArrayNode();
      ObjectNode server = objectMapper.createObjectNode();
      server.put("url", "/api/v1/camunda");
      server.put("description", "Paridad Camunda 7.24 sobre /api/v1/camunda");
      servers.add(server);
      root.set("servers", servers);
      root.put("x-camunda-source-openapi-version", "7.24.0");
      return ResponseEntity.ok(root);
    }
  }
}
