package com.newland.sdk.mtypex.cmd.packager;

public interface ReadResponseListener {
	
	public void processRslt(byte[]serial,byte[]body);
	
	public void notifyDirectMessage(byte[] cmdCode,byte[] body);

	public boolean processRslt(byte[] recvData);
}
