package com.newland.nsdkdemo.common.bean;

import android.support.annotation.NonNull;

import java.io.Serializable;

public class KeyRequest implements Serializable {

    private int MAX_KN;
    private String KRD_RN;
    private int SK_TYPE;

    private String SK;
    private int PAD;
    private String SIG;
    private String S_TOKEN;

    public int getMAX_KN() {
        return MAX_KN;
    }

    public void setMAX_KN(int MAX_KN) {
        this.MAX_KN = MAX_KN;
    }

    public String getKRD_RN() {
        return KRD_RN;
    }

    public void setKRD_RN(String KRD_RN) {
        this.KRD_RN = KRD_RN;
    }

    public int getSK_TYPE() {
        return SK_TYPE;
    }

    public void setSK_TYPE(int SK_TYPE) {
        this.SK_TYPE = SK_TYPE;
    }

    public String getSK() {
        return SK;
    }

    public void setSK(String SK) {
        this.SK = SK;
    }

    public int getPAD() {
        return PAD;
    }

    public void setPAD(int PAD) {
        this.PAD = PAD;
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

    @NonNull
    @Override
    public String toString() {
        return String.format("KeyRequest{MAX_KN='%d', KRD_RN='%s', SK_TYPE='%d', SK=%s, PAD=%d, SIG='%s', S_TOKEN='%s'}",
                MAX_KN, KRD_RN, SK_TYPE, SK, PAD, SIG, S_TOKEN);
    }
}
