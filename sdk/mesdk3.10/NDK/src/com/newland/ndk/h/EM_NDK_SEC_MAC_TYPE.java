package com.newland.ndk.h;

public enum EM_NDK_SEC_MAC_TYPE {
    NDK_SEC_MAC_LAST,              /**< MAC Digital signature with AES on last block only. 9606 */
    NDK_SEC_MAC_X99,              /**< MAC Digital signature with AES on all blocks. X99 */
    NDK_SEC_MAC_X919,           /**< MAC Digital signature with sigle AES on each block, but full AES on last block. */
    NDK_SEC_MAC_UNIONPAY_ECB,   /**< MAC Digital signature with AES on the first 4 bytes of last block. In accordance to the UnionPay requirement */
}
