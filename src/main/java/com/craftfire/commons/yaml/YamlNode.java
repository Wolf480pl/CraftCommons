/*
 * This file is part of CraftCommons.
 *
 * Copyright (c) 2011-2012, CraftFire <http://www.craftfire.com/>
 * CraftCommons is licensed under the GNU Lesser General Public License.
 *
 * CraftCommons is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * CraftCommons is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.craftfire.commons.yaml;

public class YamlNode extends YamlNodeSource {
    private YamlNodeSource parent;

    public YamlNode(YamlNodeSource parent, String name, Object data) {
        super(parent.getYamlManager(), name, data);
        this.parent = parent;
    }

    public YamlNodeSource getParent() {
        return this.parent;
    }

    protected void setParent(YamlNodeSource parent) {
        this.parent = parent;
    }

    public String[] getPathElements() {
        return getPath().split("\\.");
    }

    @Override
    public String getPath() {
        return this.parent.getPath() + "." + getName();
    }
}
