package com.newland.nsdk.core.api.internal.keymanager;

import java.util.Map;

/**
 * The parameters for Signature verification mode in {@link KeyManager#injectPubKey(Map, VerifyParameters, byte[], byte[])}.
 */
public class SignVerifyParameters extends VerifyParameters{
    private byte[] signData;

    /**
     * Gets the signature data.
     * @return The signature data.
     */
    public byte[] getSignData() {
        return signData;
    }

    /**
     * Sets the signature data.
     * @param signData The signature data.
     */
    public void setSignData(byte[] signData) {
        this.signData = signData;
    }
}
