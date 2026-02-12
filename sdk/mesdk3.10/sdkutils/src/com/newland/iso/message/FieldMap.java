package com.newland.iso.message;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import com.newland.iso.core.InnerISOUtils;
import com.newland.iso.message.convert.BooleanConverter;
import com.newland.iso.message.convert.CharConverter;
import com.newland.iso.message.convert.DecimalConverter;
import com.newland.iso.message.convert.DoubleConverter;
import com.newland.iso.message.convert.FieldConvertException;
import com.newland.iso.message.convert.IntConverter;

/**
 * 字段的容器，用于表示消息和复合字段。 如果作为内嵌字段则fieldNumber为正确的字段序号(大于等于0)，小于0的字段序号用于特殊的目的。
 * dirty用于后继类处理计算字段等的需要，一旦字段更新，dirty为true;
 *
 * @author szshen
 *
 */
public class FieldMap implements IField<FieldMap>, Serializable {

    static final long serialVersionUID = -3193357271891865972L;
    private final Map<Integer, IField<?>> fields;
    /**
     * 内嵌复合字段所属的字段序号，－1表示不是内嵌字段
     */
    protected int fieldNumber = -1;
    protected int maxField;
    protected boolean dirty, maxFieldDirty;

    /**
     * 如果不是内嵌字段
     */
    public FieldMap() {
        fields = new Hashtable<Integer, IField<?>>();
        maxField = -1;
        dirty = maxFieldDirty = true;
    }

    /**
     * 创建内嵌复合字段
     *
     * @param fieldNumber
     */
    public FieldMap(int fieldNumber) {
        this();
        this.fieldNumber = fieldNumber;
    }

    /**
     * 改变字段序号，字段序号改变并不改变引用该字段的复合字段
     */
    public void setFieldNumber(int fieldNumber) {
        this.fieldNumber = fieldNumber;
    }

    /**
     * 复合字段的序号，－1表示不是子字段而是消息
     */
    public int getFieldNumber() {
        return fieldNumber;
    }

    /**
     * 是否子字段
     *
     * @return
     */
    public boolean isInner() {
        return fieldNumber > -1;
    }

    /**
     * @return the max field number associated with this message
     */
    public int getMaxField() {
        if (maxFieldDirty) {
            recalcMaxField();
        }
        return maxField;
    }

    /**
     * 重新最大字段的值
     */
    private void recalcMaxField() {
        maxField = 0;
        for (Integer i : getFieldNumbers()) {
            maxField = Math.max(maxField, i.intValue());
        }
        maxFieldDirty = false;
    }

    /**
     * 增加字段
     *
     * @param field 添加的字段
     * @throws MessageException
     */
    public void set(IField<?> field) throws MessageException {
        int i = field.getFieldNumber();
        fields.put(i, field);
        // 需要重新计算最大字段序号
        dirty = maxFieldDirty = true;
    }

    /**
     * 删除字段
     *
     * @param fldno 字段序号
     */
    public void unset(int fldno) {
        if (fields.remove(fldno) != null) {
            dirty = maxFieldDirty = true;
        }
    }

    public void unset(int[] flds) {
        for (int i = 0; i < flds.length; i++) {
            unset(flds[i]);
        }
    }

    /**
     * 创建fldno指定的字段，并添加到这个FieldMap中，如果字段值为null，则删除相应字段
     *
     * @param fldno 字段序号
     * @param value 字段值（字符串）
     */
    public void setString(int fldno, String value) throws MessageException {
        if (value != null) {
            set(new Field<String>(fldno, value));
        } else {
            unset(fldno);
        }
    }

    /**
     * Creates an Field<byte[]> associated with field number within this
     * FieldMap
     *
     * @param fpath dot-separated field path (i.e. 63.2)
     * @param value binary field value
     */
    public void setBytes(int fieldNumber, byte[] value) throws MessageException {
        if (value != null) {
            set(new Field<byte[]>(fieldNumber, value));
        } else {
            unset(fieldNumber);
        }
    }

    public void setBoolean(int field, boolean value) throws MessageException {
        String s = BooleanConverter.convert(value);
        set(new Field<String>(field, s));
    }

    public void setChar(int field, char value) throws MessageException {
        String s = CharConverter.convert(value);
        set(new Field<String>(field, s));
    }

    public void setInt(int field, int value) throws MessageException {
        String s = IntConverter.convert(value);
        set(new Field<String>(field, s));
    }

    /**
     * 默认保留小数点后两位
     *
     * @param field
     * @param value
     * @throws MessageException
     */
    public void setDouble(int field, double value) throws MessageException {
        setDouble(field, value, 2);
    }

    public void setDouble(int field, double value, int padding) throws MessageException {
        String s = DoubleConverter.convert(value, padding);
        set(new Field<String>(field, s));
    }

