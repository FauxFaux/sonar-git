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
package org.sonar.tests.integration.selenium;

import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class DeployUIExtensionsIT extends SonarTestCase {

  @Test
  public void gwtPageIsDisplayedInHomeSidebar() throws Exception {
    selenium.open("/");
		assertTrue(selenium.getText("sidebar").contains("GWT sample"));
		selenium.click("link=GWT sample");
		selenium.waitForPageToLoad("30000");
		assertTrue(selenium.isTextPresent("this is a GWT sample"));
  }

  @Test
  public void displayHhtmlFooter() throws Exception {
    selenium.open("/");
		assertTrue(selenium.getText("ft").contains("Sample footer"));
  }

}
