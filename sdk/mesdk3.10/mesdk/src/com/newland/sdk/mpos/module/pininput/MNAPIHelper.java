package com.newland.sdk.mpos.module.pininput;

import com.newland.sdk.me.module.pininput.KeyUsage;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.pin.DukptDerivedMode;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.pin.KeyType;
import com.newland.sdk.module.pin.LoadKeyMode;
import com.newland.sdk.module.pin.LoadWKMode;
import com.newland.sdk.module.pin.MacAlgorithm;
import com.newland.sdk.module.pin.MacType;
import com.newland.sdk.module.pin.PaddingMode;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.utils.ISOUtils;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/8
 */
public class MNAPIHelper {

    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MNAPIHelper");

    private static final int ERROR_PARAM = -1;
    private static final String MSG_PARAM = "parameter error";
    private static final int LEN_STX = 1;
    private static final int LEN_SEPARATOR = 1;
    private static final int LEN_MESSAGETYPE = 2;
    private static final int LEN_LENGTH = 2; //BCD
    private static final int LEN_LRC = 1;
    private static final int LEN_ETX = 1;
    private static final byte[] STX_OVERSEAS = new byte[]{0x02};
    private static final byte[] ETX_OVERSEAS = new byte[]{0x03};
    private static final byte[] SEPARATOR_SLASH = new byte[]{0x2F};//"/"

    public static final int BLOCK_FIRST = 0x00;
    public static final int BLOCK_NEXT = 0x01;
    public static final int BLOCK_LAST = 0x02;
    public static final int BLOCK_ONLY = 0x03;

    public static final int CALC_ENCRYPT = 1;
    public static final int CALC_DECRYPT = 2;

    //7.1 Generate Key Request (0x43 0x30)->[Method] field
    public String getMethod(LoadKeyMode loadKeyMode){
        if(LoadKeyMode.CUSTOM_ENCRYPT == loadKeyMode){return "01";}//0x01 = CIPHER,
        else if(LoadKeyMode.PLAIN == loadKeyMode){return "00";}//0x00 = CLEAR,
        else if(LoadKeyMode.TR31 == loadKeyMode){return "02";}//0x02 = TR31,
//        else if(LoadKeyMode.GISKE == loadKeyMode){return "07";}//0x07 = GISKE,
//        else if(LoadKeyMode.RANDOM == loadKeyMode){return "03";}//0x03 = RANDOM,
//        else if(LoadKeyMode.RANDOM_OUT == loadKeyMode){return "04";}//0x04 = RANDOM_OUT,
        throw new DeviceRTException(ERROR_PARAM,MSG_PARAM);
        //0x05 = DUKPT_DERIVE,
        //0x06 = DIVERSIFY_X,
    }

    //7.1 Generate Key Request (0x43 0x30)->[Method] field
    public String getWKMethod(LoadWKMode loadWKMode){
        if(LoadWKMode.ENCRYPT == loadWKMode){return "01";}//0x01 = CIPHER,
        else if(LoadWKMode.PLAIN == loadWKMode){return "00";}//0x00 = CLEAR,
        else if(LoadWKMode.TR31 == loadWKMode){return "02";}//0x02 = TR31,
//        else if(LoadWKMode.GISKE == loadWKMode){return "07";}//0x07 = GISKE,
//        else if(LoadWKMode.RANDOM == loadWKMode){return "03";}//0x03 = RANDOM,
//        else if(LoadWKMode.RANDOM_OUT == loadWKMode){return "04";}//0x04 = RANDOM_OUT,
        throw new DeviceRTException(ERROR_PARAM,MSG_PARAM);
        //0x05 = DUKPT_DERIVE,
        //0x06 = DIVERSIFY_X,
    }

    //7.1 Generate Key Request (0x43 0x30)->[KEK Type] and [Key Type] field
    //7.3 Get Key Information Request (0x43 0x32)->[KEK Type] field
    public String getKeyType(AlgorithmMode algorithmMode){
        if(AlgorithmMode.DES == algorithmMode){return "00";}//0x00 = DES
        else if(AlgorithmMode.SM4 == algorithmMode){return "02";}//0x02 = SM4
        else if(AlgorithmMode.AES == algorithmMode){return "01";}//0x01 = AES
        throw new DeviceRTException(ERROR_PARAM,MSG_PARAM);
        //0x20 = RSA
        //0x21 = ECC
        //0x22 = SM2
    }

