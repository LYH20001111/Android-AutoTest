package com.newland.nsdk.core.api.common.card.contactless;

import com.newland.nsdk.core.api.common.cardreader.CardReaderListener;

/**
 * Contactless card info returned on {@link CardReaderListener#onFindContactlessCard};
 */
public class ContactlessCardInfo {
    private byte[] idmpmm;

    /**
     * Gets IDm and PMm of Felica card.
     *
     * @return IDm and PMm of Felica card.
     */
    public byte[] getIDmPMm() {
        return idmpmm;
    }

    /**
     * Sets IDm and PMm of Felica card.
     *
     * @param idmpmm IDm and PMm of Felica card.
     */
    public void setIDmPMm(byte[] idmpmm) {
        this.idmpmm = idmpmm;
    }
}
