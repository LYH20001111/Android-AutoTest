package com.newland.sdk.me.cmd.common;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.utils.TLVPackage;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;


@CommandEntity(cmdCode = { (byte)0x1D,(byte)0x07 }, responseClass = CmdGetDeviceParams.CmdGetDeviceParamsResponse.class)
public class CmdGetDeviceParams extends CommonDeviceCommand{
	
	@InstructionField(name="参数类型",index = 0, maxLen = 4000, serializer = ByteArrSerializer.class)
	private byte[] params;
	
	public CmdGetDeviceParams(int[] tags){
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for(int value:tags){
			byte[] tag = InnerUtils.intToBytes(value, 3, true);
			try {
				bos.write(tag);
			} catch (IOException e) {
			}
		}
		params = bos.toByteArray();
	}
	
	@ResponseEntity
	public static class CmdGetDeviceParamsResponse extends AbstractSuccessResponse{
		@InstructionField(name="参数数据内容",index = 0, maxLen = 4000, serializer = ByteArrSerializer.class)
		private byte[] paramsContent;
		
		public TLVPackage getParamsContent(){
			TLVPackage tlvPackage = InnerUtils.newTlvPackage();
			if(paramsContent != null){
				tlvPackage.unpack(paramsContent);
			}
			return tlvPackage;
		}
		
	}

}