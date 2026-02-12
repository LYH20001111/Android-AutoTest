package com.newland.nsdk.core.api.common.keymanager;

/**
 * Key usage.
 */
public enum KeyUsage {
    /**
     * Master key for all keys.
     */
    KEK((byte)0),
    /**
     * Master key ONLY for PIN key.
     */
    PIN_KEK((byte)1),
    /**
     * Master key ONLY for MAC generation key.
     */
    MAC_KEK((byte)2),
    /**
     * Master key ONLY for data encryption & decryption key.
     */
    DATA_KEK((byte)3),
    /**
     * Master key ONLY for data encryption key.
     */
    DATA_ENC_KEK((byte)4),
    /**
     * Master key ONLY for TR31 key block.
     */
    TR31_KEK((byte)5),
    /**
     * Key used for PIN.
     */
    PIN((byte)6),
    /**
     * Key used for MAC generation.
     */
    MAC((byte)7),
    /**
     * Key used for data encryption & decryption.
     *
     */
    DATA((byte)8),
    /**
     * Key only used for data encryption. This usage requires 24 bytes of key length.
     */
    DATA_ENC_ONLY((byte)9),

    /**
     * Data key ONLY for PAN ENCRYPTION, can only be used 120 times per hour.
     */
    PAN_ENC_ONLY((byte) 10),
    /**
     * DUKPT Initial Key.
     */
    DUKPT((byte)16);

    byte code;

    KeyUsage(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
