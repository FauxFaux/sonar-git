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
package org.sonar.batch.components;

import org.sonar.api.BatchExtension;
import org.sonar.api.CoreProperties;
import org.sonar.api.database.DatabaseSession;
import org.sonar.api.database.model.Snapshot;
import org.sonar.api.resources.Project;

import java.text.SimpleDateFormat;
import java.util.List;

public class PastSnapshotFinderByPreviousAnalysis implements BatchExtension {

  private DatabaseSession session;

  public PastSnapshotFinderByPreviousAnalysis(DatabaseSession session) {
    this.session = session;
  }

  PastSnapshot findByPreviousAnalysis(Snapshot projectSnapshot) {
    String hql = "from " + Snapshot.class.getSimpleName() + " where createdAt<:date AND resourceId=:resourceId AND status=:status and last=:last and qualifier<>:lib order by createdAt desc";
    List<Snapshot> snapshots = session.createQuery(hql)
        .setParameter("date", projectSnapshot.getCreatedAt())
        .setParameter("resourceId", projectSnapshot.getResourceId())
        .setParameter("status", Snapshot.STATUS_PROCESSED)
        .setParameter("last", true)
        .setParameter("lib", Project.QUALIFIER_LIB)
        .setMaxResults(1)
        .getResultList();

    if (snapshots.isEmpty()) {
      return null;
    }
    Snapshot snapshot = snapshots.get(0);
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    return new PastSnapshot(CoreProperties.TIMEMACHINE_MODE_PREVIOUS_ANALYSIS, snapshot.getCreatedAt(), snapshot).setModeParameter(format.format(snapshot.getCreatedAt()));
  }

}
