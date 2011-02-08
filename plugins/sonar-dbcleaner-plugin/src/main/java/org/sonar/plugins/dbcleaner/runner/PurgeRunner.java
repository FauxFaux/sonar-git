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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.api.batch.PostJob;
import org.sonar.api.batch.SensorContext;
import org.sonar.api.database.DatabaseSession;
import org.sonar.api.database.model.Snapshot;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.TimeProfiler;
import org.sonar.plugins.dbcleaner.api.Purge;

import javax.persistence.Query;

public final class PurgeRunner implements PostJob {

  private DatabaseSession session;
  private Snapshot snapshot;
  private Purge[] purges;
  private org.sonar.api.batch.Purge[] deprecatedPurges;
  private Project project;
  private static final Logger LOG = LoggerFactory.getLogger(PurgeRunner.class);

  public PurgeRunner(DatabaseSession session, Project project, Snapshot snapshot, Purge[] purges) {
    this.session = session;
    this.project = project;
    this.snapshot = snapshot;
    this.purges = purges.clone();
    this.deprecatedPurges = new org.sonar.api.batch.Purge[0];
  }

  public PurgeRunner(DatabaseSession session, Project project, Snapshot snapshot, Purge[] purges, org.sonar.api.batch.Purge[] deprecatedPurges) {
    this.session = session;
    this.project = project;
    this.snapshot = snapshot;
    this.purges = purges.clone();
    this.deprecatedPurges = deprecatedPurges.clone();
  }

  public void executeOn(Project project, SensorContext context) {
    if (shouldExecuteOn(project)) {
      purge();
    }
  }

  static boolean shouldExecuteOn(Project project) {
    return project.isRoot();
  }

  public void purge() {
    TimeProfiler profiler = new TimeProfiler(LOG).start("Database optimization");
    DefaultPurgeContext context = newContext();
    LOG.debug("Snapshots to purge: " + context);
    executeDeprecatedPurges(context);
    executePurges(context);
    profiler.stop();
  }

  private void executeDeprecatedPurges(DefaultPurgeContext context) {
    TimeProfiler profiler = new TimeProfiler();
    for (org.sonar.api.batch.Purge purge : deprecatedPurges) {
      profiler.start("Purge " + purge.getClass().getName());
      purge.purge(context);
      profiler.stop();
    }
  }

  private void executePurges(DefaultPurgeContext context) {
    TimeProfiler profiler = new TimeProfiler();
    for (Purge purge : purges) {
      profiler.start("Purge " + purge.getClass().getName());
      purge.purge(context);
      profiler.stop();
    }
  }

  private DefaultPurgeContext newContext() {
    DefaultPurgeContext context = new DefaultPurgeContext(project, snapshot);
    Snapshot previousLastSnapshot = getPreviousLastSnapshot();
    if (previousLastSnapshot != null && previousLastSnapshot.getCreatedAt().before(snapshot.getCreatedAt())) {
      context.setLastSnapshotId(previousLastSnapshot.getId());
    }
    return context;
  }

  private Snapshot getPreviousLastSnapshot() {
    Query query = session.createQuery(
        "SELECT s FROM " + Snapshot.class.getSimpleName() + " s " +
            "WHERE s.status=:status AND s.resourceId=:resourceId AND s.createdAt<:date AND s.id <> :sid ORDER BY s.createdAt DESC");
    query.setParameter("status", Snapshot.STATUS_PROCESSED);
    query.setParameter("resourceId", snapshot.getResourceId());
    query.setParameter("date", snapshot.getCreatedAt());
    query.setParameter("sid", snapshot.getId());
    query.setMaxResults(1);
    return session.getSingleResult(query, null);
  }
}
