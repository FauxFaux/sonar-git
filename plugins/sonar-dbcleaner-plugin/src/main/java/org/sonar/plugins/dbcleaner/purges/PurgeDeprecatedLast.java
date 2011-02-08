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
package org.sonar.plugins.dbcleaner.purges;

import org.sonar.api.database.DatabaseSession;
import org.sonar.api.database.model.Snapshot;
import org.sonar.plugins.dbcleaner.api.Purge;
import org.sonar.plugins.dbcleaner.api.PurgeContext;
import org.sonar.plugins.dbcleaner.api.PurgeUtils;

import java.util.List;

import javax.persistence.Query;

/**
 * @since 1.11
 */
public final class PurgeDeprecatedLast extends Purge {

  public PurgeDeprecatedLast(DatabaseSession session) {
    super(session);
  }

  public void purge(PurgeContext context) {
    Query query = getSession().createQuery("SELECT s.id FROM " + Snapshot.class.getSimpleName() +
        " s WHERE s.last=true AND s.rootId IS NOT NULL AND NOT EXISTS(FROM " + Snapshot.class.getSimpleName() + " s2 WHERE s2.id=s.rootId AND s2.last=true)");
    List<Integer> snapshotIds = query.getResultList();

    PurgeUtils.deleteSnapshotsData(getSession(), snapshotIds);
  }
}
