package com.newland.sdk.module.extpinpademv;

import android.util.Log;

import com.newland.sdk.module.extpinpademv.jniIntf.NapiEmvL3;
import com.newland.sdk.module.extpinpademv.utils.ISOUtils;
import com.newland.sdk.module.extpinpademv.utils.Logger;

import java.util.ArrayList;

/**
 * Author by bxy, Date on 2019/12/30.
 */
public class MEExtPinpadEmv implements ExtPinpadEmvModule{
    private static final String TAG = "MEExtPinpadEmv";
    private NapiEmvL3 mNapiEmvL3;
    public MEExtPinpadEmv() {
        mNapiEmvL3 = new NapiEmvL3();
    }

    @Override
    public boolean init(byte[] configuration,CommunicationListener listener) {
        if(configuration==null||listener==null){
            return false;
        }
        if(configuration.length!=8){
            return false;
        }
        int ret = mNapiEmvL3.NAPI_L3Init(configuration,listener);
        Logger.d(TAG, "init: ret="+ret);
        if(ret < 0){
            return false;
        }
        return true;
    }

    @Override
    public boolean loadTerminalConfig(CardInterface cardIntf, byte[] tlvList, ConfigMode mode) {
        if(cardIntf==null||tlvList==null||mode==null){
            return false;
        }
        int ret = mNapiEmvL3.NAPI_L3LoadTerminalConfig(cardIntf.ordinal(),tlvList,tlvList.length,mode.ordinal());
        Logger.d(TAG, "loadTerminalConfig: ret="+ret);
        if(ret < 0){
            return false;
        }
        return true;
    }

    @Override
    public boolean addAid(CardInterface cardIntf, byte[] tlvList) {
        if(cardIntf==null||tlvList==null){
            return false;
        }
        int ret = mNapiEmvL3.NAPI_L3LoadAIDConfig(cardIntf.ordinal(),null,tlvList,new int[]{tlvList.length}, ConfigMode.UPDATE.ordinal());
        Logger.d(TAG, "addAid: ret="+ret);
        if(ret < 0){
            return false;
        }
        return true;
    }

    @Override
    public byte[] getSpecifiedAid(CardInterface cardIntf, AIDEntry aidEntry) {
        if(cardIntf==null||aidEntry==null){
            return null;
        }
        byte[] tlv = new byte[2048];
        int[] len = new int[1];
        int ret = mNapiEmvL3.NAPI_L3LoadAIDConfig(cardIntf.ordinal(),aidEntry,tlv,len, ConfigMode.GET.ordinal());
        Logger.d(TAG, "getSpecifiedAid: ret="+ret);
        if(ret < 0 || len[0] <= 0){
            return null;
        }
        byte[] aid = new byte[len[0]];
        System.arraycopy(tlv,0,aid,0,len[0]);
        return aid;
    }

