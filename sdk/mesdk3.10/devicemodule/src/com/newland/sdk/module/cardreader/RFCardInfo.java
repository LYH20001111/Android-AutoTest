package com.newland.sdk.module.cardreader;

/**
 * Created by youjf on 2019/7/25 16:46
 */
public class RFCardInfo {
    /**
     * 0x20 CPU Card
     * 0x08  S50 Card
     * 0x18  s70 Card
     * 0x28  s50_pro Card
     * 0x38  s70_pro Card
     */
    private byte SAK;
    private byte[] SNR;
    private byte[] IDmPMm;
    private byte[] atqa;

    public RFCardInfo(byte SAK, byte[] snr, byte[] IDmPMm) {
        this.SAK = SAK;
        this.SNR = snr;
        this.IDmPMm = IDmPMm;
    }

    /**
     * <p>(conditional) the Select Acknowledge returned by mifare card.</p>
     * <p>0x20 CPU Card;0x08  S50 Card;0x18  s70 Card;0x28  s50_pro Card;0x38  s70_pro Card;</p>
     *
     * @return
     */
    public byte getSAK() {
        return SAK;
    }

    /**
     * <p>the Card Serial Number returned by mifare card.</p>
     *
     * @return
     */
    public byte[] getSNR() {
        return SNR;
    }


    /**
     * (conditional) the Manufacture ID (IDm) and Manufacture Parameter (PMm) returned by felica card.
     *
     * @return
     */
    public byte[] getIDmPMm() {
        return IDmPMm;
    }

    public byte[] getAtqa() {
        return atqa;
    }

    public void setAtqa(byte[] atqa) {
        this.atqa = atqa;
    }
}
