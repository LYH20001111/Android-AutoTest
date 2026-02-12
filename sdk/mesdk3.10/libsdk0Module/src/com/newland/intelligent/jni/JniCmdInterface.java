package com.newland.intelligent.jni;

import java.util.Arrays;

public class JniCmdInterface {

	private static boolean libLoadSucc = true;

	static{
		try {
			System.loadLibrary("intelligentLib");
		} catch (Throwable e) {
			libLoadSucc = false;
			e.printStackTrace();
		}
	}

	private static JniCmdInterface jniInterface;
	private JniCmdInterface(){}
	public static JniCmdInterface getInstance(){
		if(jniInterface == null){
			synchronized (JniCmdInterface.class){
				if(jniInterface == null){
					jniInterface = new JniCmdInterface();
				}
			}
		}
		return jniInterface;
	}

	public int jniMposLibCmd(byte[] inputCmd, int inLength,byte[] outputCmd,int[] outputLength){
		if(!isLibLoadSucc()){
			return -1;
		}
		return jniMposLibCmd0(inputCmd,inLength,outputCmd,outputLength);
	}
	public int jniMposLibCmdCancel(int type){
		if(!isLibLoadSucc()){
			return -1;
		}
		return jniMposLibCmdCancel0(type);
	}
	public int jniMposLibCmd(byte[] inputCmd, int inLength,byte[] outputCmd,int[] outputLength,CmdRspListener listener){
		if(!isLibLoadSucc()){
			return -1;
		}
		return jniMposLibCmd0(inputCmd,inLength,outputCmd,outputLength,listener);
	}
	public int getErrInfo(int cmd, byte[] errCode, byte[] errMsg, byte[] otherMsg){
		if(!isLibLoadSucc()){
			return -1;
		}
		return getErrInfo0(cmd,errCode,errMsg,otherMsg);
	}

	private boolean isLibLoadSucc(){
		return libLoadSucc;
	}
	private native int jniMposLibCmd0(byte[] inputCmd, int inLength,byte[] outputCmd,int[] outputLength);
	private native int jniMposLibCmdCancel0(int type);
	private native int jniMposLibCmd0(byte[] inputCmd, int inLength,byte[] outputCmd,int[] outputLength,CmdRspListener listener);
	private native int getErrInfo0(int cmd, byte[] errCode, byte[] errMsg, byte[] otherMsg);
	public native int Ndk_beginTransactions (int iTimeoutSec);
	public native int Ndk_endTransactions();
	public native int Ndk_getStatus();

	public native int encrypt(int keySys,int alg,int cipherMode,int keyIndex, byte[] inputData,int inputDataLen,byte[] iv,int ivLen, byte[] outputData,int[] outputDataLen,byte[] ksn,int[] ksnLen);

	public String getNDKEMVVersion(){
		byte[] version = new byte[64];
		Arrays.fill(version, (byte) 0);
		int ret = getNDKEMVVersion0(version);
		if(ret != 0){
			return null;
		}
		return new String(version);
	}
	private native int getNDKEMVVersion0(byte[] version);


	public String getEMVSpVersion(){
		byte[] version = new byte[64];
		Arrays.fill(version, (byte) 0);
		int ret = getEMVSpVersion0(version);
		if(ret != 0){
			return null;
		}
		return new String(version);
	}
	private native int getEMVSpVersion0(byte[] version);

	public native int isProductDevice();
}