    @Override
    public boolean deleteSpecifiedAid(CardInterface cardIntf, AIDEntry aidEntry) {
        if(cardIntf==null||aidEntry==null){
            return false;
        }
        int ret = mNapiEmvL3.NAPI_L3LoadAIDConfig(cardIntf.ordinal(),aidEntry,null,null, ConfigMode.REMOVE.ordinal());
        Logger.d(TAG, "deleteSpecifiedAid: ret="+ret);
        if(ret < 0){
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteAid(CardInterface cardIntf) {
        if(cardIntf==null){
            return false;
        }
        int ret = mNapiEmvL3.NAPI_L3LoadAIDConfig(cardIntf.ordinal(),null,null,null, ConfigMode.FLUSH.ordinal());
        Logger.d(TAG, "deleteAid: ret="+ret);
        if(ret < 0){
            return false;
        }
        return true;
    }

    @Override
    public boolean addCAPublicKey(CAPKEntry capk) {
        if(capk==null){
            return false;
        }
        int ret = mNapiEmvL3.NAPI_L3LoadCAPK(capk,ConfigMode.UPDATE.ordinal());
        Logger.d(TAG, "addCAPublicKey: ret="+ret);
        if(ret < 0){
            return false;
        }
        return true;
    }

    @Override
    public CAPKEntry getSpecifiedCAPublicKey(byte[] rid, byte index) {
        if(rid==null){
            return null;
        }
        if(rid.length!=5){
            return null;
        }
        CAPKEntry capk = new CAPKEntry();
        System.arraycopy(rid,0,capk.rid,0,rid.length);
        capk.index = index;
        int ret = mNapiEmvL3.NAPI_L3LoadCAPK(capk,ConfigMode.GET.ordinal());
        Logger.d(TAG, "getSpecifiedCAPublicKey: ret="+ret);
        if(ret < 0){
            return null;
        }
        return capk;
    }

    @Override
    public boolean deleteSpecifiedCAPublicKey(byte[] rid, byte index) {
        if(rid==null){
            return false;
        }
        if(rid.length!=5){
            return false;
        }
        CAPKEntry capk = new CAPKEntry();
        System.arraycopy(rid,0,capk.rid,0,rid.length);
        capk.index = index;
        int ret = mNapiEmvL3.NAPI_L3LoadCAPK(capk,ConfigMode.REMOVE.ordinal());
        Logger.d(TAG, "deleteSpecifiedCAPublicKey: ret="+ret);
        if(ret < 0){
            return false;
        }
        return true;
    }

    @Override
    public boolean deleteAllCAPublicKey() {
        CAPKEntry capk = new CAPKEntry();
        int ret = mNapiEmvL3.NAPI_L3LoadCAPK(capk,ConfigMode.FLUSH.ordinal());
        Logger.d(TAG, "deleteAllCAPublicKey: ret="+ret);
        if(ret < 0){
            return false;
        }
        return true;
    }

    @Override
    public boolean loadRevocationList(CRLEntry crl, ConfigMode mode) {
        if(crl == null || mode == null){
            return false;
        }
        int ret = mNapiEmvL3.NAPI_L3LoadRevocationList(crl,mode.ordinal());
        Logger.d(TAG, "loadRevocationList: ret="+ret);
        if(ret < 0){
            return false;
        }
        return true;
    }

    @Override
    public boolean loadExceptionList(ExceptionEntry exceptionList, ConfigMode mode) {
        if(exceptionList == null || mode == null){
            return false;
        }
        int ret = mNapiEmvL3.NAPI_L3LoadExceptionList(exceptionList,mode.ordinal());
        Logger.d(TAG, "loadExceptionList: ret="+ret);
        if(ret < 0){
            return false;
        }
        return true;
    }

    @Override
    public TransactionResult performTransaction(byte[] data) {
        if(data == null){
            return null;
        }
        int[] result = new int[1];
        int ret = mNapiEmvL3.NAPI_L3PerformTransaction(data,data.length,result);
        int tranResult = result[0];
        Logger.d(TAG, "performTransaction: ret="+ret+" result="+tranResult);
        if(ret < 0){
            return null;
        }
        TransactionResult transactionResult=null;
        if(tranResult== TransactionResult.OK.ordinal()){
            transactionResult = TransactionResult.OK;
        }else if(tranResult == TransactionResult.TERMINATE.ordinal()){
            transactionResult = TransactionResult.TERMINATE;
        }else if(tranResult == TransactionResult.TRY_ANOTHER.ordinal()){
            transactionResult = TransactionResult.TRY_ANOTHER;
        }else if(tranResult == TransactionResult.DECLINE.ordinal()){
            transactionResult = TransactionResult.DECLINE;
        }else if(tranResult == TransactionResult.APPROVED.ordinal()){
            transactionResult = TransactionResult.APPROVED;
        }else if(tranResult == TransactionResult.ONLINE.ordinal()){
            transactionResult = TransactionResult.ONLINE;
        }
        return transactionResult;
    }

    @Override
    public TransactionResult completeTransaction(byte[] data) {
        if(data == null){
            return null;
        }
        int[] result = new int[1];
        int ret = mNapiEmvL3.NAPI_L3CompleteTransaction(data,data.length,result);
        int tranResult = result[0];
        Logger.d(TAG, "completeTransaction: ret="+ret+" result="+tranResult);
        if(ret < 0){
            return null;
        }
        TransactionResult transactionResult=null;
        if(tranResult== TransactionResult.OK.ordinal()){
            transactionResult = TransactionResult.OK;
        }else if(tranResult == TransactionResult.TERMINATE.ordinal()){
            transactionResult = TransactionResult.TERMINATE;
        }else if(tranResult == TransactionResult.TRY_ANOTHER.ordinal()){
            transactionResult = TransactionResult.TRY_ANOTHER;
        }else if(tranResult == TransactionResult.DECLINE.ordinal()){
            transactionResult = TransactionResult.DECLINE;
        }else if(tranResult == TransactionResult.APPROVED.ordinal()){
            transactionResult = TransactionResult.APPROVED;
        }else if(tranResult == TransactionResult.ONLINE.ordinal()){
            transactionResult = TransactionResult.ONLINE;
        }
        return transactionResult;
    }

    @Override
    public boolean terminateTransaction() {
        int ret = mNapiEmvL3.NAPI_L3TerminateTransaction();
        Logger.d(TAG, "terminateTransaction: ret="+ret);
        if(ret < 0){
            return false;
        }
        return true;
    }

    @Override
    public boolean cancelTransaction() {
        int ret = mNapiEmvL3.NAPI_L3CancelTransaction();
        Logger.d(TAG, "cancelTransaction: ret="+ret);
        if(ret < 0){
            return false;
        }
        return true;
    }

    @Override
    public boolean setData(int tag, byte[] data) {
        if(data == null || (data != null && data.length <=0)){
            return false;
        }
        int ret = mNapiEmvL3.NAPI_L3SetData(tag,data,data.length);
        Logger.d(TAG, "setData: ret="+ret);
        if(ret < 0){
            return false;
        }
        return true;
    }

    @Override
    public byte[] getData(EmvData type) {
        if(type == null){
            return null;
        }
        byte[] value = new byte[152];
        int[] realLen = new int[1];
        int ret = mNapiEmvL3.NAPI_L3GetData(type.ordinal(),(byte)0,value,value.length,realLen);
        Logger.d(TAG, "getData: ret="+ret);
        if(ret < 0){
            return null;
        }
        byte[] realValue = new byte[realLen[0]];
        System.arraycopy(value,0,realValue,0,realLen[0]);
        return realValue;
    }

    @Override
    public boolean setTLVData(byte[] tlvList) {
        if(tlvList == null){
            return false;
        }
        int ret = mNapiEmvL3.NAPI_L3SetTLVData(tlvList,tlvList.length);
        Logger.d(TAG, "setTLVData: ret="+ret);
        if(ret < 0){
            return false;
        }
        return true;
    }

    @Override
    public byte[] getTlvData(ArrayList<Integer> tagList, boolean isPackZeroLen) {
        if(tagList == null || (tagList!=null&&tagList.size()<=0)){
            return null;
        }
        String tags=null;
        for(int i = 0; i < tagList.size(); ++i) {
            tags += String.format("%2x",tagList.get(i));
        }
        int isPackZeroFlag = 0;
        if(isPackZeroLen){
            isPackZeroFlag = 1;
        }
        byte[] tlvValue = new byte[1024];
        int[] tlvLen = new int[1];
        int ret = mNapiEmvL3.NAPI_L3GetTlvData(ISOUtils.hex2byte(tags),tagList.size(), (byte) 0,tlvValue,tlvValue.length,isPackZeroFlag,tlvLen);
        Logger.d(TAG, "getTlvData: ret="+ret);
        if(ret < 0){
            return null;
        }
        byte[] tlvData = new byte[tlvLen[0]];
        System.arraycopy(tlvValue,0,tlvData,0,tlvLen[0]);
        return tlvData;
    }

    @Override
    public boolean setDebugMode(int level) {
        int ret = mNapiEmvL3.NAPI_L3SetDebugMode(level);
        Logger.d(TAG, "setDebugMode: ret="+ret);
        if(ret < 0){
            return false;
        }
        return true;
    }

    @Override
    public String getVersion(EmvModuleVersion module) {
        byte[] version = new byte[64];
        int ret = mNapiEmvL3.NAPI_L3GetVersion(module.ordinal(),version);
        Logger.d(TAG, "getVersion: ret="+ret);
        if(ret < 0){
            return null;
        }
        return new String(version).trim();
    }
}
