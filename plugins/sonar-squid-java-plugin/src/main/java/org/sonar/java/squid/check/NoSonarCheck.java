/*
 * Sonar, open source software quality management tool.
 * Copyright (C) 2009 SonarSource SA
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

package org.sonar.java.squid.check;

import org.sonar.check.IsoCategory;
import org.sonar.check.Priority;
import org.sonar.check.Rule;
import org.sonar.squid.api.CheckMessage;
import org.sonar.squid.api.SourceFile;

@Rule(key = "NoSonarCheck", name = "NOSONAR occurence", isoCategory = IsoCategory.Maintainability, priority = Priority.INFO,
    description = "")
public class NoSonarCheck extends SquidCheck {

  @Override
  public void visitFile(SourceFile sourceFile) {
    for (Integer line : sourceFile.getNoSonarTagLines()) {
      CheckMessage message = new CheckMessage(this, "NOSONAR occurence");
      message.setForced(true);
      message.setLine(line);
      sourceFile.log(message);
    }
  }

}