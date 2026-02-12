package com.newland.sdk.mtypex.cmd.packager;

import android.support.annotation.LongDef;
import android.util.Log;

import java.io.EOFException;
import java.io.IOException;
import java.util.Arrays;

import com.newland.sdk.mtype.DeviceException;
import com.newland.sdk.mtype.DeviceInvokeException;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.ProcessTimeoutException;
import com.newland.sdk.mtype.common.Const;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.cmd.CommandSerializer;
import com.newland.sdk.mtypex.cmd.DeviceCommand;
import com.newland.sdk.mtypex.cmd.DeviceResponse;
import com.newland.sdk.mtypex.cmd.ErrorResponse;
import com.newland.sdk.mtypex.cmd.desc.CommandDescription;

public class NLMposProtocalPackager implements DeviceCommProtocalPackager{
	
	private static DeviceLogger logger = DeviceLoggerFactory.getLogger(NLMposProtocalPackager.class);
	
	private static final byte[] STX = new byte[]{0x02};
	
	private static final byte[] INDICATOR_CMD = new byte[]{0x2F};
	
	private static final byte[] INDICATOR_MESSAGE = new byte[]{0x3F};

	private static final byte[] INDICATOR_INITIATIVE = new byte[]{0x5F};
	
	private static final byte[] ETX = new byte[]{0x03};
	
	private static final int LEN_STX = STX.length;
	
	private static final int LEN_SERIAL = 1;
	
	private static final int LEN_INDICATOR = 1;
	
	private static final int LEN_CMD = 2;
	
	private static final int LEN_LENGTH = 2;
	
	private static final int LEN_LRC = 1;
	
	private static final int LEN_RESPCODE = 2;
	
	private static final int LEN_ETX = ETX.length;
	
	//最短长度 = 指令长度 + 指示位长度 + 序列号 + 响应码 + 响应数据域长度
	private static final int MIN_RESP_LENGTH = LEN_CMD + LEN_INDICATOR + LEN_SERIAL + LEN_RESPCODE ;
	
	protected static final int MAX_RESP_LENTH = Const.CMD_MAXBUFFER_LEN;
	
	private static final long MAX_SEND_WAITING = 120 * 1000;

	private CommandSerializer serializer;
	
	public NLMposProtocalPackager(CommandSerializer serializer){
		this.serializer = serializer;
	}
	
	@Override
	public byte[] pack(int serial, DeviceCommand cmd) {
		CommandDescription cmdDesc = getCmdDescription(cmd);
		/**计算报文体**/
		byte[] payload = makeupPayload(new byte[]{(byte)(serial & 0xff)}, cmdDesc.getCmdCode(),requestToPayload(cmd) ,INDICATOR_CMD);

		byte[] lrc = caculateLRC(payload);
		
		/**拼装请求**/
		int offset;
		byte[] rslt = new byte[payload.length + LEN_STX + LEN_LRC];
		offset = 0;
		if(logger.isDebugEnabled()){
			logger.debug("start pack up request...");
			logger.debug("pack up stx["+Dump.getHexDump(STX)+"]");
		}
		System.arraycopy(STX, 0, rslt, 0, STX.length);
		offset += STX.length;
		
		if(logger.isDebugEnabled())
			logger.debug("pack up payload["+Dump.getHexDump(payload)+"]");
		System.arraycopy(payload, 0, rslt, offset, payload.length);
		offset += payload.length;
		
		if(logger.isDebugEnabled())
			logger.debug("pack up lrc["+Dump.getHexDump(lrc)+"]");
		System.arraycopy(lrc, 0, rslt, offset, LEN_LRC);
		offset += LEN_LRC;
		
		if(logger.isDebugEnabled())
			logger.debug("end pack up request...["+Dump.getHexDump(rslt)+"]");
		return rslt;
	}