    //7.1 Generate Key Request (0x43 0x30)->[KEK Usage] and [Key Usage] field
    //7.3 Get Key Information Request (0x43 0x32)->[Key Usage] field
    public String getKeyUsage(KeyUsage keyUsage){
        if(KeyUsage.MASTER == keyUsage){return "00";}//0x00 = TMK_KEK
        else if(KeyUsage.MASTER_PIN == keyUsage){return "01";}//0x01 = PIN_KEK
        else if(KeyUsage.MASTER_MAC == keyUsage){return "02";}//0x02 = MAC_KEK
        else if(KeyUsage.MASTER_DATA == keyUsage){return "03";}//0x03 = DATA_KEK
        else if(KeyUsage.MASTER_DATA_ENC == keyUsage){return "04";}//0x04 = DATA_ENC_KEK
        else if(KeyUsage.MASTER_TR31 == keyUsage){return "05";}//0x05 = TR31_KEK
        else if(KeyUsage.WORKINGKEY_PIN == keyUsage){return "06";}//0x06 = PIN
        else if(KeyUsage.WORKINGKEY_MAC == keyUsage){return "07";}//0x07 = MAC
        else if(KeyUsage.WORKINGKEY_DATA == keyUsage){return "08";}//0x08 = DATA
        else if(KeyUsage.WORKINGKEY_DATA_ENC_ONLY == keyUsage){return "09";}//0x09 = DATA_ENC_ONLY
        else if(KeyUsage.DUKPT == keyUsage){return "10";}//0x10 = DUKPT
        throw new DeviceRTException(ERROR_PARAM,MSG_PARAM);
        //0x20 = ASYM_AUTH
        //0x21 = ASYM_DATA,
    }

    //7.1 Generate Key Request (0x43 0x30)->[Cipher Mode] field
    public String getCipherMode(CipherMode cipherMode) {
        if(CipherMode.ECB==cipherMode){return "00";}//0x00 = ECB
        else if(CipherMode.CBC==cipherMode){return "01";}//0x01 = CBC
        throw new DeviceRTException(ERROR_PARAM,MSG_PARAM);
    }

    //7.1 Generate Key Request (0x43 0x30)->[Padding Mode] field
    //7.5 Data Encryption/Decryption Request (0x43 0x34)
    public String getPaddingMode(PaddingMode.Mode paddingMode){
        if(PaddingMode.Mode.NONE == paddingMode){return "00";}
        else if(PaddingMode.Mode.PKCS7 == paddingMode){return "01";}//0x01 : PKCS7 padding
        else if(PaddingMode.Mode.ONE_AND_ZEROS == paddingMode){return "02";}//0x02 : ISO/IEC 7816-4 padding.
        else if(PaddingMode.Mode.ZEROS_AND_LEN == paddingMode){return "03";}//0x03 : ANSI X.923 padding.
        else if(PaddingMode.Mode.ZEROS == paddingMode){return "04";}//0x04: zero padding (not reversible!)
        throw new DeviceRTException(ERROR_PARAM,MSG_PARAM);
        //0x00 : Never pad (full blocks only)
    }

    //=============================================================================================//

    //7.5 Data Encryption/Decryption Request (0x43 0x34)->[Mode] field
    public String getMode(int calcMode){
        if(calcMode == CALC_ENCRYPT){
            return "00";//0x00 = Encryption
        }else if(calcMode == CALC_DECRYPT){
            return "01";//0x01 = Decryption
        }
        throw new DeviceRTException(ERROR_PARAM,MSG_PARAM);
    }

