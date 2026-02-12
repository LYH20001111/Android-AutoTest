package com.newland.intelligent.jni;

public interface CmdRspListener {
	public void callback(int status, byte[] data);
}
