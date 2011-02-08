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
package org.sonar.batch.phases;

import org.apache.commons.lang.StringUtils;
import org.sonar.api.BatchComponent;
import org.sonar.api.resources.Project;
import org.sonar.batch.MavenPluginExecutor;

public class MavenPhaseExecutor implements BatchComponent {

  public static final String PROP_PHASE = "sonar.phase";

  private MavenPluginExecutor executor;

  public MavenPhaseExecutor(MavenPluginExecutor executor) {
    this.executor = executor;
  }

  public void execute(Project project) {
    String mavenPhase = (String) project.getProperty(PROP_PHASE);
    if (!StringUtils.isBlank(mavenPhase)) {
      executor.execute(project, mavenPhase);
    }
  }
}
