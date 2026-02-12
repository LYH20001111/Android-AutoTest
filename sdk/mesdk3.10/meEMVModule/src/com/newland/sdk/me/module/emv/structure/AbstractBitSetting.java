package com.newland.sdk.me.module.emv.structure;

/**
 * AbstractBitSetting for emv <p>
 * {@link ICS}
 */
public abstract class AbstractBitSetting {

    private byte[] container;

    private int maxBytesLen = 0;

    public AbstractBitSetting(int maxBytesLen) {
        this.maxBytesLen = maxBytesLen;
        this.container = new byte[maxBytesLen];
    }

    public AbstractBitSetting(int maxBytesLen, byte[] value) {
        this.maxBytesLen = maxBytesLen;
        this.container = value;
    }

    public static class BitTag {

        private int byteIndex;
        private int bitValue;

        public BitTag(int tagvalue) {
            byteIndex = (tagvalue >> 8) & 0xFF;
            bitValue = tagvalue & 0xFF;
        }
    }

    public void set(BitTag tag) {
        container[tag.byteIndex] = (byte) (container[tag.byteIndex] | tag.bitValue);
    }

    public void unset(BitTag tag) {
        container[tag.byteIndex] = (byte) (container[tag.byteIndex] & ~tag.bitValue);
    }

    public boolean isSupported(BitTag tag) {
        return (container[tag.byteIndex] & tag.bitValue) > 0;
    }

    public byte[] toByteArray() {
        byte[] rslt = new byte[maxBytesLen];
        System.arraycopy(container, 0, rslt, 0, container.length);
        return rslt;
    }

    public int getLength() {
        return maxBytesLen;
    }

}
