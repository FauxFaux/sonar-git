#
# Sonar, open source software quality management tool.
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
class DrilldownController < ApplicationController
  before_filter :init_project

  helper ProjectHelper

  SECTION=Navigation::SECTION_RESOURCE

  def measures
    @metric = select_metric(params[:metric], Metric::NCLOC)
    @highlighted_metric = Metric.by_key(params[:highlight]) || @metric

    # selected resources
    if params[:rids]
      selected_rids= params[:rids]
    elsif params[:resource]
      highlighted_resource=Project.by_key(params[:resource])
      selected_rids=(highlighted_resource ? [highlighted_resource.id] : [])
    else
      selected_rids=[]
    end
    selected_rids=selected_rids.map{|r|r.to_i}


    # options
    options={}
    if params[:characteristic_id]
      @characteristic=Characteristic.find(params[:characteristic_id])
    elsif params[:model] && params[:characteristic]
      @characteristic=Characteristic.find(:first, :select => 'id', :include => 'quality_model', :conditions => ['quality_models.name=? AND characteristics.kee=? AND characteristics.enabled=?', params[:model], params[:characteristic], true])
    end
    options[:characteristic]=@characteristic


    # load data
    @drilldown = Drilldown.new(@project, @metric, selected_rids, options)
    @snapshot = @drilldown.snapshot
    access_denied unless has_role?(:user, @snapshot)

    @highlighted_resource=@drilldown.highlighted_resource
    if @highlighted_resource.nil? && @drilldown.columns.empty?
      @highlighted_resource=@project
    end
  end

  def violations
    @rule=Rule.by_key_or_id(params[:rule])

    # variation measures
    if params[:period].blank?
      @period_index=nil
      metric_prefix = ''
    else
      @period_index=params[:period].to_i
      metric_prefix = 'new_'
    end

    @priority_id=(params[:priority].blank? ? nil : Sonar::RulePriority.id(params[:priority]))
    if @rule.nil? && @priority_id
      @metric = Metric::by_key("#{metric_prefix}#{params[:priority].downcase}_violations")
    else
      @metric = Metric::by_key("#{metric_prefix}violations")
    end

    # selected resources
    if params[:rids]
      @selected_rids= params[:rids]
    elsif params[:resource]
      highlighted_resource=Project.by_key(params[:resource])
      @selected_rids=(highlighted_resource ? [highlighted_resource.id] : [])
    else
      @selected_rids=[]
    end
    @selected_rids=@selected_rids.map{|r|r.to_i}


    # options for Drilldown
    options={:exclude_zero_value => true, :period_index => @period_index}
    if @rule
      params[:rule]=@rule.key  # workaround for SONAR-1767 : the javascript hash named "rp" in the HTML source must contain the rule key, but not the rule id
      options[:rule_id]=@rule.id
    end
    
    # load data
    @drilldown = Drilldown.new(@project, @metric, @selected_rids, options)
    @snapshot=@drilldown.snapshot
    access_denied unless has_role?(:user, @snapshot)

    @highlighted_resource=@drilldown.highlighted_resource
    if @highlighted_resource.nil? && @drilldown.columns.empty?
      @highlighted_resource=@project
    end
  end

  protected

  def init_project
    project_key = params[:id]
    @project = project_key ? Project.by_key(project_key) : nil
    if @project.nil?
      render :text => "Project [#{project_key}] not found", :status => 404
      return
    end
  end

  def select_metric(metric_key, default_key)
    metric=nil
    if metric_key
      metric=Metric::by_key(metric_key)
    end
    if metric.nil?
      metric=Metric::by_key(default_key)
    end
    metric
  end

  def select_subsnapshot(snapshot, sid)
    if sid
      snapshot.children.each do |subsnapshot|
        return subsnapshot if subsnapshot.id==sid.to_i
      end
    end
    nil
  end

  def array_to_hash_by_id(array)
    hash={}
    array.each do |s|
      hash[s.id]=s
    end
    hash
  end

end
