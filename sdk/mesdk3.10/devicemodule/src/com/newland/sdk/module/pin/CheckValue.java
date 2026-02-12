package com.newland.sdk.module.pin;

/**
 * Author by wuhh, Date on 2020/3/2.
 */
public class CheckValue {
    /**
     * the kcv mode. {@link KcvMode}
     */
    private KcvMode kcvMode;
    /**
     * the kcv value.if value is null,it means no check.
     */
    private byte[] kcvValue;

    /**
     *
     * @param kcvMode {@link KcvMode}
     * @param kcvValue
     */
    public CheckValue(KcvMode kcvMode, byte[] kcvValue){
        this.kcvMode = kcvMode;
        this.kcvValue = kcvValue;
    }

    /**
     * KcvMode is ZERO {@link KcvMode#ZERO}
     * @param kcvValue kcv value.
     */
    public CheckValue(byte[] kcvValue){
        this.kcvMode = KcvMode.ZERO;
        this.kcvValue = kcvValue;
    }

    /**
     * get kcv mode.
     * @return
     */
    public KcvMode getKcvMode() {
        return kcvMode;
    }

    /**
     * get kcv value.
     * @return
     */
    public byte[] getKcvValue() {
        return kcvValue;
    }
}
