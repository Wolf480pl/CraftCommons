package com.craftfire.commons.database;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.sql.Blob;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

public class ValueHolderBase implements ValueHolder {

    protected final String name;
    protected final int size;
    protected final Object data;
    protected final FieldType ftype;
    protected final boolean unsigned;

    protected static FieldType typeDetect(Object data) {
        if (data == null) {
            return FieldType.NULL;
        } else if (data instanceof String) {
            return FieldType.STRING;
        } else if (data instanceof Number) {
            if (((Number) data).doubleValue() == ((Number) data).longValue()) {
                return FieldType.INTEGER;
            } else {
                return FieldType.REAL;
            }
        } else if (data instanceof Date) {
            return FieldType.DATE;
        } else if (data instanceof Blob) {
            return FieldType.BLOB;
        } else if (data instanceof byte[]) {
            return FieldType.BINARY;
        } else if (data instanceof Boolean) {
            return FieldType.BOOLEAN;
        }
        //TODO: DataManager.getLogManager().warning("Unknown data type: " + data.toString());
        return FieldType.UNKNOWN;
    }

    protected void typeCheck() {
        IllegalArgumentException e = new IllegalArgumentException("Data: "
                + this.data.toString() + " doesn't match the type: "
                + this.ftype.name());
        if (this.ftype.equals(FieldType.STRING)) {
            if (!(this.data instanceof String)) {
                throw e;
            }
        } else if (this.ftype.equals(FieldType.INTEGER) || this.ftype.equals(FieldType.REAL)) {
            if (!(this.data instanceof Number)) {
                throw e;
            }
        } else if (this.ftype.equals(FieldType.DATE)) {
            if (!(this.data instanceof Date)) {
                throw e;
            }
        } else if (this.ftype.equals(FieldType.BLOB)) {
            if (!(this.data instanceof Blob)) {
                throw e;
            }
        } else if (this.ftype.equals(FieldType.BINARY)) {
            if (!(this.data instanceof byte[])) {
                throw e;
            }
        } else if (this.ftype.equals(FieldType.BOOLEAN)) {
            if (!(this.data instanceof Boolean)) {
                throw e;
            }
        }
    }

    public ValueHolderBase(int size, Object data) {
        this(size, false, data);
    }

    public ValueHolderBase(FieldType type, int size, Object data) {
        this(type, size, false, data);
    }

    public ValueHolderBase(int size, boolean unsigned, Object data) {
        this("", size, unsigned, data);
    }

    public ValueHolderBase(FieldType type, int size, boolean unsigned, Object data) {
        this(type, "", size, unsigned, data);
    }

    public ValueHolderBase(String name, int size, boolean unsigned, Object data) {
        this(typeDetect(data), name, size, unsigned, data);
    }

