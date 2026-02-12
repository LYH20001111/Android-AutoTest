package com.newland.sdk.me.module.pininput;

import com.newland.forth.spi.crypto.cipher.KcvMode;
import com.newland.forth.spi.crypto.cipher.MacMode;
import com.newland.forth.spi.crypto.cipher.PaddingMode;
import com.newland.forth.spi.crypto.keystore.CipherMode;
import com.newland.forth.spi.crypto.keystore.DukptDerivedMode;
import com.newland.forth.spi.crypto.keystore.KeyGenerateMethod;
import com.newland.forth.spi.crypto.keystore.KeyType;
import com.newland.forth.spi.crypto.keystore.KEY_USE;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.InjectKeyType;
import com.newland.sdk.module.pin.LoadKeyMode;
import com.newland.sdk.module.pin.LoadWKMode;
import com.newland.sdk.module.pin.MasterKeyType;
import com.newland.sdk.module.pin.WorkingKeyType;

/**
 * Author by wuhh, Date on 2020/3/3.
 */
public class CryptoMap {

    public CryptoMap() {

    }

    public KeyGenerateMethod getKeyGenerateMethod(LoadKeyMode loadKeyMode) {
        if (loadKeyMode == LoadKeyMode.CUSTOM_ENCRYPT) {
            return KeyGenerateMethod.SEC_KIM_CIPHER;
        } else if (loadKeyMode == LoadKeyMode.PLAIN) {
            return KeyGenerateMethod.SEC_KIM_CLEAR;
        } else if (loadKeyMode == LoadKeyMode.TR31) {
            return KeyGenerateMethod.SEC_KIM_TR31;
        } else if (loadKeyMode == LoadKeyMode.GISKE) {
            return KeyGenerateMethod.SEC_KIM_GISKE;
        } else if (loadKeyMode == LoadKeyMode.RANDOM) {
            return KeyGenerateMethod.SEC_KIM_RANDOM;
        } else if (loadKeyMode == LoadKeyMode.RANDOM_OUT) {
            return KeyGenerateMethod.SEC_KIM_RANDOM_OUT;
        }
        return null;
    }

    public KeyType getAlgorithmMode(AlgorithmMode algorithmMode) {
        if (algorithmMode == AlgorithmMode.DES) {
            return KeyType.DES;
        } else if (algorithmMode == AlgorithmMode.SM4) {
            return KeyType.SM4;
        } else if (algorithmMode == AlgorithmMode.AES) {
            return KeyType.AES;
        }else if (algorithmMode == AlgorithmMode.HMAC) {
            return KeyType.HMAC;
        }
        return null;
    }

    public KcvMode getKcvMode(com.newland.sdk.module.pin.KcvMode kcvMode) {
        if (kcvMode == com.newland.sdk.module.pin.KcvMode.ZERO) {
            return KcvMode.NAPI_SEC_KCV_ZERO;
        } else if (kcvMode == com.newland.sdk.module.pin.KcvMode.VAL) {
            return KcvMode.NAPI_SEC_KCV_VAL;
        } else if (kcvMode == com.newland.sdk.module.pin.KcvMode.DATA) {
            return KcvMode.NAPI_SEC_KCV_DATA;
        }
        return KcvMode.NAPI_SEC_KCV_NONE;
    }

    public PaddingMode getPaddingMode(com.newland.sdk.module.pin.PaddingMode.Mode mode) {
        if (mode == com.newland.sdk.module.pin.PaddingMode.Mode.PKCS7) {
            return PaddingMode.SEC_PADDING_PKCS7;
        } else if (mode == com.newland.sdk.module.pin.PaddingMode.Mode.ONE_AND_ZEROS) {
            return PaddingMode.SEC_PADDING_ONE_AND_ZEROS;
        } else if (mode == com.newland.sdk.module.pin.PaddingMode.Mode.ZEROS_AND_LEN) {
            return PaddingMode.SEC_PADDING_ZEROS_AND_LEN;
        } else if (mode == com.newland.sdk.module.pin.PaddingMode.Mode.ZEROS) {
            return PaddingMode.SEC_PADDING_ZEROS;
        }
        return PaddingMode.SEC_PADDING_NONE;
    }

