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
package org.sonar.application;

import org.apache.commons.lang.StringUtils;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class JettyEmbedderTest {

  @Test
  public void xmlConfigurationShouldAccessToSomeSystemProperties() throws Exception {
    // useful to set the port into the XML file
    new JettyEmbedder("127.0.0.1", 9999, "/", 10000, JettyEmbedderTest.class.getResource("/org/sonar/application/jetty-test.xml"));
    assertEquals("127.0.0.1", System.getProperty("jetty.host"));
    assertEquals("9999", System.getProperty("jetty.port"));
    assertEquals("/", System.getProperty("jetty.context"));
    assertEquals("10000", System.getProperty("jetty.ajp13Port"));
  }

  @Test
  public void shouldUseDefaultConfigurationIfNoXml() throws Exception {
    JettyEmbedder jetty = new JettyEmbedder("1.2.3.4", 9999);
    assertEquals(1, jetty.getServer().getConnectors().length);
    assertEquals(9999, jetty.getServer().getConnectors()[0].getPort());
    assertEquals("1.2.3.4", jetty.getServer().getConnectors()[0].getHost());
  }

  @Test
  public void shouldLoadPluginsClasspath() throws Exception {
    JettyEmbedder jetty = new JettyEmbedder("127.0.0.1", 9999);
    String classpath = jetty.getPluginsClasspath("/org/sonar/application/JettyEmbedderTest/shouldLoadPluginsClasspath");
    classpath = StringUtils.replaceChars(classpath, "\\", "/");

    assertTrue(classpath, classpath.contains("org/sonar/application/JettyEmbedderTest/shouldLoadPluginsClasspath/plugin1.jar"));
    assertTrue(classpath, classpath.contains("org/sonar/application/JettyEmbedderTest/shouldLoadPluginsClasspath/plugin2.jar"));

    // important : directories end with /
    assertTrue(classpath, classpath.contains("org/sonar/application/JettyEmbedderTest/shouldLoadPluginsClasspath/,"));
  }
}
