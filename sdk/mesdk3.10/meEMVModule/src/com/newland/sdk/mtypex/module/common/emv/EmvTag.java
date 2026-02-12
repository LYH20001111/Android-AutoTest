package com.newland.sdk.mtypex.module.common.emv;

import com.newland.sdk.me.module.emv.structure.EMVLenType;
import com.newland.sdk.me.module.emv.structure.EMVTagClass;
import com.newland.sdk.me.module.emv.structure.EMVTagRef;
import com.newland.sdk.me.module.emv.structure.EMVTagType;
import com.newland.sdk.me.module.emv.structure.EMVTagValueType;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtype.util.InnerUtils;

public class EmvTag implements EMVTagRef {
	
	private int tag;
	private String name;
    private EMVTagValueType tagValueType;
    private EMVTagClass tagClass;
    private EMVTagType tagType;
    private EMVLenType lengthModel;
    private int fixedLen;
    private int maxLen;
    private int minLen;
    
    public EmvTag(int tag,String name,EMVTagValueType emvTagValueType){
    	this.tag = InnerUtils.toBERTLVTag(tag);
    	this.name = name;
    	this.tagValueType = emvTagValueType;
    	lengthModel = EMVLenType.VAR;
    	build();
    }
    
    public EmvTag(int tag,String name,int fixLen,EMVTagValueType emvTagValueType){
    	this.tag = InnerUtils.toBERTLVTag(tag);
    	this.name = name;
    	this.tagValueType = emvTagValueType;
    	this.fixedLen = fixLen;
    	lengthModel = EMVLenType.FIXED;
    	build();
    }
    public EmvTag(int tag,String name,int minLen,int maxLen,EMVTagValueType emvTagValueType){
    	this.tag = InnerUtils.toBERTLVTag(tag);
    	this.name = name;
    	this.tagValueType = emvTagValueType;
    	this.minLen = minLen;
    	this.maxLen = maxLen;
    	lengthModel = EMVLenType.SCOPE;
    	build();
    }

    private void build() {

    	byte[] idBytes = tagToBytes(tag);
        if(InnerUtils.isBitSet(idBytes[0], 6)){
            tagType = EMVTagType.CONSTRUCTED;
        } else {
            tagType = EMVTagType.PRIMITIVE;
        }
        //Bits 8 and 7 of the first byte of the tag field indicate a class.
        //The value 00 indicates a data object of the universal class.
        //The value 01 indicates a data object of the application class.
        //The value 10 indicates a data object of the context-specific class.
        //The value 11 indicates a data object of the private class.
        byte classValue = (byte)(idBytes[0] >>> 6 & 0x03);
        switch(classValue){
            case (byte)0x00:
                tagClass = EMVTagClass.UNIVERSAL;
                break;
            case (byte)0x01:
                tagClass = EMVTagClass.APPLICATION;
                break;
            case (byte)0x02:
                tagClass = EMVTagClass.CONTEXT_SPECIFIC;
                break;
            case (byte)0x03:
                tagClass = EMVTagClass.PRIVATE;
                break;
            default:
                throw new RuntimeException("UNEXPECTED TAG CLASS: "+Dump.getHexDump(new byte[]{classValue}) + " " + Dump.getHexDump(idBytes));
        }

    }

    private byte[] tagToBytes(int tag) {
    	return InnerUtils.hex2byte(Integer.toHexString(tag));
	}

    /* (non-Javadoc)
	 * @see com.newland.mtypex.module.common.EMVTagRef#getTag()
	 */
    @Override
	public int getTag() {
		return tag;
	}


	/* (non-Javadoc)
	 * @see com.newland.mtypex.module.common.EMVTagRef#getName()
	 */
	@Override
	public String getName() {
		return name;
	}


	/* (non-Javadoc)
	 * @see com.newland.mtypex.module.common.EMVTagRef#getTagValueType()
	 */
	@Override
	public EMVTagValueType getTagValueType() {
		return tagValueType;
	}


	/* (non-Javadoc)
	 * @see com.newland.mtypex.module.common.EMVTagRef#getTagClass()
	 */
	@Override
	public EMVTagClass getTagClass() {
		return tagClass;
	}


	/* (non-Javadoc)
	 * @see com.newland.mtypex.module.common.EMVTagRef#getTagType()
	 */
	@Override
	public EMVTagType getTagType() {
		return tagType;
	}
	@Override
	public int getFixedLen() {
		return fixedLen;
	}
	@Override
	public int getMaxLen() {
		return maxLen;
	}
	@Override
	public int getMinLen() {
		return minLen;
	}
	@Override
    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Tag[");
        sb.append(Integer.toHexString(tag));
        sb.append("] Name=");
        sb.append(getName());
        sb.append(", TagType=");
        sb.append(getTagType());
        sb.append(", ValueType=");
        sb.append(getTagValueType());
        sb.append(", Class=");
        sb.append(tagClass);
        return sb.toString();
    }
	@Override
	public EMVLenType getEmvLenType() {
		return lengthModel;
	}

	@Override
	public boolean isModelFixedLen() {
		return lengthModel == EMVLenType.FIXED;
	}

	@Override
	public boolean isModelScopeLen() {
		return lengthModel == EMVLenType.SCOPE;
	}
	
	
}
