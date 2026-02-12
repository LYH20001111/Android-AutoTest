package com.newland.ndk.napi;

/**
 * @Description
 * @Author wuhh
 * @Date 2020/11/26
 */
public enum EM_SEC_KEY_USAGE {
    /* Master key, KEK */
    KEY_USE_KEK(0),            /**<Master key for all key, same as NDK TMK*/
    KEY_USE_PIN_KEK(1),        /**<Master key ONLY for PIN key*/
    KEY_USE_MAC_KEK(2),        /**<Master key ONLY for MAC generation key*/
    KEY_USE_DATA_KEK(3),       /**<Master key ONLY for data encryption & decryption key*/
    KEY_USE_DATA_ENC_KEK(4),   /**<Master key ONLY for data encryption key*/
    KEY_USE_TR31_KEK(5),       /**<Master key ONLY for TR31 key block*/
    /* Session / Working key */
    KEY_USE_PIN(6),
    KEY_USE_MAC(7),
    KEY_USE_DATA(8),
    KEY_USE_DATA_ENC_ONLY(9),
    /* DUKPT Initial Key */
    KEY_USE_DUKPT(0x10), /**<DUKPT Initial Key*/
    /* Asym Auth Key*/
    KEY_USE_ASYM_AUTH(0x20),
    /* Asym Data Key*/
    KEY_USE_ASYM_DATA(0x21),
    /* Asym Key Use for AUTH&ENC */
    KEY_USE_ASYM_ANY(0x22),
    KEY_USE_ASYM_KEY_DISTRIBUTION(0x23);
    int code;
    EM_SEC_KEY_USAGE(int code){
        this.code = code;
    }
    public int getCode(){
        return code;
    }
}
