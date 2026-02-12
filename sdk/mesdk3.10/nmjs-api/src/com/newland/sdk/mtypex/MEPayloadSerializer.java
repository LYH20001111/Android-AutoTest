package com.newland.sdk.mtypex;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.newland.sdk.mtype.DeviceInvokeException;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.cmd.AbstractCommandSerializer;
import com.newland.sdk.mtypex.cmd.DeviceCommand;
import com.newland.sdk.mtypex.cmd.DeviceResponse;
import com.newland.sdk.mtypex.cmd.desc.CommandDescription;
import com.newland.sdk.mtypex.cmd.desc.FieldDescription;
import com.newland.sdk.mtypex.cmd.desc.ResponseDescription;

public class MEPayloadSerializer extends AbstractCommandSerializer {

	private DeviceLogger logger = DeviceLoggerFactory
			.getLogger(MEPayloadSerializer.class);

	private static final int MAX_2_BCD_LENGTH = 9999;

	private static final int LEN_LENGTH = 2;

	@Override
	protected byte[] toRequestPayload(CommandDescription cmdDesc,DeviceCommand deviceCmd) throws Exception {

		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		for (FieldDescription fieldDesc : cmdDesc.getFieldDescs()) {
			if (fieldDesc.getFixLen() > 0) {
				byte[] content = packField(deviceCmd, fieldDesc);
				bos.write(content);
			} else {
				byte[] lenbs = null;
				byte[] content = packField(deviceCmd, fieldDesc);
				if(content.length > MAX_2_BCD_LENGTH){
					lenbs = InnerUtils.intToBCD(content.length,
							LEN_LENGTH * 4, true);
				}else{
					if (content.length > fieldDesc.getMaxLen()
							|| content.length > MAX_2_BCD_LENGTH) {
						throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED,
								"LEN OUT OF RANGE!" + content.length);
					}
					lenbs = InnerUtils.intToBCD(content.length,
							LEN_LENGTH * 2, true);
				}
				bos.write(lenbs);
				bos.write(content);
			}
		}

		return bos.toByteArray();
	}

	@Override
	protected DeviceResponse loadDeviceResponse(ResponseDescription respDesc,byte[] payload) throws Exception {
		Object target = respDesc.getResponseClass().newInstance();
		ByteArrayInputStream bis = new ByteArrayInputStream(payload);
		for (FieldDescription fieldDesc : respDesc.getFieldDescs()) {
			byte[] content = null;
			if (fieldDesc.getFixLen() > 0) {
				content = new byte[fieldDesc.getFixLen()];
				int read = bis.read(content);
				if(read < 0 ){
					logger.warn("reach payload end!but still has fixed-field to unpack!");
					break;
				}
				if(read < content.length){
					logger.warn("reach payload end!but content-length is not match!"+read+",expected:"+content.length);
				}
			} else {
				byte[] lenbs = new byte[2];
				int read = bis.read(lenbs);
				if(read < 0){
					break;
				}else if(read < lenbs.length){
					throw new DeviceInvokeException("failed to read len:"+read+",expected:"+lenbs.length);
				}
				int len = InnerUtils.bcdToInt(lenbs, 0, LEN_LENGTH * 2, true);
				if(len > 0){
					content = new byte[len];
					read = bis.read(content);
					if(read < 0){
						logger.warn("reach payload end!but still has unfixed-field to unpack!");
						break;
					}
					if(read < content.length){
						logger.warn("reach payload end!but content-length is not match!"+read+",expected:"+content.length);
					}
				}else{
					content = new byte[0];
				}
			}
			
			Object o = unpackField(fieldDesc, content);
			if(logger.isDebugEnabled()){
				logger.debug("try to set "
						+ (o == null ? "null" : o.getClass().getName())
						+ " into "
						+ (target == null ? "null" : target.getClass().getName())
						+ "'s "
						+ (fieldDesc == null ? "null" : fieldDesc.getField()
								.getName()) + "!");
			}
			fieldDesc.getField().set(target, o);
		}
		return (DeviceResponse) target;
	}

}
