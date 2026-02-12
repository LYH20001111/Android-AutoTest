package com.newland.sdk.me2.cmd.iccard;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.module.iccard.ICCardSlot;
import com.newland.sdk.module.iccard.ICCardSlotState;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

import java.util.HashMap;
import java.util.Map;

@CommandEntity(cmdCode = { (byte)0xE1,(byte)0x01 }, responseClass = CmdICCardTest.CmdICCardTestResponse.class)
public class CmdICCardTest extends CommonDeviceCommand{

	@ResponseEntity
	public static class CmdICCardTestResponse extends AbstractSuccessResponse{
				
		@InstructionField( name = "卡状态State",index = 0,fixLen = 8, maxLen = 8,serializer = ByteArrSerializer.class)
		private byte[] responseArr;
		
		public Map<ICCardSlot, ICCardSlotState> getICCardState(){
			
			Map<ICCardSlot, ICCardSlotState> iccardState = new HashMap<ICCardSlot, ICCardSlotState>();
			
			for (int i = 0; i< responseArr.length; i++){				
				ICCardSlot tempSlot = getSlot(i);
				ICCardSlotState tempState = getSlotState(responseArr[i]);
				iccardState.put(tempSlot, tempState);				
			}
			
			return iccardState;
			
		}
		
		/**
		 * @Title: getSlot 
		 * @Description: 根据具体byte值返回ICCardSlot类型
		 * @param @param b
		 * @return ICCardSlot
		 */
		private ICCardSlot getSlot(int i){
			
			ICCardSlot mSlot = null;
			switch (i) {
			case 0:
				mSlot = ICCardSlot.IC1;
				break;
			case 1:
				mSlot = ICCardSlot.IC2;
				break;
			case 2:
				mSlot = ICCardSlot.IC3;
				break;
			case 3:
				mSlot = ICCardSlot.SAM1;
				break;
			case 4:
				mSlot = ICCardSlot.SAM2;
				break;
			case 5:
				mSlot = ICCardSlot.SAM3;
				break;
			default:
				break;
			}
			return mSlot;
			
		}
		
		/**
		 * @Title: getSlotState 
		 * @Description: 根据具体byte值返回ICCardSlot State值
		 * @param @param b
		 * @param @return 
		 * @return ICCardSlotState
		 */
		private ICCardSlotState getSlotState(byte b){
			
			ICCardSlotState mSlotState = null;
			
			switch (b) {
			case 0x00:
				mSlotState = ICCardSlotState.NO_CARD;
				break;
			case 0x01:
				mSlotState = ICCardSlotState.CARD_INSERTED;
				break;
			case 0x02:
				mSlotState = ICCardSlotState.CARD_POWERED;
				break;
			default:
				break;
			}
			
			return mSlotState;
			
		}	
		
	}
}
