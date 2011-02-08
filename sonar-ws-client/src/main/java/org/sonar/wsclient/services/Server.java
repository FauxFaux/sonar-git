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

/**
 * @author Evgeny Mandrikov
 */
public class Server extends Model {

  public static enum Status {
    SETUP, UP, DOWN;
  }

  private String id;
  private String version;
  private Status status;
  private String statusMessage;

  public String getVersion() {
    return version;
  }

  public String getId() {
    return id;
  }

  public Server setVersion(String s) {
    this.version = s;
    return this;
  }

  public Server setId(String id) {
    this.id = id;
    return this;
  }

  public Status getStatus() {
    return status;
  }

  public String getStatusMessage() {
    return statusMessage;
  }

  public Server setStatus(Status status) {
    this.status = status;
    return this;
  }

  public Server setStatusMessage(String statusMessage) {
    this.statusMessage = statusMessage;
    return this;
  }

}
