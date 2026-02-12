package com.newland.sdk.me.module.cardreader;

import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.rfcard.RFCardType;


/**
 * Card reader return result<p>
 *
 *
 */
public class OpenCardReaderResult {
    private CardType[] responseCardTypes;
    private RFCardType responseRFCardType;
    private boolean isMSDDataCorrectly = true;
    private byte[] snr;
    private byte[] iDmAndPmm;
    /**
     * 0x20 CPU Card
     * 0x08  S50 Card
     * 0x18  s70 Card
     * 0x28  s50_pro Card
     * 0x38  s70_pro Card
     */
    private byte SAK;
    private byte[] atqa;
    public OpenCardReaderResult(CardType[] responseCardTypes, RFCardType responseRFCardType, boolean isMSDDataCorrectly, byte[] snr, byte SAK, byte[] iDmAndPmm) {
        this.responseCardTypes = responseCardTypes;
        this.responseRFCardType = responseRFCardType;
        this.isMSDDataCorrectly = isMSDDataCorrectly;
        this.snr = snr;
        this.SAK = SAK;
        this.iDmAndPmm = iDmAndPmm;
    }

    /**
     * Get the card type in card reader response (swiping, insertion or contactless card)
     *
     * @return
     * @since 2.0.0
     */
    public CardType[] getResponseCardTypes() {
        return responseCardTypes;
    }

    /**
     *  {@link RFCardType}<p>
     * <p>
     * If the card type return includes {@link CardType#RFCARD}, this method may get the corresponding contactless type. <p>
     * Refer to：{@link RFCardType}
     *
     * @return
     * @since 2.0.0
     */
    public RFCardType getResponseRFCardType() {
        return responseRFCardType;
    }

    /**
     * Magnetic stripe card swiping result<p>
     *
     * @return
     */
    public boolean isMSDDataCorrectly() {
        return isMSDDataCorrectly;
    }

    /**
     * (Serial number of ATQA) If the contactless card type is M1, the serial number will be returned, and if the type is A, B card, the ATQA will be returned. <p>
     *
     * @return
     * @since 1.1.5
     */
    public byte[] getSnr() {
        return snr;
    }

    public byte getSAK() {
        return SAK;
    }

    public byte[] getIDmAndPmm(){
        return iDmAndPmm;
    }


    public byte[] getAtqa() {
        return atqa;
    }

    public void setAtqa(byte[] atqa) {
        this.atqa = atqa;
    }
}
