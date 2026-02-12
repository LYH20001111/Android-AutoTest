package com.newland.sdk.me.module.emvl3.external;

import java.util.ArrayList;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/30
 */
public class EmvL3Module {

    public EmvL3Module() {
    }

    public int init(String filePath, byte[] configuration, EmvL3Listener listener) {
        return -1;
    }

    public void configSet(byte[] b, int offset) {
    }

    public void configUnSet(byte[] b, int offset) {
    }

    public boolean getConfig(int opt) {
        return false;
    }

    public String getVersion(int module) {
        return null;
    }

    public void setDebugMode(int debugLv) {
    }

    static boolean checkL3Tag(int tag) {
        return false;
    }

    private int initTransaction() {
       return -1;
    }

    public int performTransaction(byte[] data) {
        return -1;
    }

    public int completeTransaction(byte[] data) {
        return -1;
    }

    public int terminateTransacion() {
       return -1;
    }

    public int getTransResult() {
        return -1;
    }

    public byte[] getData(int tag) {
        return null;
    }

    public int setData(int tag, byte[] val) {
        return -1;
    }

    public byte[] getListData(ArrayList<Integer> tagList, boolean isPackZeroLen) {
        return null;
    }

    private int getUICardData(int input) {
        return -1;
    }

    private void cardUIEvent(int uiEevent) {
    }

    public int detectCard(int input, int timeout, int[] currentInterface) {
        return -1;
    }

    public void sendDetectMessage(int messageID) {
    }

    private byte[] getPAN() {
        return null;
    }

    private String getPanFrom5A() {
        return null;
    }

    private String getPanFrom57() {
        return null;
    }

    private byte[] getTrack1() {
        return null;
    }

    private byte[] getTrack2() {
        return null;
    }

    private byte[] getTrack3() {
        return null;
    }

    private byte[] getExpireDate() {
        return null;
    }

    private byte[] getServiceCode() {
        return null;
    }

    private byte[] getCardHolderName() {
        return null;
    }
}
