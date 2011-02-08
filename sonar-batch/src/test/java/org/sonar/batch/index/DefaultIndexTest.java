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
package org.sonar.batch.index;

import org.apache.commons.lang.StringUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.sonar.api.batch.ResourceFilter;
import org.sonar.api.measures.CoreMetrics;
import org.sonar.api.measures.Measure;
import org.sonar.api.measures.MeasuresFilters;
import org.sonar.api.measures.MetricFinder;
import org.sonar.api.profiles.RulesProfile;
import org.sonar.api.resources.*;
import org.sonar.api.utils.SonarException;
import org.sonar.batch.DefaultResourceCreationLock;
import org.sonar.batch.ProjectTree;
import org.sonar.batch.ResourceFilters;
import org.sonar.batch.ViolationFilters;

import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DefaultIndexTest {

  private DefaultIndex index = null;
  private DefaultResourceCreationLock lock;

  @Before
  public void createIndex() {
    lock = new DefaultResourceCreationLock();
    MetricFinder metricFinder = mock(MetricFinder.class);
    when(metricFinder.findByKey("ncloc")).thenReturn(CoreMetrics.NCLOC);

    index = new DefaultIndex(mock(PersistenceManager.class), lock, mock(ProjectTree.class), metricFinder);
    Project project = new Project("project");

    ResourceFilter filter = new ResourceFilter() {
      public boolean isIgnored(Resource resource) {
        return StringUtils.containsIgnoreCase(resource.getKey(), "excluded");
      }
    };
    index.setCurrentProject(project, new ResourceFilters(new ResourceFilter[]{filter}), new ViolationFilters(), RulesProfile.create());
    index.doStart(project);
  }


  @Test
  public void shouldCreateUID() {
    Project project = new Project("my_project");
    assertThat(DefaultIndex.createUID(project, project), is("my_project"));

    JavaPackage javaPackage = new JavaPackage("org.foo");
    assertThat(DefaultIndex.createUID(project, javaPackage), is("my_project:org.foo"));

    Library library = new Library("junit:junit", "4.7");
    assertThat(DefaultIndex.createUID(project, library), is("junit:junit"));
  }

  @Test
  public void shouldIndexParentOfDeprecatedFiles() {
    File file = new File("org/foo/Bar.java");
    assertThat(index.index(file), is(true));

    Directory reference = new Directory("org/foo");
    assertThat(index.getResource(reference).getName(), is("org/foo"));
    assertThat(index.isIndexed(reference, true), is(true));
    assertThat(index.isExcluded(reference), is(false));
    assertThat(index.getChildren(reference).size(), is(1));
    assertThat(index.getParent(reference), is(Project.class));
  }

  @Test
  public void shouldIndexTreeOfResources() {
    Directory directory = new Directory("org/foo");
    File file = new File("org/foo/Bar.java");
    file.setLanguage(Java.INSTANCE);

    assertThat(index.index(directory), is(true));
    assertThat(index.index(file, directory), is(true));

    File fileRef = new File("org/foo/Bar.java");
    assertThat(index.getResource(fileRef).getKey(), is("org/foo/Bar.java"));
    assertThat(index.getResource(fileRef).getLanguage(), is((Language) Java.INSTANCE));
    assertThat(index.isIndexed(fileRef, true), is(true));
    assertThat(index.isExcluded(fileRef), is(false));
    assertThat(index.getChildren(fileRef).size(), is(0));
    assertThat(index.getParent(fileRef), is(Directory.class));
  }

  @Test
  public void shouldIndexLibraryOutsideProjectTree() {
    Library lib = new Library("junit", "4.8");
    assertThat(index.index(lib), is(true));

    Library reference = new Library("junit", "4.8");
    assertThat(index.getResource(reference).getQualifier(), is(Qualifiers.LIBRARY));
    assertThat(index.isIndexed(reference, true), is(true));
    assertThat(index.isExcluded(reference), is(false));
  }

  @Test
  public void shouldNotIndexResourceIfParentNotIndexed() {
    Directory directory = new Directory("org/other");
    File file = new File("org/foo/Bar.java");

    assertThat(index.index(file, directory), is(false));

    File fileRef = new File("org/foo/Bar.java");
    assertThat(index.isIndexed(directory, true), is(false));
    assertThat(index.isIndexed(fileRef, true), is(false));
    assertThat(index.isExcluded(fileRef), is(false));
    assertThat(index.getChildren(fileRef).size(), is(0));
    assertThat(index.getParent(fileRef), nullValue());
  }


  /**
   * Only a warning is logged when index is locked.
   */
  @Test
  public void shouldIndexEvenIfLocked() {
    lock.lock();

    Directory dir = new Directory("org/foo");
    assertThat(index.index(dir), is(true));
    assertThat(index.isIndexed(dir, true), is(true));
  }

  @Test(expected = SonarException.class)
  public void shouldFailIfIndexingAndLocked() {
    lock.setFailWhenLocked(true);
    lock.lock();

    Directory dir = new Directory("org/foo");
    index.index(dir);
  }

  @Test
  public void shouldBeExcluded() {
    File file = new File("org/foo/ExcludedBar.java");
    assertThat(index.index(file), is(false));
    assertThat(index.isIndexed(file, true), is(true));
    assertThat(index.isIndexed(file, false), is(false));
    assertThat(index.isExcluded(file), is(true));
  }

  @Test
  public void shouldIndexResourceWhenAddingMeasure() {
    Resource dir = new Directory("org/foo");
    index.addMeasure(dir, new Measure("ncloc").setValue(50.0));

    assertThat(index.isIndexed(dir, true), is(true));
    assertThat(index.getMeasures(dir, MeasuresFilters.metric("ncloc")).getIntValue(), is(50));
  }
}
