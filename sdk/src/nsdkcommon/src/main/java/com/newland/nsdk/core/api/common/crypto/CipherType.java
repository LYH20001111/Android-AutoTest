package com.newland.nsdk.core.api.common.crypto;

import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.KeyType;

/**
 * Cipher type.
 */
public enum CipherType {
    /**
     * DES ECB cipher mode.
     */
    DES_ECB(0),

    /**
     * DES CBC cipher mode.
     */
    DES_CBC(1),

    /**
     * DES CFB cipher mode.
     */
    DES_CFB(2),

    /**
     * DES OFB cipher mode.
     */
    DES_OFB(3),

    /**
     * DES CTR cipher mode.
     */
    DES_CTR(28),

    /**
     * AES ECB cipher mode.
     */
    AES_ECB(4),

    /**
     * AES CBC cipher mode.
     */
    AES_CBC(5),

    /**
     * AES CFB cipher mode.
     */
    AES_CFB(6),

    /**
     * AES OFB cipher mode.
     */
    AES_OFB(7),

    /**
     * AES CTR cipher mode.
     */
    AES_CTR(29),

    /**
     * 3DES encryption in ECB mode, with DUKPT key variant for data encryption: response (Input Message)
     */
    DUKPT_ECB_RESP(8),

    /**
     * 3DES encryption in ECB mode, with DUKPT key variant for data encryption: request or both ways (Output Message)
     */
    DUKPT_ECB_BOTH(9),

    /**
     * 3DES encryption in CBC mode, with DUKPT key variant for data encryption: response (Input Message)
     */
    DUKPT_CBC_RESP(10),

    /**
     * 3DES encryption in CBC mode, with DUKPT key variant for data encryption: request or both ways (Output Message)
     */
    DUKPT_CBC_BOTH(11),

    /**
     * DUKPT CFB: response (Input Message)
     */
    DUKPT_CFB_RESP(12),

    /**
     * DUKPT CFB: request or both ways (Output Message)
     */
    DUKPT_CFB_BOTH(13),

    /**
     * DUKPT OFB: response (Input Message)
     */
    DUKPT_OFB_RESP(14),

    /**
     * DUKPT OFB: request or both ways (Output Message)
     */
    DUKPT_OFB_BOTH(15),

    /**
     * AES encryption in ECB mode, with AES DUKPT key variant for Data Encryption
     */
    AES_DUKPT_ECB(16),

    /**
     * AES encryption in CBC mode, with AES DUKPT key variant for Data Encryption
     */
    AES_DUKPT_CBC(17),

    /**
     * ECB mode without specifying the key length, firmware will use appropriate algorithm according to the key length
     */
    SM4_ECB(24),

    /**
     * CBC mode without specifying the key length, firmware will use appropriate algorithm according to the key length
     */
    SM4_CBC(25),

    /**
     * AES GCM cipher mode.
     **/
    AES_GCM(30);

    int code;

    CipherType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
    public static CipherMode getCipherMode(CipherType cipherType){
        if (cipherType == null) {
            return null;
        }

        CipherMode cipherMode = null;
        String cipherTypeName = cipherType.name();
        if (cipherTypeName.indexOf(CipherMode.ECB.name()) > 0) {
            cipherMode = CipherMode.ECB;
        } else if (cipherTypeName.indexOf(CipherMode.CBC.name()) > 0) {
            cipherMode = CipherMode.CBC;
        } else if (cipherTypeName.indexOf(CipherMode.CFB.name()) > 0) {
            cipherMode = CipherMode.CFB;
        } else if (cipherTypeName.indexOf(CipherMode.OFB.name()) > 0) {
            cipherMode = CipherMode.OFB;
        } else if (cipherTypeName.indexOf(CipherMode.CTR.name()) > 0) {
            cipherMode = CipherMode.CTR;
        } else if (cipherTypeName.indexOf(CipherMode.GCM.name()) > 0) {
            cipherMode = CipherMode.GCM;
        } else if (cipherTypeName.indexOf(CipherMode.STREAM.name()) > 0) {
            cipherMode = CipherMode.STREAM;
        } else if (cipherTypeName.indexOf(CipherMode.CCM.name()) > 0) {
            cipherMode = CipherMode.CCM;
        }
        return cipherMode;
    }

    public static KeyType getKeyType(CipherType cipherType){
        if (cipherType == null) {
            return null;
        }

        KeyType keyType = null;
        String cipherTypeName = cipherType.name();
        if (cipherTypeName.indexOf(KeyType.AES.name()) > 0) {
            keyType = KeyType.AES;
        } else {
            keyType = KeyType.DES;
        }
        return keyType;
    }

    public static boolean isIvRequired(CipherType cipherType) {
        if (cipherType == null){
            return false;
        }

        return cipherType.name().indexOf("CBC") > 0;
    }
}
