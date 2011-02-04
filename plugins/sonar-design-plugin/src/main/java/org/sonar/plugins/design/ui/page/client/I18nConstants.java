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
package org.sonar.plugins.design.ui.page.client;

import com.google.gwt.core.client.GWT;

public interface I18nConstants extends com.google.gwt.i18n.client.Constants {

  static I18nConstants INSTANCE = GWT.create(I18nConstants.class);

  @DefaultStringValue("Dependency")
  String legendDependencies();

  @DefaultStringValue("Suspect dependency (cycle)")
  String legendCycles();

  @DefaultStringValue("- uses >")
  String legendUses();
  
  @DefaultStringValue("No data")
  String noData();

  @DefaultStringValue("New window")
  String newWindow();

  @DefaultStringValue("Click to highlight, double-click to display more details.")
  String cellTooltip();

  @DefaultStringValue("Click to highlight, double-click to zoom.")
  String rowTooltip();

  @DefaultStringValue("Help")
  String linkToHelp();
}
