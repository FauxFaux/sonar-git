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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.SetMultimap;
import org.sonar.api.batch.BatchExtensionDictionnary;
import org.sonar.api.batch.Decorator;
import org.sonar.api.measures.Metric;
import org.sonar.api.resources.Project;

import java.util.*;

public final class DecoratorsSelector {

  private BatchExtensionDictionnary dictionnary;

  public DecoratorsSelector(BatchExtensionDictionnary dictionnary) {
    this.dictionnary = dictionnary;
  }

  public Collection<Decorator> select(Project project) {
    List<Decorator> decorators = new ArrayList<Decorator>(dictionnary.select(Decorator.class, project, false));
    SetMultimap<Metric, Decorator> decoratorsByGeneratedMetric = getDecoratorsByMetric(decorators);
    for (Metric metric : dictionnary.select(Metric.class)) {
      if (metric.getFormula() != null) {
        decorators.add(new FormulaDecorator(metric, decoratorsByGeneratedMetric.get(metric)));
      }
    }

    return dictionnary.sort(decorators);
  }

  private SetMultimap<Metric, Decorator> getDecoratorsByMetric(Collection<Decorator> pluginDecorators) {
    SetMultimap<Metric, Decorator> decoratorsByGeneratedMetric = HashMultimap.create();
    for (Decorator decorator : pluginDecorators) {
      List dependents = dictionnary.getDependents(decorator);
      for (Object dependent : dependents) {
        if (dependent instanceof Metric) {
          decoratorsByGeneratedMetric.put((Metric) dependent, decorator);
        }
      }
    }
    return decoratorsByGeneratedMetric;
  }
}
