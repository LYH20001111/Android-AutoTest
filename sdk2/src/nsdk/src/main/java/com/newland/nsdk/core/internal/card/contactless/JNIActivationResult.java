package com.newland.nsdk.core.internal.card.contactless;

import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;

public class JNIActivationResult extends ActivationResult {
    private int uidLen;
    private int atqaLen;
    private int atsLen;
    private int atqbLen;
    private int sakLen;

    public JNIActivationResult(){
        setUID(new byte[50]);
        setATQA(new byte[50]);
        setATS(new byte[256]);
        setATQB(new byte[256]);
        setSAK(new byte[50]);
    }

    public void setUIDLen(int uidLen) {
        this.uidLen = uidLen;
    }
    public void setAtqaLen(int atqaLen) {
        this.atqaLen = atqaLen;
    }
    public void setAtsLen(int atsLen) {
        this.atsLen = atsLen;
    }
    public void setAtqbLen(int atqbLen) {
        this.atqbLen = atqbLen;
    }
    public void setSakLen(int sakLen) {
        this.sakLen = sakLen;
    }

    public int getUidLen() {return uidLen;}
    public int getAtqaLen() {
        return atqaLen;
    }
    public int getAtsLen() {return atsLen;}
    public int getAtqbLen() {return atqbLen;};
    public int getSakLen() {
        return sakLen;
    }
}
