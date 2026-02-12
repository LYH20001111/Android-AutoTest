package com.newland.nsdk.core.external.command.common;

import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.DUKPTDerivateKey;
import com.newland.nsdk.core.api.common.keymanager.DUKPTDerivateUsage;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.keymanager.SymmetricKey;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ExtToolUtils {

    public static CipherType combineCipherType(SymmetricKey key, AlgorithmParameters params) throws NSDKException {
        CipherMode cipherMode = params.getCipherMode();
        if(key instanceof DUKPTDerivateKey) {
            KeyType keyType = ((DUKPTDerivateKey) key).getDerivateKeyType();
            if(keyType == KeyType.DES) {
                DUKPTDerivateUsage derivateUsage = ((DUKPTDerivateKey) key).getDerivateUsage();
                if(cipherMode == CipherMode.ECB) {
                    switch (derivateUsage) {
                        case MAC_BOTH:
                        case DATA_BOTH:
                            return CipherType.DUKPT_ECB_BOTH;
                        default:
                            return CipherType.DUKPT_ECB_RESP;
                    }
                } else if(cipherMode == CipherMode.CBC) {
                    switch (derivateUsage) {
                        case MAC_BOTH:
                        case DATA_BOTH:
                            return CipherType.DUKPT_CBC_BOTH;
                        default:
                            return CipherType.DUKPT_CBC_RESP;
                    }
                } else if(cipherMode == CipherMode.CFB) {
                    switch (derivateUsage) {
                        case MAC_BOTH:
                        case DATA_BOTH:
                            return CipherType.DUKPT_CFB_BOTH;
                        default:
                            return CipherType.DUKPT_CFB_RESP;
                    }
                } else if(cipherMode == CipherMode.OFB) {
                    switch (derivateUsage) {
                        case MAC_BOTH:
                        case DATA_BOTH:
                            return CipherType.DUKPT_OFB_BOTH;
                        default:
                            return CipherType.DUKPT_OFB_RESP;
                    }
                }
            } else if(keyType == KeyType.AES) {
                switch (cipherMode) {
                    case ECB:
                        return CipherType.AES_DUKPT_ECB;
                    case CBC:
                        return CipherType.AES_DUKPT_CBC;
                }
            }
        } else {
            KeyType keyType = key.getKeyType();
            if(keyType == KeyType.DES) {
                switch (cipherMode) {
                    case ECB:
                        return CipherType.DES_ECB;
                    case CBC:
                        return CipherType.DES_CBC;
                    case CFB:
                        return CipherType.DES_CFB;
                    case OFB:
                        return CipherType.DES_OFB;
                }
            } else if(keyType == KeyType.AES) {
                switch (cipherMode) {
                    case ECB:
                        return CipherType.AES_ECB;
                    case CBC:
                        return CipherType.AES_CBC;
                    case CFB:
                        return CipherType.AES_CFB;
                    case OFB:
                        return CipherType.AES_OFB;
                }
            }
        }
        throw new NSDKIllegalParameterException();
    }

    public static TLVPack newTLVPack() {
        return new TLVPack();
    }

    public static class TLVPack {
        private ByteArrayOutputStream bos = null;

        private TLVPack() {
            bos = new ByteArrayOutputStream();
        }

        public int append(int tag, byte[] value){
            byte tmp = 0;
            int i;
            boolean mark = false;
            for(i=3; i>=0; i--){
                tmp = (byte) ((tag>>(i*8))&0xFF);
                if(tmp != 0){
                    mark = true;
                }
                if(mark){
                    bos.write(tmp);
                }
            }

            int len = value.length;

            if(len <= 0x7F){
                bos.write(len);
            } else {
                for(i=3; i>=0; i--){
                    tmp = (byte) ((len>>(i*8))&0xFF);
                    if(tmp != 0){
                        break;
                    }
                }

                tmp = (byte) (0x80|i+1);
                bos.write(tmp);

                for(; i>=0; i--){
                    tmp = (byte) ((len>>(i*8))&0xFF);
                    bos.write(tmp);
                }
            }

            try {
                bos.write(value);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }

        public int append(int tag, CipherType cipherType, KeyUsage keyUsage, PaddingMode paddingMode, byte[] iv) {
            byte tmp = 0;
            int i;
            boolean mark = false;
            for(i=3; i>=0; i--){
                tmp = (byte) ((tag>>(i*8))&0xFF);
                if(tmp != 0){
                    mark = true;
                }
                if(mark){
                    bos.write(tmp);
                }
            }

            int len = 6;
            if(iv != null) {
                len += iv.length;
            }

            if(len <= 0x7F){
                bos.write(len);
            } else {
                for(i=3; i>=0; i--){
                    tmp = (byte) ((len>>(i*8))&0xFF);
                    if(tmp != 0){
                        break;
                    }
                }

                tmp = (byte) (0x80|i+1);
                bos.write(tmp);

                for(; i>=0; i--){
                    tmp = (byte) ((len>>(i*8))&0xFF);
                    bos.write(tmp);
                }
            }

            try {
                bos.write(cipherType.getCode());
                bos.write(keyUsage.getCode());
                bos.write(paddingMode.getCode());
                if(iv != null && iv.length > 0) {
                    bos.write(iv.length);
                    bos.write(iv);
                } else {
                    bos.write(0);
                }
                bos.write(new byte[]{0x00, 0x00});
            } catch (IOException e) {
                e.printStackTrace();
            }

            return 0;
        }

        public int append(int tag, SymmetricKey key, AlgorithmParameters parameters) {

            CipherMode cipherMode = parameters.getCipherMode();
            CipherType cipherType = CipherType.DES_ECB;
            if(key instanceof DUKPTDerivateKey) {
                KeyType keyType = ((DUKPTDerivateKey) key).getDerivateKeyType();
                if(keyType == KeyType.DES) {
                    DUKPTDerivateUsage derivateUsage = ((DUKPTDerivateKey) key).getDerivateUsage();
                    if(cipherMode == CipherMode.ECB) {
                        switch (derivateUsage) {
                            case MAC_BOTH:
                            case DATA_BOTH:
                                cipherType = CipherType.DUKPT_ECB_BOTH;
                                break;
                            default:
                                cipherType = CipherType.DUKPT_ECB_RESP;
                                break;
                        }
                    } else if(cipherMode == CipherMode.CBC) {
                        switch (derivateUsage) {
                            case MAC_BOTH:
                            case DATA_BOTH:
                                cipherType = CipherType.DUKPT_CBC_BOTH;
                                break;
                            default:
                                cipherType = CipherType.DUKPT_CBC_RESP;
                                break;
                        }
                    } else if(cipherMode == CipherMode.CFB) {
                        switch (derivateUsage) {
                            case MAC_BOTH:
                            case DATA_BOTH:
                                cipherType = CipherType.DUKPT_CFB_BOTH;
                                break;
                            default:
                                cipherType = CipherType.DUKPT_CFB_RESP;
                                break;
                        }
                    } else if(cipherMode == CipherMode.OFB) {
                        switch (derivateUsage) {
                            case MAC_BOTH:
                            case DATA_BOTH:
                                cipherType = CipherType.DUKPT_OFB_BOTH;
                                break;
                            default:
                                cipherType = CipherType.DUKPT_OFB_RESP;
                                break;
                        }
                    }
                } else if(keyType == KeyType.AES) {
                    switch (cipherMode) {
                        case ECB:
                            cipherType = CipherType.AES_DUKPT_ECB;
                            break;
                        case CBC:
                            cipherType = CipherType.AES_DUKPT_CBC;
                            break;
                        default:
                            return -1;
                    }
                }
            } else {
                KeyType keyType = key.getKeyType();
                if(keyType == KeyType.DES) {
                    switch (cipherMode) {
                        case ECB:
                            cipherType = CipherType.DES_ECB;
                            break;
                        case CBC:
                            cipherType = CipherType.DES_CBC;
                            break;
                        case CFB:
                            cipherType = CipherType.DES_CFB;
                            break;
                        case OFB:
                            cipherType = CipherType.DES_OFB;
                            break;
                        default:
                            return -1;
                    }
                } else if(keyType == KeyType.AES) {
                    switch (cipherMode) {
                        case ECB:
                            cipherType = CipherType.AES_ECB;
                            break;
                        case CBC:
                            cipherType = CipherType.AES_CBC;
                            break;
                        case CFB:
                            cipherType = CipherType.AES_CFB;
                            break;
                        case OFB:
                            cipherType = CipherType.AES_OFB;
                            break;
                        default:
                            return -1;
                    }
                }
            }

            byte tmp = 0;
            int i;
            boolean mark = false;
            for(i=3; i>=0; i--){
                tmp = (byte) ((tag>>(i*8))&0xFF);
                if(tmp != 0){
                    mark = true;
                }
                if(mark){
                    bos.write(tmp);
                }
            }

            byte[] iv = parameters.getIV();
            int len = 7;
            if(iv != null) {
                len += iv.length;
            }

            if(len <= 0x7F){
                bos.write(len);
            } else {
                for(i=3; i>=0; i--){
                    tmp = (byte) ((len>>(i*8))&0xFF);
                    if(tmp != 0){
                        break;
                    }
                }

                tmp = (byte) (0x80|i+1);
                bos.write(tmp);

                for(; i>=0; i--){
                    tmp = (byte) ((len>>(i*8))&0xFF);
                    bos.write(tmp);
                }
            }

            try {
                bos.write(key.getKeyID());
                bos.write(cipherType.getCode());
                bos.write(key.getKeyUsage().getCode());
                bos.write(parameters.getPaddingMode().getCode());
                if(iv != null) {
                    bos.write(iv.length);
                    bos.write(iv);
                } else {
                    bos.write(0);
                }
                bos.write(new byte[]{0x00, 0x00});
            } catch (IOException e) {
                e.printStackTrace();
            }
            return 0;
        }

        public byte[] pack() {
            try {
                return bos.toByteArray();
            } finally {
                if (bos != null) {
                    try {
                        bos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    bos = null;
                }
            }
        }
    }
}
