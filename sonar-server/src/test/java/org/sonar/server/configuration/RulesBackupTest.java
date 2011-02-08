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
package org.sonar.server.configuration;

import org.junit.Before;
import org.junit.Test;
import org.sonar.api.rules.Rule;
import org.sonar.api.rules.RuleParam;
import org.sonar.api.rules.RulePriority;
import org.sonar.jpa.dao.RulesDao;
import org.sonar.jpa.test.AbstractDbUnitTestCase;

import java.util.Arrays;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class RulesBackupTest extends AbstractDbUnitTestCase {

  private RulesBackup rulesBackup;
  private SonarConfig sonarConfig;

  private Rule rule;

  @Before
  public void setUp() {
    rulesBackup = new RulesBackup(getSession());
    sonarConfig = new SonarConfig();

    rule = Rule.create("repo", "key", "name").setDescription("description");
    rule.createParameter("param").setDefaultValue("value");
  }

  private Rule createUserRule() {
    Rule userRule = Rule.create("repo", "key2", "name2").setDescription("description2");
    userRule.setParent(rule);
    userRule.setSeverity(RulePriority.INFO);
    userRule.createParameter("param").setDefaultValue("value");
    return userRule;
  }

  @Test
  public void shouldExportRules() {
    Rule userRule = createUserRule();
    RulesBackup rulesBackup = new RulesBackup(Arrays.asList(userRule));
    rulesBackup.exportXml(sonarConfig);

    assertThat(sonarConfig.getRules().size(), is(1));
    assertTrue(sonarConfig.getRules().iterator().next() == userRule);
  }

  @Test
  public void shouldImportRules() {
    getSession().save(rule);

    sonarConfig.setRules(Arrays.asList(createUserRule()));
    rulesBackup.importXml(sonarConfig);

    verify();
  }

  private void verify() {
    assertThat(getSession().getResults(Rule.class).size(), is(2));
    Rule importedRule = getDao().getRulesDao().getRuleByKey("repo", "key2");
    assertThat(importedRule.getParent(), is(rule));
    assertThat(importedRule.isEnabled(), is(true));
    assertThat(importedRule.getName(), is("name2"));
    assertThat(importedRule.getDescription(), is("description2"));
    assertThat(importedRule.getSeverity(), is(RulePriority.INFO));
    assertThat(importedRule.getParams().size(), is(1));
    RuleParam param = importedRule.getParams().get(0);
    assertThat(param.getKey(), is("param"));
    assertThat(param.getDefaultValue(), is("value"));
  }

  @Test
  public void shouldUpdateRules() {
    getSession().save(rule);
    getSession().save(Rule.create("repo", "key2", ""));

    sonarConfig.setRules(Arrays.asList(createUserRule()));
    rulesBackup.importXml(sonarConfig);

    verify();
  }

  @Test
  public void shouldIgnoreIncorrectRule() {
    sonarConfig.setRules(Arrays.asList(createUserRule()));
    rulesBackup.importXml(sonarConfig);

    assertThat(getSession().getResults(Rule.class).size(), is(0));
  }

  @Test
  public void shouldIgnoreIncorrectParam() {
    Rule rule = Rule.create("repo", "key", "name").setDescription("description");
    getSession().save(rule);
    sonarConfig.setRules(Arrays.asList(createUserRule()));
    rulesBackup.importXml(sonarConfig);

    assertThat(getSession().getResults(Rule.class).size(), is(2));
    RulesDao rulesDao = getDao().getRulesDao();
    Rule importedRule = rulesDao.getRuleByKey("repo", "key2");
    assertThat(importedRule, notNullValue());
    assertThat(rulesDao.getRuleParam(importedRule, "param"), nullValue());
  }
}
