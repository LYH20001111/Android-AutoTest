package com.newland.sdk.me.cmd.swiper;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.me.cmd.serializer.keyManagementSerializer;
import com.newland.sdk.me.cmd.swiper.CmdCalculateTrackData.CmdCalculateTrackDataResponse;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.swiper.MSDAlgorithmType;
import com.newland.sdk.module.swiper.SwipResultCode;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

@CommandEntity(cmdCode = { (byte) 0xD1, (byte) 0x07 }, responseClass = CmdCalculateTrackDataResponse.class)
public class CmdCalculateTrackData extends CommonDeviceCommand {

	@InstructionField(name = "密钥体系", index = 0, fixLen = 1, maxLen = 1, serializer = keyManagementSerializer.class)
	private KeyManagement keyManagement;

	@InstructionField(name = "磁道算法标识", index = 1, fixLen = 1, maxLen = 1, serializer = MSDAlgorithmTypeSerializer.class)
	private MSDAlgorithmType msdAlgorithmType;

	@InstructionField( name = "1磁道",index=2,maxLen=256,serializer=StringSerializer.class)
	private String firstTrackData;

	@InstructionField( name = "2磁道",index=3,maxLen=256,serializer=StringSerializer.class)
	private String secondTrackData;

	@InstructionField( name = "3磁道",index=4,maxLen=256,serializer=StringSerializer.class)
	private String thirdTrackData ;

	@InstructionField(name = "密钥索引", index = 5, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int keyIndex;

	@InstructionField(name = "密钥", index = 6, maxLen = 24, serializer = ByteArrSerializer.class)
	private byte[] keyPayload;

	public CmdCalculateTrackData(KeyManagement keyManagement, MSDAlgorithmType msdAlgorithmType, String firstTrackData, String secondTrackData, String thirdTrackData, int index, byte[] externalKeyData) {
		this.keyManagement = keyManagement;
		this.msdAlgorithmType =msdAlgorithmType;
		this.keyIndex = index;
		if (externalKeyData !=null) {
			keyPayload = externalKeyData;
		} else {
			keyPayload = new byte[0];
		}
		this.firstTrackData = firstTrackData;
		this.secondTrackData=secondTrackData;
		this.thirdTrackData=thirdTrackData;
	}

	public static class SwipResultTypeSerializer extends AbstractEnumSerializer {
		public SwipResultTypeSerializer() {
			super(SwipResultCode.class, new byte[][] { { 0x00 }, { 0x61 }, { 0x62 }, { 0x63 }, { 0x64 }, { 0x65 }, { (byte) 0x92 }, { (byte) 0x93 } });
		}
	}

	@ResponseEntity
	public static class CmdCalculateTrackDataResponse extends AbstractSuccessResponse {

		@InstructionField(name = "返回状态", index = 0, fixLen = 1, maxLen = 1, serializer = SwipResultTypeSerializer.class)
		private SwipResultCode rsltType;

		@InstructionField(name = "一磁道数据", index = 1, maxLen = 256, serializer = ByteArrSerializer.class)
		private byte[] firstTrackData;

		@InstructionField(name = "二磁道数据", index = 2, maxLen = 256, serializer = ByteArrSerializer.class)
		private byte[] secondTrackData;

		@InstructionField(name = "三磁道数据", index = 3, maxLen = 256, serializer = ByteArrSerializer.class)
		private byte[] thirdTrackData;

		@InstructionField(name = "ksn", index = 4, fixLen = 10, maxLen = 10, serializer = ByteArrSerializer.class)
		private byte[] ksn;


		public SwipResultCode getRsltType() {
			return rsltType;
		}

		public byte[] getKsn() {
			return ksn;
		}

		public byte[] getFirstTrackData() {
			return firstTrackData;
		}

		public byte[] getSecondTrackData() {
			return secondTrackData;
		}

		public byte[] getThirdTrackData() {
			return thirdTrackData;
		}
	}


	public static class MSDAlgorithmTypeSerializer extends AbstractEnumSerializer{
		public MSDAlgorithmTypeSerializer() {
			super(MSDAlgorithmType.class, new byte[][]{{0x01},{0x02}});
		}
	}

}
