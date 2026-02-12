package com.newland.sdk.me.cmd.swiper;

import java.util.HashSet;
import java.util.Set;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.me.cmd.serializer.IntegerSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.me.cmd.serializer.keyManagementSerializer;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.swiper.Account;
import com.newland.sdk.module.swiper.MSDAlgorithmType;
import com.newland.sdk.module.swiper.SwipResultCode;
import com.newland.sdk.module.swiper.SwiperReadModel;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;
import com.newland.sdk.mtypex.serializer.Serializer;

@CommandEntity(cmdCode = { (byte) 0xD1, (byte) 0x05 }, responseClass = CmdReadEncryptTrackData.CmdReadEncryptTrackDataResponse.class)
public class CmdReadEncryptTrackData extends CommonDeviceCommand {
	// 各磁道掩码
	private static final int MASK_FIRSTTRACK = 0x01;

	private static final int MASK_SECONDTRACK = 0x02;

	private static final int MASK_THIRDTRACK = 0x04;
	/**
	 * IC 卡二磁道等效信息
	 */
	private static final int MASK_IC_SECONDTRACK = 0x12;

	@InstructionField(name = "密钥体系", index = 0, fixLen = 1, maxLen = 1, serializer = keyManagementSerializer.class)
	private KeyManagement keyManagement;

	@InstructionField(name = "磁道算法标识", index = 1, fixLen = 1, maxLen = 1, serializer = CmdCalculateTrackData.MSDAlgorithmTypeSerializer.class)
	private MSDAlgorithmType msdAlgorithmType;

	@InstructionField(name = "磁道信息读取模式", index = 2, fixLen = 1, maxLen = 1, serializer = ByteSerializer.class)
	private byte readModel;

	// 写死输出,不允许改账号掩码.
	@InstructionField(name = "主账号屏蔽掩码", index = 3, fixLen = 10, maxLen = 10, serializer = ByteArrSerializer.class)
	private byte[] acctMask = new byte[] { 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00, 0x00 };


	@InstructionField(name = "密钥索引", index = 4, fixLen = 1, maxLen = 1, serializer = IntegerSerializer.class)
	private int keyIndex;

	@InstructionField(name = "密钥", index = 5, maxLen = 24, serializer = ByteArrSerializer.class)
	private byte[] keyPayload;

	public CmdReadEncryptTrackData(KeyManagement keyManagement, MSDAlgorithmType msdAlgorithmType, SwiperReadModel[] readModel, int keyIndex, byte[] acctMask, byte[] externalKeyData) {
		parseParams(keyManagement,msdAlgorithmType, readModel, keyIndex, acctMask, externalKeyData);
	}


	public void parseParams(KeyManagement keyManagement, MSDAlgorithmType msdAlgorithmType, SwiperReadModel[] readModel, int index, byte[] acctMask, byte[] externalKeyData) {
		this.keyManagement = keyManagement;
		this.msdAlgorithmType = msdAlgorithmType;
		keyIndex = index;
		if (externalKeyData !=null) {
			keyPayload = externalKeyData;
		} else {
			keyPayload = new byte[0];
		}   
		this.readModel = toReadModel(readModel);

		if (acctMask != null)
			this.acctMask = acctMask;
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

		@InstructionField(name = "主账号", index = 1, fixLen = 20, maxLen = 20, serializer = AccountSerializer.class)
		private String account;

		@InstructionField(name = "主账号哈希值", index = 2, fixLen = 20, maxLen = 20, serializer = ByteArrSerializer.class)
		private byte[] accountHash;

		@InstructionField(name = "1磁道", index = 3, maxLen = 256, serializer = ByteArrSerializer.class)
		private byte[] firstTrackData;

		@InstructionField(name = "2磁道", index = 4, maxLen = 256, serializer = ByteArrSerializer.class)
		private byte[] secondTrackData;

		@InstructionField(name = "3磁道", index = 5, maxLen = 256, serializer = ByteArrSerializer.class)
		private byte[] thirdTrackData;

		@InstructionField(name = "二磁的有效期", index = 6, fixLen = 4, maxLen = 4, serializer = StringSerializer.class)
		private String validDate;

		@InstructionField(name = "服务代码", index = 7, fixLen = 3, maxLen = 3, serializer = StringSerializer.class)
		private String serviceCode;

		@InstructionField(name = "ksn", index = 8, fixLen = 10, maxLen = 10, serializer = ByteArrSerializer.class)
		private byte[] ksn;

		public SwipResultCode getRsltType() {
			return rsltType;
		}

		public Account getAccount() {
			return new Account(account, InnerUtils.hexString(accountHash));
		}

		public SwiperReadModel[] getReadModels() {
			return parse();
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


		public  SwiperReadModel[] parse() {
			Set<SwiperReadModel> rslt = new HashSet<SwiperReadModel>();
				if (firstTrackData != null)
					rslt.add(SwiperReadModel.FIRST_TRACK);
				if (secondTrackData != null)
					rslt.add(SwiperReadModel.SECOND_TRACK);
				if (thirdTrackData != null)
					rslt.add(SwiperReadModel.THIRD_TRACK);
			return rslt.toArray(new SwiperReadModel[rslt.size()]);
		}
	}
}