    public void setBigDecimal(int field, BigDecimal value) throws MessageException {
        setBigDecimal(field, value, 2);
    }

    public void setBigDecimal(int field, BigDecimal value, int padding) throws MessageException {
        String s = DecimalConverter.convert(value, padding);
        set(new Field<BigDecimal>(field, new BigDecimal(s)));
    }

    public void setDate(int field, Date value) throws MessageException {
        set(new Field<Date>(field, value));
    }

    /**
     * Creates an Field<String> associated with field path within this FieldMap
     *
     * @param fpath
     * @param field
     * @throws MessageException
     */
    protected void set(String fpath, IField<?> field) throws MessageException {
        StringTokenizer st = new StringTokenizer(fpath, ".");
        FieldMap m = this;
        for (;;) {
            int fldno = Integer.parseInt(st.nextToken());
            if (st.hasMoreTokens()) {
                Object obj = m.getValue(fldno);
                if (obj instanceof FieldMap) {
                    m = (FieldMap) obj;
                } else {
                    m.set(m = new FieldMap(fldno));
                }
            } else {
                field.setFieldNumber(fldno);
                m.set(field);
                break;
            }
        }
    }

    /**
     * Creates an Field<String> associated with field path within this FieldMap
     *
     * @param fpath dot-separated field path (i.e. 63.2)
     * @param value binary field value
     */
    public void set(String fpath, String value) throws MessageException {
        set(fpath, new Field<String>(-1, value));
    }

    /**
     * Creates an Field<byte[]> associated with field path within this FieldMap
     *
     * @param fpath dot-separated field path (i.e. 63.2)
     * @param value binary field value
     */
    public void set(String fpath, byte[] value) throws MessageException {
        set(fpath, new Field<byte[]>(-1, value));
    }

    /**
     * 拷贝另一个FieldMap的字段值
     */
    public void setValue(FieldMap value) throws MessageException {
        fields.clear();
        fields.putAll(value.fields);
        dirty = maxFieldDirty = true;
    }

    public void clear() {
        fields.clear();
    }

    public boolean isEmpty() {
        return fields.size() == 0;
    }

    /**
     * 检查给定的字段是否存在
     *
     * @param fieldNumber 字段序号
     * @return 表示字段是否存在
     */
    public boolean hasField(int fieldNumber) {
        return fields.get(fieldNumber) != null;
    }

    public Map<Integer, IField<?>> getFields() {
        return fields;
    }

    /**
     * 检查是否给定的字段全部存在
     *
     * @param fields 需要检查字段的序号数组
     * @return true 如果全部的字段都存在
     *
     */
    public boolean hasFields(int[] fields) {
        for (int i = 0; i < fields.length; i++) {
            if (!hasField(fields[i])) {
                return false;
            }
        }
        return true;
    }

    /**
     * 获得包含的所有字段序号
     *
     * @return
     */
    public Set<Integer> getFieldNumbers() {
        return fields.keySet();
    }

    /**
     * 获得指定字段
     *
     * @param fieldNumber
     * @return
     */
    public IField<?> getField(int fieldNumber) {
        return fields.get(fieldNumber);
    }

    /**
     * 获得指定路径的字段
     *
     * @param fpath
     * @return
     * @throws MessageException
     */
    public IField<?> getField(String fpath) throws MessageException {
        StringTokenizer st = new StringTokenizer(fpath, ".");
        FieldMap m = this;
        IField<?> field = null;

        for (;;) {
            int fldno = Integer.parseInt(st.nextToken());
            field = m.getField(fldno);
            if (st.hasMoreTokens()) {
                // 判断是否复合字段
                if (field.getValue() == field) {
                    m = (FieldMap) field;
                } else {
                    throw new MessageException("Invalid path '" + fpath + "'");
                }
            } else {
                break;
            }
        }
        return field;
    }

    /**
     * 复合字段的值就是自己
     */
    public FieldMap getValue() {
        return this;
    }

    /**
     * 获取指定字段的值，复合字段将返回复合字段本身
     *
     * @see FieldMap#getValue()
     * @param fldno
     * @return
     * @throws MessageException
     */
    public Object getValue(int fldno) throws MessageException {
        IField<?> fld = this.getField(fldno);
        return fld != null ? fld.getValue() : null;
    }

    /**
     * 返回给定路径的字段值
     *
     * @param fpath 字段路径
     * @return the field value (may by null)
     * @throws MessageException
     */
    public Object getValue(String fpath) throws MessageException {
        IField<?> fld = getField(fpath);
        return fld != null ? fld.getValue() : null;
    }

