package com.tto.workflow.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "ACT_GE_SCHEMA_LOG")
public class ActGeSchemaLog {

  @Id
  @Column(name = "ID_", length = 64)
  private String id;

  @Column(name = "TIMESTAMP_")
  private Instant timestamp;

  @Column(name = "VERSION_", length = 255)
  private String version;

  protected ActGeSchemaLog() {}

  public String getId() {
    return id;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public String getVersion() {
    return version;
  }
}
