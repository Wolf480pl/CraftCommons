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
package com.craftfire.commons.util;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;

import org.junit.Test;

public class TestValueHolderBase {

    @Test
    public void testTypeDetect() throws SerialException, SQLException {
        assertEquals(ValueType.NULL, ValueHolderBase.typeDetect(null));
        assertEquals(ValueType.STRING, ValueHolderBase.typeDetect("test"));
        assertEquals(ValueType.INTEGER, ValueHolderBase.typeDetect(9));
        assertEquals(ValueType.INTEGER, ValueHolderBase.typeDetect(-1L));
        assertEquals(ValueType.INTEGER, ValueHolderBase.typeDetect(BigInteger.TEN));
        assertEquals(ValueType.REAL, ValueHolderBase.typeDetect(12.5));
        assertEquals(ValueType.REAL, ValueHolderBase.typeDetect(10.5f));
        assertEquals(ValueType.REAL, ValueHolderBase.typeDetect(BigDecimal.valueOf(2.55)));
        assertEquals(ValueType.DATE, ValueHolderBase.typeDetect(new Date()));
        assertEquals(ValueType.BLOB, ValueHolderBase.typeDetect(new SerialBlob(new byte[] { 0, 1, 2, 17, 113, -10 })));
        assertEquals(ValueType.BINARY, ValueHolderBase.typeDetect(new byte[] { 0, 1, 2, 17, 113, -10 }));
        assertEquals(ValueType.BOOLEAN, ValueHolderBase.typeDetect(true));
        assertEquals(ValueType.UNKNOWN, ValueHolderBase.typeDetect(new ArrayList<Object>()));
        assertEquals(ValueType.UNKNOWN, ValueHolderBase.typeDetect(new HashMap<String, Object>()));
    }

    @Test
    public void testConstructors() {
        ValueHolderBase holder = new ValueHolderBase(-8);
        assertFalse(holder.isUnsigned());
        assertEquals("", holder.getName());

        holder = new ValueHolderBase(ValueType.INTEGER, 9);
        assertFalse(holder.isUnsigned());
        assertEquals("", holder.getName());

        holder = new ValueHolderBase(false, 10);
        assertEquals("", holder.getName());

        holder = new ValueHolderBase(ValueType.INTEGER, false, -2L);
        assertEquals("", holder.getName());
    }

    @Test
    public void testGetName() {
        assertEquals("test", new ValueHolderBase("test", false, null).getName());
        assertEquals("test", new ValueHolderBase(ValueType.NULL, "test", false, null).getName());
    }

    @Test
    public void testGetValue() {
        assertEquals(10.0f, new ValueHolderBase(10.0f).getValue());
    }

    @Test
    public void testBinary() {
        ValueHolderBase holder = new ValueHolderBase(ValueType.BINARY, new byte[] { 0, 1, 77, -3 });
        assertEquals(ValueType.BINARY, holder.getType());
        assertArrayEquals(new byte[] { 0, 1, 77, -3 }, (byte[]) holder.getValue());
        assertEquals(new String(new byte[] { 0, 1, 77, -3 }), holder.getString());
        assertEquals(85501, holder.getInt());
        assertEquals(85501, holder.getLong());
        assertEquals(BigInteger.valueOf(85501), holder.getBigInt());
        assertEquals(85501, holder.getDouble(), 0);
        assertEquals(85501, holder.getFloat(), 0);
        assertArrayEquals(new byte[] { 0, 1, 77, -3 }, holder.getBytes());
        assertTrue(holder.getBool());
        assertFalse(holder.isNull());
    }

    @Test
    public void testBlob() throws SerialException, SQLException {
        ValueHolderBase holder = new ValueHolderBase(ValueType.BLOB, new SerialBlob(new byte[] { 0, 1, 77, -3 }));
        assertEquals(ValueType.BLOB, holder.getType());
        assertArrayEquals(new byte[] { 0, 1, 77, -3 }, ((Blob) holder.getValue()).getBytes(1, 4));
        assertEquals(new String(new byte[] { 0, 1, 77, -3 }), holder.getString());
        assertEquals(85501, holder.getInt());
        assertEquals(85501, holder.getLong());
        assertEquals(BigInteger.valueOf(85501), holder.getBigInt());
        assertEquals(85501, holder.getDouble(), 0);
        assertEquals(85501, holder.getFloat(), 0);
        assertArrayEquals(new byte[] { 0, 1, 77, -3 }, holder.getBytes());
        assertArrayEquals(new byte[] { 0, 1, 77, -3 }, holder.getBlob().getBytes(1, 4));
        assertTrue(holder.getBool());
        assertFalse(holder.isNull());
    }

    @Test
    public void testBool() {
        ValueHolderBase holder = new ValueHolderBase(ValueType.BOOLEAN, true);
        assertEquals(ValueType.BOOLEAN, holder.getType());
        assertEquals(true, holder.getValue());
        assertEquals("true", holder.getString());
        assertEquals(1, holder.getInt());
        assertEquals(1, holder.getLong());
        assertEquals(BigInteger.ONE, holder.getBigInt());
        assertEquals(1, holder.getDouble(), 0);
        assertEquals(1, holder.getFloat(), 0);
        assertEquals(BigDecimal.ONE, holder.getDecimal());
        assertArrayEquals(new byte[] { 1 }, holder.getBytes());
        assertTrue(holder.getBool());
        assertFalse(holder.isNull());
    }

