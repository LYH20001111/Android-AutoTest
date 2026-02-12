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


/**
 * Implements a Literal Interpreter. No conversion is done.
 * 
 * @author joconnor
 * @version $Revision: 2605 $ $Date: 2008-02-12 12:25:27 -0200 (Tue, 12 Feb 2008) $
 */
public class LiteralInterpreter implements Interpreter
{
    /** An instance of this Interpreter. Only one needed for the whole system */
    public static final LiteralInterpreter INSTANCE = new LiteralInterpreter();

    /**
	 * (non-Javadoc)
	 * 
	 * @see com.newland.iso.core.jpos.iso.Interpreter#interpret(java.lang.String)
	 */
    public byte[] interpret(String data)
    {
        byte[] raw = data.getBytes();
        return raw;
    }

    /**
	 * (non-Javadoc)
	 * 
	 * @see com.newland.iso.core.jpos.iso.Interpreter#uninterpret(byte[])
	 */
    public String uninterpret(byte[] rawData, int offset, int length) {
        try {
            return new String(rawData, offset, length, "ISO8859_1");
        } catch (java.io.UnsupportedEncodingException e) { }
        return null;// should never happen, ISO8859_1 is a supported encoding
    }
    
    /**
	 * Each numeric digit is packed into a nibble, so 2 digits per byte, plus the
     * possibility of padding.
	 * 
	 * @see com.newland.iso.core.jpos.iso.Interpreter#getPackedLength(int)
	 */
    public int getPackedLength(int nDataUnits)
    {
        return nDataUnits;
    }
}
