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

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.util.Date;

import com.craftfire.commons.util.ValueHolder;
import com.craftfire.commons.util.ValueHolderBase;
import com.craftfire.commons.util.ValueType;

public class YamlNode implements ValueHolder {
    private ValueHolder holder;
    private String[] path;

    public YamlNode(String[] path, Object data) {
        this.holder = new ValueHolderBase(path[path.length - 1], false, data);
        this.path = path;
    }

    public String[] getPathElements() {
        return this.path;
    }

    public void setValue(Object data) {
        this.holder = new ValueHolderBase(this.holder.getName(), false, data);
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#getName()
     */
    @Override
    public String getName() {
        return this.holder.getName();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#getType()
     */
    @Override
    public ValueType getType() {
        return this.holder.getType();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#getObject()
     */
    @Override
    public Object getValue() {
        return this.holder.getValue();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#getString()
     */
    @Override
    public String getString() {
        return this.holder.getString();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#getInt()
     */
    @Override
    public int getInt() {
        return this.holder.getInt();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#getLong()
     */
    @Override
    public long getLong() {
        return this.holder.getLong();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#getBigInt()
     */
    @Override
    public BigInteger getBigInt() {
        return this.holder.getBigInt();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#getDouble()
     */
    @Override
    public double getDouble() {
        return this.holder.getDouble();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#getFloat()
     */
    @Override
    public float getFloat() {
        return this.holder.getFloat();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#getDecimal()
     */
    @Override
    public BigDecimal getDecimal() {
        return this.holder.getDecimal();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#getBytes()
     */
    @Override
    public byte[] getBytes() {
        return this.holder.getBytes();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#getDate()
     */
    @Override
    public Date getDate() {
        return this.holder.getDate();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#getBlob()
     */
    @Override
    public Blob getBlob() {
        return this.holder.getBlob();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#getBool()
     */
    @Override
    public boolean getBool() {
        return this.holder.getBool();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#isNull()
     */
    @Override
    public boolean isNull() {
        return this.holder.isNull();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.util.ValueHolder#isUnsigned()
     */
    @Override
    public boolean isUnsigned() {
        return this.holder.isUnsigned();
    }
}
