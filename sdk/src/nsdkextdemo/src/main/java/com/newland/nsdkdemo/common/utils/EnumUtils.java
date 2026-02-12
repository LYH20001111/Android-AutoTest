package com.newland.nsdkdemo.common.utils;

import android.content.Context;
import android.widget.ArrayAdapter;

import com.newland.nsdk.core.api.common.crypto.AsymCryptoMode;
import com.newland.nsdk.core.api.common.crypto.AsymEncodingMode;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.crypto.KCVMode;
import com.newland.nsdk.core.api.common.crypto.MACType;
import com.newland.nsdk.core.api.common.crypto.MessageDigestType;
import com.newland.nsdk.core.api.common.crypto.PaddingMode;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyType;
import com.newland.nsdk.core.api.common.keymanager.AsymKeyUsage;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.DUKPTDerivateUsage;
import com.newland.nsdk.core.api.common.keymanager.ECCType;
import com.newland.nsdk.core.api.common.keymanager.KDFType;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.common.keymanager.KeyUsage;
import com.newland.nsdk.core.api.common.serialport.BaudRate;
import com.newland.nsdk.core.api.common.serialport.DataBits;
import com.newland.nsdk.core.api.common.serialport.ParityBit;
import com.newland.nsdk.core.api.common.serialport.StopBits;
import com.newland.nsdk.core.api.external.cardemulator.EmulateCardType;
import com.newland.nsdk.core.api.external.cardemulator.EmulateEventType;
import com.newland.nsdk.core.api.external.cardemulator.EmulateFileType;
import com.newland.nsdk.core.api.external.devicemanager.BeeperTone;
import com.newland.nsdk.core.api.external.keyboard.KeyboardMode;
import com.newland.nsdk.core.api.external.setting.ExtSettings;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;

import java.nio.charset.StandardCharsets;

public class EnumUtils {
    private static final byte NFC_START = (byte) 0x03;
    private static final byte NFC_TAIL = (byte) 0xFE;
    private static final byte NFC_FORUM_TYPE = (byte) 0xD1;
    private static final byte NFC_RECORD_TYPE_LEN = (byte) 0x01;
    private static final byte NFC_URI_TYPE = (byte) 0x55;
    public static AsymCryptoMode getAsymCryptoMode(String tmpStr) {
        for (AsymCryptoMode ac : AsymCryptoMode.values()) {
            if (ac.name().equals(tmpStr)) {
                return ac;
            }
        }
        return null;
    }

    public static KeyType getKeyType(String tmpStr) {
        for (KeyType kt : KeyType.values()) {
            if (kt.name().equals(tmpStr)) {
                return kt;
            }
        }
        return null;
    }

    public static DUKPTDerivateUsage getDukptDerivateUsage(String tmpStr) {
        for (DUKPTDerivateUsage ddu : DUKPTDerivateUsage.values()) {
            if(ddu.name().equals(tmpStr)) {
                return ddu;
            }
        }
        return null;
    }

    public static MACType getMacType(String tmpStr) {
        MACType MacType = null;
        for (MACType mt : MACType.values()) {
            if(mt.name().equals(tmpStr)) {
                MacType = mt;
                break;
            }
        }
        return MacType;
    }

    public static KeyUsage getKeyUsage(String tempStr) {
        for (KeyUsage item : KeyUsage.values()) {
            if (item.name().equals(tempStr)) {
                return item;
            }
        }
        return null;
    }

    public static AsymEncodingMode getAsymEncodingMode(String tempStr) {
        for (AsymEncodingMode e : AsymEncodingMode.values()) {
            if (e.name().equals(tempStr)) {
                return e;
            }
        }
        return null;
    }

    public static MessageDigestType getMessageDigestType(String tempStr) {
        for (MessageDigestType m : MessageDigestType.values()) {
            if (m.name().equals(tempStr)) {
                return m;
            }
        }
        return null;
    }

    public static AsymKeyUsage getAsymKeyUsage(String tempStr) {
        for (AsymKeyUsage u : AsymKeyUsage.values()) {
            if (u.name().equals(tempStr)) {
                return u;
            }
        }
        return null;
    }

    public static AsymKeyType getAsymKeyType(String tempStr) {
        for (AsymKeyType t : AsymKeyType.values()) {
            if (t.name().equals(tempStr)) {
                return t;
            }
        }
        return null;
    }

    public static CipherType getCipherType(String tempStr) {
        for (CipherType item : CipherType.values()) {
            if (item.name().equals(tempStr)) {
                return item;
            }
        }
        return null;
    }

