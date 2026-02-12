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
 * BinaryPrefixer constructs a prefix storing the length in binary.
 * 
 * @author joconnor
 * @version $Revision: 2594 $ $Date: 2008-01-22 14:41:31 -0200 (Tue, 22 Jan 2008) $
 */
public class BinaryPrefixer implements Prefixer
{
    /**
	 * A length prefixer for upto 255 chars. The length is encoded with 1 unsigned byte.
	 */
    public static final BinaryPrefixer B = new BinaryPrefixer(1);

    /**
     * A length prefixer for upto 65535 chars. The length is encoded with 2 unsigned bytes.
     */
    public static final BinaryPrefixer BB = new BinaryPrefixer(2);

    /** The number of digits allowed to express the length */
    private int nBytes;

    public BinaryPrefixer(int nBytes)
    {
        this.nBytes = nBytes;
    }
    
    public void encodeLength(int length, byte[] b) {
    	encodeLength( length, b, 0);
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.iso.Prefixer#encodeLength(int, byte[])
	 */
    public void encodeLength(int length, byte[] b, int offset)
    {
        for (int i = nBytes - 1; i >= 0; i--) {
            b[i+offset] = (byte)(length & 0xFF);
            length >>= 8;
        }
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.iso.Prefixer#decodeLength(byte[], int)
	 */
    public int decodeLength(byte[] b, int offset)
    {
        int len = 0;
        for (int i = 0; i < nBytes; i++)
        {
        	
            len = 256 * len + (b[offset + i] & 0xFF);
        }
        return len;
    }

    /*
	 * (non-Javadoc)
	 * 
	 * @see org.jpos.iso.Prefixer#getLengthInBytes()
	 */
    public int getPackedLength()
    {
        return nBytes;
    }
}