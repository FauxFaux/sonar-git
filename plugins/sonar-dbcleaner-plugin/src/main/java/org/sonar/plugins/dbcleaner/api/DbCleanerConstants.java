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
package org.sonar.plugins.dbcleaner.api;

public interface DbCleanerConstants {

  String PLUGIN_KEY = "dbcleaner";
  String PLUGIN_NAME = "DbCleaner";
  String MONTHS_BEFORE_KEEPING_ONLY_ONE_SNAPSHOT_BY_WEEK = "sonar.dbcleaner.monthsBeforeKeepingOnlyOneSnapshotByWeek";
  String MONTHS_BEFORE_KEEPING_ONLY_ONE_SNAPSHOT_BY_MONTH = "sonar.dbcleaner.monthsBeforeKeepingOnlyOneSnapshotByMonth";
  String MONTHS_BEFORE_DELETING_ALL_SNAPSHOTS = "sonar.dbcleaner.monthsBeforeDeletingAllSnapshots";
  String ONE_MONTH = "1";
  String ONE_YEAR = "12";
  String FIVE_YEARS = "60";
}
