package com.newland.sdk.me.module.emv;

import com.newland.sdk.module.emv.AID;
import com.newland.sdk.module.emv.CAPK;
import com.newland.sdk.mtype.common.Const.EmvSelfDefinedReference;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.module.emv.EmvPackager;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SimpleAIDCAPKParser {
	
	private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger(SimpleAIDCAPKParser.class);
	
	private EmvPackager packager = EMVInnerUtils.newEmvPackager();
	
	private Pattern tlvPattern = Pattern.compile("\\{\\\"([A-Fa-f0-9]+)\\\"\\}");
	
	private Pattern verPattern = Pattern.compile("^[vV][0-9]3$");
	
	private List<AID> aids = new ArrayList<AID>();
	
	private List<CAPK> capks = new ArrayList<CAPK>();
	
	private int version = -1;
	
	public void parser(InputStream is) {
		InputStreamReader isr = new InputStreamReader(is);
		parser(isr);
	}
	
	public void parser(Reader reader) {
		aids.clear();
		capks.clear();
		
		boolean isAidParser = true;
		FileReader fr = null;
		try{
			BufferedReader br = new BufferedReader(reader);
			
			String line = null;
			boolean isFirstLine = true;
			while((line = br.readLine()) != null){
				if(isFirstLine){
					isFirstLine = false;
					Matcher m = verPattern.matcher(line);
					if(m.find()){
						try{
							version = Integer.parseInt(m.group());
						}catch(Exception e){
							version = 0;
						}
						continue;
					}
				}
				
				if(line.startsWith("AID")){
					isAidParser = true;
				}else if(line.startsWith("CAPK")){
					isAidParser = false;
				}else{
					Matcher m = tlvPattern.matcher(line);
					if(m.find()){
						String read = m.group(1);
						byte[] payload = null;
						try{
							payload = EMVInnerUtils.hex2byte(read);
							if(payload != null){
								if(isAidParser){
									AID config = packager.unpack(payload, AID.class, null);
									aids.add(config);
								}else{
									CAPK capk = packager.unpack(payload, CAPK.class, null);
									byte[] datebs = capk.getExternalPackage().getValue(EmvSelfDefinedReference.CA_PK_EXPIRATION_DATE);
									if(datebs!= null){//针对错误的DF05做异常处理
										if(datebs.length == 8){ //对字节长度为8的数据做处理
											String date = new String(datebs);
											capk.setExternal(EmvSelfDefinedReference.CA_PK_EXPIRATION_DATE, EMVInnerUtils.str2bcd(date, true));
										}
									}
									capks.add(capk);
								}
							}
								
						}catch(Exception e){
							deviceLogger.error("failed to parser :"+read,e);
						}
						
					}
				}
			}
		}catch(Exception e){
			deviceLogger.error("failed to parse aid/ca config!",e);
		}finally{
			if(fr != null){
				try {
					fr.close();
				} catch (IOException e) {
				}
			}
		}
		
	}

	public List<AID> getAids() {
		return aids;
	}

	public List<CAPK> getCapks() {
		return capks;
	}

	public int getVersion() {
		return version;
	}
	


}
