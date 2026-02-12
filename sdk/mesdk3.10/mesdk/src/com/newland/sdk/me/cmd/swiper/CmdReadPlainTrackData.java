package com.newland.sdk.me.cmd.swiper;

import java.util.HashSet;
import java.util.Set;

import com.newland.sdk.me.cmd.serializer.ByteArrSerializer;
import com.newland.sdk.me.cmd.serializer.ByteSerializer;
import com.newland.sdk.me.cmd.serializer.StringSerializer;
import com.newland.sdk.module.swiper.Account;
import com.newland.sdk.module.swiper.SwipResultCode;
import com.newland.sdk.module.swiper.SwiperReadModel;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.cmd.AbstractSuccessResponse;
import com.newland.sdk.mtypex.cmd.CommandEntity;
import com.newland.sdk.mtypex.cmd.InstructionField;
import com.newland.sdk.mtypex.cmd.ResponseEntity;
import com.newland.sdk.mtypex.conn.CommonDeviceCommand;
import com.newland.sdk.mtypex.serializer.AbstractEnumSerializer;
import com.newland.sdk.mtypex.serializer.Serializer;

@CommandEntity(cmdCode = { (byte)0xD1,(byte)0x04 }, responseClass = CmdReadPlainTrackData.CmdReadClearTrackDataResponse.class)
public class CmdReadPlainTrackData extends CommonDeviceCommand{
	
	private static final int MASK_FIRSTTRACK = 0x01;
	
	private static final int MASK_SECONDTRACK = 0x02;
	
	private static final int MASK_THIRDTRACK = 0x04;
	
	@InstructionField( name = "模式",index=1,fixLen = 1,maxLen=1,serializer=ByteSerializer.class)
	private byte readModel;
	
	public CmdReadPlainTrackData(SwiperReadModel[] readModels){
		readModel = toReadModel(readModels);
	}
	
	private byte toReadModel(SwiperReadModel[] readModels) {
		int rslt = 0;
		for(SwiperReadModel model:readModels){
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
		return (byte)(rslt & 0xff);
	}
	
	public static class SwipResultTypeSerializer extends AbstractEnumSerializer{
		public SwipResultTypeSerializer() {
			super(SwipResultCode.class, new byte[][]{{0x00},{0x61},{0x62},{0x63},{0x64},{0x65},{(byte)0x92},{(byte)0x93}});
		}
	}
    public static class AccountSerializer implements Serializer{

		@Override
		public byte[] pack(Object obj) throws Exception {
			throw new UnsupportedOperationException("not supported this method!");
		}

		@Override
		public Object unpack(byte[] input, int offset, int len)
				throws Exception {
			byte[] pan = new byte[len];
			System.arraycopy(input,offset,pan,0,len);
			String acct = new String(pan);//InnerUtils.bcd2str(input, offset, len * 2, false);
			acct = acct.replace('E', '*');
			
			int index = acct.indexOf('F');
			if(index > 0){
				return acct.substring(0, index);
			}else{
				return acct;
			}
		}
    	
    }

	@ResponseEntity
	public static class CmdReadClearTrackDataResponse extends AbstractSuccessResponse{

		@InstructionField( name = "返回状态",index=0,fixLen = 1,maxLen=1,serializer=SwipResultTypeSerializer.class)
		private SwipResultCode rsltType;
		
		@InstructionField( name = "主账号",index=2,maxLen=40,serializer=AccountSerializer.class)
		private String account;
		
		@InstructionField( name = "主账号哈希值",index=3,fixLen = 20,maxLen=20,serializer=ByteArrSerializer.class)
		private byte[] accountHash;
		
		@InstructionField( name = "磁道信息指示位",index=4,fixLen = 1,maxLen=1,serializer=ByteSerializer.class)
		private byte trackIndicatingbit;
		
		@InstructionField( name = "1磁道",index=5,maxLen=256,serializer=ByteArrSerializer.class)
		private byte[] firstTrackData;
		
		@InstructionField( name = "2磁道",index=6,maxLen=256,serializer=ByteArrSerializer.class)
		private byte[] secondTrackData;
		
		@InstructionField( name = "3磁道",index=7,maxLen=256,serializer=ByteArrSerializer.class)
		private byte[] thirdTrackData ;
		@InstructionField(name = "二磁的有效期", index = 8, fixLen = 4, maxLen = 4, serializer = StringSerializer.class)
		private String validDate;

		@InstructionField(name = "服务代码", index = 9, fixLen = 3, maxLen = 3, serializer = StringSerializer.class)
		private String serviceCode;

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


		public void setValidDate(String validDate) {
			this.validDate = validDate;
		}


		public String getServiceCode() {
			return serviceCode;
		}


		public void setServiceCode(String serviceCode) {
			this.serviceCode = serviceCode;
		}


		public static final SwiperReadModel[] parse(byte value){
			if(value <= 0 || value > (MASK_FIRSTTRACK + MASK_SECONDTRACK + MASK_THIRDTRACK))
				throw new IllegalArgumentException("illegal input!"+Dump.getHexDump(new byte[]{value}));
			
			Set<SwiperReadModel> rslt = new HashSet<SwiperReadModel>();
			if((value & MASK_FIRSTTRACK) != 0) rslt.add(SwiperReadModel.FIRST_TRACK);
			if((value & MASK_SECONDTRACK) != 0) rslt.add(SwiperReadModel.SECOND_TRACK);
			if((value & MASK_THIRDTRACK) != 0) rslt.add(SwiperReadModel.THIRD_TRACK);
			
			return rslt.toArray(new SwiperReadModel[rslt.size()]);
		}
	}
}
