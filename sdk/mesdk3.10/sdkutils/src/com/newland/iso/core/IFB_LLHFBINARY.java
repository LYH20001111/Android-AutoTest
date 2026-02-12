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
import com.newland.iso.message.IField;
import com.newland.iso.message.MessageException;
import com.newland.iso.message.packager.FieldPackager;


/**
 * ISOFieldPackager Binary Hex Fixed LLBINARY
 *
 * @author apr@cs.com.uy
 * @version $Id: IFB_LLHFBINARY.java 2594 2008-01-22 16:41:31Z apr $
 * @see ISOComponent
 */
public class IFB_LLHFBINARY extends FieldPackager {
    public IFB_LLHFBINARY() {
        super();
    }
    /**
    * @param len - field len
    * @param description symbolic descrption
    */
    public IFB_LLHFBINARY (int len, String description) {
    super(len, description);
    }
   /**
    * @param c - a component
    * @return packed component
    * @exception MessageException
    */
    public byte[] pack (IField<?> c) throws MessageException {
        int len = ((byte[]) c.getValue()).length;
        if (len > getLength() || len>255) {
            throw new MessageException (
                "invalid len "+len +" packing field "+ c.getFieldNumber()
            );
        }
        byte[] b = new byte[getLength() + 1];
        b[0] = (byte) len;
        System.arraycopy(c.getValue(), 0, b, 1, len);
        return b;
    }
   /**
    * @param c - the Component to unpack
    * @param b - binary image
    * @param offset - starting offset within the binary image
    * @return consumed bytes
    * @exception MessageException
    */
    public int unpack (IField<?> c, byte[] b, int offset) throws MessageException
    {
        int len = b[offset] & 0xFF;
        byte[] value = new byte[len];
        System.arraycopy(b, ++offset, value, 0, len);
        ((IField<byte[]>)c).setValue (value);
        return getLength()+1;
    }

    public IField<?> createComponent(int fieldNumber) {
        return new Field<byte[]> (fieldNumber);
    }
    public int getMaxPackedLength() {
        return getLength() + 1;
    }
}

