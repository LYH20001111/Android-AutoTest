package com.newland.iso.message.packager;

import com.newland.iso.message.IField;
import com.newland.iso.message.MessageException;

public interface IFieldPackager {

	public abstract String getDescription();

	public abstract int getLength();

	/**
	 * @param c - a component
	 * @return packed component
	 * @exception MessageException
	 */
	public abstract byte[] pack(IField<?> c) throws MessageException;

	/**
	 * @param c - the Component to unpack
	 * @param b - binary image
	 * @param offset - starting offset within the binary image
	 * @return consumed bytes
	 * @exception MessageException
	 */
	public abstract int unpack(IField<?> c, byte[] b, int offset)
			throws MessageException;

	/**
	 * 默认返回Field<String>
	 * @param fldNo
	 * @return
	 */
	public abstract IField<?> createComponent(int fldNo);

}