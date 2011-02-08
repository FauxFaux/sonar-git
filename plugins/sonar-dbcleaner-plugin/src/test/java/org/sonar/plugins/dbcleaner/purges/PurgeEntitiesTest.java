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

import org.apache.commons.configuration.PropertiesConfiguration;
import org.junit.Test;
import org.sonar.jpa.test.AbstractDbUnitTestCase;

public class PurgeEntitiesTest extends AbstractDbUnitTestCase {

  @Test
  public void purgeEntities() {
    setupData("sharedFixture", "purgeEntities");

    final PurgeEntities purge = new PurgeEntities(getSession(), new PropertiesConfiguration());
    purge.purge(null);

    checkTables("purgeEntities", "snapshots", "project_measures", "measure_data", "rule_failures", "snapshot_sources");
  }
}
