package com.newland.sdk.module.pin;

/**
 * The enum Mac mode.
 */
public enum MacType {
    /* TDES MAC */
    /**
     * MAC Digital signature with TDES on last block only. 9606
     */
    MKSK_DES_9606,
    /**
     * MAC Digital signature with TDES on all blocks. X99
     */
    MKSK_DES_X99,
    /**
     * MAC Digital signature with sigle DES on each block, but full TDES on last block.
     */
    MKSK_DES_X919,
    /**
     * MAC Digital signature with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement
     */
    MKSK_DES_UNIONPAY_ECB,

    /* DUKPT MAC */
    /**
     * MAC using a DUKPT key with TDES on last block only. 9606
     */
    DUKPT_DES_9606,
    /**
     * MAC using a DUKPT key with TDES on all blocks.  X99
     */
    DUKPT_DES_X99,
    /**
     * MAC Digital signature with sigle DES on each block, but full TDES on last block.
     */
    DUKPT_DES_X919,
    /**
     * MAC Digital signature with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement
     */
    DUKPT_DES_UNIONPAY_ECB,


    /* DUKPT RESPONSE MAC */
    /**
     * MAC using a DUKPT key (response) with TDES on last block only. 9606
     */
    DUKPT_DES_RESP_9606,
    /**
     * MAC using a DUKPT key (response) with TDES on all blocks.  X99
     */
    DUKPT_DES_RESP_X99,
    /**
     * MAC using a DUKPT key (response) with sigle DES on each block, but full TDES on last block.
     */
    DUKPT_DES_RESP_X919,
    /**
     * MAC using a DUKPT key (response) with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement
     */
    DUKPT_DES_RESP_UNIONPAY_ECB,

    /* AES MAC */
    /**
     * MAC Digital signature with AES on last block only. 9606
     */
    MKSK_AES_9606,
    /**
     * MAC Digital signature with AES on all blocks. X99
     */
    MKSK_AES_X99,
    /* AES DUKPT MAC */
    /**
     *  MAC using a DUKPT key with AES on last block only. 9606
     */
    DUKPT_AES_9606,
    /**
     * MAC using a DUKPT key with AES on all blocks. X99
     */
    DUKPT_AES_X99,
    /**
     * MAC using a DUKPT key (response) with AES on last block only. 9606
     */
    //DUKPT_AES_RESP_9606,
    DUKPT_AES_X919,//SEC_MAC_AES_DUKPT_X919,
    /**
     * MAC using a DUKPT key (response) with AES on all blocks. X99
     */
//    DUKPT_AES_RESP_X99,
    DUKPT_AES_UNIONPAY_ECB,//SEC_MAC_AES_DUKPT_UNIONPAY_ECB

    /* SM4 MAC*/
    /**
     * MAC Digital signature with SM4 on last block only. 9606
     */
    MKSK_SM4_9606,
    /**
     * MAC Digital signature with SM4 on all blocks. X99
     */
    MKSK_SM4_X99,

    /**
     *
     */
    MKSK_SM4_UNIONPAY,
    /**
     *
     */
    HMAC_SHA1,
    /**
     *
     */
    HMAC_SHA256,

}