    public ValueHolderBase(FieldType type, String name, int size, boolean unsigned, Object data) {
        if (data == null) {
            this.ftype = FieldType.NULL;
        } else {
            this.ftype = type;
        }
        this.name = name;
        this.size = size;
        this.unsigned = unsigned;
        this.data = data;
        typeCheck();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#getFieldName()
     */
    @Override
    public String getFieldName() {
        return this.name;
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#getFieldType()
     */
    @Override
    public FieldType getFieldType() {
        return this.ftype;
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#getFieldSize()
     */
    @Override
    public int getFieldSize() {
        return this.size;
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#getData()
     */
    @Override
    public Object getData() {
        return this.data;
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#getString()
     */
    @Override
    public String getString() {
        if (getFieldType().equals(FieldType.STRING)) {
            return (String) this.data;
        } else if (getFieldType().equals(FieldType.BINARY)
                || getFieldType().equals(FieldType.BLOB)) {
            return new String(getBytes());
        }
        return this.data.toString();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#getInt()
     */
    @Override
    public int getInt() {
        return (int) getLong();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#getLong()
     */
    @Override
    public long getLong() {
        if (getFieldType().equals(FieldType.INTEGER)
                || getFieldType().equals(FieldType.REAL)) {
            return ((Number) this.data).longValue();
        } else if (getFieldType().equals(FieldType.BOOLEAN)) {
            return ((Boolean) this.data).booleanValue() ? 1 : 0;
        } else if (getFieldType().equals(FieldType.DATE)) {
            return ((Date) this.data).getTime();
        } else if (getFieldType().equals(FieldType.BINARY)
                || getFieldType().equals(FieldType.BLOB)) {
            byte[] bytes = { 0, 0, 0, 0, 0, 0, 0, 0 };
            byte[] bytes1 = getBytes();
            if (bytes1.length >= 8) {
                System.arraycopy(bytes1, 0, bytes, 0, 8);
            } else {
                System.arraycopy(bytes1, 0, bytes, 8 - bytes1.length,
                        bytes1.length);
            }
            return ByteBuffer.wrap(bytes).getLong();
        } else if (getFieldType().equals(FieldType.STRING)) {
            try {
                return Long.parseLong((String) this.data);
            } catch (NumberFormatException e) {
            }
            return new Double(getDouble()).longValue();
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#getBigInt()
     */
    @Override
    public BigInteger getBigInt() {
        try {
            if (this.data instanceof BigInteger) {
                return (BigInteger) this.data;
            } else if (getFieldType().equals(FieldType.BOOLEAN)) {
                return ((Boolean) this.data).booleanValue() ? BigInteger.ONE
                        : BigInteger.ZERO;
            } else if (getFieldType().equals(FieldType.BINARY)
                    || getFieldType().equals(FieldType.BLOB)) {
                byte[] bytes = getBytes();
                return new BigInteger(bytes);
            } else if (getFieldType().equals(FieldType.STRING)) {
                return new BigInteger(this.data.toString());
            } else {
                long l = 0;
                if (getFieldType().equals(FieldType.INTEGER)
                        || getFieldType().equals(FieldType.REAL)) {
                    l = ((Number) this.data).longValue();
                } else if (getFieldType().equals(FieldType.DATE)) {
                    l = ((Date) this.data).getTime();
                } else {
                    return null;
                }
                return new BigInteger(String.valueOf(l));
            }
        } catch (NumberFormatException e) {
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#getDouble()
     */
    @Override
    public double getDouble() {
        if (getFieldType().equals(FieldType.INTEGER)
                || getFieldType().equals(FieldType.REAL)) {
            return ((Number) this.data).doubleValue();
        } else if (getFieldType().equals(FieldType.BOOLEAN)) {
            return ((Boolean) this.data).booleanValue() ? 1 : 0;
        } else if (getFieldType().equals(FieldType.DATE)) {
            return new Long(((Date) this.data).getTime()).doubleValue();
        } else if (getFieldType().equals(FieldType.BINARY)
                || getFieldType().equals(FieldType.BLOB)) {
            byte[] bytes = { 0, 0, 0, 0, 0, 0, 0, 0 };
            byte[] bytes1 = getBytes();
            if (bytes1.length >= 8) {
                System.arraycopy(bytes1, 0, bytes, 0, 8);
            } else {
                System.arraycopy(bytes1, 0, bytes, 8 - bytes1.length,
                        bytes1.length);
            }
            return ByteBuffer.wrap(bytes).getLong();
        } else if (getFieldType().equals(FieldType.STRING)) {
            try {
                return Double.parseDouble((String) this.data);
            } catch (NumberFormatException e) {
            }
            try {
                return Double.parseDouble(((String) this.data)
                        .replace(',', '.'));
            } catch (NumberFormatException e) {
                e.getCause();
            }
        }
        return 0;
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#getFloat()
     */
    @Override
    public float getFloat() {
        return (float) getDouble();
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#getDecimal()
     */
    @Override
    public BigDecimal getDecimal() {
        try {
            if (this.data instanceof BigDecimal) {
                return (BigDecimal) this.data;
            } else if (getFieldType().equals(FieldType.BOOLEAN)) {
                return ((Boolean) this.data).booleanValue() ? BigDecimal.ONE
                        : BigDecimal.ZERO;
            } else if (getFieldType().equals(FieldType.STRING)) {
                return new BigDecimal(this.data.toString());
            } else if (getFieldType().equals(FieldType.INTEGER)
                    || getFieldType().equals(FieldType.REAL)) {
                return new BigDecimal(((Number) this.data).doubleValue());
            } else if (getFieldType().equals(FieldType.DATE)) {
                return new BigDecimal(((Date) this.data).getTime());
            }
        } catch (NumberFormatException e) {
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#getBytes()
     */
    @Override
    public byte[] getBytes() {
        if (getFieldType().equals(FieldType.BINARY)) {
            return (byte[]) this.data;
        } else if (getFieldType().equals(FieldType.BLOB)) {
            try {
                return ((Blob) this.data).getBytes(1,
                        (int) ((Blob) this.data).length());
            } catch (SQLException e) {
                e.getClass();
            }
        } else if (getFieldType().equals(FieldType.BOOLEAN)) {
            return ByteBuffer.allocate(1)
                    .put((byte) (((Boolean) this.data).booleanValue() ? 1 : 0))
                    .array();
        } else if (getFieldType().equals(FieldType.INTEGER)) {
            return ByteBuffer.allocate(8).putLong(getLong()).array();
        } else if (getFieldType().equals(FieldType.REAL)) {
            return ByteBuffer.allocate(8).putDouble(getDouble()).array();
        } else if (getFieldType().equals(FieldType.STRING)) {
            return this.data.toString().getBytes();
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#getDate()
     */
    @Override
    public Date getDate() {
        if (getFieldType().equals(FieldType.DATE)) {
            return (Date) this.data;
        } else if (getFieldType().equals(FieldType.INTEGER)) {
            return new Date(getLong());
        } else if (getFieldType().equals(FieldType.STRING)) {
            try {
                return DateFormat.getDateInstance().parse((String) this.data);
            } catch (ParseException e) {
            }
            try {
                return DateFormat.getDateTimeInstance().parse(
                        (String) this.data);
            } catch (ParseException e) {
            }
            try {
                return DateFormat.getTimeInstance().parse((String) this.data);
            } catch (ParseException e) {
            }
            try {
                return DateFormat.getInstance().parse((String) this.data);
            } catch (ParseException e) {
            }
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#getBlob()
     */
    @Override
    public Blob getBlob() {
        if (getFieldType().equals(FieldType.BLOB)) {
            return (Blob) this.data;
        }
        return null;
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#getBool()
     */
    @Override
    public boolean getBool() {
        if (getFieldType().equals(FieldType.BOOLEAN)) {
            return (Boolean) this.data;
        } else if (getFieldType().equals(FieldType.INTEGER)
                || getFieldType().equals(FieldType.REAL)
                || getFieldType().equals(FieldType.DATE)) {
            return getLong() != 0;
        } else if (getFieldType().equals(FieldType.BINARY)
                || getFieldType().equals(FieldType.BLOB)
                || getFieldType().equals(FieldType.STRING)) {
            String s = getString();
            return (s != null) && !s.isEmpty();
        }
        return false;
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#isNull()
     */
    @Override
    public boolean isNull() {
        return getFieldType().equals(FieldType.NULL);
    }

    /* (non-Javadoc)
     * @see com.craftfire.commons.database.IValueHolder#isUnsigned()
     */
    @Override
    public boolean isUnsigned() {
        return this.unsigned;
    }

    @Override
    public String toString() {
        return "ValueHolder " + getFieldType().name() + "(" + this.size
                + ") " + this.name + " = " + this.data.toString();
    }

}