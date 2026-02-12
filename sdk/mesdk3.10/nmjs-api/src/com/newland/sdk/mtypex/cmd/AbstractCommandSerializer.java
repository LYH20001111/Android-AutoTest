package com.newland.sdk.mtypex.cmd;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.cmd.desc.CommandDescription;
import com.newland.sdk.mtypex.cmd.desc.FieldDescription;
import com.newland.sdk.mtypex.cmd.desc.ResponseDescription;
import com.newland.sdk.mtypex.serializer.Serializer;

public abstract  class AbstractCommandSerializer  implements CommandSerializer{
	
	private static final DeviceLogger logger = DeviceLoggerFactory.getLogger(AbstractCommandSerializer.class);
	
	private static final Map<Class<? extends Serializer> ,Serializer> serializerMapping = new HashMap<Class<? extends Serializer>,Serializer>();
	
	private static final Map<Class<? extends DeviceCommand>,CommandDescription> commandsMapping = new HashMap<Class<? extends DeviceCommand>, CommandDescription>();

	@Override
	public <T extends DeviceCommand> byte[] toRequestPayload(T deviceCmd) {
		try{
			CommandDescription cmdDesc = getDescription(deviceCmd);
			return toRequestPayload(cmdDesc,deviceCmd);
		}catch(Exception e){
			logger.error("serialize failed!",e);
			throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "serialize cmd failed" ,e);
		}
	}
	
	@Override
	public <T extends DeviceCommand> DeviceResponse loadNotifiedDeviceResponse(
			T deviceCmd, byte[] payload) {
		try{
			CommandDescription cmdDesc = getDescription(deviceCmd);
			ResponseDescription respDesc = cmdDesc.getNotificationResponseDescription();
			if(cmdDesc.getNotificationResponseDescription() == null){
				if(logger.isDebugEnabled())
					logger.debug("cmd:"+Dump.getHexDump(cmdDesc.getCmdCode())+" not support notification during invoking,but received!");
				return null;
			}
			return loadDeviceResponse(respDesc,payload);
		}catch(Exception e){
			throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "serialize cmd failed" ,e);
		}
	}
	
	@Override
	public <T extends DeviceCommand> DeviceResponse loadDeviceResponse(T deviceCmd,byte[] payload) {
		try{
			CommandDescription cmdDesc = getDescription(deviceCmd);
			return loadDeviceResponse(cmdDesc.getResponseDescription(),payload);
		}catch(Exception e){
			throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "serialize cmd failed" ,e);
		}
	}

	private <T extends DeviceCommand> CommandDescription getDescription(T deviceCmd) throws IllegalAccessException, InstantiationException {
		synchronized (commandsMapping) {
			Class<T> cmdClazz = (Class<T>) deviceCmd.getClass();
			CommandDescription cmdDesc = commandsMapping.get(cmdClazz);
			if(cmdDesc == null){
				if(logger.isDebugEnabled())
					logger.debug("start making Command Description:"+cmdClazz.getName());
				CommandEntity entityConfig = cmdClazz.getAnnotation(CommandEntity.class);
				if(entityConfig == null)
					throw new IllegalArgumentException(cmdClazz.getName()+" should be config by CommandEntity!");
				byte[] cmdCode = entityConfig.cmdCode();
				List<FieldDescription> cmdFieldDescs = getFieldDescription(cmdClazz);
				
				Class<? extends DeviceResponse> responseClass = entityConfig.responseClass();
				ResponseEntity respConfig = responseClass.getAnnotation(ResponseEntity.class);
				if(respConfig == null)
					throw new IllegalArgumentException(responseClass.getName()+" should be config by ResponseEntity!");
				List<FieldDescription> respFieldDescs = getFieldDescription(responseClass) ;
				ResponseDescription respDesc = new ResponseDescription(responseClass, respFieldDescs);
				
				
				Class<? extends DeviceResponse> notificationResponseClass = entityConfig.notificationResponseClass();
				ResponseDescription notifiedRespDesc = null;
				if(!notificationResponseClass.equals(DeviceResponse.class)){ //如果设置过对应的响应类型
					List<FieldDescription> fieldDesc = getFieldDescription(notificationResponseClass) ;
					notifiedRespDesc = new ResponseDescription(notificationResponseClass, fieldDesc);
				}
				
				cmdDesc = new CommandDescription(cmdCode,respDesc,notifiedRespDesc,cmdFieldDescs);
				commandsMapping.put(cmdClazz, cmdDesc);
			}
			return cmdDesc;
		}
	}
	
	private List<FieldDescription> getFieldDescription(Class<?> expected) throws IllegalAccessException, InstantiationException{
		List<FieldDescription> fieldDescs = new ArrayList<FieldDescription>();
		for(Field field:expected.getDeclaredFields()){
			InstructionField fieldConfig =	field.getAnnotation(InstructionField.class);
			if(fieldConfig != null){
				field.setAccessible(true);
				Serializer serializer = getSerializer(fieldConfig.serializer());
				FieldDescription fieldDesc = new FieldDescription(fieldConfig.name(),fieldConfig.index(), fieldConfig.type(), field, serializer, fieldConfig.maxLen(),fieldConfig.fixLen(),fieldConfig.paddingType(),fieldConfig.padding());
				fieldDescs.add(fieldDesc);
			}
		}
		Collections.sort(fieldDescs, new Comparator<FieldDescription>() {
			@Override
			public int compare(FieldDescription desc1, FieldDescription desc2) {
				if(desc1.getIndex() > desc2.getIndex()){
					return 1;
				}else if(desc1.getIndex() == desc2.getIndex()){
					return 0;
				}else{
					return -1;
				}
			}
		});
		return fieldDescs;
	}

	private <T extends Serializer> T getSerializer(Class<T> serializerClass) throws IllegalAccessException, InstantiationException {
		synchronized (serializerMapping) {
			T serializer =	(T) serializerMapping.get(serializerClass);
			if(serializer == null){
				serializer = serializerClass.newInstance();
				serializerMapping.put(serializerClass, serializer);
			}
			return serializer;
		}
	}


	@Override
	public <T extends DeviceCommand> CommandDescription getCmdDescription(T deviceCmd) {
		try{
			return getDescription(deviceCmd);
		}catch(Exception e){
			logger.error("failed to get cmdDesc",e);
			throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "get cmdDesc failed" ,e);
		}
	}
	
	protected Object unpackField(FieldDescription fieldDesc,byte[] content) throws Exception{
		Serializer serializer = (Serializer) fieldDesc.getSerializer();
		if(fieldDesc.getFixLen() <0){
			if(content.length > fieldDesc.getMaxLen()){
				throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED,"["+fieldDesc.getName()+"]len bigger than maxmium("+fieldDesc.getMaxLen()+"):"+content.length);
			}
			if(content.length == 0) //若变长长度为空，则直接返回空对象。
				return null;
		}else{
			if(content.length != fieldDesc.getFixLen()){
				throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED,"["+fieldDesc.getName()+"]len bigger than fixLen("+fieldDesc.getFixLen()+"):"+content.length);
			}
			if(fieldDesc.getPaddingType() == PaddingType.LEFT){
				content = InnerUtils.unpadLeft(content, fieldDesc.getPadding());
			}else if(fieldDesc.getPaddingType() == PaddingType.RIGHT){
				content = InnerUtils.unpadRight(content, fieldDesc.getPadding());
			}
		}
		return serializer.unpack(content, 0, content.length );
	}
	
    
	protected byte[] packField(Object tgt,FieldDescription fieldDesc) throws Exception{
		Object value = fieldDesc.getField().get(tgt);
		Serializer serializer = (Serializer) fieldDesc.getSerializer();
		byte[] content = value == null? new byte[0] : serializer.pack(value); //若数据为空,则尝试使用0字节数据打包
		if(fieldDesc.getFixLen() < 0){//如果没有定长,则会判定最大长度是否合法
			if(fieldDesc.getMaxLen() < content.length){
				throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED,"["+fieldDesc.getName()+"]len bigger than maxmium("+fieldDesc.getMaxLen()+"):"+content.length);
			}
		}else{
			if(logger.isDebugEnabled())
				logger.debug("start pack up:" + fieldDesc == null?"null":fieldDesc.getName() +",bytes:["+content == null?"":Dump.getHexDump(content)+"]");
			if(content.length > fieldDesc.getFixLen()){
				throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED,"["+fieldDesc.getName()+"]len bigger than fixLen("+fieldDesc.getFixLen()+"):"+content.length);
			}else if(content.length < fieldDesc.getFixLen()){
				if(fieldDesc.getPaddingType() == PaddingType.NONE){
					throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED,"["+fieldDesc.getName()+"]len smaller than fixLen("+fieldDesc.getFixLen()+"):"+content.length);
				}else if(fieldDesc.getPaddingType() == PaddingType.LEFT){
					content = InnerUtils.padLeft(content, fieldDesc.getFixLen(), fieldDesc.getPadding());
				}else{
					content = InnerUtils.padRight(content, fieldDesc.getFixLen(), fieldDesc.getPadding());
				}
			}
		}
		return content;
	}
	
	protected abstract byte[] toRequestPayload(CommandDescription cmdDesc,DeviceCommand deviceCmd) throws Exception;
	
	protected abstract DeviceResponse loadDeviceResponse(ResponseDescription respDesc,byte[] payload) throws Exception;
	
	

}
