package com.newland.sdkdemo.fragment.dock.emv;

/**
 * Author by wuhh, Date on 2020/3/19.
 */
public class TransParam {

    private int cardInputMode;
    private int transType;
    private long amount;
    private long amountOther;
    private Integer currentCardInterfaces;//当前已经上电的卡类型


    public int getCardInputMode() {
        return cardInputMode;
    }

    public void setCardInputMode(int cardInputMode) {
        this.cardInputMode = cardInputMode;
    }

    public int getTransType() {
        return transType;
    }

    public void setTransType(int transType) {
        this.transType = transType;
    }

    public long getAmount() {
        return amount;
    }

    public void setAmount(long amount) {
        this.amount = amount;
    }

    public long getAmountOther() {
        return amountOther;
    }

    public void setAmountOther(long amountOther) {
        this.amountOther = amountOther;
    }

    public Integer getCurrentCardInterfaces() {
        return currentCardInterfaces;
    }

    public void setCurrentCardInterfaces(Integer currentCardInterfaces) {
        this.currentCardInterfaces = currentCardInterfaces;
    }
}
