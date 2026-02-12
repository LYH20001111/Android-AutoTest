package com.newland.forth.spi.crypto.cipher;

/**
 * The enum Padding mode.
 */
public enum PaddingMode {
    /**
     * Sec padding none padding mode.
     */
    SEC_PADDING_NONE,
    /**
     * < Never pad (full blocks only)
     */
    SEC_PADDING_PKCS7,
    /**
     * < PKCS7 padding, same as PKCS5. Always padded even though the data is multiple of block size. The value of each added byte is the number of bytes that are added, e.g. "DD DD DD DD 04 04 04 04"
     */
    SEC_PADDING_ONE_AND_ZEROS,
    /**
     * < ISO/IEC 7816-4 padding. First byte is a mandatory byte valued '80' then rest bytes are set to zero, e.g. "DD DD DD DD 80 00 00 00"
     */
    SEC_PADDING_ZEROS_AND_LEN,
    /**
     * < ANSI X.923 padding. Zeros are padded and the last byte defines the padding boundaries or the number of padded bytes, e.g. "DD DD DD DD 00 00 00 04"
     */
    SEC_PADDING_ZEROS,         /**< zero padding (not reversible!), e.g. "DD DD DD DD 00 00 00 00" */
}
