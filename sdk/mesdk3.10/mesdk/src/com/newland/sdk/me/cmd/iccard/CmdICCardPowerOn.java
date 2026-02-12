package com.newland.sdk.me.cmd.iccard;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ICCardClassSerializer;
import com.newland.sdk.me.cmd.serializer.ICCardSlotSerializer;
import com.newland.sdk.module.iccard.ICCardSlot;
import com.newland.sdk.module.iccard.ICCardType;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte)0xE1,(byte)0x02 }, responseClass = CmdICCardPowerOn.CmdICCardPowerOnResponse.class)
public class CmdICCardPowerOn extends CommonDeviceCommand{

	@InstructionField( name = "卡Slot",index = 0,fixLen = 1, maxLen = 1,serializer = ICCardSlotSerializer.class)
	private ICCardSlot slot;
	@InstructionField( name = "卡类型",index = 1,fixLen = 1, maxLen = 1,serializer = ICCardClassSerializer.class)
	private ICCardType iCCardType;
	
	public CmdICCardPowerOn(ICCardSlot slot, ICCardType iCCardType){
		
		this.slot = slot;
		this.iCCardType=iCCardType;
	}
	
	
	@ResponseEntity
	public static class CmdICCardPowerOnResponse extends AbstractSuccessResponse{
		
		@InstructionField( name = "ATR",index=0, maxLen=200,serializer=ByteArrSerializer.class)
		private byte[] atr;
		
		public byte[] getATR(){
			return atr;
		}
		
	}
}

