package com.newland.sdk.mtypex.cmd.desc;

import java.lang.reflect.Field;

import com.newland.sdk.mtypex.cmd.PaddingType;
import com.newland.sdk.mtypex.serializer.Serializer;

/**
 * The instruction domain description definition
 * @author chenliang
 *
 */
public class FieldDescription {
	private final String name;
	private final Field field;
	private final int index;
	private final byte[] type;
	private final Serializer serializer;
	private final int maxLen;
	private final int fixLen;
	private final PaddingType paddingType;
	private final byte padding;

	public FieldDescription(String name,int index, byte[] type, Field field,
			Serializer serializer, int maxLen, int fixLen,
			PaddingType paddingType, byte padding) {
		this.name = name;
		this.index = index;
		this.type = type;
		this.field = field;
		this.serializer = serializer;
		this.maxLen = maxLen;
		this.fixLen = fixLen;
		this.padding = padding;
		this.paddingType = paddingType;
	}

	
	public String getName() {
		return name;
	}


	public Field getField() {
		return field;
	}

	public int getIndex() {
		return index;
	}

	public byte[] getType() {
		return type;
	}

	public Serializer getSerializer() {
		return serializer;
	}

	public int getFixLen() {
		return fixLen;
	}

	public PaddingType getPaddingType() {
		return paddingType;
	}

	public byte getPadding() {
		return padding;
	}

	public int getMaxLen() {
		return maxLen;
	}

}
