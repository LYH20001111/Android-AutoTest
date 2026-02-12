package com.newland.sdk.me.cmd.printer;

import com.newland.sdk.module.printer.PrinterStatus;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

@CommandEntity(cmdCode = { (byte)0x1B,(byte)0x01 }, responseClass = CmdGetStatus.CmdGetPrinterStatusResponse.class)
public class CmdGetStatus extends CommonDeviceCommand{
	
	public static class PrinterStatusSerializer extends AbstractEnumSerializer{
		public PrinterStatusSerializer() {
			super(PrinterStatus.class, new byte[][]{{(byte)0x00},{(byte)0x04},{(byte)0x08},{(byte)0x40},{(byte)0x80},
					{(byte)0x81},{(byte)0x82}});
		}
	}

	@ResponseEntity
	public static class CmdGetPrinterStatusResponse extends AbstractSuccessResponse{
		
		@InstructionField(name="获取打印机状态",index = 0, fixLen = 1, maxLen = 1, serializer = PrinterStatusSerializer.class)
		private PrinterStatus printerStatus;
		public PrinterStatus getPrinterStatus(){
			return printerStatus;
		}
	}
	public CmdGetStatus(){
	}
}
