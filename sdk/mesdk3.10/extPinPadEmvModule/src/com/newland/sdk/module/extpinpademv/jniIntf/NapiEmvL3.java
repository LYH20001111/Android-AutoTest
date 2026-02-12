package com.newland.sdk.module.extpinpademv.jniIntf;

import com.newland.sdk.module.extpinpademv.AIDEntry;
import com.newland.sdk.module.extpinpademv.CAPKEntry;
import com.newland.sdk.module.extpinpademv.CRLEntry;
import com.newland.sdk.module.extpinpademv.CommunicationListener;
import com.newland.sdk.module.extpinpademv.ExceptionEntry;

/**
 * Author by bxy, Date on 2019/12/30.
 */
public class NapiEmvL3 {
    public native int NAPI_L3Init(byte[] configuration, CommunicationListener listener);
    public native int NAPI_L3LoadTerminalConfig(int cardType, byte[] tlv_list, int tlv_len, int mode);
    public native int NAPI_L3LoadAIDConfig(int cardType, AIDEntry aidEntry, byte[] tlv_list, int[] tlv_len, int mode);
    public native int NAPI_L3LoadCAPK(CAPKEntry capk, int mode);
    public native int NAPI_L3LoadRevocationList(CRLEntry crl, int mode);
    public native int NAPI_L3LoadExceptionList(ExceptionEntry exceptionList, int mode);
    public native int NAPI_L3PerformTransaction(byte[] data, int dataLen, int[] res);
    public native int NAPI_L3CompleteTransaction(byte[] data, int dataLen, int[] res);
    public native int NAPI_L3TerminateTransaction();
    public native int NAPI_L3CancelTransaction();
    public native int NAPI_L3SetData(int tag,byte[] data,int len);
    public native int NAPI_L3GetData(int type, byte KeyIndex, byte[] data,int maxLen,int[] realLen);
    public native int NAPI_L3SetTLVData(byte[] TLV_List, int len);
    public native int NAPI_L3GetTlvData(byte[] tagList, int tagNum, byte KeyIndex, byte[] tlvData, int maxLen,int ctl,int[] realLen);
    public native int NAPI_L3SetDebugMode(int level);
    public native int NAPI_L3GetVersion(int module, byte[] version);
    static {
        System.loadLibrary("extpinpademv");
    }
}
