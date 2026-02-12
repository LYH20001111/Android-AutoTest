package com.newland.sdk.module.pin;

/**
 * Author by wuhh, Date on 2020/3/3.
 */
public class PaddingMode {
    /**
     * Padding mode.
     */
    private Mode mMode;
    /**
     * if No padding,then the length of input KeyData.<p>
     * The PaddingMode mode is not empty, exactly the length of injecting key .<p>
     */
    private int keyLen;

    /**
     * @param mode the padding mode
     * @param keyLen the key length
     */
    public PaddingMode(Mode mode, int keyLen){
        this.mMode = mode;
        this.keyLen = keyLen;
    }

    /**
     *  get padding mode
     * @return
     */
    public Mode getMode() {
        return mMode;
    }

    /**
     * get the key length
     * @return
     */
    public int getKeyLen() {
        return keyLen;
    }


    public enum Mode {
        /**
         * Never pad (full blocks only)
         */
        NONE,
        /**
         * PKCS7 padding, same as PKCS5. Always padded even though the data is multiple of block size. The value of each added byte is the number of bytes that are added, e.g. "DD DD DD DD 04 04 04 04"
         */
        PKCS7,
        /**
         * ISO/IEC 7816-4 padding. First byte is a mandatory byte valued '80' then rest bytes are set to zero, e.g. "DD DD DD DD 80 00 00 00"
         */
        ONE_AND_ZEROS,
        /**
         * ANSI X.923 padding. Zeros are padded and the last byte defines the padding boundaries or the number of padded bytes, e.g. "DD DD DD DD 00 00 00 04"
         */
        ZEROS_AND_LEN,
        /**
         * zero padding (not reversible!), e.g. "DD DD DD DD 00 00 00 00".
         */
        ZEROS
    }

}
