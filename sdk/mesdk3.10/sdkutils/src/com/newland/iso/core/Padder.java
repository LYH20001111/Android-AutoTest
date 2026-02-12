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

import com.newland.iso.message.MessageException;


/**
 * An interface for padding and unpadding strings and byte arrays.
 * 
 * @author joconnor
 * @version $Revision: 2594 $ $Date: 2008-01-22 14:41:31 -0200 (Tue, 22 Jan 2008) $
 */
public interface Padder
{
    /**
	 * Returns a padded string upto a maximum length. If the data is longer
	 * than maxLength, then the data is truncated.
	 * 
	 * @param data
	 *            The string to pad.
	 * @param maxLength
	 *            The maximum length of the padded string.
	 * @return A padded string.
	 */
    String pad(String data, int maxLength) throws MessageException;

    /**
	 * Removes the padding from a padded string.
	 * 
	 * @param paddedData
	 *            The string to unpad.
	 * @return The unpadded string.
	 */
    String unpad(String paddedData) throws MessageException;
}