    public static CipherMode getCipherMode(String tempStr) {
        for(CipherMode cm : CipherMode.values()) {
            if (cm.name().equals(tempStr)) {
                return cm;
            }
        }

        return null;
    }

    public static PaddingMode getPaddingMode(String tempStr) {
        for (PaddingMode item : PaddingMode.values()) {
            if (item.name().equals(tempStr)) {
                return item;
            }
        }
        return null;
    }
    public static int getKeyLen(String tempStr) {
        int keylen = 0;
        switch (tempStr) {
            case "8":
               keylen = 8;
                break;
            case "16":
               keylen = 16;
                break;
            case "24":
               keylen = 24;
                break;
            case "32":
               keylen = 32;
                break;
            default:
                break;
        }
        return keylen;
    }

    public static KCVMode getKcvMode(String tempStr) {
        for(KCVMode kcm : KCVMode.values()) {
            if(kcm.name().equals(tempStr)) {
                return kcm;
            }
        }
        return null;
    }



    public static String getMessageDigestInstance(String tempStr) {
        String messageDigestInstance = null;
        switch (tempStr) {
            case "NONE":
                messageDigestInstance = "NONE";
                break;
            case "SHA1":
                messageDigestInstance = "SHA-1";
                break;
            case "SHA224":
                messageDigestInstance = "SHA-224";
                break;
            case "SHA256":
                messageDigestInstance = "SHA-256";
                break;
            case "SHA384":
                messageDigestInstance = "SHA-384";
                break;
            case "SHA512":
                messageDigestInstance = "SHA-512";
                break;
            case "SM3":
                messageDigestInstance = "SM-3";
                break;
            default:
                break;
        }
        return messageDigestInstance;
    }
    public static KDFType getKDFType(String tempStr) {
        KDFType kdfType = null;
        for (KDFType item : KDFType.values()) {
            if(item.name().equals(tempStr)) {
                kdfType = item;
                break;
            }
        }
        return kdfType;
    }

    public static ECCType getECCType(String tempStr) {
        ECCType eccType = null;
        for (ECCType item : ECCType.values()) {
            if(item.name().equals(tempStr)) {
                eccType = item;
                break;
            }
        }
        return eccType;
    }

    public static byte getDefaultKeyID(String tempStr) {
        byte defaultKeyID = 1;
        switch (tempStr) {
            case "MKSK_DES_INDEX_MK":
                defaultKeyID = AppConfig.Keys.MKSK_DES_INDEX_MK;
                break;
            case "MKSK_DES_INDEX_WK_PIN":
                defaultKeyID = AppConfig.Keys.MKSK_DES_INDEX_WK_PIN;
                break;
            case "MKSK_DES_INDEX_WK_DATA":
                defaultKeyID = AppConfig.Keys.MKSK_DES_INDEX_WK_DATA;
                break;
            case "MKSK_DES_INDEX_WK_MAC":
                defaultKeyID = AppConfig.Keys.MKSK_DES_INDEX_WK_MAC;
                break;
            case "MKSK_DES_INDEX_WK_TRACK":
                defaultKeyID = AppConfig.Keys.MKSK_DES_INDEX_WK_TRACK;
                break;
            case "MKSK_AES_INDEX_MK":
                defaultKeyID = AppConfig.Keys.MKSK_AES_INDEX_MK;
                break;
            case "MKSK_AES_INDEX_WK_PIN":
                defaultKeyID = AppConfig.Keys.MKSK_AES_INDEX_WK_PIN;
                break;
            case "MKSK_AES_INDEX_WK_DATA":
                defaultKeyID = AppConfig.Keys.MKSK_AES_INDEX_WK_DATA;
                break;
            case "MKSK_AES_INDEX_WK_MAC":
                defaultKeyID = AppConfig.Keys.MKSK_AES_INDEX_WK_MAC;
                break;
            case "MKSK_AES_INDEX_WK_TRACK":
                defaultKeyID = AppConfig.Keys.MKSK_AES_INDEX_WK_TRACK;
                break;
            case "DUKPT_DES_INDEX":
                defaultKeyID = AppConfig.Keys.DUKPT_DES_INDEX;
                break;
            case "DUKPT_AES_INDEX":
                defaultKeyID = AppConfig.Keys.DUKPT_AES_INDEX;
                break;
            case "TR31_KEK":
                defaultKeyID = AppConfig.Keys.TR31_KEK;
                break;
            case "TR31_KEY":
                defaultKeyID = AppConfig.Keys.TR31_KEY;
                break;
            case "RKI_SK_ID":
                defaultKeyID = AppConfig.Keys.RKI_SK_ID;
                break;
            case "RKI_ECDHE_SK_ID":
                defaultKeyID = AppConfig.Keys.RKI_ECDHE_SK_ID;
                break;
            case "RKI_DEMO_DEVICE_KEY_ID":
                defaultKeyID = AppConfig.Keys.RKI_DEMO_DEVICE_KEY_ID;
                break;
            case "RKI_NORMAL_DEVICE_KEY_ID":
                defaultKeyID = AppConfig.Keys.RKI_NORMAL_DEVICE_KEY_ID;
                break;
            default:
                break;
        }
        return defaultKeyID;
    }

