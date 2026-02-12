package com.newland.iso.message;

import java.io.Serializable;


public class Field<T> implements Serializable, IField<T> {
	private static final long serialVersionUID = 1L;
	protected int fieldNumber;
	protected T value;

	/**
	 * @param fieldNumber - the FieldNumber
	 */
	public Field(int fieldNumber) {
		this.fieldNumber = fieldNumber;
	}

	/**
	 * @param fieldNumber
	 *            - fieldNumber
	 * @param value
	 *            - field value 
	 */
	public Field(int fieldNumber, T value) {
		this.fieldNumber = fieldNumber;
		this.value = value;
	}

	/* (non-Javadoc)
	 * @see com.newland.iso.IField#setFieldNumber(int)
	 */
	public void setFieldNumber(int fieldNumber) {
		this.fieldNumber = fieldNumber;
	}
	
	/* (non-Javadoc)
	 * @see com.newland.iso.IField#getFieldNumber()
	 */
	public int getFieldNumber()
	{
		return this.fieldNumber;
	}

	/* (non-Javadoc)
	 * @see com.newland.iso.IField#getValue()
	 */
	public T getValue() {
		return value;
	}

	/* (non-Javadoc)
	 * @see com.newland.iso.IField#setValue(T)
	 */
	public void setValue(T value) throws MessageException {
		this.value = value;
	}
	
	public boolean equals(Object object) 
	{
		if (super.equals(object)) return true;
		
		if (object == null) return false;
		
		if (!(object instanceof Field)) 
			return false;
		
		IField<?> fld = (IField<?>)object;
		if (fieldNumber != fld.getFieldNumber()) return false;
		if (value!= null && value.equals(fld.getValue())) return true;
		return value == fld.getValue();
	}
	
	
	public int hashCode() {
		if (value == null) return 0;
		return value.hashCode();
	}
}
