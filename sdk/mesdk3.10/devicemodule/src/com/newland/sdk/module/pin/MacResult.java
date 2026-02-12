package com.newland.sdk.module.pin;

/**
 * mac calculated result<p>
 * 
 * @author liud
 * @since 1.0.6
 */
public class MacResult {
	
	private byte[] mac;
	
	private byte[] ksn;

	public MacResult(byte[] mac, byte[] ksn) {
		this.mac = mac;
		this.ksn = ksn;
	}

	/**
	 * Return the process result data
	 * 
	 * @return
	 * @since 1.0.6
	 */
	public byte[] getMac() {
		return mac;
	}

	/**
	 * Return the processed ksn<p>
	 * （or the computing data with the participation of given random number, designated by different algorithms）
	 * 
	 * 
	 * @return
	 * @since 1.0.6
	 */
	public byte[] getKsn() {
		return ksn;
	}

}
