package com.newland.sdk.me.module.emv.structure;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Pboc transaction log format definition
 * 
 *
 *
 */
public class PbocTransFormat {

	private static final String FMT_TRADEDATE = "tradeDate";
	
	private static final String FMT_TRADETIME = "tradeTime";
	
	private static final String FMT_TRADEAMOUNT = "tradeAmount";
	
	private static final String FMT_OTHERAMOUNT = "otherAmount";
	
	private static final String FMT_COUNTRYCODE = "countryCode";
	
	private static final String FMT_CURRENCYCODE = "currencyCode";
	
	private static final String FMT_MERCHANTNAME = "merchantName";
	
	private static final String FMT_TRADETYPE = "tradeType";
	
	private static final String FMT_TRANSCOUNT = "transCount";
	
	private List<PbocTransFormatGrid> formatList = new ArrayList<PbocTransFormatGrid>();
	
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
	
	private Map<BytesKey,PbocTransFormatGrid> formatMapping = new HashMap<BytesKey,PbocTransFormatGrid>();
	private void initContainer(){
		formatMapping.put(new BytesKey(new byte[]{(byte)0x9A}), new PbocTransFormatGrid(3,-1,-1, FMT_TRADEDATE));
		formatMapping.put(new BytesKey(new byte[]{(byte)0x9C}), new PbocTransFormatGrid(1,-1,-1, FMT_TRADETYPE));
		formatMapping.put(new BytesKey(new byte[]{(byte)0x9F,(byte)0x21}), new PbocTransFormatGrid(3,-1,-1, FMT_TRADETIME));
		formatMapping.put(new BytesKey(new byte[]{(byte)0x9F,(byte)0x02}), new PbocTransFormatGrid(6,-1,-1, FMT_TRADEAMOUNT));
		formatMapping.put(new BytesKey(new byte[]{(byte)0x9F,(byte)0x03}), new PbocTransFormatGrid(6,-1,-1, FMT_OTHERAMOUNT));
		formatMapping.put(new BytesKey(new byte[]{(byte)0x9F,(byte)0x1A}), new PbocTransFormatGrid(2,-1,-1, FMT_COUNTRYCODE));
		formatMapping.put(new BytesKey(new byte[]{(byte)0x9F,(byte)0x4E}), new PbocTransFormatGrid(-1,0,20, FMT_MERCHANTNAME));
		formatMapping.put(new BytesKey(new byte[]{(byte)0x5F,(byte)0x2A}), new PbocTransFormatGrid(2,-1,-1, FMT_CURRENCYCODE));
		formatMapping.put(new BytesKey(new byte[]{(byte)0x9F,(byte)0x36}), new PbocTransFormatGrid(2,-1,-1, FMT_TRANSCOUNT));	
	}
	
	private byte[] fmt;
	
	public PbocTransFormat(byte[] fmt){
		this.fmt = fmt;
		initContainer();
		init();
	}
	
	private void init(){
		int offset = 3;
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
			PbocTransFormatGrid grid = formatMapping.get(new BytesKey(key));
			grid.setLen(len);
			formatList.add(grid);
		}
	}
	
	public List<PbocTransFormatGrid> getGridlist(){
		return formatList;
	}
	
}
