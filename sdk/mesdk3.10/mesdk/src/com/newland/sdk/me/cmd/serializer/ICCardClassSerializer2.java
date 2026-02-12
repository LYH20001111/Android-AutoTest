package com.newland.sdk.me.cmd.serializer;

import com.newland.sdk.module.iccard.ICCardType;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

public class ICCardClassSerializer2 extends AbstractEnumSerializer {

    public ICCardClassSerializer2() {
        super(ICCardType.class, new byte[][]{{0x00},{0x07},{0x08},{0x09},{0x0A},{0x0B},{0x0C},{0x0D},{0x06},{0x06},{0x06},{0x06},{0x06},{0x06},{0x06},{0x06},{0x06},{(byte)0xFF},{(byte)0xFF},{(byte)0xFF},{(byte)0xFF}});
    }
}