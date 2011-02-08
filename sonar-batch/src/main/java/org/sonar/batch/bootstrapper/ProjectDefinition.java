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
package org.sonar.batch.bootstrapper;

import com.google.common.collect.Lists;

import java.io.File;
import java.util.List;
import java.util.Properties;

/**
 * Defines project in a form suitable to bootstrap Sonar batch.
 * We assume that project is just a set of configuration properties and directories.
 * This is a part of bootstrap process, so we should take care about backward compatibility.
 * 
 * @since 2.6
 */
public class ProjectDefinition {

  private File baseDir;
  private File workDir;
  private Properties properties;
  private List<String> sourceDirs = Lists.newArrayList();
  private List<String> testDirs = Lists.newArrayList();
  private List<String> binaries = Lists.newArrayList();
  private List<String> libraries = Lists.newArrayList();

  /**
   * @param baseDir project base directory
   * @param properties project properties
   */
  public ProjectDefinition(File baseDir, File workDir, Properties properties) {
    this.baseDir = baseDir;
    this.workDir = workDir;
    this.properties = properties;
  }

  public File getBaseDir() {
    return baseDir;
  }

  public File getWorkDir() {
    return workDir;
  }

  public Properties getProperties() {
    return properties;
  }

  public List<String> getSourceDirs() {
    return sourceDirs;
  }

  /**
   * @param path path to directory with main sources.
   *          It can be absolute or relative to project directory.
   */
  public void addSourceDir(String path) {
    sourceDirs.add(path);
  }

  public List<String> getTestDirs() {
    return testDirs;
  }

  /**
   * @param path path to directory with test sources.
   *          It can be absolute or relative to project directory.
   */
  public void addTestDir(String path) {
    testDirs.add(path);
  }

  public List<String> getBinaries() {
    return binaries;
  }

  /**
   * @param path path to directory with compiled source. In case of Java this is directory with class files.
   *          It can be absolute or relative to project directory.
   * @TODO currently Sonar supports only one such directory due to dependency on MavenProject
   */
  public void addBinaryDir(String path) {
    binaries.add(path);
  }

  public List<String> getLibraries() {
    return libraries;
  }

  /**
   * @param path path to file with third-party library. In case of Java this is path to jar file.
   *          It can be absolute or relative to project directory.
   */
  public void addLibrary(String path) {
    libraries.add(path);
  }
}
