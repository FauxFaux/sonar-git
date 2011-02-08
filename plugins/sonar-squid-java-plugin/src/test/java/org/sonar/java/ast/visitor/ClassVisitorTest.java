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
package org.sonar.java.ast.visitor;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;
import static org.sonar.java.ast.SquidTestUtils.getFile;

import java.util.Iterator;

import org.junit.Before;
import org.junit.Test;
import org.sonar.java.ast.JavaAstScanner;
import org.sonar.java.squid.JavaSquidConfiguration;
import org.sonar.squid.Squid;
import org.sonar.squid.api.SourceClass;
import org.sonar.squid.api.SourceCode;
import org.sonar.squid.api.SourcePackage;
import org.sonar.squid.measures.Metric;

public class ClassVisitorTest {

  private Squid squid;

  @Before
  public void setup() {
    squid = new Squid(new JavaSquidConfiguration());
  }

  @Test
  public void analyseTest003() {
    squid.register(JavaAstScanner.class).scanFile(getFile("/metrics/classes/Test003.java"));
    SourceCode project = squid.aggregate();
    SourceCode defaultPackage = project.getFirstChild();
    SourceCode file = defaultPackage.getFirstChild();
    assertEquals(3, file.getInt(Metric.CLASSES));
    Iterator<SourceCode> classes = file.getChildren().iterator();
    SourceCode anotherClass = classes.next();
    assertEquals("AnotherCar", anotherClass.getKey());
    assertTrue(anotherClass.isType(SourceClass.class));
    SourceCode carClass = classes.next();
    assertEquals("Car", carClass.getKey());
    assertTrue(carClass.isType(SourceClass.class));
    SourceCode wheelClass = squid.search("Car$Wheel");
    assertNotNull(wheelClass);
    assertEquals(wheelClass.getParent(), carClass);
  }

  @Test
  public void analyseClassCounterEnum() {
    squid.register(JavaAstScanner.class).scanFile(getFile("/metrics/classes/ClassCounterEnum.java"));
    SourceCode project = squid.aggregate();
    SourceCode defaultPackage = project.getFirstChild();
    assertEquals(1, defaultPackage.getInt(Metric.CLASSES));
  }

  @Test
  public void analyseAnnotationDefinition() {
    squid.register(JavaAstScanner.class).scanFile(getFile("/special_cases/annotations/AnnotationDefinition.java"));
    SourceCode project = squid.aggregate();
    SourceCode annotation = project.getFirstChild();
    assertEquals(1, annotation.getInt(Metric.CLASSES));
    assertNotNull(squid.search("org/sonar/plugins/api/AnnotationDefinition"));
  }

  @Test
  public void analyseInterface() {
    squid.register(JavaAstScanner.class).scanFile(getFile("/metrics/classes/Interface.java"));
    SourceCode project = squid.aggregate();
    assertEquals(1, project.getInt(Metric.INTERFACES));
  }

  @Test
  public void analyseAbstractClass() {
    squid.register(JavaAstScanner.class).scanFile(getFile("/metrics/classes/AbstractClass.java"));
    SourceCode project = squid.aggregate();
    assertEquals(1, project.getInt(Metric.ABSTRACT_CLASSES));
  }

  @Test
  public void testStartAtLine() {
    squid.register(JavaAstScanner.class).scanFile(getFile("/metrics/classes/AbstractClass.java"));
    SourceCode classTest = squid.search("org/sonar/AbstractClass");
    assertEquals(4, classTest.getStartAtLine());
  }

  @Test
  public void analysePrivateInnerClass() {
    squid.register(JavaAstScanner.class).scanFile(getFile("/metrics/classes/InnerClassTests.java"));
    SourceCode project = squid.aggregate();
    SourceCode defaultPackage = project.getFirstChild();
    SourceCode defaultClassFile = defaultPackage.getFirstChild();
    SourceCode innerClassTests = defaultClassFile.getFirstChild();
    assertEquals(3, innerClassTests.getInt(Metric.CLASSES));
    SourceCode privateInnerClass = squid.search("InnerClassTests$TestPrivateInnerClass");
    assertEquals(1, privateInnerClass.getInt(Metric.PUBLIC_API));
    assertEquals(0, privateInnerClass.getInt(Metric.PUBLIC_DOC_API));
    assertEquals(1, privateInnerClass.getInt(Metric.CLASSES));
    SourceCode publicInnerClass = squid.search("InnerClassTests$TestPublicInnerClass");
    assertEquals(2, publicInnerClass.getInt(Metric.PUBLIC_API));
    assertEquals(0, privateInnerClass.getInt(Metric.PUBLIC_DOC_API));
    assertEquals(1, publicInnerClass.getInt(Metric.CLASSES));
  }

  @Test
  public void detectSuppressWarningsAnnotation() {
    squid.register(JavaAstScanner.class).scanFile(getFile("/rules/ClassWithSuppressWarningsAnnotation.java"));
    SourceClass sourceClass = (SourceClass) squid.search("ClassWithSuppressWarningsAnnotation");
    assertThat(sourceClass.isSuppressWarnings(), is(true));
  }

  @Test
  public void testCreateSquidClassFromPackage() {
    SourceClass squidClass = ClassVisitor.createSourceClass(new SourcePackage("org/sonar"), "Squid");
    assertEquals("org/sonar/Squid", squidClass.getKey());
  }

  @Test
  public void testCreateSquidClassFromEmptyPackage() {
    SourceClass squidClass = ClassVisitor.createSourceClass((SourcePackage) new SourcePackage(""), "Squid");
    assertEquals("Squid", squidClass.getKey());
  }

  @Test
  public void testCreateInnerSquidClass() {
    SourceClass squidClass = ClassVisitor.createSourceClass(new SourceClass("org/sonar/Squid"), "Key");
    assertEquals("org/sonar/Squid$Key", squidClass.getKey());
  }
}
