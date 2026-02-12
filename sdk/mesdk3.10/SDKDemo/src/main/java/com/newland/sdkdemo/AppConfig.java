package com.newland.sdkdemo;

import com.newland.sdk.inter.externalpin.AccessType;
import com.newland.sdk.inter.externalpin.ExtParams;
import com.newland.sdk.module.emv.EMVTransInfo;
import com.newland.sdk.module.swiper.SwipResult;
import com.newland.sdkdemo.event.PinEntryListener;

import java.math.BigDecimal;

/**
 * Author by bxy, Date on 2019/5/14 0014.
 */
public class AppConfig {
    public static final String MKSK_DES  = "MKSK_DES";
    public static final String MKSK_SM4  = "MKSK_SM4";
    public static final String MKSK_AES  = "MKSK_AES";
    public static final String DUKPT_DES = "DUKPT_DES";
    public static final String DUKPT_AES = "DUKPT_AES";

    public static String KEY_SYS_ALG = MKSK_DES;
    public static boolean isStandBy = true;
    public static boolean isSimpleFlow = false;

    public static PinEntryListener pinEntryListener;

    public static class Pin{
        public static final int MKSK_DES_INDEX_MK = 100;
        public static final int MKSK_DES_INDEX_WK_PIN = 1;
        public static final int MKSK_DES_INDEX_WK_TRACK = 3;
        public static final int MKSK_DES_INDEX_WK_MAC = 4;

        public static final int MKSK_SM4_INDEX_MK = 1;
        public static final int MKSK_SM4_INDEX_WK_PIN = 2;
        public static final int MKSK_SM4_INDEX_WK_TRACK = 3;
        public static final int MKSK_SM4_INDEX_WK_MAC = 4;

        public static final int MKSK_AES_INDEX_MK = 1;
        public static final int MKSK_AES_INDEX_WK_PIN = 2;
        public static final int MKSK_AES_INDEX_WK_TRACK = 3;
        public static final int MKSK_AES_INDEX_WK_MAC = 4;

        public static final int DUKPT_DES_INDEX = 1;
        public static final int DUKPT_AES_INDEX = 2;

        public static byte[] encryptResult = null;
    }

    public static class EMV{
        /**
         * Password entered is complete
         */
        public static BigDecimal amt;
        public static String icCardNum;
        public static EMVTransInfo emvTransInfo;// Pass ic/rf Consumption message params
        public static byte[] ic55Data;
        public static byte[] pinBlock;
        public static SwipResult swipResult;// Swiping result
    }
    public static int INDEX_FRAGMENT_START = -1;
    public static int INDEX_FRAGMENT_PIN = -1;

    public static class ScanResult{
        public static final int SCAN_FINISH = 0;
        public static final int SCAN_RESPONSE = 1;
        public static final int SCAN_ERROR = 2;
        public static final int SCAN_TIMEOUT = 3;
        public static final int SCAN_CANCEL = 4;
    }

    public static class ScanType {
        /**
         * Front scan
         */
        public static final int FRONT = 1;
        /**
         * Back scan
         */
        public static final int BACK = 0;
    }

    public static PinEntryListener getPinEntryListener() {
        return pinEntryListener;
    }

    public static void setPinEntryListener(PinEntryListener pinEntryListener) {
        AppConfig.pinEntryListener = pinEntryListener;
    }

    public static boolean isExternalEmv = false;
    public static boolean isUsePinpadByDockUSB=false;
    public static boolean isUsePinpadByDockRS232=false;

    public static String accNo;
    public static byte[] modulus;
    public static byte[] exponent;

    public enum PinInputResult {
        SUCCESS,
        FAIL,
        CANCEL,
        TIME_OUT,
        BYPASS,
    }
}