    //7.5 Data Encryption/Decryption Request (0x43 0x34)->[Cipher Type] field
    public String getCipherType(KeyManagement keyManagement,AlgorithmMode algorithmMode,CipherMode cipherMode,DukptDerivedMode dukptDerivedMode){
        if(keyManagement == KeyManagement.MKSK){
            if (algorithmMode == AlgorithmMode.DES) {
                if(cipherMode == CipherMode.ECB){
                    return "00";//0x00 = DES_ECB,
                }else if(cipherMode == CipherMode.CBC){
                    return "01";//0x01 = DES_CBC,
                }else if(cipherMode == CipherMode.CFB){
                    return "02";//0x02 = DES_CFB,
                }else if(cipherMode == CipherMode.OFB){
                    return "03";//0x03 = DES_OFB,
                }
            }else if (algorithmMode == AlgorithmMode.AES){
                if(cipherMode == CipherMode.ECB){
                    return "04";//0x04 = AES_ECB,
                }else if(cipherMode == CipherMode.CBC){
                    return "05";//0x05 = AES_CBC,
                }else if(cipherMode == CipherMode.CFB){
                    return "06";//0x06 = AES_CFB,
                }else if(cipherMode == CipherMode.OFB){
                    return "07";//0x07 = AES_OFB,
                }
            }
        }else if(keyManagement == KeyManagement.DUKPT){
            if (algorithmMode == AlgorithmMode.DES) {
                if(cipherMode == CipherMode.ECB){
                    if(dukptDerivedMode == DukptDerivedMode.RESP){
                        return "08";//0x08 = DUKPT_ECB_RESP,
                    } else if(dukptDerivedMode == DukptDerivedMode.BOTH){
                        return "09";//0x09 = DUKPT_ECB_BOTH,
                    }
                }else if(cipherMode == CipherMode.CBC){
                    if(dukptDerivedMode == DukptDerivedMode.RESP){
                        return "0A";//0x0A= DUKPT_CBC_RESP,
                    }else if(dukptDerivedMode == DukptDerivedMode.BOTH){
                        return "0B";//0x0B = DUKPT_CBC_BOTH,
                    }
                }else if(cipherMode == CipherMode.CFB){
                    if(dukptDerivedMode == DukptDerivedMode.RESP){
                        return "0C";//0x0C = DUKPT_CFB_RESP,
                    } else if(dukptDerivedMode == DukptDerivedMode.BOTH){
                        return "0D";//0x0D = DUKPT_CFB_BOTH,
                    }
                }else if(cipherMode == CipherMode.OFB){
                    if(dukptDerivedMode == DukptDerivedMode.RESP){
                        return "0E";//0x0E = DUKPT_OFB_RESP,
                    } else if(dukptDerivedMode == DukptDerivedMode.BOTH){
                        return "0F";//0x0F = DUKPT_OFB_BOTH,
                    }
                }
            }
        }
        throw new DeviceRTException(ERROR_PARAM,MSG_PARAM);
    }