    public CipherMode getCipherMode(com.newland.sdk.module.pin.CipherMode cipherMode) {
        if (cipherMode == com.newland.sdk.module.pin.CipherMode.ECB) {
            return CipherMode.SEC_CIPHER_MODE_ECB;
        } else if (cipherMode == com.newland.sdk.module.pin.CipherMode.CBC) {
            return CipherMode.SEC_CIPHER_MODE_CBC;
        } else if (cipherMode == com.newland.sdk.module.pin.CipherMode.CFB) {
            return CipherMode.SEC_CIPHER_MODE_CFB;
        } else if (cipherMode == com.newland.sdk.module.pin.CipherMode.OFB) {
            return CipherMode.SEC_CIPHER_MODE_OFB;
        } else if (cipherMode == com.newland.sdk.module.pin.CipherMode.CTR) {
            return CipherMode.SEC_CIPHER_MODE_CTR;
        } else if (cipherMode == com.newland.sdk.module.pin.CipherMode.GCM) {
            return CipherMode.SEC_CIPHER_MODE_GCM;
        } else if (cipherMode == com.newland.sdk.module.pin.CipherMode.STREAM) {
            return CipherMode.SEC_CIPHER_MODE_STREAM;
        } else if (cipherMode == com.newland.sdk.module.pin.CipherMode.CCM) {
            return CipherMode.SEC_CIPHER_MODE_CCM;
        }
        return CipherMode.SEC_CIPHER_MODE_ECB;
    }

    public DukptDerivedMode getDukptDerivedMode(com.newland.sdk.module.pin.DukptDerivedMode dukptDerivedMode) {
        if (dukptDerivedMode == com.newland.sdk.module.pin.DukptDerivedMode.BOTH) {
            return DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH;
        } else if (dukptDerivedMode == com.newland.sdk.module.pin.DukptDerivedMode.RESP) {
            return DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_RESP;
        }
        return DukptDerivedMode.SEC_DUKPT_DERIVED_MODE_BOTH;
    }

    public KEY_USE getKeyUsage(KeyUsage keyUsage) {
        if (keyUsage == KeyUsage.MASTER) {
            return KEY_USE.KEY_USE_KEK;
        } else if (keyUsage == KeyUsage.MASTER_PIN) {
            return KEY_USE.KEY_USE_PIN_KEK;
        } else if (keyUsage == KeyUsage.MASTER_MAC) {
            return KEY_USE.KEY_USE_MAC_KEK;
        } else if (keyUsage == KeyUsage.MASTER_DATA) {
            return KEY_USE.KEY_USE_DATA_KEK;
        } else if (keyUsage == KeyUsage.MASTER_DATA_ENC) {
            return KEY_USE.KEY_USE_DATA_ENC_KEK;
        } else if (keyUsage == KeyUsage.MASTER_TR31) {
            return KEY_USE.KEY_USE_TR31_KEK;
        } else if (keyUsage == KeyUsage.WORKINGKEY_PIN) {
            return KEY_USE.KEY_USE_PIN;
        } else if (keyUsage == KeyUsage.WORKINGKEY_MAC) {
            return KEY_USE.KEY_USE_MAC;
        } else if (keyUsage == KeyUsage.WORKINGKEY_DATA) {
            return KEY_USE.KEY_USE_DATA;
        } else if (keyUsage == KeyUsage.WORKINGKEY_DATA_ENC_ONLY) {
            return KEY_USE.KEY_USE_DATA_ENC_ONLY;
        } else if (keyUsage == KeyUsage.DUKPT) {
            return KEY_USE.KEY_USE_DUKPT;
        }
        return null;
    }

    public KeyUsage getSDKKeyUsageByWorkingKeyType(com.newland.sdk.module.pin.WorkingKeyType workingKeyType) {
        if (workingKeyType == WorkingKeyType.PIN) {
            return KeyUsage.WORKINGKEY_PIN;
        } else if (workingKeyType == WorkingKeyType.MAC) {
            return KeyUsage.WORKINGKEY_MAC;
        } else if (workingKeyType == WorkingKeyType.TRACK) {
            return KeyUsage.WORKINGKEY_DATA;
        }
        return null;
    }
    public enum MacAlgMode {
        LAST,
        X99,
        X919,
        UNIONPAY_ECB,
        SM4_UNIONPAY,
    }
    public MacMode getMacMode(com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode macMode) {
        if (macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.LAST) {
            return MacMode.SEC_MAC_MODE_LAST;
        } else if (macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.X99) {
            return MacMode.SEC_MAC_MODE_X99;
        } else if (macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.X919) {
            return MacMode.SEC_MAC_MODE_X919;
        } else if (macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.UNIONPAY_ECB) {
            return MacMode.SEC_MAC_MODE_UNIONPAY_ECB;
        }
        return null;
    }

