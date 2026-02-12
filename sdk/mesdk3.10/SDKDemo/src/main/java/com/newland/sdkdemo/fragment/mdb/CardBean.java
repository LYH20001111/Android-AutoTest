package com.newland.sdkdemo.fragment.mdb;

public class CardBean {
    private int result = 0;

    private boolean isReadCard = false;

    public void setResult(int result) {
        this.result = result;
    }

    public int getResult() {
        return result;
    }

    public boolean isReadCard() {
        return isReadCard;
    }

    public void setReadCard(boolean readCard) {
        isReadCard = readCard;
    }
}
