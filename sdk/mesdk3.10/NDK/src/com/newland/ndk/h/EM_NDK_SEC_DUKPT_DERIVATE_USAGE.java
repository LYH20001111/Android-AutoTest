package com.newland.ndk.h;

/**
 * Copyright © 2023 Fujian Newland Payment Technology Co., Ltd
 * Author: wuhh
 * Date: 2024/12/18 10:39
 * Description:
 * History:
 * <author> <time> <version> <desc>
 */
public enum EM_NDK_SEC_DUKPT_DERIVATE_USAGE {
    NDK_DUKPT_DERIVATE_NONE,        /**NONE */
    NDK_DUKPT_DERIVATE_KEK,         /**< key Encryption Key */
    NDK_DUKPT_DERIVATE_PIN,         /**< PIN Encryption */
    NDK_DUKPT_DERIVATE_MAC_GEN,     /**< Message Authentication,generation */
    NDK_DUKPT_DERIVATE_MAC_VERIFY,  /**< Message Authentication,verification */
    NDK_DUKPT_DERIVATE_MAC_BOTH,    /**< Message Authentication, both ways(When the key type is DES, it is consistent with DUKPT_DERIVATE_MAC_GEN) */
    NDK_DUKPT_DERIVATE_DATA_ENC,    /**< Data Encryption, encrypt */
    NDK_DUKPT_DERIVATE_DATA_DEC,    /**< Data Encryption, decrypt */
    NDK_DUKPT_DERIVATE_DATA_BOTH,    /**< Data Encryption, both ways (When the key type is DES, it is consistent with DUKPT_DERIVATE_DATA_ENC)*/
    NDK_DUKPT_DERIVATE_DERIVATEKEY, /**< Key Derivation */
    NDK_DUKPT_DERIVATE_DERIVATEKEY_INITIAL, /**< Initial Key Derivation */
}
