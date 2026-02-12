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
 * Implementations convert Strings into byte arrays and vice versa.
 * 
 * @author joconnor
 * @version $Revision: 2594 $ $Date: 2008-01-22 14:41:31 -0200 (Tue, 22 Jan 2008) $
 */
public interface Interpreter
{
    /**
	 * Converts the string data into a different interpretation. Standard
	 * interpretations are ASCII, EBCDIC, BCD and LITERAL.
	 * 
	 * @param data
	 *            The data to be interpreted.
	 * @return The interpreted data.
	 */
    byte[] interpret(String data);

    /**
	 * Converts the byte array into a String. This reverses the interpret
	 * method.
	 * 
	 * @param rawData
	 *            The interpreted data.
	 * @param offset
	 *            The index in rawData to start interpreting at.
	 * @param length
	 *            The number of data units to interpret.
	 * @return The uninterpreted data.
	 */
    String uninterpret(byte[] rawData, int offset, int length);
    
    /**
	 * Returns the number of bytes required to interpret a String of length
	 * nDataUnits.
	 */
    int getPackedLength(int nDataUnits);
}