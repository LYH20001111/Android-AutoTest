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
 * Implements ASCII Interpreter. Strings are converted to and from ASCII bytes.
 * This uses the US-ASCII encoding which all JVMs must support.
 * 
 * @author joconnor
 * @version $Revision: 2605 $ $Date: 2008-02-12 12:25:27 -0200 (Tue, 12 Feb 2008) $
 */
public class AsciiInterpreter implements Interpreter
{
    /** An instance of this Interpreter. Only one needed for the whole system */
    public static final AsciiInterpreter INSTANCE = new AsciiInterpreter();

    /**
	 * (non-Javadoc)
	 * 
	 * @see com.newland.iso.core.jpos.iso.Interpreter#interpret(java.lang.String)
	 */
    public byte[] interpret(String data)
    {
    	byte[] b = new byte[data.length()];
        for (int i = data.length() - 1; i >= 0; i--)
        {
            b[i] = (byte) data.charAt(i);
        }
        return b;
    }

    /**
	 * (non-Javadoc)
	 * 
	 * @see com.newland.iso.core.jpos.iso.Interpreter#uninterpret(byte[])
	 */
    public String uninterpret (byte[] rawData, int offset, int length) {
        byte[] ret = new byte[length];
        for (int i = 0; i < length; i++) {
            ret[i] = (byte)rawData[offset + i];
        }
        String s = null;
        try {
            s = new String(ret, "ISO8859_1");
        } catch (java.io.UnsupportedEncodingException e) {
            // ISO8859_1 is a supported encoding.
        }
        return s;
    }
    
    public int getPackedLength(int nDataUnits)
    {
        return nDataUnits;
    }
}
