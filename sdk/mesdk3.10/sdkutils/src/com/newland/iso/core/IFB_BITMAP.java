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

import java.util.BitSet;

import com.newland.iso.message.IField;
import com.newland.iso.message.MessageException;

/**
 * ISOFieldPackager Binary Bitmap
 *
 * @author apr@cs.com.uy
 * @version $Id: IFB_BITMAP.java 2607 2008-02-20 22:44:37Z apr $
 * @see ISOComponent
 * @see ISOBitMapPackager
 */
public class IFB_BITMAP extends ISOBitMapPackager {
    public IFB_BITMAP() {
        super();
    }
    /**
     * @param len - field len
     * @param description symbolic descrption
     */
    public IFB_BITMAP(int len, String description) {
        super(len, description);
    }
    /**
     * @param c - a component
     * @return packed component
     * @exception MessageException
     */
    public byte[] pack (IField<?> c) throws MessageException {
        BitSet b = ((IField<BitSet>) c).getValue();
        int len = 
            getLength() >= 8 ?
                (((b.length()+62)>>6)<<3) : getLength();
        return InnerISOUtils.bitSet2byte (b, len);
    }
    /**
     * @param c - the Component to unpack
     * @param b - binary image
     * @param offset - starting offset within the binary image
     * @return consumed bytes
     * @exception MessageException
     */
    public int unpack (IField<?> c, byte[] b, int offset)
        throws MessageException
    {
        int len;
        BitSet bmap = InnerISOUtils.byte2BitSet (b, offset, getLength() << 3);
        ((IField<BitSet>) c).setValue(bmap);
        len = bmap.get(1) ? 128 : 64;
        if (getLength() > 16 && bmap.get(65))
            len = 192;
        return (Math.min (getLength(), len >> 3));
    }
    
    
    public int getMaxPackedLength() {
        return getLength() >> 3;
    }
}
