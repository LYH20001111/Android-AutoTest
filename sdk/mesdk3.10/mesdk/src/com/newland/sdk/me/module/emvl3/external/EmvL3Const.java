package com.newland.sdk.me.module.emvl3.external;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/30
 */
public class EmvL3Const {
    public EmvL3Const() {
    }

    public class DetectEvent {
        public static final int CANCELL = 1;
        public static final int SUCC_MANUAL = 2;

        public DetectEvent() {
        }
    }

    public class EntryMode {
        public static final int MANUAL = 1;
        public static final int MSR = 2;
        public static final int ICC = 5;
        public static final int CLSS = 7;
        public static final int CT_FALLBACK = 128;

        public EntryMode() {
        }
    }

    public class Config_OP {
        public static final int UPDATE = 0;
        public static final int GET = 1;
        public static final int REMOVE = 2;
        public static final int FLUSH = 3;

        public Config_OP() {
        }
    }

    public class L3_DATA {
        public static final int PAN = 0;
        public static final int TRACK1 = 1;
        public static final int TRACK2 = 2;
        public static final int TRACK3 = 3;
        public static final int DD_CARD_TRACK1 = 4;
        public static final int DD_CARD_TRACK2 = 5;
        public static final int EXPIRE_DATE = 6;
        public static final int SERVICE_CODE = 7;
        public static final int CARDHOLDER_NAME = 8;
        public static final int POS_ENTRY_MODE = 9;
        public static final int CARD_SCHEME_ID = 10;
        public static final int SIGNATURE = 11;
        public static final int ADVISE = 12;
        public static final int ISSUER_SCRIPT_RESULT = 13;

        public L3_DATA() {
        }
    }

    public class UICard {
        public static final int UI_KEYIN = 0;
        public static final int UI_STRIPE = 1;
        public static final int UI_INSERT = 2;
        public static final int UI_TAP = 3;
        public static final int UI_INSERTC_TAP = 4;
        public static final int UI_STRIPE_INSERT = 5;
        public static final int UI_STRIPE_TAP = 6;
        public static final int UI_STRIPE_INSERT_TAP = 7;
        public static final int UI_STRIPE_INSERT_TAP_MANUAL = 8;
        public static final int UI_PRESENTCARD_AGAIN = 9;
        public static final int UI_USE_CHIP = 10;
        public static final int UI_FALLBACK_CT = 11;
        public static final int UI_FALLBACK_CLSS = 12;
        public static final int UI_STRIPE_INSERT_MANUAL = 13;
        public static final int UI_STRIPE_TAP_MANUAL = 14;
        public static final int UI_INSERT_TAP_MANUAL = 15;
        public static final int UI_STRIPE_MANUAL = 16;
        public static final int UI_INSERT_MANUAL = 17;
        public static final int UI_TAP_MANUAL = 18;

        public UICard() {
        }
    }

    public class UIEvent {
        public static final int UI_PRESENT_CARD = 0;
        public static final int UI_PROCESSING = 1;
        public static final int UI_CAPK_LOAD_FAIL = 2;
        public static final int UI_SEE_PHONE = 3;
        public static final int UI_CARDNUM_CONFIRM = 4;
        public static final int UI_CHIP_ERR_RETRY = 5;

        public UIEvent() {
        }
    }

    public class CardInterface {
        public static final int MAGSTRIPE = 1;
        public static final int CONTACT = 2;
        public static final int CONTACTLESS = 4;
        public static final int MANUAL = 8;

        public CardInterface() {
        }
    }

    public class PINType {
        public static final int PIN_ONLINE = 0;
        public static final int PIN_OFFLINE = 1;
        public static final int PIN_OFFLINE_ENCIPHERED = 2;

        public PINType() {
        }
    }

    public class MODULE {
        public static final int L3_MODULE_API = 0;
        public static final int L3_MODULE_EMV = 1;
        public static final int L3_MODULE_EP = 2;
        public static final int L3_MODULE_QPBOC = 3;
        public static final int L3_MODULE_PAYPASS = 4;
        public static final int L3_MODULE_PAYWAVE = 5;
        public static final int L3_MODULE_EXPRESSPAY = 6;
        public static final int L3_MODULE_DPAS = 7;
        public static final int L3_MODULE_JCB = 8;
        public static final int L3_MODULE_PURE = 9;
        public static final int L3_MODULE_RUPAY = 10;
        public static final int L3_MODULE_INTERAC = 11;
        public static final int L3_MODULE_MIR = 12;
        public static final int L3_MDDULE_MULTIBANCO = 13;

        public MODULE() {
        }
    }

    public class CONFIG {
        public static final int L3_CFG_SUPPORT_EC = 1;
        public static final int L3_CFG_SUPPORT_SM = 2;
        public static final int L3_CFG_SUPPORT_EXTERNAL_READER = 4;

        public CONFIG() {
        }
    }

    public class TransResult {
        public static final int L3_TXN_OK = 0;
        public static final int L3_TXN_TERMINATE = 1;
        public static final int L3_TXN_TRY_ANOTHER = 2;
        public static final int L3_TXN_DECLINE = 3;
        public static final int L3_TXN_APPROVED = 4;
        public static final int L3_TXN_ONLINE = 5;

        public TransResult() {
        }
    }
}

