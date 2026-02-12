package com.newland.nsdk.core.internal.cardreader;

/**
 * Author by wuhh, Date on 2020/2/11.
 */
public class CardReaderResult {
    private byte cardInterface;
    private byte contactlessCardType;
    private MagResult magResult = new MagResult();
    private ContactlessResult contactlessResult = new ContactlessResult();

    public MagResult getMagResult() {
        return magResult;
    }

    public byte getCardInterface() {
        return cardInterface;
    }

    public byte getContactlessCardType() {
        return contactlessCardType;
    }

    public ContactlessResult getContactlessResult() {
        return contactlessResult;
    }
}
