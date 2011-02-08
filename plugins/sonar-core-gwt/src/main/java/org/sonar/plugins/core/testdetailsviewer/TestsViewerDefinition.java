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
package org.sonar.plugins.core.testdetailsviewer;

import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.resources.Qualifiers;
import org.sonar.api.web.*;
import org.sonar.plugins.core.testdetailsviewer.client.TestsViewer;

@ResourceQualifier(Qualifiers.UNIT_TEST_FILE)
@NavigationSection(NavigationSection.RESOURCE_TAB)
@DefaultTab(metrics = {CoreMetrics.TESTS_KEY, CoreMetrics.TEST_EXECUTION_TIME_KEY, CoreMetrics.TEST_SUCCESS_DENSITY_KEY, CoreMetrics.TEST_FAILURES_KEY, CoreMetrics.TEST_ERRORS_KEY, CoreMetrics.SKIPPED_TESTS_KEY})
@UserRole(UserRole.CODEVIEWER)
public class TestsViewerDefinition extends GwtPage {

  public String getTitle() {
    return "Tests";
  }

  public String getGwtId() {
    return TestsViewer.GWT_ID;
  }

}