    //7.5 Data Encryption/Decryption Request (0x43 0x34)->[Key Usage] field
    public String getKeyUsage(KeyManagement keyManagement,KeyUsage keyUsage){
        if(keyManagement == KeyManagement.DUKPT) {
            return "10";//0x10 = DUKPT
        }else if(keyManagement == KeyManagement.MKSK) {
            if(keyUsage == KeyUsage.WORKINGKEY_DATA){
                return "08";//0x08 = DATA
            }else if(keyUsage == KeyUsage.WORKINGKEY_DATA_ENC_ONLY){
                return "09";//0x09 = DATA_ENC_ONLY
            }
        }
        throw new DeviceRTException(ERROR_PARAM,MSG_PARAM);
    }
    //7.7 MAC Generation Request (0x43 0x36)
    public String getMacType(MacType macType){
        if(MacType.MKSK_DES_9606 == macType)                   {return "00";}  //0x00 = TDES_9606
        else if(MacType.MKSK_DES_X99 == macType)               {return "01";}  //0x01 = TDES_X99
        else if(MacType.MKSK_DES_X919 == macType)              {return "02";}  //0x02 = TDES_X919
        else if(MacType.MKSK_DES_UNIONPAY_ECB == macType)      {return "03";}  //0x03 = UNIONPAY_ECB
        else if(MacType.DUKPT_DES_9606 == macType)             {return "04";}  //0x04 = DUKPT_9606
        else if(MacType.DUKPT_DES_X99 == macType)              {return "05";}  //0x05 = DUKPT_X99
        else if(MacType.DUKPT_DES_X919 == macType)             {return "06";}  //0x06 = DUKPT_X919
        else if(MacType.DUKPT_DES_UNIONPAY_ECB == macType)     {return "07";}  //0x07 = UNIONPAY_ECB
        else if(MacType.DUKPT_DES_RESP_9606 == macType)        {return "08";}  //0x08 = DUKPT_RESP_9606
        else if(MacType.DUKPT_DES_RESP_X99 == macType)         {return "09";}  //0x09 = DUKPT_RESP_X99
        else if(MacType.DUKPT_DES_RESP_X919 == macType)        {return "0A";}  //0x0A = DUKPT_RESP_X919
        else if(MacType.DUKPT_DES_RESP_UNIONPAY_ECB == macType){return "0B";}  //0x0B = DUKPT_RESP_UNIONPAY_ECB
        else if(MacType.MKSK_AES_9606 == macType)              {return "0C";}  //0x0C = AES_9606
        else if(MacType.MKSK_AES_X99 == macType)               {return "0D";}  //0x0D = AES_X99
        else if(MacType.DUKPT_AES_9606 == macType)             {return "0E";}  //0x0E = AES_DUKPT_LAST_9606
        else if(MacType.DUKPT_AES_X99 == macType)              {return "0F";}  //0x0F = AES_DUKPT_X99
//        else if(MacType.DUKPT_AES_RESP_9606 == macType)        {return "10";}  //0x10 = AES_DUKPT_RESP_LAST_9606
//        else if(MacType.DUKPT_AES_RESP_X99 == macType)         {return "11";}  //0x11 = AES_DUKPT_RESP_X99
        else if(MacType.MKSK_SM4_9606 == macType)              {return "12";}  //0x12 = SM4_LAST_9606
        else if(MacType.MKSK_SM4_X99 == macType)               {return "13";}  //0x13 = SM4_X99
        else if(MacType.MKSK_SM4_UNIONPAY == macType)          {return "14";}  //0x14 = SM4_UNIONPAY_ECB
        throw new DeviceRTException(ERROR_PARAM,MSG_PARAM);
    }

    //7.11Delete Key request (0x42 0x4a)
    public String getKeyType(KeyType keyType){
        if(keyType == KeyType.MASTER_KEY){return "03";}//03-Master key,
        else if(keyType == KeyType.PIN_KEY){return "00";}//0 - PIN,
        else if(keyType == KeyType.MAC_KEY){return "01";}//1- MAC,
        else if(keyType == KeyType.TRACK_KEY){return "02";}//2 – Data,
        //04-Dukpt,
        //05-RSA
        throw new DeviceRTException(ERROR_PARAM,MSG_PARAM);
    }
    //7.11Delete Key request (0x42 0x4a)
    public String getBlockFormat(AlgorithmMode algorithm){
        //0-DES,1-AES,2-SM4
        if(algorithm == AlgorithmMode.DES){return "00";}
        if(algorithm == AlgorithmMode.SM4){return "02";}
        if(algorithm == AlgorithmMode.AES){return "01";}
        throw new DeviceRTException(ERROR_PARAM,MSG_PARAM);
    }

    //7.3 Get Key Information Request (0x43 0x32)->[Key Usage] field
    public String getKeyUsage(KeyType keyType){
        if(KeyType.MASTER_KEY == keyType){return "00";}//0x00 = TMK_KEK
        else if(KeyType.PIN_KEY == keyType){return "06";}//0x06 = PIN
        else if(KeyType.MAC_KEY == keyType){return "07";}//0x07 = MAC
        else if(KeyType.TRACK_KEY == keyType){return "08";}//0x08 = DATA
        throw new DeviceRTException(ERROR_PARAM,MSG_PARAM);
    }

