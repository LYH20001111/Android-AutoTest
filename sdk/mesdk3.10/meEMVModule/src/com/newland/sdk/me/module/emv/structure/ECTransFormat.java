package com.newland.sdk.me.module.emv.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Ec Trans log Format
 * 
 *
 *
 */
public class ECTransFormat {

	private static final String FMT_TRADEDATE = "tradeDate";
	
	private static final String FMT_TRADETIME = "tradeTime";
	
	private static final String FMT_COUNTRYCODE = "countryCode";
	
	private static final String FMT_MERCHANTNAME = "merchantName";
	
	private static final String FMT_TRANSCOUNT = "transCount";
	
	private static final String FMT_DF4F = "DF4F";
	
	private List<ECTransFormatGrid> formatList = new ArrayList<ECTransFormatGrid>();
	
	private class BytesKey{
		
		private byte[] value;
		
		public BytesKey(byte[] value){
			this.value = value;
		}
		
		public int hashCode(){
			return value.length;
		}
		
		public boolean equals(Object obj){
			if(obj == null)
				return false;
			
			if(this == obj)
				return true;
			
			if(!(obj instanceof BytesKey)){
				return false;
			}
			BytesKey key = (BytesKey)obj;
			return Arrays.equals(this.value, key.value);
		}
	}
	
	private Map<BytesKey,ECTransFormatGrid> formatMapping = new HashMap<BytesKey,ECTransFormatGrid>();
	private void initContainer(){//DF4F0E9A039F21039F1A029F4E149F3602
		formatMapping.put(new BytesKey(new byte[]{(byte)0xDF,(byte)0x4F}), new ECTransFormatGrid(14,-1,-1, FMT_DF4F));
		formatMapping.put(new BytesKey(new byte[]{(byte)0x9A}), new ECTransFormatGrid(3,-1,-1, FMT_TRADEDATE));
		formatMapping.put(new BytesKey(new byte[]{(byte)0x9F,(byte)0x21}), new ECTransFormatGrid(3,-1,-1, FMT_TRADETIME));
		formatMapping.put(new BytesKey(new byte[]{(byte)0x9F,(byte)0x1A}), new ECTransFormatGrid(2,-1,-1, FMT_COUNTRYCODE));
		formatMapping.put(new BytesKey(new byte[]{(byte)0x9F,(byte)0x4E}), new ECTransFormatGrid(-1,0,20, FMT_MERCHANTNAME));
		formatMapping.put(new BytesKey(new byte[]{(byte)0x9F,(byte)0x36}), new ECTransFormatGrid(2,-1,-1, FMT_TRANSCOUNT));
	}
	
	private byte[] fmt;
	
	public ECTransFormat(byte[] fmt){
		this.fmt = fmt;
		initContainer();
		init();
	}
	
	private void init(){
		int offset = 0;
		while(offset < fmt.length){
			byte first = fmt[offset];
			offset ++;
			byte[] key = null;
			if((first & 0x0F) == 0x0F ){
				key = new byte[2];
				key[0] = first;
				key[1] = fmt[offset];
				offset ++;
			}else{
				key = new byte[1];
				key[0] = first;
			}
			int len = fmt[offset] & 0xff;
			offset ++;
			ECTransFormatGrid grid = formatMapping.get(new BytesKey(key));
			grid.setLen(len);
			formatList.add(grid);
		}
	}
	
	public List<ECTransFormatGrid> getGridlist(){
		return formatList;
	}
	
}
