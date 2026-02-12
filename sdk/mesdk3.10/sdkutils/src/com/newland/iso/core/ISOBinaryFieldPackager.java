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
import com.newland.iso.util.Dump;

/**
 * @author joconnor
 * @version $Revision: 2594 $ $Date: 2008-01-22 14:41:31 -0200 (Tue, 22 Jan 2008) $
 */
public class ISOBinaryFieldPackager extends FieldPackager
{
    private BinaryInterpreter interpreter;
    private Prefixer prefixer;

    /**
     * Constructs a default ISOBinaryFieldPackager. There is no length prefix and a
     * literal interpretation. The set methods must be called to make this
     * ISOBinaryFieldPackager useful.
     */
    public ISOBinaryFieldPackager()
    {
        super();
        this.interpreter = LiteralBinaryInterpreter.INSTANCE;
        this.prefixer = NullPrefixer.INSTANCE;
    }

    /**
     * Creates an ISOBinaryFieldPackager.
     * @param maxLength The maximum length of the field in characters or bytes depending on the datatype.
     * @param description The description of the field. For human readable output.
     * @param interpreter The interpreter used to encode the field.
     * @param prefixer The type of length prefixer used to encode this field.
     */
    public ISOBinaryFieldPackager(int maxLength, String description,
                                  BinaryInterpreter interpreter, Prefixer prefixer)
    {
        super(maxLength, description);
        this.interpreter = interpreter;
        this.prefixer = prefixer;
    }

    /**
     * Creates an ISOBinaryFieldPackager.
     * @param interpreter The interpreter used to encode the field.
     * @param prefixer The type of length prefixer used to encode this field.
     */
    public ISOBinaryFieldPackager(BinaryInterpreter interpreter, Prefixer prefixer)
    {
        super();
        this.interpreter = interpreter;
        this.prefixer = prefixer;
    }

    /**
     * Sets the Interpreter.
     * @param interpreter The interpreter to use in packing and unpacking.
     */
    public void setInterpreter(BinaryInterpreter interpreter)
    {
        this.interpreter = interpreter;
    }

    /**
     * Sets the length prefixer.
     * @param prefixer The length prefixer to use during packing and unpacking.
     */
    public void setPrefixer(Prefixer prefixer)
    {
        this.prefixer = prefixer;
    }


    /**
	 * Convert the component into a byte[].
	 */
    public byte[] pack(IField<?> c) throws MessageException
    {
        try
        {
            byte[] data = (byte[])c.getValue();
            int packedLength = prefixer.getPackedLength();
            if (packedLength == 0)
            {
                if (data.length != getLength())
                {
                    throw new MessageException("Binary data length not the same as the packager length (" + data.length + "/" + getLength() + ")");
                }
            }
            byte[] ret = new byte[interpreter.getPackedLength(data.length) + packedLength];
            prefixer.encodeLength(data.length, ret);
            interpreter.interpret(data, ret, packedLength);
            return ret;
        } catch(Exception e)
        {
            throw new MessageException(makeExceptionMessage(c, "packing"), e);
        }
    }

    /**
	 * @see com.newland.message.packager.FieldPackager.iso.ISOFieldPackager#unpack(org.jpos.iso.ISOComponent,
	 *      byte[], int)
	 */
    public int unpack(IField<?> c, byte[] b, int offset) throws MessageException
    {
        try
        {
            int len = prefixer.decodeLength(b, offset);
            if (len == -1)
            {
                // The prefixer doesn't know how long the field is, so use
    			// maxLength instead
                len = getLength();
            }
            int lenLen = prefixer.getPackedLength();
            byte[] unpacked = interpreter.uninterpret(b, offset + lenLen, len);
            ((IField<byte[]>)c).setValue(unpacked);
            return lenLen + interpreter.getPackedLength(len);
        } catch(Exception e)
        {
            throw new MessageException(makeExceptionMessage(c, "unpacking"), e);
        }
    }


    /**
     * component factory
     * @param fieldNumber - the field number
     * @return the newly created component
     */
    public IField<?> createComponent(int fieldNumber) {
        return new Field<byte[]> (fieldNumber);
    }

    /** Create a nice readable message for errors */
    private String makeExceptionMessage(IField<?> c, String operation) {
        Object fieldKey = "unknown";
        if (c != null)
        {
            try
            {
                fieldKey = new Integer(c.getFieldNumber());
            } catch (Exception ignore)
            {
            }
        }
        return getClass().getName() + ": Problem " + operation + " field " + fieldKey;
    }


    /**
     * Checks the length of the data against the maximum, and throws an IllegalArgumentException.
     * This is designed to be called from field Packager constructors and the setLength()
     * method.
     * @param len The length of the data for this field packager.
     * @param maxLength The maximum length allowed for this type of field packager.
     *          This depends on the prefixer that is used.
     * @throws IllegalArgumentException If len > maxLength.
     */
    protected void checkLength(int len, int maxLength) throws IllegalArgumentException
    {
        if (len > maxLength)
        {
            throw new IllegalArgumentException("Length " + len + " too long for " + getClass().getName());
        }
    }
}
