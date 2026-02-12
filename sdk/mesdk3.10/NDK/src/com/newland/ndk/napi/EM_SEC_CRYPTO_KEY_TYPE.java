package com.newland.ndk.napi;

/**
 * @Description
 * @Author wuhh
 * @Date 2020/11/26
 */
public enum  EM_SEC_CRYPTO_KEY_TYPE {
    KEY_TYPE_DES(0),
    KEY_TYPE_AES(1),
    KEY_TYPE_SM4(2),
    KEY_TYPE_ASYM_RSA(0x20),
    KEY_TYPE_ASYM_ECC(0x21),
    KEY_TYPE_ASYM_SM2(0x22);
    int code;
    EM_SEC_CRYPTO_KEY_TYPE(int code){
        this.code = code;
    }
    public int getCode(){
        return code;
    }
}
