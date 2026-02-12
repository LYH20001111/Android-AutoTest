package com.newland.sdk.module.pin;

public class CipherResult {
	private byte[] data;
	private String ksn;

	public CipherResult(byte[] data, String ksn) {
		this.data = data;
		this.ksn = ksn;
	}

	/**
	 * get the encrypted/Edcrypted data
	 * @return
	 */
	public byte[] getData() {
		return data;
	}

	/**
	 * get ksn data
	 * @return ksn data
	 */
	public String getKsn() {
		return ksn;
	}

}
