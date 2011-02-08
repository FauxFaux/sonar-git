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
package org.sonar.wsclient.unmarshallers;

import org.sonar.wsclient.services.Model;
import org.sonar.wsclient.services.WSUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractUnmarshaller<MODEL extends Model> implements Unmarshaller<MODEL> {

  public final MODEL toModel(String json) {
    WSUtils utils = WSUtils.getINSTANCE();
    MODEL result = null;
    Object array = utils.parse(json);
    if (utils.getArraySize(array) >= 1) {
      Object elt = utils.getArrayElement(array, 0);
      if (elt != null) {
        result = parse(elt);
      }
    }
    return result;

  }

  public final List<MODEL> toModels(String json) {
    WSUtils utils = WSUtils.getINSTANCE();
    List<MODEL> result = new ArrayList<MODEL>();
    Object array = utils.parse(json);
    for (int i = 0; i < utils.getArraySize(array); i++) {
      Object elt = utils.getArrayElement(array, i);
      if (elt != null) {
        result.add(parse(elt));
      }
    }
    return result;
  }

  protected abstract MODEL parse(Object elt);
}
