package com.newland.nsdk.core.api.external.cardemulator;

public class EmulateConfig {
    byte[] uid;
    /**
     * For T2T card, the memory size shall between 48 and 992, which shall be multiply of 8. For T4T card, memory size shall between 1 and 2048.
     */
    int memorySize;

    /**
     * SAK and ATS could only be readable
     */
    byte sak;
    byte[] ats;

    public byte[] getUid() {
        return uid;
    }

    public void setUid(byte[] uid) {
        this.uid = uid;
    }

    public int getMemorySize() {
        return memorySize;
    }

    public void setMemorySize(int memorySize) {
        this.memorySize = memorySize;
    }

    public byte getSak() {
        return sak;
    }

    public void setSak(byte sak) {
        this.sak = sak;
    }

    public byte[] getAts() {
        return ats;
    }

    public void setAts(byte[] ats) {
        this.ats = ats;
    }
}
