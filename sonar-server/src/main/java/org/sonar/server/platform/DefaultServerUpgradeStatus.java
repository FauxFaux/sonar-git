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
package org.sonar.server.platform;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;
import org.apache.commons.lang.builder.ToStringStyle;
import org.sonar.api.platform.ServerUpgradeStatus;
import org.sonar.jpa.entity.SchemaMigration;
import org.sonar.jpa.session.DatabaseConnector;

/**
 * @since 2.5
 */
public final class DefaultServerUpgradeStatus implements ServerUpgradeStatus {

  private int initialDbVersion;
  private DatabaseConnector dbConnector;

  public DefaultServerUpgradeStatus(DatabaseConnector dbConnector) {
    this.dbConnector = dbConnector;
  }

  public void start() {
    this.initialDbVersion = dbConnector.getDatabaseVersion();
  }

  public boolean isUpgraded() {
    return !isFreshInstall() &&(initialDbVersion < SchemaMigration.LAST_VERSION);
  }

  public boolean isFreshInstall() {
    return initialDbVersion <= 0;
  }

  public int getInitialDbVersion() {
    return initialDbVersion;
  }

  @Override
  public String toString() {
    return new ReflectionToStringBuilder(this, ToStringStyle.SHORT_PREFIX_STYLE).toString();
  }
}
