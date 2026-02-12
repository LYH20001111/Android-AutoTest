package com.newland.sdk.me.cmd.serializer;

import com.newland.sdk.module.iccard.ICCardSlot;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

/**
 * @ClassName: ICCardSlotSerializer 
 * @Description: ICCard Slot 卡槽类型序列化器
 * @author More mingsanchi@gmail.com 
 */
public class ICCardSlotSerializer extends AbstractEnumSerializer {
	public ICCardSlotSerializer(){
		super(ICCardSlot.class,new byte[][]{{0x00},{0x01},{0x02},{0x03},{0x04},{0x05}});
	}

}
