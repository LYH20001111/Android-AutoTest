package com.newland.nsdk.core.api.common.crypto;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;

/**
 * Padding mode.
 *
 * <p>Note: When cipher mode is {@link CipherMode#ECB}, only {@link PaddingMode#NONE} is supported.</p>
 */
public enum PaddingMode {
    /**
     * Never pad (full blocks only)
     *
     */
    NONE((byte)0),

    /**
     * PKCS7 padding, same as PKCS5. Always padded even though the data is multiple of block size.
     * <p>The value of each added byte is the number of bytes that are added, e.g. "DD DD DD DD 04 04 04 04"</p>
     */
    PKCS7((byte)1),

    /**
     * ISO/IEC 7816-4 padding. First byte is a mandatory byte valued '80' then rest bytes are set to zero, e.g. "DD DD DD DD 80 00 00 00"
     */
    ONE_AND_ZEROS((byte)2),

    /**
     * ANSI X.923 padding. Zeros are padded and the last byte defines the padding boundaries or the number of padded bytes, e.g. "DD DD DD DD 00 00 00 04"
     */
    ZEROS_AND_LEN((byte)3),

    /**
     * Zero padding (not reversible!), e.g. "DD DD DD DD 00 00 00 00"
     */
    ZEROS((byte)4);

    byte code;

    PaddingMode(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
