package com.newland.sdk.module.pin;

public class PinInputResult {
	private byte[] pinblock;
	private byte[] KSN;
	public PinInputResult(byte[] pinblock,byte[] ksn){
		this.pinblock = pinblock;
		this.KSN = ksn;
	}
	public byte[] getPinblock() {
		return pinblock;
	}
	public byte[] getKSN() {
		return KSN;
	}
	public void setPinblock(byte[] pinblock) {
		this.pinblock = pinblock;
	}
	public void setKSN(byte[] kSN) {
		KSN = kSN;
	}
}
