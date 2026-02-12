package com.newland.iso.core;

import java.io.UnsupportedEncodingException;

public class GBKInterpreter implements Interpreter
{
	    /** An instance of this Interpreter. Only one needed for the whole system */
	    public static final GBKInterpreter INSTANCE = new GBKInterpreter();

	    /**
		 * (non-Javadoc)
		 * 
		 * @see com.newland.iso.core.jpos.iso.Interpreter#interpret(java.lang.String)
		 */
	    public byte[] interpret(String data)
	    {
	    	byte[] b;
	    	try {
	    		 b = data.getBytes("GBK");
	    	} catch (UnsupportedEncodingException e) {
	    		b = new byte[data.length()];
	    		for (int i = data.length() - 1; i >= 0; i--)
		        {
		            b[i] = (byte) data.charAt(i);
		        }
	    	}
	        return b;
	    }

	    /**
		 * (non-Javadoc)
		 * 
		 * @see com.newland.iso.core.jpos.iso.Interpreter#uninterpret(byte[])
		 */
	    public String uninterpret (byte[] rawData, int offset, int length) {
	        byte[] ret = new byte[length];
	        System.arraycopy(rawData, offset, ret, 0, length);
	        for (int i = 0; i < length; i++) {
	            ret[i] = (byte)rawData[offset + i];
	        }
	        String s = null;
	        try {
	            s = new String(ret, "GBK");
	        } catch (java.io.UnsupportedEncodingException e) {
	            // GBK is a supported encoding.
	        }
	        return s;
	    }
	    
	    public int getPackedLength(int nDataUnits)
	    {
	        return nDataUnits;
	    }
}
