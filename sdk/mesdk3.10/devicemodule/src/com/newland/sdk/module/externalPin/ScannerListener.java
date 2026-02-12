package com.newland.sdk.module.externalPin;

/**
 * ScannerModule event listening<p>
 * @author chenliang
 */
public interface ScannerListener {
	/**
	 * Scan timeout
	 */
	public void onTimeout();
	/** 
	 * Scan Response
	 * @param scanResults  The result of the scan
	 */
	public void onResponse(String[] scanResults);

	/**
	 * Receive the event when the scanning is ended<p>
	 */
	public void onFinish();

	/**
	 * scan failed
	 * @param errorCode error code
	 * @param message error message
	 */
	public void onError(int errorCode, String message);

	/**
	 * cancel scan
	 */
	public void onCancel();
}
