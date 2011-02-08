 #
 # Sonar, entreprise quality control tool.
 # Copyright (C) 2008-2011 SonarSource
 # mailto:contact AT sonarsource DOT com
 #
 # Sonar is free software; you can redistribute it and/or
 # modify it under the terms of the GNU Lesser General Public
 # License as published by the Free Software Foundation; either
 # version 3 of the License, or (at your option) any later version.
 #
 # Sonar is distributed in the hope that it will be useful,
 # but WITHOUT ANY WARRANTY; without even the implied warranty of
 # MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 # Lesser General Public License for more details.
 #
 # You should have received a copy of the GNU Lesser General Public
 # License along with Sonar; if not, write to the Free Software
 # Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02
 #
module DrilldownHelper

  def period_select_options(snapshot, index)
    return nil if snapshot.nil? || snapshot.project_snapshot.nil?
    mode=snapshot.project_snapshot.send "period#{index}_mode"
    mode_param=snapshot.project_snapshot.send "period#{index}_param"
    date=snapshot.project_snapshot.send "period#{index}_date"

    if mode
      if mode=='days'
        label = "Added over %s days" % mode_param
      elsif mode=='version'
        label = "Added since version %s" % mode_param
      elsif mode=='previous_analysis'
        label = "Added since previous analysis (%s)" % date.strftime("%Y %b. %d")
      elsif mode=='date'
        label = "Added since #{date.strftime("%Y %b %d")}"
      end
      if label
        selected=(params[:period]==index.to_s ? 'selected' : '')
        "<option value='#{index}' #{selected}>#{label}</option>"
      end
    else
      nil
    end

  end

  def measure_or_variation_value(measure)
    if measure
      @period_index ? measure.variation(@period_index) : measure.value
    else
      nil
    end
  end
end