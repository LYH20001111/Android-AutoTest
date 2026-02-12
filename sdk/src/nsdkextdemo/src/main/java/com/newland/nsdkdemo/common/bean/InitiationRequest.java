package com.newland.nsdkdemo.common.bean;

import java.io.Serializable;
import java.util.List;

public class InitiationRequest implements Serializable {

    private String VER;
    private String SN;
    private String S_CERT;
    private List<CA_CHAIN> CA_CHAIN;
    private int L_TYPE;
    private String LOCATION;

    public String getVER() {
        return VER;
    }

    public void setVER(String VER) {
        this.VER = VER;
    }

    public String getSN() {
        return SN;
    }

    public void setSN(String SN) {
        this.SN = SN;
    }

    public String getS_CERT() {
        return S_CERT;
    }

    public void setS_CERT(String s_CERT) {
        this.S_CERT = s_CERT;
    }

    public List<CA_CHAIN> getCA_CHAIN() {
        return CA_CHAIN;
    }

    public void setCA_CHAIN(List<CA_CHAIN> CA_CHAIN) {
        this.CA_CHAIN = CA_CHAIN;
    }

    public int getL_TYPE() {
        return L_TYPE;
    }

    public void setL_TYPE(int l_TYPE) {
        this.L_TYPE = l_TYPE;
    }

    public String getLOCATION() {
        return LOCATION;
    }

    public void setLOCATION(String LOCATION) {
        this.LOCATION = LOCATION;
    }

    @Override
    public String toString() {
        return String.format("InitationData{VER='%s', SN='%s', S_CERT='%s', CA_CHAIN=%s, L_TYPE=%d, LOCATION='%s'}", VER, SN, S_CERT, CA_CHAIN, L_TYPE, LOCATION);
    }

    public class CA_CHAIN {
        private String name;
        private String type;
        private String vlaue;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getVlaue() {
            return vlaue;
        }

        public void setVlaue(String vlaue) {
            this.vlaue = vlaue;
        }

        @Override
        public String toString() {
            return String.format("CA_CHAIN{name='%s', type='%s', vlaue='%s'}", name, type, vlaue);
        }
    }
}
