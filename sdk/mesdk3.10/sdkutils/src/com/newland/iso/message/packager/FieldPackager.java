package com.newland.iso.message.packager;

import com.newland.iso.message.Field;
import com.newland.iso.message.IField;
import com.newland.iso.message.MessageException;

/**
 * base class for the various IF*.java Field Packagers
 * Implements "FlyWeight" pattern
 *
 */
public abstract class FieldPackager implements IFieldPackager {
	/**
	 * 字段长度
	 */
    private int len;
    /**
     * 字段描述
     */
    private String description;
    /**
     * 是否填充
     */
    protected boolean pad;

    /**
     * Default Constructor
     */
    public FieldPackager()
    {
        this.len = -1;
        this.description = null;
    }

    /**
     * @param len - field Len
     * @param description - details
     */
    public FieldPackager(int len, String description) {
        this.len = len;
        this.description = description;
    }
    
    /* (non-Javadoc)
	 * @see com.newland.message.packager.IFieldPackager#getDescription()
	 */
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    /* (non-Javadoc)
	 * @see com.newland.message.packager.IFieldPackager#getLength()
	 */
    public int getLength() {
        return len;
    }
    
    public void setLength(int len) {
        this.len = len;
    }

    public void setPad(boolean pad) {
        this.pad = pad;
    }

   // public abstract int getMaxPackedLength();

    /* (non-Javadoc)
	 * @see com.newland.message.packager.IFieldPackager#pack(com.newland.message.IField)
	 */
    public abstract byte[] pack (IField<?> c) throws MessageException;

 
    /* (non-Javadoc)
	 * @see com.newland.message.packager.IFieldPackager#unpack(com.newland.message.IField, byte[], int)
	 */
    public abstract int unpack (IField<?> c, byte[] b, int offset)
        throws MessageException;

    
    /* (non-Javadoc)
	 * @see com.newland.message.packager.IFieldPackager#createComponent(int)
	 */
    public IField<?> createComponent(int fldNo) {
    	return new Field<String>(fldNo);
    }
    
}