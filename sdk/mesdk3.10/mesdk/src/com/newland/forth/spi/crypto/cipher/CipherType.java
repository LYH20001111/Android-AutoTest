package com.newland.forth.spi.crypto.cipher;

/**
 * The enum Cipher mode.
 */
public enum CipherType {
    /**
     * The Sec cipher des ecb.
     */
    /* TDES Algorithm */
    SEC_CIPHER_DES_ECB(0),
    /**
     * < ECB mode without specifying the key length, firmware will use appropriate algorithm according to the key length
     */
    SEC_CIPHER_DES_CBC(1),
    /**
     * < CBC mode without specifying the key length, firmware will use appropriate algorithm according to the key length
     */
    SEC_CIPHER_DES_CFB(2),
    /**
     * Sec cipher des ofb cipher mode.
     */
    SEC_CIPHER_DES_OFB(3),
    /**
     * Sec cipher aes ecb cipher mode.
     */
    SEC_CIPHER_AES_ECB(4),
    /**
     * < ECB mode without specifying the key length, firmware will use appropriate algorithm according to the key length
     */
    SEC_CIPHER_AES_CBC(5),
    /**
     * < CBC mode without specifying the key length, firmware will use appropriate algorithm according to the key length
     */
    SEC_CIPHER_AES_CFB(6),
    /**
     * Sec cipher aes ofb cipher mode.
     */
    SEC_CIPHER_AES_OFB(7),
    /**
     * The Sec cipher dukpt ecb resp.
     */
    /* DUKPT TDES Algorithm */
    SEC_CIPHER_DUKPT_ECB_RESP(8),
    /**
     * < 3DES encryption in ECB mode, with DUKPT key variant for Data Encryption: response (Input Message)
     */
    SEC_CIPHER_DUKPT_ECB_BOTH(9),
    /**
     * < 3DES encryption in ECB mode, with DUKPT key variant for Data Encryption: request or both ways (Output Message)
     */
    SEC_CIPHER_DUKPT_CBC_RESP(10),
    /**
     * < 3DES encryption in CBC mode, with DUKPT key variant for Data Encryption: response (Input Message)
     */
    SEC_CIPHER_DUKPT_CBC_BOTH(11),
    /**
     * < 3DES encryption in CBC mode, with DUKPT key variant for Data Encryption: request or both ways (Output Message)
     */
    SEC_CIPHER_DUKPT_CFB_RESP(12),
    /**
     * Sec cipher dukpt cfb both cipher mode.
     */
    SEC_CIPHER_DUKPT_CFB_BOTH(13),
    /**
     * Sec cipher dukpt ofb resp cipher mode.
     */
    SEC_CIPHER_DUKPT_OFB_RESP(14),
    /**
     * Sec cipher dukpt ofb both cipher mode.
     */
    SEC_CIPHER_DUKPT_OFB_BOTH(15),
    /**
     *  AES encryption in ECB mode, with AES DUKPT key variant for Data Encryption
     */
    SEC_CIPHER_AES_DUKPT_ECB(16),
    /**
     * AES  encryption in CBC mode, with AES DUKPT key variant for Data Encryption
     */
    SEC_CIPHER_AES_DUKPT_CBC(17),
    /**
     * The Sec cipher sm 4 ecb.
     */
    /* SM4 Algorithm*/
    SEC_CIPHER_SM4_ECB(24),
    /**
     * < ECB mode without specifying the key length, firmware will use appropriate algorithm according to the key length
     */
    SEC_CIPHER_SM4_CBC(25),
    /**
     * < CBC mode without specifying the key length, firmware will use appropriate algorithm according to the key length
     */
//    SEC_CIPHER_SM4_CFB,
//    SEC_CIPHER_SM4_OFB,
    SEC_CIPHER_KEYLEN_8(1<<8),			/**<Encrypted with 8-byte  key*/
    SEC_CIPHER_KEYLEN_16(1<<9),		   /**<Encrypted with 16-byte  key*/
    SEC_CIPHER_KEYLEN_24(1<<10),			/**<Encrypted with 24-byte	key*/
    SEC_CIPHER_DERIVE_PIN(1<<11),           /**< DUKPT key derive a key for PIN*/
    SEC_CIPHER_DERIVE_MAC(1<<12),            /**<DUKPT key derive a key for MAC*/
    SEC_CIPHER_DERIVE_MAC_RESP(1<<13),           /**<DUKPT key derive a key for MAC(response)*/
    EM_SEC_CIPHER_TYPE_MAX(65536);
    int code;
    private CipherType(int code) {
        this.code = code;
    }

    public int getCode() {
        return this.code;
    }
}
