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
package org.sonar.batch;

import org.apache.commons.collections.CollectionUtils;
import org.junit.Test;
import org.picocontainer.containers.TransientPicoContainer;
import org.sonar.api.batch.BatchExtensionDictionnary;
import org.sonar.api.batch.Decorator;
import org.sonar.api.batch.DecoratorContext;
import org.sonar.api.batch.DependedUpon;
import org.sonar.api.measures.*;
import org.sonar.api.resources.Project;
import org.sonar.api.resources.Resource;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.junit.internal.matchers.IsCollectionContaining.hasItem;

public class DecoratorsSelectorTest {

  private Metric withFormula1 = new Metric("metric1").setFormula(new FakeFormula());
  private Metric withFormula2 = new Metric("metric2").setFormula(new FakeFormula());
  private Metric withoutFormula3 = new Metric("metric3");

  @Test
  public void selectAndSortFormulas() {
    Project project = new Project("key");
    BatchExtensionDictionnary dictionnary = newDictionnary(withFormula1, withoutFormula3, withFormula2);

    Collection<Decorator> decorators = new DecoratorsSelector(dictionnary).select(project);
    assertThat(decorators.size(), is(2));
    assertThat(decorators, hasItem((Decorator) new FormulaDecorator(withFormula1)));
    assertThat(decorators, hasItem((Decorator) new FormulaDecorator(withFormula2)));
  }

  @Test
  public void decoratorsShouldBeExecutedBeforeFormulas() {
    Project project = new Project("key");
    Decorator metric1Decorator = new Metric1Decorator();
    BatchExtensionDictionnary dictionnary = newDictionnary(metric1Decorator, withFormula1);

    Collection<Decorator> decorators = new DecoratorsSelector(dictionnary).select(project);

    Decorator firstDecorator = (Decorator)CollectionUtils.get(decorators, 0);
    Decorator secondDecorator = (Decorator)CollectionUtils.get(decorators, 1);

    assertThat(firstDecorator, is(Metric1Decorator.class));
    assertThat(secondDecorator, is(FormulaDecorator.class));

    FormulaDecorator formulaDecorator = (FormulaDecorator) secondDecorator;
    assertThat(formulaDecorator.dependsUponDecorators().size(), is(1));
    assertThat(CollectionUtils.get(formulaDecorator.dependsUponDecorators(), 0), is((Object)firstDecorator));
  }

  private BatchExtensionDictionnary newDictionnary(Object... extensions) {
    TransientPicoContainer ioc = new TransientPicoContainer();
    int index = 0;
    for (Object extension : extensions) {
      ioc.addComponent("" + index, extension);
      index++;
    }
    return new BatchExtensionDictionnary(ioc);
  }


  class FakeFormula implements Formula {
    public List<Metric> dependsUponMetrics() {
      return Arrays.asList();
    }

    public Measure calculate(FormulaData data, FormulaContext context) {
      return null;
    }
  }

  public class Metric1Decorator implements Decorator {
    @DependedUpon
    public Metric generatesMetric1Measure() {
      return withFormula1;
    }

    public void decorate(Resource resource, DecoratorContext context) {

    }

    public boolean shouldExecuteOnProject(Project project) {
      return true;
    }
  }

  public class FakeDecorator implements Decorator {
    public void decorate(Resource resource, DecoratorContext context) {

    }

    public boolean shouldExecuteOnProject(Project project) {
      return true;
    }
  }
}
