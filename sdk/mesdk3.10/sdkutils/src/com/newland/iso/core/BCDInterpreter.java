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
 * Implements BCD Interpreter. Numeric Strings (consisting of chars '0'..'9' are converted
 * to and from BCD bytes. Thus, "1234" is converted into 2 bytes: 0x12, 0x34.
 * 
 * @author joconnor
 * @version $Revision: 2594 $ $Date: 2008-01-22 14:41:31 -0200 (Tue, 22 Jan 2008) $
 */
public class BCDInterpreter implements Interpreter
{
    /** This BCDInterpreter sometimes adds a 0-nibble to the left. */
    public static final BCDInterpreter LEFT_PADDED = new BCDInterpreter(true, false);
    /** This BCDInterpreter sometimes adds a 0-nibble to the right. */
    public static final BCDInterpreter RIGHT_PADDED = new BCDInterpreter(false, false);
    /** This BCDInterpreter sometimes adds a F-nibble to the right. */
    public static final BCDInterpreter RIGHT_PADDED_F = new BCDInterpreter(false, true);
    /** This BCDInterpreter sometimes adds a F-nibble to the left. */
    public static final BCDInterpreter LEFT_PADDED_F = new BCDInterpreter(true, true);

    private boolean leftPadded;
    private boolean fPadded;

    /** Kept private. Only two instances are possible. */
    private BCDInterpreter(boolean leftPadded, boolean fPadded) {
        this.leftPadded = leftPadded;
        this.fPadded = fPadded;
    }

    /**
	 * (non-Javadoc)
	 * 
	 * @see com.newland.iso.core.jpos.iso.Interpreter#interpret(java.lang.String)
	 */
    public byte[] interpret(String data)
    {
    	byte[] b = new byte[getPackedLength(data.length())];
        InnerISOUtils.str2bcd(data, leftPadded, b, 0);
        if (fPadded && !leftPadded && data.length()%2 == 1)
            b[b.length-1] |= (byte)(b[b.length-1] << 4) == 0 ? 0x0F : 0x00;
        return b;
    }

    /**
	 * (non-Javadoc)
	 * 
	 * @see com.newland.iso.core.jpos.iso.Interpreter#uninterpret(byte[])
	 */
    public String uninterpret(byte[] rawData, int offset, int length)
    {
        return InnerISOUtils.bcd2str (rawData, offset, length, leftPadded);
    }
    /**
	 * Each numeric digit is packed into a nibble, so 2 digits per byte, plus the
     * possibility of padding.
	 */
    public int getPackedLength(int nDataUnits)
    {
        return (nDataUnits + 1) / 2;
    }
}
