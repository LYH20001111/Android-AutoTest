package com.newland.sdk.me.module.emvl3.external;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/30
 */
public class Capk {

    public int load(CapkEntry capk) {
        return -1;
    }

    public CapkEntry get(byte[] rid, int index) {
        return null;
    }

    public boolean remove(byte[] rid, int index) {
        return false;
    }

    public boolean flush() {
        return false;
    }

    public int getCapkCount() {
        return -1;
    }
}