    //7.3 Get Key Information Request (0x43 0x32)
    public static final int KEYINFOID_LENGTH = 0x00;
    public static final int KEYINFOID_KCV = 0x01;
    public static final int KEYINFOID_KSN = 0x02;
    public static final int KEYINFOID_CERTIFICATE = 0x03;
    public static final int KEYINFOID_PKEY_CERTIFICATE_LENGTH = 0x04;
    public static final int KEYINFOID_PUBLIC_KEY = 0x05;
    public static final int KEYINFOID_CMAC_KCV = 0x06;

    public byte[] pack(byte[] messageType, String body0) {
        devicelogger.debug("pack messageType="+ISOUtils.hexString(messageType)+" body0="+body0);
        int offset = 0;

        byte[] body = (body0==null?null:ISOUtils.hex2byte(body0));

        byte[] payload = new byte[LEN_STX + LEN_LENGTH + LEN_MESSAGETYPE + LEN_SEPARATOR + (body == null ? 0 : body.length) + LEN_ETX + LEN_LRC];

        devicelogger.debug("start make request payload...");
        devicelogger.debug("pack up stx[" + Dump.getHexDump(STX_OVERSEAS) + "]");
        System.arraycopy(STX_OVERSEAS, 0, payload, 0, LEN_STX);
        offset += LEN_STX;

        if (body != null) {
            int len = LEN_MESSAGETYPE + LEN_SEPARATOR + body.length;
            byte[] lenbs = InnerUtils.intToBCD(len, LEN_LENGTH * 2, true);
            System.arraycopy(lenbs, 0, payload, offset, LEN_LENGTH);
            devicelogger.debug("pack up len[" + Dump.getHexDump(lenbs) + "]");
            offset += LEN_LENGTH;
        } else {
            int len = LEN_MESSAGETYPE + LEN_SEPARATOR;
            byte[] lenbs = InnerUtils.intToBCD(len, LEN_LENGTH * 2, true);
            System.arraycopy(lenbs, 0, payload, offset, LEN_LENGTH);
            devicelogger.debug("pack up len[" + Dump.getHexDump(lenbs) + "]");
            offset += LEN_LENGTH;
        }

        devicelogger.debug("pack up cmd[" + Dump.getHexDump(messageType) + "]");
        System.arraycopy(messageType, 0, payload, offset, LEN_MESSAGETYPE);
        offset += LEN_MESSAGETYPE;

        devicelogger.debug("pack up signedSymbol[" + Dump.getHexDump(SEPARATOR_SLASH) + "]");
        System.arraycopy(SEPARATOR_SLASH, 0, payload, offset, LEN_SEPARATOR);
        offset += LEN_SEPARATOR;

        if (body != null) {
            devicelogger.debug("pack up body[" + Dump.getHexDump(body) + "]");
            System.arraycopy(body, 0, payload, offset, body.length);
            offset += body.length;
        }

        devicelogger.debug("pack up ETX[" + Dump.getHexDump(ETX_OVERSEAS) + "]");
        System.arraycopy(ETX_OVERSEAS, 0, payload, offset, LEN_ETX);
        offset += LEN_ETX;

        byte[] lrcData = new byte[payload.length - LEN_STX - LEN_LRC];
        System.arraycopy(payload, LEN_STX, lrcData, 0, lrcData.length);
        devicelogger.debug("pack up lrcData[" + Dump.getHexDump(lrcData) + "]");

        byte[] lrc = caculateLRC(lrcData);
        devicelogger.debug("pack up lrc[" + Dump.getHexDump(lrc) + "]");
        System.arraycopy(lrc, 0, payload, offset, LEN_LRC);

        devicelogger.debug("make payload finish...[" + Dump.getHexDump(payload) + "],total len:" + payload.length);
        return payload;
    }

    private byte[] caculateLRC(byte[] payload) {
        int offset = 0;
        byte lrc = payload[0];
        do {
            offset++;
            lrc ^= payload[offset];
        } while (offset < payload.length - 1);

        return new byte[]{lrc};
    }

    public String getData(byte[] data){
        return (data == null ? "" : ISOUtils.hexString(data));
    }
}
