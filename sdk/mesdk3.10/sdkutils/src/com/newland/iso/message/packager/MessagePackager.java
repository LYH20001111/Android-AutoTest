package com.newland.iso.message.packager;

import com.newland.iso.message.FieldMap;
import com.newland.iso.message.MessageException;

/**
 * 打包和解包消息报文
 * 
 * @author szshen
 */
public interface MessagePackager {
    /**
     * @param   m   the Component to pack
     * @return      Message image
     * @exception MessageException
     */
    public byte[] pack (FieldMap m) throws MessageException;

    /**
     * @param   m   the Container of this message
     * @param   b   ISO message image
     * @return      consumed bytes
     * @exception MessageException
     */
    public int  unpack(FieldMap m, byte[] b, int offset, int len) throws MessageException;
    
    public int  unpack(FieldMap m, byte[] b) throws MessageException;

    /**
     * @param   m   the Container (i.e. an ISOMsg)
     * @param   fldNumber the Field Number
     * @return  Field Description
     */
    public String getFieldDescription(FieldMap m, int fldNumber);

}

