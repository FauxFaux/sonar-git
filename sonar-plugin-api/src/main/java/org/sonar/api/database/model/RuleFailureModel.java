/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
 * mailto:contact AT sonarsource DOT com
 *
 * Sonar is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3 of the License, or (at your option) any later version.
 *
 * Sonar is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with Sonar; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 */
package org.sonar.api.database.model;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.sonar.api.database.BaseIdentifiable;
import org.sonar.api.rules.RulePriority;

import java.util.Date;

import javax.persistence.*;

@Entity
@Table(name = "rule_failures")
public class RuleFailureModel extends BaseIdentifiable {

  public static final int MESSAGE_COLUMN_SIZE = 4000;

  @Column(name = "snapshot_id")
  protected Integer snapshotId;

  @Column(name = "rule_id", updatable = false, nullable = false)
  private Integer ruleId;

  @Column(name = "failure_level", updatable = true, nullable = false)
  @Enumerated(EnumType.ORDINAL)
  private RulePriority priority;

  @Column(name = "message", updatable = false, nullable = true, length = MESSAGE_COLUMN_SIZE)
  private String message;

  @Column(name = "line", updatable = true, nullable = true)
  private Integer line;

  @Column(name = "cost", updatable = true, nullable = true)
  private Double cost;

  @Temporal(TemporalType.TIMESTAMP)
  @Column(name = "created_at", updatable = true, nullable = true)
  private Date createdAt;

  @Column(name = "checksum", updatable = true, nullable = true, length = 1000)
  private String checksum;

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = abbreviateMessage(message);
  }

  public static String abbreviateMessage(String message) {
    return StringUtils.abbreviate(StringUtils.trim(message), MESSAGE_COLUMN_SIZE);
  }

  public RulePriority getLevel() {
    return priority;
  }

  public void setLevel(RulePriority priority) {
    this.priority = priority;
  }

  public Integer getRuleId() {
    return ruleId;
  }

  public void setRuleId(Integer ruleId) {
    this.ruleId = ruleId;
  }

  public Integer getLine() {
    return line;
  }

  public RulePriority getPriority() {
    return priority;
  }

  public Integer getSnapshotId() {
    return snapshotId;
  }

  public void setSnapshotId(Integer snapshotId) {
    this.snapshotId = snapshotId;
  }

  public void setPriority(RulePriority priority) {
    this.priority = priority;
  }

  public void setLine(Integer line) {
    this.line = line;
  }

  public Double getCost() {
    return cost;
  }

  public RuleFailureModel setCost(Double d) {
    this.cost = d;
    return this;
  }

  public Date getCreatedAt() {
    return createdAt;
  }

  public void setCreatedAt(Date createdAt) {
    this.createdAt = createdAt;
  }

  public String getChecksum() {
    return checksum;
  }

  public void setChecksum(String checksum) {
    this.checksum = checksum;
  }

  @Override
  public boolean equals(Object obj) {
    if (!(obj instanceof RuleFailureModel)) {
      return false;
    }
    if (this == obj) {
      return true;
    }
    RuleFailureModel other = (RuleFailureModel) obj;
    return new EqualsBuilder()
        .append(getId(), other.getId()).isEquals();
  }

  @Override
  public int hashCode() {
    return new HashCodeBuilder(17, 37).
        append(getId()).toHashCode();
  }

  @Override
  public String toString() {
    return ReflectionToStringBuilder.toString(this);
  }
}