    @Test
    public void testDate() {
        Date now = new Date();
        ValueHolderBase holder = new ValueHolderBase(ValueType.DATE, now);
        assertEquals(ValueType.DATE, holder.getType());
        assertEquals(now, holder.getValue());
        assertEquals(now.toString(), holder.getString());
        assertEquals((int) now.getTime(), holder.getInt());
        assertEquals(now.getTime(), holder.getLong());
        assertEquals(BigInteger.valueOf(now.getTime()), holder.getBigInt());
        assertEquals(now.getTime(), holder.getDouble(), 0);
        assertEquals((float) now.getTime(), holder.getFloat(), 0);
        assertEquals(BigDecimal.valueOf(now.getTime()), holder.getDecimal());
        assertEquals(now, holder.getDate());
        assertTrue(holder.getBool());
        assertFalse(holder.isNull());
    }

    @Test
    public void testInt() {
        ValueHolderBase holder = new ValueHolderBase(ValueType.INTEGER, 480);
        assertEquals(ValueType.INTEGER, holder.getType());
        assertEquals(480, holder.getValue());
        assertEquals("480", holder.getString());
        assertEquals(480, holder.getInt());
        assertEquals(480, holder.getLong());
        assertEquals(BigInteger.valueOf(480), holder.getBigInt());
        assertEquals(480, holder.getDouble(), 0);
        assertEquals(480, holder.getFloat(), 0);
        assertEquals(BigDecimal.valueOf(480), holder.getDecimal());
        assertArrayEquals(new byte[] { 0, 0, 0, 0, 0, 0, 1, -32 }, holder.getBytes());
        assertEquals(new Date(480), holder.getDate());
        assertTrue(holder.getBool());
        assertFalse(holder.isNull());
    }

    @Test
    public void testNull() {
        ValueHolderBase holder = new ValueHolderBase(ValueType.NULL, null);
        assertEquals(ValueType.NULL, holder.getType());
        assertNull(holder.getValue());
        assertTrue(holder.isNull());
    }

    @Test
    public void testDouble() {
        ValueHolderBase holder = new ValueHolderBase(ValueType.REAL, 480.5);
        assertEquals(ValueType.REAL, holder.getType());
        assertEquals(480.5, holder.getValue());
        assertEquals("480.5", holder.getString());
        assertEquals(480, holder.getInt());
        assertEquals(480, holder.getLong());
        assertEquals(BigInteger.valueOf(480), holder.getBigInt());
        assertEquals(480.5, holder.getDouble(), 0);
        assertEquals(480.5, holder.getFloat(), 0);
        assertEquals(BigDecimal.valueOf(480.5), holder.getDecimal());
        assertArrayEquals(new byte[] { 64, 126, 8, 0, 0, 0, 0, 0 }, holder.getBytes());
        assertTrue(holder.getBool());
        assertFalse(holder.isNull());
    }

    @Test
    public void testString() {
        ValueHolderBase holder = new ValueHolderBase(ValueType.STRING, "14");
        assertEquals(ValueType.STRING, holder.getType());
        assertEquals("14", holder.getValue());
        assertEquals("14", holder.getString());
        assertEquals(14, holder.getInt());
        assertEquals(14, holder.getLong());
        assertEquals(BigInteger.valueOf(14), holder.getBigInt());
        assertEquals(14, holder.getFloat(), 0);
        assertEquals(14, holder.getDouble(), 0);
        assertEquals(BigDecimal.valueOf(14), holder.getDecimal());
        assertArrayEquals("14".getBytes(), holder.getBytes());
        assertTrue(holder.getBool());
        assertFalse(holder.isNull());
    }

    @Test
    public void testMap() {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("alice", 1);
        map.put("bob", true);
        ValueHolderBase holder = new ValueHolderBase(map);
        assertEquals(ValueType.UNKNOWN, holder.getType());
        assertEquals(map, holder.getValue());
        assertFalse(holder.isNull());
    }

    @Test
    public void testList() {
        List<Object> list = new ArrayList<Object>();
        list.add("alice");
        list.add(1);
        ValueHolderBase holder = new ValueHolderBase(list);
        assertEquals(ValueType.UNKNOWN, holder.getType());
        assertEquals(list, holder.getValue());
        assertFalse(holder.isNull());
    }

    @Test
    public void testDefaults() {
        ValueHolderBase holder = new ValueHolderBase(null);
        Random rnd = new Random();
        long randomLong = rnd.nextLong();
        int randomInt = (int) randomLong;
        double randomDouble = rnd.nextDouble();
        float randomFloat = (float) randomDouble;
        byte[] randomBytes = new byte[10];
        rnd.nextBytes(randomBytes);
        assertEquals("test" + randomInt, holder.getString("test" + randomInt));
        assertEquals(randomInt, holder.getInt(randomInt));
        assertEquals(randomLong, holder.getLong(randomLong));
        assertEquals(BigInteger.valueOf(randomInt), holder.getBigInt(BigInteger.valueOf(randomInt)));
        assertEquals(randomFloat, holder.getFloat(randomFloat), 0);
        assertEquals(randomDouble, holder.getDouble(randomDouble), 0);
        assertEquals(BigDecimal.valueOf(randomDouble), holder.getDecimal(BigDecimal.valueOf(randomDouble)));
        assertArrayEquals(randomBytes, holder.getBytes(randomBytes));
        assertTrue(holder.getBool(true));
        assertFalse(holder.getBool(false));
    }

}