    /**
     * 获得字段的String值
     *
     * @param fldno
     * @return
     */
    public String getString(int fldno) {
        String s = null;
        if (hasField(fldno)) {
            try {
                Object obj = getValue(fldno);
                if (obj instanceof String) {
                    s = (String) obj;
                } else if (obj instanceof byte[]) {
                    s = InnerISOUtils.hexString((byte[]) obj);
                } else {
                    s = obj.toString();
                }
            } catch (MessageException e) {
                // ignore ISOException - return null
            }
        }
        return s;
    }

    /**
     * Return the String value associated with the given field path
     *
     * @param fpath field path
     * @return field's String value (may be null)
     */
    public String getString(String fpath) {
        String s = null;
        try {
            Object obj = getValue(fpath);
            if (obj instanceof String) {
                s = (String) obj;
            } else if (obj instanceof byte[]) {
                s = InnerISOUtils.hexString((byte[]) obj);
            } else {
                s = obj.toString();
            }
        } catch (MessageException e) {
            // ignore ISOException - return null
        }
        return s;
    }

    /**
     * Return the byte[] value associated with the given ISOField number
     *
     * @param fldno the Field Number
     * @return field's byte[] value
     */
    public byte[] getBytes(int fldno) {
        byte[] b = null;
        if (hasField(fldno)) {
            try {
                Object obj = getValue(fldno);
                if (obj instanceof String) {
                    b = ((String) obj).getBytes();
                } else if (obj instanceof byte[]) {
                    b = ((byte[]) obj);
                }
            } catch (MessageException e) {
                // ignore ISOException - return null
            }
        }
        return b;
    }

    /**
     * Return the String value associated with the given field path
     *
     * @param fpath field path
     * @return field's byte[] value (may be null)
     */
    public byte[] getBytes(String fpath) {
        byte[] b = null;
        try {
            Object obj = getValue(fpath);
            if (obj instanceof String) {
                b = ((String) obj).getBytes();
            } else if (obj instanceof byte[]) {
                b = ((byte[]) obj);
            }
        } catch (MessageException e) {
            // ignore ISOException - return null
        }
        return b;
    }

    public boolean getBoolean(int field) {
        IField<?> f = this.getField(field);
        if (f == null) {
            return false;
        }
        if (f.getValue() instanceof Boolean) {
            return (Boolean) f.getValue();
        } else {
            String value = (String) f.getValue();
            try {
                return BooleanConverter.convert(value);
            } catch (FieldConvertException e) {
                throw new RuntimeException("cann't convert '" + value + "' to boolean ");
            }
        }
    }

    public char getChar(int field) throws FieldNotFound {
        IField<?> f = this.getField(field);
        if (f == null) {
            throw new FieldNotFound(field);
        }
        if (f.getValue() instanceof Character) {
            return (Character) f.getValue();
        } else {
            String value = (String) f.getValue();
            try {
                return CharConverter.convert(value);
            } catch (FieldConvertException e) {
                throw new RuntimeException("cann't convert '" + value + "' to char ");
            }
        }
    }

    public int getInt(int field) throws FieldNotFound {
        IField<?> f = this.getField(field);
        if (f == null) {
            throw new FieldNotFound(field);
        }
        if (f.getValue() instanceof Integer) {
            return (Integer) f.getValue();
        } else {
            String value = (String) f.getValue();
            try {
                return IntConverter.convert(value);
            } catch (FieldConvertException e) {
                throw new RuntimeException("cann't convert '" + value + "' to int ");
            }
        }
    }

    public double getDouble(int field) throws FieldNotFound {
        IField<?> f = this.getField(field);
        if (f == null) {
            throw new FieldNotFound(field);
        }
        if (f.getValue() instanceof Double) {
            return (Double) f.getValue();
        } else {
            String value = (String) f.getValue();
            try {
                return DoubleConverter.convert(value);
            } catch (FieldConvertException e) {
                throw new RuntimeException("cann't convert '" + value + "' to double ");
            }
        }
    }

    public BigDecimal getDecimal(int field) throws FieldNotFound {
        IField<?> f = this.getField(field);
        if (f == null) {
            throw new FieldNotFound(field);
        }
        if (f.getValue() instanceof BigDecimal) {
            return (BigDecimal) f.getValue();
        } else {
            String value = (String) f.getValue();
            try {
                return DecimalConverter.convert(value);
            } catch (FieldConvertException e) {
                throw new RuntimeException("cann't convert '" + value + "' to bigdecimal ");
            }
        }
    }

    public Date getDate(int field) throws FieldNotFound {
        IField<?> f = this.getField(field);
        if (f == null) {
            throw new FieldNotFound(field);
        }
        if (f.getValue() instanceof Date) {
            return (Date) f.getValue();
        } else {
            throw new RuntimeException("cann't convert '" + f.getValue() + "' to Date ");
        }
    }
}