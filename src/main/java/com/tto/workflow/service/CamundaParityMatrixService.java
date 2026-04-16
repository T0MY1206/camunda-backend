package com.tto.workflow.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class CamundaParityMatrixService {

  private final ObjectMapper objectMapper;

  public CamundaParityMatrixService(ObjectMapper objectMapper) {
    this.objectMapper = objectMapper;
  }

  public Map<String, Object> buildSummary() {
    JsonNode root = loadOpenApi();
    JsonNode pathsNode = root.path("paths");
    int totalPaths = 0;
    int totalOperations = 0;
    List<Map<String, String>> samples = new ArrayList<>();

    Iterator<Map.Entry<String, JsonNode>> paths = ((ObjectNode) pathsNode).properties().iterator();
    while (paths.hasNext()) {
      Map.Entry<String, JsonNode> pathEntry = paths.next();
      totalPaths++;
      String enginePath = pathEntry.getKey();
      JsonNode operations = pathEntry.getValue();
      Iterator<String> methods = operations.fieldNames();
      while (methods.hasNext()) {
        String method = methods.next();
        totalOperations++;
        if (samples.size() < 30) {
          samples.add(
              Map.of(
                  "method", method.toUpperCase(),
                  "engineRestPath", enginePath,
                  "parityPath", "/api/v1/camunda" + enginePath));
        }
      }
    }

    Map<String, Object> matrix = new LinkedHashMap<>();
    matrix.put("source", "camunda-engine-rest-openapi 7.24.0");
    matrix.put("engineRestPrefix", "/engine-rest");
    matrix.put("parityPrefix", "/api/v1/camunda");
    matrix.put("totalPaths", totalPaths);
    matrix.put("totalOperations", totalOperations);
    matrix.put("notes", List.of("Case resources are not included by Camunda OpenAPI artifact"));
    matrix.put("sampleMappings", samples);
    return matrix;
  }

  private JsonNode loadOpenApi() {
    try (InputStream stream = getClass().getClassLoader().getResourceAsStream("openapi.json")) {
      if (stream == null) {
        throw new IllegalStateException("openapi.json not found in classpath");
      }
      return objectMapper.readTree(stream);
    } catch (IOException ex) {
      throw new IllegalStateException("Cannot load Camunda OpenAPI matrix", ex);
    }
  }
}
