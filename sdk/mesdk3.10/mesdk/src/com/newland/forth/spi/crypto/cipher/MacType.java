package com.newland.forth.spi.crypto.cipher;

/**
 * The enum Mac mode.
 */
public enum MacType {
    /* TDES MAC */
    /**
     * MAC Digital signature with TDES on last block only. 9606
     */
    SEC_MAC_TDES_LAST,
    /**
     * MAC Digital signature with TDES on all blocks. X99
     */
    SEC_MAC_TDES_X99,
    /**
     * MAC Digital signature with sigle DES on each block, but full TDES on last block.
     */
    SEC_MAC_TDES_X919,
    /**
     * MAC Digital signature with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement
     */
    SEC_MAC_TDES_UNIONPAY_ECB,

    /* DUKPT MAC */
    /**
     * MAC using a DUKPT key with TDES on last block only. 9606
     */
    SEC_MAC_DUKPT_LAST,
    /**
     * MAC using a DUKPT key with TDES on all blocks.  X99
     */
    SEC_MAC_DUKPT_X99,
    /**
     * MAC Digital signature with sigle DES on each block, but full TDES on last block.
     */
    SEC_MAC_DUKPT_X919,
    /**
     * MAC Digital signature with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement
     */
    SEC_MAC_DUKPT_UNIONPAY_ECB,

    /* DUKPT RESPONSE MAC */
    /**
     * MAC using a DUKPT key (response) with TDES on last block only. 9606
     */
    SEC_MAC_DUKPT_RESP_LAST,
    /**
     * MAC using a DUKPT key (response) with TDES on all blocks.  X99
     */
    SEC_MAC_DUKPT_RESP_X99,
    /**
     * MAC using a DUKPT key (response) with sigle DES on each block, but full TDES on last block.
     */
    SEC_MAC_DUKPT_RESP_X919,
    /**
     * MAC using a DUKPT key (response) with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement
     */
    SEC_MAC_DUKPT_RESP_UNIONPAY_ECB,

    /* AES MAC */
    /**
     * MAC Digital signature with AES on last block only. 9606
     */
    SEC_MAC_AES_LAST,
    /**
     * MAC Digital signature with AES on all blocks. X99
     */
    SEC_MAC_AES_X99,
    /* AES DUKPT MAC */
    /**
     *  MAC using a DUKPT key with AES on last block only. 9606
     */
    SEC_MAC_AES_DUKPT_LAST,
    /**
     * MAC using a DUKPT key with AES on all blocks. X99
     */
    SEC_MAC_AES_DUKPT_X99,
    /**
     * MAC using a DUKPT key  with sigle DES on each block, but full TDES on last block.
     */
    SEC_MAC_AES_DUKPT_X919,
    /**
     * MAC using a DUKPT key  with TDES on the first 4 bytes of last block. In accordance to the UnionPay requirement
     */
    SEC_MAC_AES_DUKPT_UNIONPAY_ECB,

    /* SM4 MAC*/
    /**
     * MAC Digital signature with SM4 on last block only. 9606
     */
    SEC_MAC_SM4_LAST,
    /**
     * MAC Digital signature with SM4 on all blocks. X99
     */
    SEC_MAC_SM4_X99,
    /**
     *
     */
    SEC_MAC_SM4_UNIONPAY_ECB,

}
