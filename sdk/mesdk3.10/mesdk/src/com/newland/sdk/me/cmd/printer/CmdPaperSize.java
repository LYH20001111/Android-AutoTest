package com.newland.sdk.me.cmd.printer;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.module.printer.PaperSize;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

import java.util.Arrays;

@CommandEntity(cmdCode = { (byte)0x1B,(byte)0x02 }, responseClass = com.newland.sdk.me.cmd.printer.CmdPaperSize.CmdPaperSizeResponse.class)
public class CmdPaperSize extends CommonDeviceCommand{

	@InstructionField(name = "纸张大小", index = 0, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
	private byte paperSize = 0;

	public CmdPaperSize(PaperSize size){
		if(size.equals(PaperSize.SIZE_2INCH)){
			paperSize = 2;
		}else if(size.equals(PaperSize.SIZE_3INCH)){
			paperSize = 3;
		}
	}
	@ResponseEntity
	public static class CmdPaperSizeResponse extends AbstractSuccessResponse{

		@InstructionField( name = "应答码",index = 0,fixLen = 2,maxLen = 2, serializer = ByteArrSerializer.class)
		private byte[] resultCode;

		public boolean getResultCode(){
			try {
				if(resultCode!=null && Arrays.equals(resultCode,new byte[]{0x30,0x31})){
					return true;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			return false;
		}
	}

}
