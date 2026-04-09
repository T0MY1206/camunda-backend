package com.tto.workflow.api.v1.custom;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/custom")
@Tag(name = "Custom", description = "APIs personalizadas (demo)")
public class CustomStatusController {

  @GetMapping("/status")
  @Operation(summary = "Estado del servicio personalizado")
  public Map<String, String> status() {
    return Map.of("status", "ok", "layer", "custom");
  }
}
