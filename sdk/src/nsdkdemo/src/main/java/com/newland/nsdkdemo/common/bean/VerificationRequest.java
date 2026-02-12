package com.newland.nsdkdemo.common.bean;

import java.io.Serializable;
import java.util.List;

public class VerificationRequest implements Serializable {

    private List<VerificationRequest.RESULT> RESULT;

    private String KRD_RN;
    private String SIG;
    private String S_TOKEN;

    public List<VerificationRequest.RESULT> getRESULT() {
        return RESULT;
    }

    public void setRESULT(List<VerificationRequest.RESULT> RESULT) {
        this.RESULT = RESULT;
    }

    public String getKRD_RN() {
        return KRD_RN;
    }

    public void setKRD_RN(String KRD_RN) {
        this.KRD_RN = KRD_RN;
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

    public class RESULT {
        private String name;
        private int result;
        private String errmsg;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getResult() {
            return result;
        }

        public void setResult(int result) {
            this.result = result;
        }

        public String getErrmsg() {
            return errmsg;
        }

        public void setErrmsg(String errmsg) {
            this.errmsg = errmsg;
        }

        @Override
        public String toString() {
            return String.format("{\"name\":\"%s\",\"result\":%d,\"errmsg\":\"%s\"}", name, result, errmsg);

        }
    }

    @Override
    public String toString() {
        return String.format("VerificationData{RESULT=%s, KRD_RN='%s', SIG='%s', S_TOKEN='%s'}", RESULT, KRD_RN, SIG, S_TOKEN);
    }
}