    public KeyUsage getSDKKeyUsageByMasterKeyType(MasterKeyType masterKeyType) {
        if (masterKeyType == MasterKeyType.MASTER) {
            return KeyUsage.MASTER;
        } else if (masterKeyType == MasterKeyType.MASTER_PIN) {
            return KeyUsage.MASTER_PIN;
        } else if (masterKeyType == MasterKeyType.MASTER_MAC) {
            return KeyUsage.MASTER_MAC;
        } else if (masterKeyType == MasterKeyType.MASTER_DATA) {
            return KeyUsage.MASTER_DATA;
        } else if (masterKeyType == MasterKeyType.MASTER_DATA_ENC) {
            return KeyUsage.MASTER_DATA_ENC;
        } else if (masterKeyType == MasterKeyType.MASTER_TR31) {
            return KeyUsage.MASTER_TR31;
        }
        return null;
    }

    public int getMacAlgorithm(AlgorithmMode algorithmMode, com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode macMode){
        if(algorithmMode == AlgorithmMode.DES){
            if(macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.LAST){
                return 0x03;//9606
            }else if(macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.X99){
                return 0x00;
            }else if(macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.X919){
                return 0x01;
            }else if(macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.UNIONPAY_ECB){
                return 0x02;//ECB
            }
        }else if(algorithmMode == AlgorithmMode.SM4){
            if(macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.LAST){
                return -1;
            }else if(macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.X99){
                return 0x05;//
            }else if(macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.X919){
                return -1;
            }else if(macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.UNIONPAY_ECB){
                return -1;
            }else if(macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.SM4_UNIONPAY){
                return 0x06;//SM4_UNIONPAY = 0x06;
            }
        }else if(algorithmMode == AlgorithmMode.AES){
            if(macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.LAST){
                return -1;
            }else if(macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.X99){
                return 0x07;
            }else if(macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.X919){
                return -1;
            }else if(macMode == com.newland.sdk.me.module.pininput.CryptoMap.MacAlgMode.UNIONPAY_ECB){
                return -1;
            }
        }
        return -1;
    }
    public int getSDKMKSKKeyType(KeyUsage keyUsage){
        if(keyUsage == KeyUsage.MASTER){
            return 0x01;
        }else if(keyUsage == KeyUsage.WORKINGKEY_PIN){
            return 0x02;
        }else if(keyUsage == KeyUsage.WORKINGKEY_MAC){
            return 0x03;
        }else if(keyUsage == KeyUsage.WORKINGKEY_DATA){
            return 0x04;
        }
        return -1;
    }

    public LoadKeyMode getLoadWKMode2LoadKeyMode(LoadWKMode loadWKMode){
        if(loadWKMode == LoadWKMode.ENCRYPT){
            return LoadKeyMode.CUSTOM_ENCRYPT;
        }else if(loadWKMode == LoadWKMode.PLAIN){
            return LoadKeyMode.PLAIN;
        }else if(loadWKMode == LoadWKMode.TR31){
            return LoadKeyMode.TR31;
        }else if(loadWKMode == LoadWKMode.GISKE){
            return LoadKeyMode.GISKE;
        }else if(loadWKMode == LoadWKMode.RANDOM){
            return LoadKeyMode.RANDOM;
        }else if(loadWKMode == LoadWKMode.RANDOM_OUT){
            return LoadKeyMode.RANDOM_OUT;
        }
        return null;
    }

    public KeyUsage getKeyUsage(InjectKeyType keyType) {
        if (keyType == InjectKeyType.MASTER) {
            return KeyUsage.MASTER;
        } else if (keyType == InjectKeyType.MASTER_PIN) {
            return KeyUsage.MASTER_PIN;
        } else if (keyType == InjectKeyType.MASTER_MAC) {
            return KeyUsage.MASTER_MAC;
        } else if (keyType == InjectKeyType.MASTER_DATA) {
            return KeyUsage.MASTER_DATA;
        } else if (keyType == InjectKeyType.MASTER_DATA_ENC) {
            return KeyUsage.MASTER_DATA_ENC;
        } else if (keyType == InjectKeyType.MASTER_TR31) {
            return KeyUsage.MASTER_TR31;
        } else if (keyType == InjectKeyType.WORKINGKEY_PIN) {
            return KeyUsage.WORKINGKEY_PIN;
        } else if (keyType == InjectKeyType.WORKINGKEY_MAC) {
            return KeyUsage.WORKINGKEY_MAC;
        } else if (keyType == InjectKeyType.WORKINGKEY_DATA) {
            return KeyUsage.WORKINGKEY_DATA;
        } else if (keyType == InjectKeyType.WORKINGKEY_DATA_ENC_ONLY) {
            return KeyUsage.WORKINGKEY_DATA_ENC_ONLY;
        } else if (keyType == InjectKeyType.DUKPT) {
            return KeyUsage.DUKPT;
        }
        return null;
    }

}

