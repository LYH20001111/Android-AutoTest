package com.newland.nsdkdemo.common.bean;

import java.io.Serializable;

public class InitiationResponse implements Serializable {

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
    // 0 – TDES (24 bytes) 1 – AES
    private int SK_TYPE;

    private String S_CERT;
    private String E_CERT;
//    private List<InitationData.CA_CHAIN> CA_CHAIN;
    private String S_TOKEN;

    public String getKDH_RN() {
        return KDH_RN;
    }

    public void setKDH_RN(String KDH_RN) {
        this.KDH_RN = KDH_RN;
    }

    public int getSK_TYPE() {
        return SK_TYPE;
    }

    public void setSK_TYPE(int SK_TYPE) {
        this.SK_TYPE = SK_TYPE;
    }

    public String getS_CERT() {
        return S_CERT;
    }

    public void setS_CERT(String s_CERT) {
        S_CERT = s_CERT;
    }

    public String getE_CERT() {
        return E_CERT;
    }

    public void setE_CERT(String e_CERT) {
        E_CERT = e_CERT;
    }

//    public List<InitationData.CA_CHAIN> getCA_CHAIN() {
//        return CA_CHAIN;
//    }
//
//    public void setCA_CHAIN(List<InitationData.CA_CHAIN> CA_CHAIN) {
//        this.CA_CHAIN = CA_CHAIN;
//    }

    public String getS_TOKEN() {
        return S_TOKEN;
    }

    public void setS_TOKEN(String s_TOKEN) {
        S_TOKEN = s_TOKEN;
    }

    @Override
    public String toString() {
        return String.format("InitationResponse{STATUS='%s', MSG='%s', KDH_RN='%s', SK_TYPE=%d, S_CERT='%s', E_CERT='%s', S_TOKEN='%s'}", STATUS, MSG, KDH_RN, SK_TYPE, S_CERT, E_CERT, S_TOKEN);
    }
}
