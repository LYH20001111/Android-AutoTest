package com.newland.ndk.h;

public class ST_SEC_KEY_INFO {
	public byte	ucScrKeyType;
	public byte	ucDstKeyType;
	public byte	ucScrKeyIdx;
	public byte	ucDstKeyIdx;
	public int	nDstKeyLen;
	public byte[] DstKeyValue = new byte[24];
}
