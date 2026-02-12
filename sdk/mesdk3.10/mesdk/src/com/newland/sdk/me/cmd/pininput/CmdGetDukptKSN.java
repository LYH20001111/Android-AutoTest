package com.newland.sdk.me.cmd.pininput;


import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte) 0x1A, (byte) 0x28 }, responseClass = com.newland.sdk.me.cmd.pininput.CmdGetDukptKSN.CmdGetDukptKSNResponse.class )
public class CmdGetDukptKSN extends CommonDeviceCommand {

	@InstructionField( name = "密钥索引",index=0,fixLen = 1,maxLen=1,serializer=IntegerSerializer.class)
	private int keyIndex;

	public CmdGetDukptKSN(int keyIndex){
		this.keyIndex = keyIndex;
	}
	@ResponseEntity
	public static class CmdGetDukptKSNResponse extends AbstractSuccessResponse {
		@InstructionField(name = "ksn", index = 0, fixLen = 10, maxLen=10, serializer = ByteArrSerializer.class)
		private byte[] ksn;

		public byte[] getKSN() {
			return ksn;
		}
	}
}
