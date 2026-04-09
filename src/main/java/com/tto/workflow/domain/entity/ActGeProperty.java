package com.tto.workflow.domain.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "ACT_GE_PROPERTY")
public class ActGeProperty {

  @Id
  @Column(name = "NAME_", length = 64)
  private String name;

  @Column(name = "VALUE_", length = 300)
  private String value;

  @Column(name = "REV_")
  private Integer rev;

  protected ActGeProperty() {}

  public String getName() {
    return name;
  }

  public String getValue() {
    return value;
  }

  public Integer getRev() {
    return rev;
  }
}
