package com.craftfire.commons.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.util.Date;

public interface ValueHolder {

    String getName();

    ValueType getType();

    int getSize();

    Object getObject();

    String getString();

    int getInt();

    long getLong();

    BigInteger getBigInt();

    double getDouble();

    float getFloat();

    BigDecimal getDecimal();

    byte[] getBytes();

    Date getDate();

    Blob getBlob();

    boolean getBool();

    boolean isNull();

    boolean isUnsigned();

}