	@Override
	public void readResponseFrom(ProtocalPackagerReader reader,ReadResponseListener listener) throws IOException, InterruptedException, ReadTimeout {
		try{
			byte ack = 0x00;
			byte[] stxbuf = new byte[STX.length];
			read(reader,stxbuf);
			while(!Arrays.equals(stxbuf, STX)){ //查找STX
				try{
					ack = stxbuf[0];

					byte[] temp = new byte[STX.length];
					System.arraycopy(stxbuf, 1, temp, 0, STX.length - 1);
					stxbuf = temp;
					temp = null;
					read(reader,stxbuf,STX.length - 1,1);
					if(logger.isDebugEnabled())
						logger.debug("read \"stx\" :"+Dump.getHexDump(stxbuf));

					if(stxbuf[0] == 0x16 || stxbuf[0] == 0x15){
						listener.processRslt(new byte[]{0x15});
						return;
					}
				}catch(ReadTimeout rte){
//					logger.debug("read stxbuf[0]="+stxbuf[0]+" "+rte.getMessage());
				}
			}
			byte[] lenbs = new byte[LEN_LENGTH]; //读取全长
			read(reader,lenbs);
			if(logger.isDebugEnabled())
				logger.debug("get response len["+Dump.getHexDump(lenbs)+"]");
			int len = InnerUtils.bcdToInt(lenbs, 0, LEN_LENGTH * 2, true); //bcd码处理
			if(logger.isDebugEnabled())
				logger.debug("get response len["+Dump.getHexDump(lenbs)+"]:"+len);
			if(len > MAX_RESP_LENTH || len < MIN_RESP_LENGTH){ //最短也要有MIN_RESP_LENGTH
//				throw new DeviceException(ErrorCode.UNKNOWN, "response len should between ["+MIN_RESP_LENGTH+","+MAX_RESP_LENTH +"],but receive "+len);
			}
			
			if(logger.isDebugEnabled())
				logger.debug("got a response...");
			byte[] cmd = new byte[LEN_CMD];
			read(reader,cmd);
			if(logger.isDebugEnabled())
				logger.debug("reached cmd["+Dump.getHexDump(cmd)+"]...");
			
			byte[] signedSymbol = new byte[LEN_INDICATOR];
			read(reader,signedSymbol);
			
			if(Arrays.equals(signedSymbol, INDICATOR_MESSAGE)){
				if(logger.isDebugEnabled())
					logger.debug("meet device message,do notify!");
			}else if(Arrays.equals(signedSymbol, INDICATOR_INITIATIVE)){
				if(logger.isDebugEnabled())
					logger.debug("meet device initiative inmessage,do notify!");
			}else if(!Arrays.equals(signedSymbol, INDICATOR_CMD)){
				throw new DeviceException(ErrorCode.UNKNOWN, "signedSymbol not match,expected:"+Dump.getHexDump(INDICATOR_CMD)+",but is "+Dump.getHexDump(signedSymbol));
			}
			
			byte[] serial = new byte[LEN_SERIAL]; //获取序列号
			read(reader,serial);
			if(logger.isDebugEnabled())
				logger.debug("and serial["+Dump.getHexDump(serial)+"]");
			
			byte[] body = new byte[len - LEN_CMD - LEN_INDICATOR - LEN_SERIAL];
			read(reader,body);
			if(logger.isDebugEnabled())
				logger.debug("and body["+Dump.getHexDump(body)+"]");
			
			byte[] etxbuf = new byte[ETX.length];
			read(reader,etxbuf);
			if(!Arrays.equals(ETX, etxbuf)){//期待结尾不一致		
				throw new DeviceException(ErrorCode.UNKNOWN, "etx not match!expected["+Dump.getHexDump(ETX)+"],but is ["+Dump.getHexDump(etxbuf)+"]");
			}
			
			byte[] payload = makeupPayload(serial, cmd, body,signedSymbol);
			byte[] lrc = new byte[LEN_LRC];
			read(reader,lrc);
			if(logger.isDebugEnabled())
				logger.debug("and lrc["+Dump.getHexDump(lrc)+"]");
			validateLRC(payload,lrc);//校验LRC

			//全部校验通过,结果处理
			if(Arrays.equals(signedSymbol, INDICATOR_MESSAGE)){//通知消息结果
				listener.notifyDirectMessage(null,body);
				return;
			}
			if(Arrays.equals(signedSymbol, INDICATOR_INITIATIVE)){//设备主动消息结果
				listener.notifyDirectMessage(cmd,body);
				return;
			}

			byte[] recvData = new byte[STX.length + payload.length + LEN_LRC];
			recvData[0] = 0x02;
			System.arraycopy(payload, 0, recvData, 1, payload.length);
			recvData[recvData.length - 1] = lrc[0];

			boolean hadDealRslt = listener.processRslt(recvData);
			if(logger.isDebugEnabled())
				logger.debug("readResponseFrom hadDealRslt="+hadDealRslt);
			if(!hadDealRslt){
				listener.processRslt(serial,body);
			}

		}catch(ReadTimeout e){
		}catch(DeviceException de){//对于数据不匹配的异常，缓存区很可能有数据，所以期望一个缓存清理，该缓存清理过程不期待抛出任何超时异常。
			try{
				reader.clearBuffer(MAX_RESP_LENTH);
			}catch(Exception e){
			}
		}
	}
	
