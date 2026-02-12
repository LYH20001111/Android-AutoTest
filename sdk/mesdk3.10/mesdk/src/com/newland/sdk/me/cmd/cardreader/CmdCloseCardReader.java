package com.newland.sdk.me.cmd.cardreader;

import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;

@CommandEntity(cmdCode = { (byte)0xD1,(byte)0x02 }, responseClass = CmdCloseCardReader.CmdCloseCardReaderResponse.class)
public class CmdCloseCardReader extends CommonDeviceCommand{

	@ResponseEntity
	public static class CmdCloseCardReaderResponse extends AbstractSuccessResponse{}

}