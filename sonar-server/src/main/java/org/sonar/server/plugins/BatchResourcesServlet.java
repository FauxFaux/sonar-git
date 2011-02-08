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
package org.sonar.server.plugins;

import com.google.common.collect.Lists;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.List;
import java.util.Set;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * This servlet allows to load libraries from directory "WEB-INF/lib" in order to provide them for batch-bootstrapper.
 * Most probably this is not a best solution.
 */
public class BatchResourcesServlet extends HttpServlet {

  private static final Logger LOG = LoggerFactory.getLogger(BatchResourcesServlet.class);

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
    String resource = getResource(request);
    if (StringUtils.isEmpty(resource)) {
      PrintWriter writer = null;
      try {
        response.setContentType("text/html");
        writer = response.getWriter();
        for (String lib : getLibs()) {
          writer.println(lib);
        }
      } catch (IOException e) {
        LOG.error("Unable to provide list of batch resources", e);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      } finally {
        IOUtils.closeQuietly(writer);
      }
    } else {
      InputStream in = null;
      OutputStream out = null;
      try {
        in = getServletContext().getResourceAsStream("/WEB-INF/lib/" + resource);
        if (in == null) {
          // TODO
        } else {
          out = response.getOutputStream();
          IOUtils.copy(in, out);
        }
      } catch (Exception e) {
        LOG.error("Unable to load batch resource '" + resource + "'", e);
        response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
      } finally {
        IOUtils.closeQuietly(in);
        IOUtils.closeQuietly(out);
      }
    }
  }

  List<String> getLibs() {
    List<String> libs = Lists.newArrayList();
    Set paths = getServletContext().getResourcePaths("/WEB-INF/lib");
    for (Object obj : paths) {
      String path = (String) obj;
      if (StringUtils.endsWith(path, ".jar")) {
        libs.add(StringUtils.removeStart(path, "/WEB-INF/lib/"));
      }
    }
    return libs;
  }

  /**
   * @return part of request URL after servlet path
   */
  String getResource(HttpServletRequest request) {
    return StringUtils.substringAfter(request.getRequestURI(), request.getContextPath() + request.getServletPath() + "/");
  }

}