	private void read(ProtocalPackagerReader reader,byte[] buffer) throws ReadTimeout, IOException, InterruptedException{
		int read = reader.read(buffer);
		if(read != buffer.length){
			throw new EOFException("eof reached!");
		}
		
	}
	private void read(ProtocalPackagerReader reader,byte[] buffer , int offset, int len) throws ReadTimeout, IOException, InterruptedException{
		int read = reader.read(buffer);
		if(read != len){
			throw new EOFException("eof reached!");
		}
	}
	
	/**
	 * 生成数据包体<p>
	 * 包体将直接参与lrc计算。
	 * 
	 * @param serial
	 * @param cmdcode
	 * @param body
	 * @return
	 */
	private byte[] makeupPayload(byte[] serial,byte[] cmdcode,byte[] body,byte[] indicator){
		if(serial == null)
			throw new IllegalArgumentException("serial should not be null!");
		
		if(cmdcode == null)
			throw new IllegalArgumentException("cmdcode should not be null!");
		
		if(body == null)
			throw new IllegalArgumentException("body should not be null!");
		
		int offset = 0;
		byte[] payload = new byte[LEN_LENGTH + LEN_CMD + LEN_INDICATOR + LEN_SERIAL + body.length + LEN_ETX];
		
		if(logger.isDebugEnabled())
			logger.debug("start make request payload...");
		
		int len = LEN_CMD + LEN_INDICATOR + LEN_SERIAL + body.length;
		byte[] lenbs = InnerUtils.intToBCD(len,LEN_LENGTH * 2, true);
		System.arraycopy(lenbs, 0, payload, offset, LEN_LENGTH);
		if(logger.isDebugEnabled())
			logger.debug("pack up len["+Dump.getHexDump(lenbs)+"]");
		offset += LEN_LENGTH;
		
		if(logger.isDebugEnabled())
			logger.debug("pack up cmd["+Dump.getHexDump(cmdcode)+"]");
		System.arraycopy(cmdcode, 0, payload, offset, LEN_CMD);
		offset += LEN_CMD;
		
		if(logger.isDebugEnabled())
			logger.debug("pack up signedSymbol["+Dump.getHexDump(indicator)+"]");
		System.arraycopy(indicator, 0, payload, offset, LEN_INDICATOR);
		offset += LEN_INDICATOR;
		
		if(logger.isDebugEnabled())
			logger.debug("pack up serial["+Dump.getHexDump(serial)+"]");
		System.arraycopy(serial, 0, payload, offset, LEN_SERIAL);
		offset += LEN_SERIAL;
		
		if(logger.isDebugEnabled())
			logger.debug("pack up body["+Dump.getHexDump(body)+"]");
		System.arraycopy(body, 0, payload, offset, body.length);
		offset += body.length;
		
		if(logger.isDebugEnabled())
			logger.debug("pack up ETX["+Dump.getHexDump(ETX)+"]");
		System.arraycopy(ETX, 0, payload, offset, LEN_ETX);		
		
		if(logger.isDebugEnabled())
			logger.debug("make payload finish...["+Dump.getHexDump(payload)+"],total len:"+payload.length);
		return payload;
	}
	
