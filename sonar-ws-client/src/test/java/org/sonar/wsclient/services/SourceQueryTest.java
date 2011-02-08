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
package org.sonar.wsclient.services;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SourceQueryTest {

  @Test
  public void create() {
    assertThat(SourceQuery.create("myproject:org.foo.Bar").getUrl(), is("/api/sources?resource=myproject:org.foo.Bar&"));
    assertThat(SourceQuery.create("myproject:org.foo.Bar").getModelClass().getName(), is(Source.class.getName()));
  }

  @Test
  public void createWithHighlightedSyntax() {
    assertThat(SourceQuery.createWithHighlightedSyntax("myproject:org.foo.Bar").getUrl(), is("/api/sources?resource=myproject:org.foo.Bar&color=true&"));
    assertThat(SourceQuery.createWithHighlightedSyntax("myproject:org.foo.Bar").getModelClass().getName(), is(Source.class.getName()));
  }

  @Test
  public void getOnlyAFewLines() {
    assertThat(SourceQuery.create("myproject:org.foo.Bar").setFromLineToLine(10, 30).getUrl(), is("/api/sources?resource=myproject:org.foo.Bar&from=10&to=30&"));
    assertThat(SourceQuery.create("myproject:org.foo.Bar").setLinesFromLine(10, 20).getUrl(), is("/api/sources?resource=myproject:org.foo.Bar&from=10&to=30&"));
  }
}
