package com.newland.nsdk.core.api.common.crypto;

/**
 * MAC type.
 */
public enum MACType {
    /**
     * MAC Digital signature with TDES on last block only, 9606
     */
    TDES_LAST(0),

    /**
     * MAC Digital signature with TDES on all blocks, X99
     */
    TDES_X99(1),

    /**
     * MAC Digital signature with single DES on each block, but full TDES on last block.
     */
    TDES_X919(2),

    /**
     * MAC Digital signature with TDES on the first 4 bytes of last block, in accordance to the UnionPay requirement.
     */
    TDES_UNIONPAY_ECB(3),

    /**
     * MAC using a DUKPT key with TDES on last block only, 9606
     */
    DUKPT_LAST(4),

    /**
     * MAC using a DUKPT key with TDES on all blocks, X99
     */
    DUKPT_X99(5),

    /**
     * MAC Digital signature with single DES on each block, but full TDES on last block.
     */
    DUKPT_X919(6),

    /**
     * MAC Digital signature with TDES on the first 4 bytes of last block, in accordance to the UnionPay requirement.
     */
    DUKPT_UNIONPAY_ECB(7),

    /**
     * MAC using a DUKPT key (response) with TDES on last block only, 9606
     */
    DUKPT_RESP_LAST(8),

    /**
     * MAC using a DUKPT key (response) with TDES on all blocks, X99
     */
    DUKPT_RESP_X99(9),

    /**
     * MAC using a DUKPT key (response) with single DES on each block, but full TDES on last block.
     */
    DUKPT_RESP_X919(10),

    /**
     * MAC using a DUKPT key (response) with TDES on the first 4 bytes of last block, in accordance to the UnionPay requirement.
     */
    DUKPT_RESP_UNIONPAY_ECB(11),

    /**
     * MAC Digital signature with AES on last block only, 9606
     */
    AES_LAST(12),

    /**
     * MAC Digital signature with AES on all blocks, X99
     */
    AES_X99(13),

    /**
     * MAC using a DUKPT key with AES on last block only, 9606
     */
    AES_DUKPT_LAST(14),

    /**
     * MAC using a DUKPT key with AES on all blocks, X99
     */
    AES_DUKPT_X99(15),

    /**
     * MAC using a DUKPT key  with sigle DES on each block, but full TDES on last block
     */
    AES_DUKPT_X919(16),

    /**
     * MAC using a DUKPT key  with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement
     */
    AES_DUKPT_UNIONPAY_ECB(17),
    /**
     * MAC Digital signature with SM4 on last block only. 9606
     */
    SM4_LAST(18),
    /**
     * MAC Digital signature with SM4 on all blocks. X99
     */
    SM4_X99(19),
    /**
     * MAC Digital signature with SM4 on the first 4 bytes of last block. In accordance to the UnionPay requirement
     */
    SM4_UNIONPAY_ECB(20),
    /**
     * HMAC using a HMAC key with SHA1 hash function
     */
    HMAC_SHA1(21),
    /**
     * HMAC using a HMAC key with SHA256 hash function
     */
    HMAC_SHA256(22),

    /**
     * CMAC calculating by a TDES key.
     */
    TDES_CMAC(30),
    /**
     * CMAC calculating by a DUKPT key.
     */
    DUKPT_CMAC(31),

    /**
     * CMAC calculating by a AES key.
     */
    AES_CMAC(32),

    /**
     * CMAC calculating by an AES_DUKPT key.
     */
    AES_DUKPT_CMAC(33);


    int code;
    MACType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