	private void validateLRC(byte[] payload, byte[] lrc) throws DeviceException {
		byte[] rslt = caculateLRC(payload);
		if(!Arrays.equals(rslt, lrc)){
			throw new DeviceException(ErrorCode.UNKNOWN, "lrc not match!expected:"+Dump.getHexDump(lrc)+",but is "+Dump.getHexDump(rslt));
		}
		
	}

	private byte[] caculateLRC(byte[] payload) {
		int offset = 0;
		byte lrc = payload[0];
		do{
			offset ++;
			lrc ^= payload[offset];
		}while(offset < payload.length - 1);
		
		return new byte[]{lrc};
	}
	
	public void unpack(DeviceCommand cmd,byte[] rslt,ResponseUnpackListener listener){
		DeviceResponse response = null;
		boolean isNotifyResponse = false;
			if(rslt != null){
				byte[] respCode = new byte[LEN_RESPCODE];
				final byte[] content = new byte[rslt.length - respCode.length];
				System.arraycopy(rslt, 0, respCode, 0, LEN_RESPCODE);
				System.arraycopy(rslt, LEN_RESPCODE , content, 0, content.length);
				try{
					String respCodeStr = new String(respCode,"iso8859-1");
					int nativeCode = Integer.valueOf(respCodeStr);
					if(logger.isDebugEnabled())
						logger.debug("receive resp nativeCode:"+nativeCode);
					switch (nativeCode) {
						case 0:
							if(logger.isDebugEnabled())
								logger.debug("start unpack response,content["+Dump.getHexDump(content)+"]");
							response = loadDeviceResponse(cmd, content);
							break;
						case 7://设备执行超时
							ProcessTimeoutException timeoutException = new ProcessTimeoutException("device invoke timeout!"+nativeCode);
							response = new ErrorResponse(timeoutException);
							break;
						case 8:{ //当状态为8时,则可能是一个临时的事件响应,这时候会判断是否该指令是否传入了对应执行的事件监听器
							isNotifyResponse = true;
							response = loadNotifiedDeviceResponse(cmd,content);
							break;
						}
						default:
							DeviceInvokeException invokeException = new DeviceInvokeException(respCodeStr, "device invoke failed!"+nativeCode);
							response = new ErrorResponse(invokeException);
							break;
					}
				}catch(Exception e){
					DeviceRTException e1 = new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "serialize response failed!["+Dump.getHexDump(rslt)+"]",e);
					response = new ErrorResponse(e1);
				}
			}
			listener.unpackFinished(isNotifyResponse, response);
		}

	/**
	 * 将指令序列化成一个字节流
	 * 
	 * @since ver3.10.01
	 * @param deviceCmd 对应指令
	 * @return
	 * 		请求字节流
	 */
	protected <T extends DeviceCommand> byte[] requestToPayload(T deviceCmd){
		return serializer.toRequestPayload(deviceCmd);
	}
	/**
	 * 将字节流反序列化成一个响应
	 * 
	 * @since ver3.10.01
	 * @param deviceCmd 对应的指令类型
	 * @param payload 响应实体
	 * @return
	 * 		响应实体
	 */
	protected  <T extends DeviceCommand> DeviceResponse loadDeviceResponse(T deviceCmd,byte[] payload){
		return serializer.loadDeviceResponse(deviceCmd, payload);
	}
	
	protected  <T extends DeviceCommand> DeviceResponse loadNotifiedDeviceResponse(T deviceCmd,byte[] payload){
		return serializer.loadNotifiedDeviceResponse(deviceCmd, payload);
	}
	/**
	 * 获得一个指令的描述
	 * 
	 * @since ver3.10.01
	 * @param deviceCmd 指令类型
	 * @return
	 * 		指令描述
	 */
	protected <T extends DeviceCommand> CommandDescription getCmdDescription(T deviceCmd){
		return serializer.getCmdDescription(deviceCmd);
	}
}
