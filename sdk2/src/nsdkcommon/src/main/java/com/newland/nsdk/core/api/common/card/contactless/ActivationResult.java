package com.newland.nsdk.core.api.common.card.contactless;

/**
 * Contactless card info returned after card activated.
 *
 * <p>Different cards will return different results.</p>
 * <li>{@link ContactlessCardType#TYPE_A} - {@link SubContactlessCardType#CPU}: {@link #uid}, {@link #atqa}, {@link #sak}, {@link #ats}</li>
 * <li>{@link ContactlessCardType#TYPE_B} - {@link SubContactlessCardType#CPU}: {@link #uid}, {@link #atqb} </li>
 * <li>{@link SubContactlessCardType#M0}: {@link #uid}, {@link #atqa}, {@link #sak} </li>
 * <li>{@link SubContactlessCardType#M1}: {@link #uid}, {@link #atqa}, {@link #sak}</li>
 */
public class ActivationResult {
    private byte[] uid;
    private byte[] atqa;
    private byte[] ats;
    private byte[] atqb;
    private byte[] sak;

    /**
     * Sets UID.
     *
     * @param uid UID. Unique IDentifier, Type A.
     */
    public void setUID(byte[] uid) {this.uid = uid;}

    /**
     * Sets ATQB.
     *
     * @param atqb ATQB. Answer To reQuest, Type B.
     */
    public void setATQB(byte[] atqb) {this.atqb = atqb;}

    /**
     * Sets ATS.
     *
     * @param ats ATS. Answer To Select, Type A.
     */
    public void setATS(byte[] ats) {this.ats = ats; }
    /**
     * Sets ATQA.
     *
     * @param atqa ATQA. Answer To reQuest, Type A.
     */
    public void setATQA(byte[] atqa) {
        this.atqa = atqa;
    }

    /**
     * Sets SAK.
     *
     * @param sak SAK. Select AcKnowledge, Type A.
     */
    public void setSAK(byte[] sak) {
        this.sak = sak;
    }

    /**
     * Gets UID.
     *
     * @return UID. Unique IDentifier, Type A.
     */
    public byte[] getUID() {return uid;}

    /**
     * Gets ATQB.
     *
     * @return ATQB. Answer To reQuest, Type B.
     */
    public byte[] getATQB() {return atqb;}

    /**
     * Gets ATS.
     *
     * @return ATS. Answer To Select, Type A.
     */
    public byte[] getATS() {return ats;}
    /**
     * Gets ATQA.
     *
     * @return ATQA. Answer To reQuest, Type A.
     */
    public byte[] getATQA() {
        return atqa;
    }

    /**
     * Gets SAK.
     *
     * @return SAK. Select AcKnowledge, Type A.
     */
    public byte[] getSAK() {
        return sak;
    }
}
