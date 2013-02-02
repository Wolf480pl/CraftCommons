package com.craftfire.commons.database;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.util.Date;

public interface ValueHolder {

    String getFieldName();

    FieldType getFieldType();

    int getFieldSize();

    Object getData();

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