package com.newland.nsdk.core.api.external.pinentry;

import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.KeyType;

/**
 * PAN that is encrypted by protection key.
 */
public class CipherPAN {
    private Key panKey;
    private byte[] cipherPAN;
    private int clearPANLen;

    public CipherPAN() {
    }

    /**
     * Gets the key which is used to encrypt the plain PAN.
     *
     * @return Key which is used to encrypt the plain PAN.
     * <ul>
     *     <li>Key ID value range: [129-255].</li>
     *     <li>Key type: {@link KeyType#DES}, {@link KeyType#AES}</li>
     * </ul>
     */
    public Key getPANKey() {
        return panKey;
    }

    /**
     * Sets the key which is used to encrypt the plain PAN.
     *
     * @param panKey Key which is used to encrypt the plain PAN.
     *               <ul>
     *               <li>Key ID value range: [129-255]</li>
     *               <li>Key type: {@link KeyType#DES}, {@link KeyType#AES}</li>
     *               </ul>
     */
    public void setPANKey(Key panKey) {
        this.panKey = panKey;
    }

    /**
     * Gets encrypted PAN data.
     *
     * @return Encrypted PAN data.
     */
    public byte[] getCipherPAN() {
        return cipherPAN;
    }

    /**
     * Sets encrypted PAN data.
     *
     * @param cipherPAN Encrypted PAN data.
     */
    public void setCipherPAN(byte[] cipherPAN) {
        this.cipherPAN = cipherPAN;
    }

    /**
     * Gets length of clear PAN.
     *
     * @return Length of clear PAN.
     */
    public int getClearPANLen() {
        return clearPANLen;
    }

    /**
     * Sets length of clear PAN.
     *
     * @param clearPANLen Length of clear PAN.
     */
    public void setClearPANLen(int clearPANLen) {
        this.clearPANLen = clearPANLen;
    }
}
