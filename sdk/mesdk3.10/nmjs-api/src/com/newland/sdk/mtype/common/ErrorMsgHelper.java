package com.newland.sdk.mtype.common;

import android.util.Log;

import com.newland.intelligent.jni.JniCmdInterface;

import java.util.Arrays;

/**
 * Author by bxy, Date on 2019/8/21 0021.
 */
public class ErrorMsgHelper {
    private static ErrorMsgHelper helper;
    private ErrorMsgHelper(){}
    public static ErrorMsgHelper getInstance(){
        if(helper == null){
            synchronized (ErrorMsgHelper.class){
                if(helper == null){
                    helper = new ErrorMsgHelper();
                }
            }
        }
        return helper;
    }

    public ErrorMsg getErrorMsg(int cmd){
        ErrorMsg msg = new ErrorMsg();
        byte[] errCode = new byte[64];
        byte[] errMsg = new byte[128];
        byte[] otherMsg = new byte[128];
        int ret = JniCmdInterface.getInstance().getErrInfo(cmd,errCode,errMsg,otherMsg);
        if(ret != 0){
            return msg;
        }
        msg.setErrCode(new String(errCode).trim());
        msg.setErrMsg(new String(errMsg).trim());
        msg.setOtherMsg(new String(otherMsg).trim());
        return msg;
    }

}
