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
package org.sonar.api.test;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.project.MavenProject;
import org.sonar.api.CoreProperties;
import org.sonar.api.batch.maven.MavenUtils;
import org.sonar.api.resources.DefaultProjectFileSystem;
import org.sonar.api.resources.Java;
import org.sonar.api.resources.Languages;
import org.sonar.api.resources.Project;
import org.sonar.api.utils.SonarException;

import java.io.File;
import java.io.FileReader;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public final class MavenTestUtils {

  public static MavenProject loadPom(Class clazz, String path) {
    String fullpath = "/" + clazz.getName().replaceAll("\\.", "/") + "/" + path;
    return loadPom(fullpath);
  }

  public static MavenProject loadPom(String pomUrlInClasspath) {
    FileReader fileReader = null;
    try {
      File pomFile = new File(MavenTestUtils.class.getResource(pomUrlInClasspath).toURI());
      MavenXpp3Reader pomReader = new MavenXpp3Reader();
      fileReader = new FileReader(pomFile);
      Model model = pomReader.read(fileReader);
      MavenProject project = new MavenProject(model);
      project.setFile(pomFile);
      project.getBuild().setDirectory(pomFile.getParentFile().getPath());
      project.addCompileSourceRoot(pomFile.getParentFile().getPath() + "/src/main/java");
      project.addTestCompileSourceRoot(pomFile.getParentFile().getPath() + "/src/test/java");
      return project;
    } catch (Exception e) {
      throw new SonarException("Failed to read Maven project file : " + pomUrlInClasspath, e);

    } finally {
      IOUtils.closeQuietly(fileReader);
    }
  }

  public static Project loadProjectFromPom(Class clazz, String path) {
    MavenProject pom = loadPom(clazz, path);
    Configuration configuration = new MapConfiguration(pom.getProperties());
    Project project = new Project(pom.getGroupId() + ":" + pom.getArtifactId())
        .setPom(pom)
        .setConfiguration(configuration);
    configuration.setProperty("sonar.java.source", MavenUtils.getJavaSourceVersion(pom));
    configuration.setProperty("sonar.java.target", MavenUtils.getJavaVersion(pom));
    configuration.setProperty(CoreProperties.ENCODING_PROPERTY, MavenUtils.getSourceEncoding(pom));
    DefaultProjectFileSystem fs = new DefaultProjectFileSystem(project, new Languages(Java.INSTANCE));
    project.setFileSystem(fs);
    return project;
  }

  public static MavenProject mockPom(String packaging) {
    MavenProject mavenProject = mock(MavenProject.class);
    when(mavenProject.getPackaging()).thenReturn(packaging);
    return mavenProject;
  }
}
