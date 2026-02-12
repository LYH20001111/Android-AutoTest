package com.newland.nsdkdemo.common.bean;

import java.io.Serializable;
import java.util.List;

public class KeyResponse implements Serializable {

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

    private int KN;
    private String KDH_RN;
    private String PKG_NAME;
    private String PKG_ID;
    private List<KEY_LIST> KEY_LIST;
    private String SIG;
    private String S_TOKEN;

    public int getKN() {
        return KN;
    }

    public void setKN(int KN) {
        this.KN = KN;
    }

    public String getKDH_RN() {
        return KDH_RN;
    }

    public void setKDH_RN(String KDH_RN) {
        this.KDH_RN = KDH_RN;
    }

    public String getPKG_NAME() {
        return PKG_NAME;
    }

    public void setPKG_NAME(String PKG_NAME) {
        this.PKG_NAME = PKG_NAME;
    }

    public String getPKG_ID() {
        return PKG_ID;
    }

    public void setPKG_ID(String PKG_ID) {
        this.PKG_ID = PKG_ID;
    }

    public List<KEY_LIST> getKEY_LIST() {
        return KEY_LIST;
    }

    public void setKEY_LIST(List<KEY_LIST> KEY_LIST) {
        this.KEY_LIST = KEY_LIST;
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

    public class KEY_LIST {

        private int index;
        private String kcv;
        private String name;
        private int type;
        private int usage;
        private String value;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getKcv() {
            return kcv;
        }

        public void setKcv(String kcv) {
            this.kcv = kcv;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public int getType() {
            return type;
        }

        public void setType(int type) {
            this.type = type;
        }

        public int getUsage() {
            return usage;
        }

        public void setUsage(int usage) {
            this.usage = usage;
        }

        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }

        @Override
        public String toString() {
            return String.format("{\"index\":%d,\"kcv\":\"%s\",\"name\":\"%s\",\"type\":%d,\"usage\":%d,\"value\":\"%s\"}", index, kcv, name, type, usage, value);
        }
    }

    @Override
    public String toString() {
        return String.format("RequestResponse{STATUS='%s', MSG='%s', KN=%d, KDH_RN='%s', PKG_NAME='%s', PKG_ID=%s, KEY_LIST=%s, SIG='%s', S_TOKEN='%s'}", STATUS, MSG, KN, KDH_RN, PKG_NAME, PKG_ID, KEY_LIST, SIG, S_TOKEN);
    }
}
