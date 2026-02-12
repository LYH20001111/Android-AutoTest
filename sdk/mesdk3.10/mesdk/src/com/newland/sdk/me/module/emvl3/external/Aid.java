package com.newland.sdk.me.module.emvl3.external;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/30
 */
public class Aid {
    public static final int CONTACT = 1;
    public static final int CONTACTLESS = 2;

    public Aid(int aidInterface) {

    }

    public int loadTerminalConfig(byte[] config) {
       return -1;
    }

    public byte[] getTerminalConfig() {
        return null;
    }

    public int loadAID(byte[] config) {
        return -1;
    }

    public byte[] getAID(AidEntry aidentry) {
        return null;
    }

    public boolean remove(AidEntry aidentry) {
        return false;
    }

    public boolean flush() {
        return false;
    }

    public int getAidCount() {
        return -1;
    }

    public int getConfigFromFile(byte[] tlvData, int[] tlvLen, int[] aidLen, int offset) {
        return -1;
    }
}
