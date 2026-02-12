/*
 * jPOS Project [http://jpos.org]
 * Copyright (C) 2000-2008 Alejandro P. Revilla
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.newland.iso.core;

import com.newland.iso.message.Field;
import com.newland.iso.message.FieldMap;
import com.newland.iso.message.IField;
import com.newland.iso.message.MessageException;
import com.newland.iso.message.packager.FieldPackager;
import com.newland.iso.message.packager.IFieldPackager;
import com.newland.iso.message.packager.MessagePackager;

/**
 * ISOMsgFieldPackager复合字段的打包/解包 (one message inside another one, and so on...)
 */
public class ISOMsgFieldPackager extends FieldPackager {
	protected MessagePackager msgPackager;
	protected IFieldPackager fieldPackager;

	/**
	 * @param fieldPackager
	 *            low level field packager
	 * @param msgPackager
	 *            ISOMsgField default packager
	 */
	public ISOMsgFieldPackager(IFieldPackager fieldPackager,
			MessagePackager msgPackager) {
		super(fieldPackager.getLength(), fieldPackager.getDescription());
		this.msgPackager = msgPackager;
		this.fieldPackager = fieldPackager;
	}

	/**
	 * @param c 待打包的复合字段
	 *            - 复合字段
	 * @return packed component
	 * @exception MessageException
	 */
	public byte[] pack(IField<?> c) throws MessageException {
		if (c.getValue() == c) {
			FieldMap m = (FieldMap) c;
			// m.recalcBitMap();
			Field<byte[]> f = new Field<byte[]>(0, msgPackager.pack(m));
			return fieldPackager.pack(f);
		}
		return fieldPackager.pack(c);
	}

	/**
	 * @param c
	 *            - the Component to unpack
	 * @param b
	 *            - binary image
	 * @param offset
	 *            - starting offset within the binary image
	 * @return consumed bytes
	 * @exception MessageException
	 */
	public int unpack(IField<?> c, byte[] b, int offset) throws MessageException {
		Field<byte[]> f = new Field<byte[]>(0);
		int consumed = fieldPackager.unpack(f, b, offset);
		if (c.getValue() == c) {
			msgPackager.unpack((FieldMap) c, f.getValue());
		}
		return consumed;
	}


	public int getMaxPackedLength() {
		return fieldPackager.getLength();
	}
}
