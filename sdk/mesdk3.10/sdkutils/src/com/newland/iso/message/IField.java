package com.newland.iso.message;

/**
 * 字段包含字段序号和字段的值，复合字段的值为字段本身
 * 用于ISO 8583，TLV， FIX等数据报文的格式，字段的序号在各种消息中具有一致性
 * 
 * @author szshen
 *
 * @param <T> 字段的值类型
 */
public interface IField<T> {

	/**
	 * changes this Component field number<br>
	 * Use with care, this method does not change any reference held by a
	 * Composite.
	 * 
	 * @param fieldNumber
	 *            new field number
	 */
	public abstract void setFieldNumber(int fieldNumber);

	public abstract int getFieldNumber();

	/**
	 * @return Object representing this field value
	 */
	public abstract T getValue();

	/**
	 * @param value
	 *            - Object representing this field value
	 * @exception MessageException
	 */
	public abstract void setValue(T value) throws MessageException;
}