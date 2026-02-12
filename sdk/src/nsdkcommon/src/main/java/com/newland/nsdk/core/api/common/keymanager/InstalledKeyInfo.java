package com.newland.nsdk.core.api.common.keymanager;

import com.newland.nsdk.core.api.common.utils.ISOUtils;

/**
 * Installed key info.
 */
public class InstalledKeyInfo {
    int index;
    int type;
    int usage;
    byte[] kcv;

    /**
     * Gets key index.
     *
     * @return Key index.
     */
    public int getIndex() {
        return index;
    }

    /**
     * Sets key index.
     *
     * @param index Key index.
     */
    public void setIndex(int index) {
        this.index = index;
    }

    /**
     * Gets key type.
     *
     * @return Key type.
     */
    public int getType() {
        return type;
    }

    /**
     * Sets key type.
     *
     * @param type Key type.
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * Gets key usage.
     *
     * @return Key usage.
     */
    public int getUsage() {
        return usage;
    }

    /**
     * Sets key usage.
     *
     * @param usage Key usage.
     */
    public void setUsage(int usage) {
        this.usage = usage;
    }

    /**
     * Gets KCV.
     *
     * @return KCV.
     */
    public byte[] getKCV() {
        return kcv;
    }

    /**
     * Sets KCV.
     *
     * @param kcv KCV.
     */
    public void setKCV(byte[] kcv) {
        this.kcv = kcv;
    }

    @Override
    public String toString(){
        return String.format("{\"index\":\"%d\",\"type\":%d,\"usage\":\"%d\",\"kcv\":\"%s\"}", index, type, usage, ISOUtils.hexString(kcv));
    }
}
