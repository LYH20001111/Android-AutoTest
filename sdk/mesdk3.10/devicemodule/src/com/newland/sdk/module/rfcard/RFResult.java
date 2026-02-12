package com.newland.sdk.module.rfcard;

/**
 * <p>RF card search result</p>
 * <p>Different card types have different results.</p>
 * <li>{@link RFCardType#ACARD}: {@link #SNR#ATQA#SAK#ATS} </li>
 * <li>{@link RFCardType#BCARD}: {@link #SNR} </li>
 * <li>{@link RFCardType#M0CARD}: {@link #SNR#SAK} </li>
 * <li>{@link RFCardType#M1CARD}: {@link #SNR#SAK} </li>
 * <li>{@link RFCardType#FELICA_CARD}: {@link #IDmPMm} </li>
 *
 * @author linsi
 * @since V3.10.01
 */
public class RFResult {
    private RFCardType rfcardType;
    private byte[] SNR;
    private byte[] ATQA;
    private byte SAK;
    private byte[] IDmPMm;
    private byte[] ATS;

    /**
     * get the specific card type detected.
     *
     * @return RFCard type {@link RFCardType}
     */
    public RFCardType getRfcardType() {
        return rfcardType;
    }

    public void setRfcardType(RFCardType rfcardType) {
        this.rfcardType = rfcardType;
    }

    /**
     * <p>(contiditonal)the Card Serial Number returned by mifare card.</p>
     *
     * @return SNR
     */
    public byte[] getSNR() {
        return SNR;
    }

    /**
     * <p>(contiditonal)Answer To Request acc. to ISO/IEC 14443-4.</p>
     * <p>It will be work when tap the type A card. {@link RFCardType#ACARD}.</p>
     *
     * @return ATQA
     */
    public byte[] getATQA() {
        return ATQA;
    }

    /**
     * <p>(conditional) the Select Acknowledge returned by mifare card.</p>
     * <p>0x20 CPU Card;0x08  S50 Card;0x18  s70 Card;0x28  s50_pro Card;0x38  s70_pro Card;</p>
     *
     * @return SAK
     */
    public byte getSAK() {
        return SAK;
    }

    /**
     * <p>(contiditonal)Get the IDm And PMm.</p>
     * <p>It will be work when tap the type A card. {@link RFCardType#FELICA_CARD}.</p>
     *
     * @return ATQA
     */
    public byte[] getIDmPMm() {
        return IDmPMm;
    }

    /**
     * <p>(contiditonal)Answer To Select acc. to ISO/IEC 14443-4.</p>
     * <p>It will be work when tap the type A card. {@link RFCardType#ACARD}.</p>
     *
     * @return ATS
     */
    public byte[] getATS() {
        return ATS;
    }

    public RFResult(RFCardType rfcardType, byte[] snr, byte[] atqa, byte sak, byte[] iDmAndPmm, byte[] ats) {
        this.rfcardType = rfcardType;
        this.SNR = snr;
        this.ATQA = atqa;
        this.SAK = sak;
        this.IDmPMm = iDmAndPmm;
        this.ATS = ats;
    }
}
