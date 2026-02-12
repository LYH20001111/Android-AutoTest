package com.newland.nsdk.core.external.command.emv.listener;

/**
 * @author Helen
 * @date 2021/6/28
 */
public class ExtCandidateAID {
    private byte[] aid;
    private byte[] label;
    private byte[] preferName;
    private byte issuerCodeTableIndex;
    private byte[] terminalCodeTable;
    private byte[] languagePreference;
    private byte priority;
    private byte[] kernelId;
    private byte[] extendAid;
    private byte terminalPriority;
    private byte[] tag9F0A;
    private byte[] customTagData;

    /**
     * Gets AID.
     *
     * @return AID: Application ID.
     */
    public byte[] getAID() {
        return aid;
    }

    /**
     * Sets AID.
     *
     * @param aid AID: Application ID.
     */
    public void setAID(byte[] aid) {
        this.aid = aid;
    }

    /**
     * Gets application label.
     *
     * @return Application label 50(ICC), ans, 1-16 bytes.
     */
    public byte[] getLabel() {
        return label;
    }

    /**
     * Sets application label.
     *
     * @param label Application label 50(ICC), ans, 1-16 bytes.
     */
    public void setLabel(byte[] label) {
        this.label = label;
    }

    /**
     * Gets application preferred name.
     *
     * @return Application Preferred Name 9F12(ICC), ans, 1-16 bytes.
     */
    public byte[] getPreferName() {
        return preferName;
    }

    /**
     * Sets application preferred name.
     *
     * @param preferName Application Preferred Name 9F12(ICC), ans, 1-16 bytes.
     */
    public void setPreferName(byte[] preferName) {
        this.preferName = preferName;
    }

    /**
     * Gets issuer code table index.
     *
     * @return Issuer code table index.
     */
    public byte getIssuerCodeTableIndex() {
        return issuerCodeTableIndex;
    }

    /**
     * Sets issuer code table index.
     *
     * @param issuerCodeTableIndex Issuer code table index.
     */
    public void setIssuerCodeTableIndex(byte issuerCodeTableIndex) {
        this.issuerCodeTableIndex = issuerCodeTableIndex;
    }

    /**
     * Gets terminal code table.
     *
     * @return Terminal code table.
     */
    public byte[] getTerminalCodeTable() {
        return terminalCodeTable;
    }

    /**
     * Sets terminal code table.
     *
     * @param terminalCodeTable Terminal code table.
     */
    public void setTerminalCodeTable(byte[] terminalCodeTable) {
        this.terminalCodeTable = terminalCodeTable;
    }

    /**
     * Gets language preference.
     *
     * @return Language preference.
     */
    public byte[] getLanguagePreference() {
        return languagePreference;
    }

    /**
     * Sets language preference.
     *
     * @param languagePreference Language preference.
     */
    public void setLanguagePreference(byte[] languagePreference) {
        this.languagePreference = languagePreference;
    }

    /**
     * Gets application priority indicator
     *
     * @return Application priority indicator 87(ICC), 1 bytes.
     */
    public byte getPriority() {
        return priority;
    }

    /**
     * Sets application priority indicator
     *
     * @param priority Application priority indicator 87(ICC), 1 bytes.
     */
    public void setPriority(byte priority) {
        this.priority = priority;
    }

    /**
     * Gets kernel ID.
     *
     * @return Kernel ID, tag DF37.
     */
    public byte[] getKernelID() {
        return kernelId;
    }

    /**
     * Sets kernel ID.
     *
     * @param kernelId Kernel ID, tag DF37.
     */
    public void setKernelID(byte[] kernelId) {
        this.kernelId = kernelId;
    }

    /**
     * Gets extend AID.
     *
     * @return Extend AID.
     */
    public byte[] getExtendAID() {
        return extendAid;
    }

    /**
     * Sets extend AID.
     *
     * @param extendAid Extend AID.
     */
    public void setExtendAID(byte[] extendAid) {
        this.extendAid = extendAid;
    }

    /**
     * Gets terminal priority.
     *
     * @return Terminal priority, 0 to 255; 255 is the greatest priority.
     */
    public byte getTerminalPriority() {
        return terminalPriority;
    }

    /**
     * Sets terminal priority.
     *
     * @param terminalPriority Terminal priority, 0 to 255; 255 is the greatest priority.
     */
    public void setTerminalPriority(byte terminalPriority) {
        this.terminalPriority = terminalPriority;
    }

    /**
     * Gets value of tag 9F0A.
     *
     * @return Value of tag 9F0A.
     */
    public byte[] getTag9F0A() {
        return tag9F0A;
    }

    /**
     * Sets value of tag 9F0A.
     *
     * @param tag9F0A Value of tag 9F0A.
     */
    public void setTag9F0A(byte[] tag9F0A) {
        this.tag9F0A = tag9F0A;
    }

    /**
     * Gets custom tag data.
     *
     * @return Custom tag data.
     */
    public byte[] getCustomTagData() {
        return customTagData;
    }

    /**
     * Sets custom tag data.
     *
     * @param customTagData Custom tag data.
     */
    public void setCustomTagData(byte[] customTagData) {
        this.customTagData = customTagData;
    }

}
