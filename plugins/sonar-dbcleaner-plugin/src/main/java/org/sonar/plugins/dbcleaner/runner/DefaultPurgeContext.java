/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2008-2011 SonarSource
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
package org.sonar.plugins.dbcleaner.runner;

import org.apache.commons.lang.builder.ToStringBuilder;
import org.sonar.api.database.model.Snapshot;
import org.sonar.api.resources.Project;
import org.sonar.plugins.dbcleaner.api.PurgeContext;

public final class DefaultPurgeContext implements org.sonar.api.batch.PurgeContext, PurgeContext {

  private Project project;
  private Integer currentSid;
  private Integer previousSid;

  public DefaultPurgeContext(Project project, Snapshot currentSnapshot) {
    this(project, currentSnapshot, null);
  }

  public DefaultPurgeContext(Project project, Snapshot currentSnapshot, Snapshot previousSnapshot) {
    this.project = project;
    if (currentSnapshot != null) {
      currentSid = currentSnapshot.getId();
    }
    if (previousSnapshot != null) {
      previousSid = previousSnapshot.getId();
    }
  }

  public DefaultPurgeContext setLastSnapshotId(Integer previousSid) {
    this.previousSid = previousSid;
    return this;
  }

  public Integer getSnapshotId() {
    return currentSid;
  }

  public Integer getPreviousSnapshotId() {
    return previousSid;
  }

  public Project getProject() {
    return project;
  }

  @Deprecated
  public Integer getLastSnapshotId() {
    return currentSid;
  }

  @Override
  public String toString() {
    return new ToStringBuilder(this)
        .append("currentSid", currentSid)
        .append("previousSid", previousSid)
        .toString();
  }
}
