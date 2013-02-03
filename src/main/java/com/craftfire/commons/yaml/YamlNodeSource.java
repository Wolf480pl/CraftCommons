package com.craftfire.commons.yaml;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.Blob;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.craftfire.commons.util.ValueHolder;
import com.craftfire.commons.util.ValueHolderBase;
import com.craftfire.commons.util.ValueType;

public abstract class YamlNodeSource implements ValueHolder {
    private List<YamlNode> listCache = null;
    private Map<String, YamlNode> mapCache = null;
    private ValueHolder holder;
    private YamlManager manager;

    protected YamlNodeSource(YamlManager manager, String name, Object data) {
        this.holder = new ValueHolderBase(name, false, data);
        this.manager = manager;
    }

    public abstract String getPath();

    public abstract String[] getPathElements();

    public YamlManager getYamlManager() {
        return this.manager;
    }

    public boolean isMap() {
        return getValue() instanceof Map<?, ?>;
    }

    public boolean isList() {
        return getValue() instanceof Collection<?>;
    }

    public boolean isScalar() {
        return !isMap() && !isList();
    }

    public YamlNode getChild(String name) throws YamlException {
        return getChildrenMap().get(name);
    }

    public boolean hasChild(String name) throws YamlException {
        if (!isMap()) {
            return false;
        }
        return getChildrenMap().containsKey(name);
    }

    public Map<String, YamlNode> getChildrenMap() throws YamlException {
        if (!isMap()) {
            throw new YamlException("Node is not a map!", getPath());
        }
        if (this.mapCache == null) {
            this.mapCache = new HashMap<String, YamlNode>();
            for (Map.Entry<?, ?> entry : ((Map<?, ?>) getValue()).entrySet()) {
                String name = entry.getKey().toString();
                this.mapCache.put(name, new YamlNode(this, name, entry.getValue()));
            }
        }
        return new HashMap<String, YamlNode>(this.mapCache);
    }

    public List<YamlNode> getChildrenList() throws YamlException {
        if (isMap()) {
            if (this.mapCache != null) {
                return new ArrayList<YamlNode>(this.mapCache.values());
            }
            return new ArrayList<YamlNode>(getChildrenMap().values());
        }
        if (!isList()) {
            throw new YamlException("Node is not a list!", getPath());
        }
        if (this.listCache == null) {
            this.listCache = new ArrayList<YamlNode>();
            for (Object o : (Collection<?>) getValue()) {
                this.listCache.add(new YamlNode(this, null, o));
            }
        }
        return new ArrayList<YamlNode>(this.listCache);
    }

    protected void clearCache() {
        this.listCache = null;
        this.mapCache = null;
    }

    public YamlNode addChild(String name, Object value) throws YamlException {
        if (isScalar()) {
            throw new YamlException("Can't add child to scalar node", getPath());
        }
        if (value instanceof ValueHolder) {
            value = ((ValueHolder) value).getValue();
        }
        if (isList()) {
            List<Object> list = new ArrayList<Object>((Collection<?>) getValue());
            list.add(value);
            setValue(list);
            return getChildrenList().get(list.lastIndexOf(value));
        }
        Map<Object, Object> map;
        map = new HashMap<Object, Object>((Map<?, ?>) getValue());
        map.put(name, value);
        return getChild(name);
    }

    public YamlNode addChild(YamlNode node) throws YamlException {
        return addChild(node.getName(), node.getValue());
    }

    public YamlNode removeChild(String name) throws YamlException {
        if (hasChild(name)) {
            return removeChild(getChild(name));
        }
        return null;
    }

    public YamlNode removeChild(YamlNode node) {
        if (isScalar() || node.getParent() != this) {
            return null;
        }
        if (isList()) {
            ((Collection<?>) getValue()).remove(node.getValue());
        } else {
            ((Map<?, ?>) getValue()).remove(node.getName());
        }
        node.setParent(null);
        clearCache();
        return node;
    }

    public void setValue(Object data) {
        this.holder = new ValueHolderBase(this.holder.getName(), false, data);
        clearCache();
    }

    @Override
    public String getName() {
        return this.holder.getName();
    }

    @Override
    public ValueType getType() {
        return this.holder.getType();
    }

    @Override
    public Object getValue() {
        return this.holder.getValue();
    }

    @Override
    public String getString() {
        return this.holder.getString();
    }

    @Override
    public int getInt() {
        return this.holder.getInt();
    }

    @Override
    public long getLong() {
        return this.holder.getLong();
    }

    @Override
    public BigInteger getBigInt() {
        return this.holder.getBigInt();
    }

    @Override
    public double getDouble() {
        return this.holder.getDouble();
    }

    @Override
    public float getFloat() {
        return this.holder.getFloat();
    }

    @Override
    public BigDecimal getDecimal() {
        return this.holder.getDecimal();
    }

    @Override
    public byte[] getBytes() {
        return this.holder.getBytes();
    }

    @Override
    public Date getDate() {
        return this.holder.getDate();
    }

    @Override
    public Blob getBlob() {
        return this.holder.getBlob();
    }

    @Override
    public boolean getBool() {
        return this.holder.getBool();
    }

    @Override
    public boolean isNull() {
        return this.holder.isNull();
    }

    @Override
    public boolean isUnsigned() {
        return this.holder.isUnsigned();
    }
}
