//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.newland.sdk.me.module.externalPininput;

import java.util.Arrays;

public class EmvTLV {
    private int tag;
    private int len;
    private byte[] value;

    public EmvTLV() {
    }

    public int getTag() {
        return this.tag;
    }

    public void setTag(int tag) {
        this.tag = tag;
    }

    public int getLen() {
        return this.len;
    }

    public void setLen(int len) {
        this.len = len;
    }

    public byte[] getValue() {
        return this.value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public String toString() {
        return "EmvTLV{tag=" + this.tag + ", len=" + this.len + ", value=" + Arrays.toString(this.value) + '}';
    }
}
