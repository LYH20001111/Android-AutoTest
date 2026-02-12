package com.newland.sdk.me2.cmd.swiper;

import com.newland.sdk.me2.cmd.swiper.CmdCalculateTrackData.CmdCalculateTrackDataResponse;
import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.module.swiper.SwipResultCode;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.PaddingType;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;

@CommandEntity(cmdCode = { (byte) 0xD1, (byte) 0x07 }, responseClass = CmdCalculateTrackDataResponse.class)
public class CmdCalculateTrackData extends CommonDeviceCommand {

	private static final class PublicKeyIndex {
		/**
		 * MK/SK
		 */
		public static final int MKSK = 0xFF;
		/**
		 * DUKPT
		 */
		public static final int DUKPT = 0x01;
		/**
		 * 拉卡拉的固定密钥方式
		 */
		public static final int FIXED = 0x02;
	}

	/**
	 * IC 卡二磁道等效信息
	 */
	private static final int MASK_IC_SECONDTRACK = 0x12;

	@InstructionField(name = "公钥索引", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int publicKeyIndex;

	@InstructionField(name = "模式", index = 1, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
	private byte readModel = MASK_IC_SECONDTRACK;

	// 写死输出,不允许改账号掩码.
	@InstructionField(name = "二磁道数据", index = 2, maxLen = 256, serializer = StringSerializer.class)
	private String secondTrack;
	@InstructionField(name = "三磁道数据", index = 3, maxLen = 256, serializer = StringSerializer.class)
	private String thirdTrack;
	@InstructionField(name = "加密算法标识", index = 4, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int algorithmType;

	@InstructionField(name = "密钥索引", index = 5, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int keyIndex;

	@InstructionField(name = "密钥", index = 6, maxLen = 24, serializer = ByteArrSerializer.class)
	private byte[] keyPayload;

	@InstructionField(name = "随机数(或金额)", index = 7, fixLen = 8, maxLen = 8, padding = 0x00, paddingType = PaddingType.RIGHT, serializer = ByteArrSerializer.class)
	private byte[] random;

	@InstructionField(name = "平台流水号", index = 8, fixLen = 12, maxLen = 12, padding = 0x00, paddingType = PaddingType.RIGHT, serializer = ByteArrSerializer.class)
	private byte[] flowId;

//	@InstructionField(name = "算法模式", index = 9, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
//	private int algorithmModule;

	@InstructionField(name = "附加信息", index = 9, maxLen = 256, serializer = ByteArrSerializer.class)
	private byte[] extInfo;

	public CmdCalculateTrackData(String secondTrackData, String thirdTrackData, int wkIndex, byte[] wkData, int alg, int keyManagement) {
		if (wkData !=null) {
			keyIndex = wkIndex;
			keyPayload = wkData;
		} else {
			keyIndex = wkIndex;
			keyPayload = new byte[0];
		}
		this.publicKeyIndex = keyManagement;
		secondTrack=secondTrackData;
		thirdTrack=thirdTrackData;
		this.algorithmType = alg;
		this.random = null;
		this.flowId = null;
//		this.algorithmModule = alg.getAlgorithmModule();
		this.extInfo = null;
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

		@InstructionField(name = "磁道信息指示位", index = 1, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
		private byte readModel;

		@InstructionField(name = "加密的磁道数据", index = 2, maxLen = 256, serializer = ByteArrSerializer.class)
		private byte[] trackData;

		@InstructionField(name = "ksn", index = 3, fixLen = 10, maxLen = 10, serializer = ByteArrSerializer.class)
		private byte[] ksn;

		@InstructionField(name = "附加信息", index = 4, maxLen = 256, serializer = ByteArrSerializer.class)
		private byte[] extInfo;

		public SwipResultCode getRsltType() {
			return rsltType;
		}

		public byte[] getKsn() {
			return ksn;
		}

		public byte[] getExtInfo() {
			return extInfo;
		}

		public byte[] getTrackData() {
			return trackData;
		}

		public byte getReadModel() {
			return readModel;
		}

	}
}
