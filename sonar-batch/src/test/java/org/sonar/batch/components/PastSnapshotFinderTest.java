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
package org.sonar.batch.components;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.hamcrest.BaseMatcher;
import org.hamcrest.Description;
import org.junit.Before;
import org.junit.Test;
import org.sonar.api.CoreProperties;
import org.sonar.api.database.model.Snapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static junit.framework.Assert.assertNull;
import static org.hamcrest.Matchers.nullValue;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

public class PastSnapshotFinderTest {

  private PastSnapshotFinderByDays finderByDays;
  private PastSnapshotFinderByDate finderByDate;
  private PastSnapshotFinderByVersion finderByVersion;
  private PastSnapshotFinderByPreviousAnalysis finderByPreviousAnalysis;
  private PastSnapshotFinder finder;

  @Before
  public void initFinders() {
    finderByDays = mock(PastSnapshotFinderByDays.class);
    finderByDate = mock(PastSnapshotFinderByDate.class);
    finderByVersion = mock(PastSnapshotFinderByVersion.class);
    finderByPreviousAnalysis = mock(PastSnapshotFinderByPreviousAnalysis.class);
    finder = new PastSnapshotFinder(finderByDays, finderByVersion, finderByDate, finderByPreviousAnalysis);
  }

  @Test
  public void shouldFindByNumberOfDays() {
    when(finderByDays.findFromDays(null, 30)).thenReturn(new PastSnapshot("days", null).setModeParameter("30"));

    PastSnapshot variationSnapshot = finder.find(null, 1, "30");

    verify(finderByDays).findFromDays(null, 30);
    assertNotNull(variationSnapshot);
    assertThat(variationSnapshot.getIndex(), is(1));
    assertThat(variationSnapshot.getMode(), is("days"));
    assertThat(variationSnapshot.getModeParameter(), is("30"));
  }

  @Test
  public void shouldNotFindByNumberOfDays() {
    PastSnapshot variationSnapshot = finder.find(null, 1, "30");

    verify(finderByDays).findFromDays(null, 30);
    assertNull(variationSnapshot);
  }

  @Test
  public void shouldFindByDate() throws ParseException {
    final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    final Date date = format.parse("2010-05-18");
    when(finderByDate.findByDate(null, date)).thenReturn(new PastSnapshot("date", new Snapshot()));

    PastSnapshot variationSnapshot = finder.find(null, 2, "2010-05-18");

    verify(finderByDate).findByDate((Snapshot) anyObject(), argThat(new BaseMatcher<Date>() {
      public boolean matches(Object o) {
        return o.equals(date);
      }

      public void describeTo(Description description) {

      }
    }));
    assertThat(variationSnapshot.getIndex(), is(2));
    assertThat(variationSnapshot.getMode(), is("date"));
    assertThat(variationSnapshot.getProjectSnapshot(), not(nullValue()));
  }

  @Test
  public void shouldNotFindByDate() throws ParseException {
    when(finderByDate.findByDate((Snapshot) anyObject(), (Date) anyObject())).thenReturn(null);

    PastSnapshot variationSnapshot = finder.find(null, 2, "2010-05-18");

    verify(finderByDate).findByDate((Snapshot) anyObject(), (Date) anyObject());
    assertNull(variationSnapshot);
  }

  @Test
  public void shouldFindByPreviousAnalysis() throws ParseException {
    final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    final Date date = format.parse("2010-05-18");
    Snapshot snapshot = new Snapshot();
    snapshot.setCreatedAt(date);
    when(finderByPreviousAnalysis.findByPreviousAnalysis(null)).thenReturn(new PastSnapshot(CoreProperties.TIMEMACHINE_MODE_PREVIOUS_ANALYSIS, snapshot));

    PastSnapshot variationSnapshot = finder.find(null, 2, CoreProperties.TIMEMACHINE_MODE_PREVIOUS_ANALYSIS);

    verify(finderByPreviousAnalysis).findByPreviousAnalysis(null);
    assertThat(variationSnapshot.getIndex(), is(2));
    assertThat(variationSnapshot.getMode(), is(CoreProperties.TIMEMACHINE_MODE_PREVIOUS_ANALYSIS));
    assertThat(variationSnapshot.getProjectSnapshot(), not(nullValue()));
  }

  @Test
  public void shouldNotFindPreviousAnalysis() {
    when(finderByPreviousAnalysis.findByPreviousAnalysis(null)).thenReturn(null);

    PastSnapshot variationSnapshot = finder.find(null, 2, CoreProperties.TIMEMACHINE_MODE_PREVIOUS_ANALYSIS);

    verify(finderByPreviousAnalysis).findByPreviousAnalysis(null);

    assertNull(variationSnapshot);
  }

  @Test
  public void shouldFindByVersion() {
    when(finderByVersion.findByVersion(null, "1.2")).thenReturn(new PastSnapshot("version", new Snapshot()));

    PastSnapshot variationSnapshot = finder.find(null, 2, "1.2");

    verify(finderByVersion).findByVersion(null, "1.2");
    assertThat(variationSnapshot.getIndex(), is(2));
    assertThat(variationSnapshot.getMode(), is("version"));
    assertThat(variationSnapshot.getProjectSnapshot(), not(nullValue()));
  }

  @Test
  public void shouldNotFindVersion() {
    when(finderByVersion.findByVersion(null, "1.2")).thenReturn(null);

    PastSnapshot variationSnapshot = finder.find(null, 2, "1.2");

    verify(finderByVersion).findByVersion(null, "1.2");
    assertNull(variationSnapshot);
  }

  @Test
  public void shouldNotFailIfUnknownFormat() {
    when(finderByPreviousAnalysis.findByPreviousAnalysis(null)).thenReturn(new PastSnapshot(CoreProperties.TIMEMACHINE_MODE_PREVIOUS_ANALYSIS, new Snapshot())); // should not be called
    assertNull(finder.find(null, 2, "foooo"));
  }

  @Test
  public void shouldGetPropertyValue() {
    PropertiesConfiguration conf = new PropertiesConfiguration();
    conf.setProperty("sonar.timemachine.period1", "5");

    assertThat(PastSnapshotFinder.getPropertyValue(conf, 1), is("5"));
    assertThat(PastSnapshotFinder.getPropertyValue(conf, 999), nullValue());
  }

  @Test
  public void shouldGetDefaultPropertyValue() {
    PropertiesConfiguration conf = new PropertiesConfiguration();
    conf.setProperty("sonar.timemachine.period1", "5");

    assertThat(PastSnapshotFinder.getPropertyValue(conf, 2), is(CoreProperties.TIMEMACHINE_DEFAULT_PERIOD_2));
  }
}
