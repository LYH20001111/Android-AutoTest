package com.newland.sdk.module.scanner;


/**
 * Decode event listening<p>
 * 
 * @author lindan
 */
public interface DecodeListener {
	
	/**
	 * decode result data
	 * @param decodeResult decode result data
	 */
	public void onResult(byte[] decodeResult);

	/**
	 * decode failure
	 */
	public void onError(int errorCode, String errMsg);

}
