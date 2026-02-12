package com.newland.sdk.mtype.common;

/**
 * Author by bxy, Date on 2019/8/9 0009.
 */
public class ErrorMsg {

    private String errCode;

    private String errMsg;

    private String otherMsg;

    public String getErrCode() {
        return errCode;
    }

    public String getErrMsg() {
        return errMsg;
    }

    public String getOtherMsg() {
        return otherMsg;
    }

    public void setErrCode(String errCode) {
        this.errCode = errCode;
    }

    public void setErrMsg(String errMsg) {
        this.errMsg = errMsg;
    }

    public void setOtherMsg(String otherMsg) {
        this.otherMsg = otherMsg;
    }
}
