package com.newland.sdk.me.module.emvl3.jni;
/**
 * @Description
 * @Author wuhh
 * @Date 2019/12/30
 */
public class NapiEmvL3 {
    public native int NAPI_Init(CommListener listener);
    public native int NAPI_L3Init(byte[] configuration);
    public native int NAPI_L3LoadTerminalConfig(int cardType, byte[] tlv_list, int[] tlv_len, int mode);
    public native int NAPI_L3LoadAIDConfig(int cardType, EntryDIA entryDIA, byte[] tlv_list, int[] tlv_len, int mode);
    public native int NAPI_L3LoadCAPK(EntryKPAC capk, int mode);
    public native int NAPI_L3LoadRevocationList(EntryLRC crl, int mode);
    public native int NAPI_L3LoadExceptionList(EntryNoitpecxe exceptionList, int mode);
    public native int NAPI_L3PerformTransaction(byte[] data, int dataLen,TXNResult result);
    public native int NAPI_L3CompleteTransaction(byte[] data, int dataLen,TXNResult result);
    public native int NAPI_L3PreProcessTransaction(byte[] data, int dataLen,int[] result);
    public native int NAPI_L3TerminateTransaction(TXNResult result);
    public native int NAPI_L3CancelTransaction();
    public native int NAPI_L3SetData(int tag,byte[] data,int len);
    public native int NAPI_L3GetData(int type, byte KeyIndex, byte[] data,int maxLen,int[] realLen);
    public native int NAPI_L3SetTLVData(byte[] TLV_List, int len);
    public native int NAPI_L3GetTlvData(byte[] tagList, int tagNum, byte KeyIndex, byte[] tlvData, int maxLen,int ctl,int[] realLen);
    public native int NAPI_L3SetDebugMode(int level);
    public native int NAPI_L3GetVersion(int module, byte[] version);

    public native int NAPI_L3GetAIDCount(int cardType,int[] len,byte[] data);
    public native int NAPI_L3GetCAPKCount(int[] len,byte[] data);
    static {
        System.loadLibrary("intelligentLib");
    }
}