    public static byte getAsymDefaultKeyID(String tempStr) {
        byte asymDefaultKeyID = 1;
        switch (tempStr) {
            case "ASYM_KEY_DISTRIBUTION_ID":
                asymDefaultKeyID = AppConfig.Keys.ASYM_KEY_DISTRIBUTION_ID;
                break;
            case "ASYM_AUTH_ID":
                asymDefaultKeyID = AppConfig.Keys.ASYM_AUTH_ID;
                break;
            case "ASYM_DATA_ID":
                asymDefaultKeyID = AppConfig.Keys.ASYM_DATA_ID;
                break;
            default:
                break;
        }
        return asymDefaultKeyID;
    }

    public static KeyboardMode getKeyboardMode(String tempStr) {
        KeyboardMode keyboardMode = null;
        for(KeyboardMode km : KeyboardMode.values()) {
            if(km.name().equals(tempStr)) {
                keyboardMode = km;
            }
        }
        return keyboardMode;
    }

    public static BeeperTone getBeeperTone(String tempStr) {
        BeeperTone beeperTone = null;
        for (BeeperTone bp : BeeperTone.values()) {
            if(bp.name().equals(tempStr)) {
                beeperTone = bp;
            }
        }
        return beeperTone;
    }

    public static ArrayAdapter getAESDUKPTCipherTypeArrayAdapter(Context context) {
        String[] AES_DUKPT_cipherType = new String[] {"AES_DUKPT_ECB", "AES_DUKPT_CBC"};
        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.spinner_items, AES_DUKPT_cipherType);
        return adapter;
    }

    public static ArrayAdapter getOtherCipherTypeArrayAdapter(Context context) {
        String[] Others_cipherType = new String[] {"DES_ECB", "DES_CBC", "DES_CFB", "DES_OFB", "AES_ECB", "AES_CBC", "AES_CFB", "AES_OFB", "DUKPT_ECB_RESP", "DUKPT_ECB_BOTH", "DUKPT_CBC_RESP", "DUKPT_CBC_BOTH",
                "DUKPT_CFB_RESP", "DUKPT_CFB_BOTH", "DUKPT_OFB_RESP", "DUKPT_OFB_BOTH", "SM4_ECB", "SM4_CBC"};
        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.spinner_items, Others_cipherType);
        return adapter;
    }

    public static ArrayAdapter getAESDUKPTMacTypeArrayAdapter(Context context) {
        String[] mactypes = new String[] {"AES_DUKPT_LAST", "AES_DUKPT_X99", "AES_DUKPT_X919", "AES_DUKPT_UNIONPAY_ECB"};
        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.spinner_items, mactypes);
        return adapter;
    }

    public static ArrayAdapter getOtherMacTypeArrayAdapter(Context context) {
        String[] macTypes = new String[] {"TDES_LAST", "TDES_X99", "TDES_X919", "TDES_UNIONPAY_ECB", "DUKPT_LAST", "DUKPT_X99", "DUKPT_X919", "DUKPT_UNIONPAY_ECB",
                "DUKPT_RESP_LAST", "DUKPT_RESP_X99", "DUKPT_RESP_X919", "DUKPT_RESP_UNIONPAY_ECB", "AES_LAST", "AES_X99", "AES_DUKPT_LAST",
                "AES_DUKPT_X99", "AES_DUKPT_X919", "AES_DUKPT_UNIONPAY_ECB", "SM4_LAST", "SM4_X99", "SM4_UNIONPAY_ECB"};
        ArrayAdapter adapter = new ArrayAdapter(context, R.layout.spinner_items, macTypes);
        return adapter;
    }

    public static BaudRate getBaudRate(String tempStr) {
        BaudRate baudRate = null;
        for (BaudRate br : BaudRate.values()) {
            if (br.name().equals(tempStr)) {
                baudRate = br;
            }
        }
        return baudRate;
    }

    public static DataBits getDataBits(String tempStr) {
        DataBits dataBits = null;
        for (DataBits db : DataBits.values()) {
            if (db.name().equals(tempStr)) {
                dataBits = db;
            }
        }
        return dataBits;
    }

    public static ParityBit getParityBit(String tempStr) {
        ParityBit parityBit = null;
        for (ParityBit pb : ParityBit.values()) {
            if (pb.name().equals(tempStr)) {
                parityBit = pb;
            }
        }
        return parityBit;
    }

    public static StopBits getStopBits(String tempStr) {
        StopBits stopBits = null;
        for (StopBits sb : StopBits.values()) {
            if (sb.name().equals(tempStr)) {
                stopBits = sb;
            }
        }
        return stopBits;
    }

    public static String getDataBitsValue(String tempStr) {
        String dataBitsValue;
        switch (tempStr) {
            case "DATA_BIT_5":
                dataBitsValue = "5";
                break;
            case "DATA_BIT_6":
                dataBitsValue = "6";
                break;
            case "DATA_BIT_7":
                dataBitsValue = "7";
                break;
            case "DATA_BIT_8":
                dataBitsValue = "8";
                break;
            default:
                dataBitsValue = "5";
                break;
        }
        return dataBitsValue;
    }

    public static String getParityBitValue(String tempStr) {
        String parityBitValue;
        switch (tempStr) {
            case "NO_CHECK":
                parityBitValue = "N";
                break;
            case "ODD_CHECK":
                parityBitValue = "O";
                break;
            case "EVEN_CHECK":
                parityBitValue = "E";
                break;
            default:
                parityBitValue = "N";
                break;

        }
        return parityBitValue;
    }

    public static String getStopBitsValue(String tempStr) {
        String stopBitsValue;
        switch (tempStr) {
            case "STOP_BIT_ONE":
                stopBitsValue = "1";
                break;
            case "STOP_BIT_TWO":
                stopBitsValue = "2";
                break;
            default:
                stopBitsValue = "1";
                break;
        }
        return stopBitsValue;
    }

    public static boolean getIsBlockEnabled(String tempStr) {
        if ("B".equals(tempStr)) {
            return true;
        } else if ("N".equals(tempStr)) {
            return false;
        }
        return false;
    }

    public static EmulateCardType getEmulateCardType(String tempStr) {
        for (EmulateCardType cardType : EmulateCardType.values()) {
            if (tempStr.equals(cardType.name())) {
                return cardType;
            }
        }
        return null;
    }

    public static EmulateFileType getEmulateFileType(String tempStr) {
        for (EmulateFileType fileType : EmulateFileType.values()) {
            if (tempStr.equals(fileType.name())) {
                return fileType;
            }
        }
        return null;
    }

    public static EmulateEventType getEmulateEventType(String tempStr) {
        for (EmulateEventType emulateEventType: EmulateEventType.values()) {
            if (tempStr.equals(emulateEventType.name())) {
                return emulateEventType;
            }
        }
        return null;
    }

    public static String getPropertyKey(String tempStr) {
        switch (tempStr) {
            case "RO_BUILD_MODEL":
                return ExtSettings.RO_BUILD_MODEL;
            case "RO_BUILD_BOOT_VERSION":
                return ExtSettings.RO_BUILD_BOOT_VERSION;
            case "RO_BUILD_DEVCFG_VERSION":
                return ExtSettings.RO_BUILD_DEVCFG_VERSION;
            case "RO_OS_VERSION":
                return ExtSettings.RO_OS_VERSION;
            case "RO_NAPI_API_VERSION":
                return ExtSettings.RO_NAPI_API_VERSION;
            case "RO_NAPI_LIB_VERSION":
                return ExtSettings.RO_NAPI_LIB_VERSION;
            case "RO_PCI_FW_VERSION":
                return ExtSettings.RO_PCI_FW_VERSION;
            case "RO_PCI_HW_VERSION":
                return ExtSettings.RO_PCI_HW_VERSION;
            case "RO_POS_CPU_TYPE":
                return ExtSettings.RO_POS_CPU_TYPE;
            case "RO_POS_SN":
                return ExtSettings.RO_POS_SN;
            case "RO_POS_PN":
                return ExtSettings.RO_POS_PN;
            case "RO_POS_BOARD_VER":
                return ExtSettings.RO_POS_BOARD_VER;
            case "RO_POS_BOARD_NUM":
                return ExtSettings.RO_POS_BOARD_NUM;
            case "RO_RFID_VERSION":
                return ExtSettings.RO_RFID_VERSION;
            case "RO_POS_CUSTOMERID":
                return ExtSettings.RO_POS_CUSTOMERID;
            case "RO_RFID_TYPE":
                return ExtSettings.RO_RFID_TYPE;
            case "RO_POS_HW":
                return ExtSettings.RO_POS_HW;
            case "PERSIST_SYS_LANGUAGE":
                return ExtSettings.PERSIST_SYS_LANGUAGE;
            case "PERSIST_SYS_AUTORUN":
                return ExtSettings.PERSIST_SYS_AUTORUN;
            case "PERSIST_SYS_BACKLIGHT_ONOFF":
                return ExtSettings.PERSIST_SYS_BACKLIGHT_ONOFF;
            case "PERSIST_SYS_KEYVOL":
                return ExtSettings.PERSIST_SYS_KEYVOL;
            case "SYS_LED_COLOR":
                return ExtSettings.SYS_LED_COLOR;
            case "RO_BUILD_SP_MASTER_VERSION":
                return ExtSettings.RO_BUILD_SP_MASTER_VERSION;
            case "RO_BUILD_SP_MAPP_VERSION":
                return ExtSettings.RO_BUILD_SP_MAPP_VERSION;
            case "RO_BUILD_FW_TYPE":
                return ExtSettings.RO_BUILD_FW_TYPE;
            case "PERSIST_SYS_BRIGHTNESS":
                return ExtSettings.PERSIST_SYS_BRIGHTNESS;
            case "PERSIST_SYS_KEY_BACKLIGHT":
                return ExtSettings.PERSIST_SYS_KEY_BACKLIGHT;
            case "PERSIST_SYS_TIME_ZONE":
                return ExtSettings.PERSIST_SYS_TIME_ZONE;
            case "SYS_POWER_MODE":
                return ExtSettings.SYS_POWER_MODE;
            case "SYS_ETH_DHCP":
                return ExtSettings.SYS_ETH_DHCP;
            case "SYS_COMM_PRIORITY":
                return ExtSettings.SYS_COMM_PRIORITY;
            case "SYS_BEEP_VOLUME":
                return ExtSettings.SYS_BEEP_VOLUME;
            case "STATISTICS_POWER_RUN_TIME":
                return ExtSettings.STATISTICS_POWER_RUN_TIME;
            case "SYS_BATTERY_STATUS":
                return ExtSettings.SYS_BATTERY_STATUS;
            case "SYS_BATTERY_LEVEL":
                return ExtSettings.SYS_BATTERY_LEVEL;
            case "PERSIST_SYS_NET_AUTOCONNTYPE":
                return ExtSettings.PERSIST_SYS_NET_AUTOCONNTYPE;
            case "PERSIST_SYS_TIME_FORMAT":
                return ExtSettings.PERSIST_SYS_TIME_FORMAT;
            case "PERSIST_SYS_DATE_FORMAT":
                return ExtSettings.PERSIST_SYS_DATE_FORMAT;
        }
        return null;
    }

    public static byte[] spliceDataForConfiguration(EmulateCardType cardType, String url, byte uriCode) {
        byte[] tempData = new byte[100];
        byte[] result = null;
        int inLen = url.length();
        if (cardType == EmulateCardType.T2T) {
            tempData[0] = NFC_START;
            tempData[1] = (byte) (inLen + 5);
            tempData[2] = NFC_FORUM_TYPE;
            tempData[3] = NFC_RECORD_TYPE_LEN;
            tempData[4] = (byte) (inLen + 1);
            tempData[5] = NFC_URI_TYPE;
            tempData[6] = uriCode;
            System.arraycopy(url.getBytes(StandardCharsets.UTF_8), 0, tempData, 7, inLen);
            System.arraycopy(new byte[] {NFC_TAIL}, 0, tempData, 7 + inLen, 1);
            result = new byte[inLen + 8];
            System.arraycopy(tempData, 0, result, 0, result.length);
        } else if (cardType == EmulateCardType.T4T) {
            int ndefLen = inLen + 5;
            tempData[0] = (byte) (ndefLen >> 8);
            tempData[1] = (byte) ndefLen;
            tempData[2] = NFC_FORUM_TYPE;
            tempData[3] = NFC_RECORD_TYPE_LEN;
            tempData[4] = (byte) (inLen + 1);
            tempData[5] = NFC_URI_TYPE;
            tempData[6] = uriCode;
            System.arraycopy(url.getBytes(StandardCharsets.UTF_8), 0, tempData, 7, inLen);
            result = new byte[inLen + 7];
            System.arraycopy(tempData, 0, result, 0, result.length);
        }
        return result;
    }
}
