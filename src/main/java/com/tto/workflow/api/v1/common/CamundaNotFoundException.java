package com.tto.workflow.api.v1.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.NOT_FOUND)
public class CamundaNotFoundException extends RuntimeException {

  public CamundaNotFoundException(String message) {
    super(message);
  }
}
