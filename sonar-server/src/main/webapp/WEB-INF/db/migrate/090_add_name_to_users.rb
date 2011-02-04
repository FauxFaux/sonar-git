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
class AddNameToUsers < ActiveRecord::Migration

  def self.up
    begin
      unless User.column_names.include?('name')
        add_column(:users, :name, :string, :null => true, :limit => 200)
        User.reset_column_information
        User.update_all("name='Administrator'", "login='admin'")
      end
    rescue
      # already created in migration 010
    end
    
  end

  def self.down

  end

end
