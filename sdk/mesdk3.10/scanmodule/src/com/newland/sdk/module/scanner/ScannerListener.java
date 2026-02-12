package com.newland.sdk.module.scanner;


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
	 * It may be timeout or be annually invoked.{@link ScannerModule#stopScan()}
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
