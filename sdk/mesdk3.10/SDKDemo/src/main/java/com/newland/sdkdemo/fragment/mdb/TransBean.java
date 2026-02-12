package com.newland.sdkdemo.fragment.mdb;

public class TransBean {
    private int result = 0;

    private boolean isVending = false;

    public void setResult(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public boolean isVending() {
        return isVending;
    }

    public void setVending(boolean vending) {
        isVending = vending;
    }
}
