package com.newland.sdk.me2.cmd.swiper;


import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.module.swiper.Account;
import com.newland.sdk.module.swiper.SwipResultCode;
import com.newland.sdk.module.swiper.SwiperReadModel;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.PaddingType;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;
import com.newland.sdk.mtypex.serializer.Serializer;
import java.util.HashSet;
import java.util.Set;
import com.newland.sdk.me2.cmd.swiper.CmdReadEncryptTrackData.CmdReadEncryptTrackDataResponse;

@CommandEntity(cmdCode = { (byte) 0xD1, (byte) 0x05 }, responseClass = CmdReadEncryptTrackDataResponse.class)
public class CmdReadEncryptTrackData extends CommonDeviceCommand {

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

	// 各磁道掩码
	private static final int MASK_FIRSTTRACK = 0x01;

	private static final int MASK_SECONDTRACK = 0x02;

	private static final int MASK_THIRDTRACK = 0x04;
	/**
	 * IC 卡二磁道等效信息
	 */
	private static final int MASK_IC_SECONDTRACK = 0x12;

	@InstructionField(name = "公钥索引", index = 0, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int publicKeyIndex;

	@InstructionField(name = "模式", index = 1, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
	private byte readModel;

	// 写死输出,不允许改账号掩码.
	@InstructionField(name = "主账号屏蔽掩码", index = 2, fixLen = 10, maxLen = 10, serializer = ByteArrSerializer.class)
	private byte[] acctMask = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };

	@InstructionField(name = "加密算法标识", index = 3, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int algorithmType;

	@InstructionField(name = "密钥索引", index = 4, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int keyIndex;

	@InstructionField(name = "密钥", index = 5, maxLen = 24, serializer = ByteArrSerializer.class)
	private byte[] keyPayload;

	@InstructionField(name = "随机数(或金额)", index = 6, fixLen = 8, maxLen = 8, padding = 0x00, paddingType = PaddingType.RIGHT, serializer = ByteArrSerializer.class)
	private byte[] random;

	@InstructionField(name = "平台流水号", index = 7, fixLen = 12, maxLen = 12, padding = 0x00, paddingType = PaddingType.RIGHT, serializer = ByteArrSerializer.class)
	private byte[] flowId;

	@InstructionField(name = "算法模式", index = 8, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int algorithmModule;

	@InstructionField(name = "附加信息", index = 9, maxLen = 256, serializer = ByteArrSerializer.class)
	private byte[] extInfo;

	public CmdReadEncryptTrackData(SwiperReadModel[] readModel, int wkIndex, byte[] wkData, byte[] acctMask, int alg, int keyManagement) {
		parseParams(false, readModel, wkIndex, wkData, acctMask, alg, keyManagement);
	}


	public void parseParams(boolean isICcard, SwiperReadModel[] readModel, int wkIndex, byte[] wkData, byte[] acctMask, int alg, int keyManagement) {
		if (wkData !=null) {
			keyIndex = wkIndex;
			keyPayload = wkData;
		} else {
			keyIndex = wkIndex;
			keyPayload = new byte[0];
		}
		if (isICcard) {
			this.readModel = MASK_IC_SECONDTRACK;
		} else {
			this.readModel = toReadModel(readModel);
		}
		if (acctMask != null)
			this.acctMask = acctMask;

		this.algorithmModule = 0x00;
		this.publicKeyIndex = keyManagement;
		this.algorithmType = alg;
		this.random = null;
		this.flowId = null;
		this.extInfo=null;
	}

	private byte toReadModel(SwiperReadModel[] readModels) {
		int rslt = 0;
		for (SwiperReadModel model : readModels) {
			switch (model) {
			case FIRST_TRACK:
				rslt |= MASK_FIRSTTRACK;
				break;
			case SECOND_TRACK:
				rslt |= MASK_SECONDTRACK;
				break;
			case THIRD_TRACK:
				rslt |= MASK_THIRDTRACK;
				break;
			default:
				throw new IllegalArgumentException("not support read model!");
			}
		}
		return (byte) (rslt & 0xff);
	}

	public static class SwipResultTypeSerializer extends AbstractEnumSerializer {
		public SwipResultTypeSerializer() {
			super(SwipResultCode.class, new byte[][] { { 0x00 }, { 0x61 }, { 0x62 }, { 0x63 }, { 0x64 }, { 0x65 }, { (byte) 0x92 }, { (byte) 0x93 } });
		}
	}

	public static class AccountSerializer implements Serializer {

		@Override
		public byte[] pack(Object obj) throws Exception {
			throw new UnsupportedOperationException("not supported this method!");
		}

		@Override
		public Object unpack(byte[] input, int offset, int len) throws Exception {
			String acct = InnerUtils.bcd2str(input, offset, len * 2, false);
			acct = acct.replace('E', '*');

			int index = acct.indexOf('F');
			if (index > 0) {
				return acct.substring(0, index);
			} else {
				return acct;
			}
		}

	}

	@ResponseEntity
	public static class CmdReadEncryptTrackDataResponse extends AbstractSuccessResponse {

		@InstructionField(name = "返回状态", index = 0, fixLen = 1, maxLen = 1, serializer = SwipResultTypeSerializer.class)
		private SwipResultCode rsltType;

		@InstructionField(name = "主账号", index = 1, fixLen = 10, maxLen = 10, serializer = AccountSerializer.class)
		private String account;

		@InstructionField(name = "主账号哈希值", index = 2, fixLen = 20, maxLen = 20, serializer = ByteArrSerializer.class)
		private byte[] accountHash;

		@InstructionField(name = "磁道信息指示位", index = 3, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
		private byte trackIndicatingbit;

		@InstructionField(name = "1磁道", index = 4, maxLen = 256, serializer = ByteArrSerializer.class)
		private byte[] firstTrackData;

		@InstructionField(name = "2磁道", index = 5, maxLen = 256, serializer = ByteArrSerializer.class)
		private byte[] secondTrackData;

		@InstructionField(name = "3磁道", index = 6, maxLen = 256, serializer = ByteArrSerializer.class)
		private byte[] thirdTrackData;

		@InstructionField(name = "二磁的有效期", index = 7, fixLen = 4, maxLen = 4, serializer = StringSerializer.class)
		private String validDate;

		@InstructionField(name = "服务代码", index = 8, fixLen = 3, maxLen = 3, serializer = StringSerializer.class)
		private String serviceCode;

		@InstructionField(name = "ksn", index = 9, fixLen = 10, maxLen = 10, serializer = ByteArrSerializer.class)
		private byte[] ksn;

		@InstructionField(name = "附加信息", index = 10, maxLen = 256, serializer = ByteArrSerializer.class)
		private byte[] extInfo;

		public SwipResultCode getRsltType() {
			return rsltType;
		}

		public Account getAccount() {
			return new Account(account, InnerUtils.hexString(accountHash));
		}

		public SwiperReadModel[] getReadModels() {
			return parse(trackIndicatingbit);
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

		public String getValidDate() {
			return validDate;
		}

		public String getServiceCode() {
			return serviceCode;
		}

		public byte[] getKsn() {
			return ksn;
		}

		public byte[] getExtInfo() {
			return extInfo;
		}

		public static final SwiperReadModel[] parse(byte value) {
			Set<SwiperReadModel> rslt = new HashSet<SwiperReadModel>();
			if (value == MASK_IC_SECONDTRACK) {
				rslt.add(SwiperReadModel.SECOND_TRACK);
			} else {
				if (value <= 0 || value > (MASK_FIRSTTRACK + MASK_SECONDTRACK + MASK_THIRDTRACK))
					throw new IllegalArgumentException("illegal input!" + Dump.getHexDump(new byte[] { value }));
				if ((value & MASK_FIRSTTRACK) != 0)
					rslt.add(SwiperReadModel.FIRST_TRACK);
				if ((value & MASK_SECONDTRACK) != 0)
					rslt.add(SwiperReadModel.SECOND_TRACK);
				if ((value & MASK_THIRDTRACK) != 0)
					rslt.add(SwiperReadModel.THIRD_TRACK);
				if ((value & MASK_THIRDTRACK) != 0)
					rslt.add(SwiperReadModel.THIRD_TRACK);
			}
			return rslt.toArray(new SwiperReadModel[rslt.size()]);
		}
	}
}
