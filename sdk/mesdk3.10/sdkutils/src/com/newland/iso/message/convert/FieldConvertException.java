package com.newland.iso.message.convert;

public class FieldConvertException extends Exception {
	private static final long serialVersionUID = 1L;
	protected int fieldNumber;
	
	public FieldConvertException(String msg) {
		super(msg);
	}
	
	public FieldConvertException(String msg, int fieldNumber) {
		super(msg);
		this.fieldNumber = fieldNumber;
	}
}
