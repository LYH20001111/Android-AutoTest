package com.newland.nsdkdemo.common.bean;

import java.io.Serializable;

public class VerificationResponse implements Serializable {

    private String STATUS;

    private String MSG;
    public String getSTATUS() {
        return STATUS;
    }

    public void setSTATUS(String STATUS) {
        this.STATUS = STATUS;
    }

    public String getMSG() {
        return MSG;
    }

    public void setMSG(String MSG) {
        this.MSG = MSG;
    }

    private String KDH_RN;
    private String SIG;
    private String S_TOKEN;

    public String getKDH_RN() {
        return KDH_RN;
    }

    public void setKDH_RN(String KDH_RN) {
        this.KDH_RN = KDH_RN;
    }

    public String getSIG() {
        return SIG;
    }

    public void setSIG(String SIG) {
        this.SIG = SIG;
    }

    public String getS_TOKEN() {
        return S_TOKEN;
    }

    public void setS_TOKEN(String s_TOKEN) {
        S_TOKEN = s_TOKEN;
    }

    @Override
    public String toString() {
        return String.format("VerificationResponse{STATUS='%s', MSG='%s', KDH_RN='%s', SIG='%s', S_TOKEN='%s'}", STATUS, MSG, KDH_RN, SIG, S_TOKEN);
    }
}
