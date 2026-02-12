package com.newland.sdk.me.module.emvl3.impl;

/**
 * @Description
 * @Author wuhh
 * @Date 2020/9/16
 */
public class EmvL3Constant {
    public static final int ECHO_TEST_COUNT = 5;
    public static final int ECHO_TEST_TIMEOUT_MS = 50;

    public static final int PERFORM_TIMEOUT = 60;
    public class CardInterfaces{
        public static final int MSR = 0x01;
        public static final int CONTACT = 0x02;
        public static final int CONTACTLESS = 0x04;
    }
    public static final int APPSELECTION_CONTACT = 0x80;
    public static final int APPSELECTION_CONTACTLESS = 0x40;

    public static final int CALLBACK_ENABLE_NOTIFICATION = 0x80;
    public static final int CALLBACK_ENABLE_SELECT_CANDIDATE_LIST = 0x40;
    public static final int CALLBACK_ENABLE_CHECK_CREDENTIALS = 0x20;
    public static final int CALLBACK_ENABLE_AFTER_FINAL_SELECT = 0x10;
    public static final int CALLBACK_ENABLE_GET_PIN = 0x08;

    public static final int FUNCTIONID_LOG = 0x21;
    public static final int FUNCTIONID_CALLBACK = 0x36;

    public static final int EXCEPTION_CANCEL_FINISH = -497;
    public static final int EXCEPTION_INTERRUPT_FINISH = -498;
    public static final int EXCEPTION_COMM_FINISH = -499;

    public class FunctionId{
        /** EMV Configuration File Command Function ID*/
        public static final int COMMAND_TERMINAL_CONFIG_UPDATE      = 0x01;
        public static final int COMMAND_TERMINAL_CONFIG_GET         = 0x02;
        public static final int COMMAND_AID_CONFIG_UPDATE           = 0x03;
        public static final int COMMAND_AID_CONFIG_GET              = 0x04;
        public static final int COMMAND_AID_CONFIG_REMOVE_ONE       = 0x05;
        public static final int COMMAND_AID_CONFIG_REMOVE_ALL       = 0x06;
        public static final int COMMAND_CAPK_UPDATE                 = 0x07;
        public static final int COMMAND_CAPK_GET                    = 0x08;
        public static final int COMMAND_CAPK_REMOVE_ONE             = 0x09;
        public static final int COMMAND_CAPK_REMOVE_ALL             = 0x0A;
        public static final int COMMAND_CERT_BLACK_UPDATE           = 0x0B;
        public static final int COMMAND_CERT_BLACK_GET              = 0x0C;
        public static final int COMMAND_CERT_BLACK_REMOVE_ONE       = 0x0D;
        public static final int COMMAND_CERT_BLACK_REMOVE_ALL       = 0x0E;
        public static final int COMMAND_CARD_BLACK_UPDATE           = 0x0F;
        public static final int COMMAND_CARD_BLACK_GET              = 0x10;
        public static final int COMMAND_CARD_BLACK_REMOVE_ONE       = 0x11;
        public static final int COMMAND_CARD_BLACK_REMOVE_ALL       = 0x12;
        public static final int COMMAND_AID_GET_COUNT               = 0x13;
        public static final int COMMAND_CAPK_GET_COUNT              = 0x14;

        /** Function Command Function ID*/
        public static final int COMMAND_DEBUG_MASSAGE               = 0x21;
        public static final int COMMAND_INIT_EMV_KERNEL             = 0x22;
        public static final int COMMAND_SET_DATA                    = 0x23;
        public static final int COMMAND_GET_DATA                    = 0x24;
        public static final int COMMAND_SET_TLV_LIST                = 0x25;
        public static final int COMMAND_GET_TLV_LIST                = 0x26;
        public static final int COMMAND_SET_DEBUG_MODE              = 0x27;
        public static final int COMMAND_GET_VERSION                 = 0x28;

        /** Transaction Command Function ID*/
        public static final int COMMAND_PERFORM_TRANSACTION         = 0x31;
        public static final int COMMAND_COMPLETE_TRANSACTION        = 0x32;
        public static final int COMMAND_TERMINATE_TRANSACTION       = 0x33;
        public static final int COMMAND_PREPROCESS_TRANSACTION      = 0x34;

        /*自定义*/
        public static final int COMMAND_CANCEL                      = 0xB2;
    }

    /*COMMAND_CALLBACK = 0x36*/
    public class CallbackId{
        public static final int COMMAND_NOTIFICATION = 0x01;
        public static final int COMMAND_SELECT_CANDIDATE_LIST = 0x04;
        public static final int COMMAND_AFTER_FINAL_SELECT = 0x0A;
        public static final int COMMAND_CARDNUM_CONFIRM = 0x0D;
        public static final int COMMAND_CHECK_CREDENTIALS = 0x07;
        public static final int COMMAND_GET_PIN = 0x02;
    }

    public class UIEvent {
        public static final int UI_NONE = -1;
        public static final int UI_PRESENT_CARD = 0;
        public static final int UI_PROCESSING = 1;
        public static final int UI_CAPK_LOAD_FAIL = 2;
        public static final int UI_SEE_PHONE = 3;
        public static final int UI_CARDNUM_CONFIRM = 4;
        public static final int UI_CHIP_ERR_RETRY = 5;
        public static final int UI_PIN_STATUS = 6;
    }

    public class Credentials{
        public static final int ID_CARD = 0;
        public static final int MILITARY_ID_CARD = 1;
        public static final int PASSPORT = 2;
        public static final int ENTRY_PERMIT = 3;
        public static final int TEMPORARY_ID_CARD = 4;
        public static final int OTHER = 5;
    }

    public class PinEntryStatus{
        public static final int SUCC = 0;
        public static final int FAIL = -501;
        public static final int CANCLE = -502;
        public static final int TIMEOUT = -503;
        public static final int BYPASS =-508;
    }

    public class TransResult {
        public static final int L3_TXN_OK = 0;//步骤执行成功
        public static final int L3_TXN_TERMINATE = 1;//交易终止
        public static final int L3_TXN_TRY_ANOTHER = 2;//哪些情况会出现
        public static final int L3_TXN_DECLINE = 3;//脱机交易拒绝 == 第一次GAC失败
        public static final int L3_TXN_APPROVED = 4;//脱机交易批准
        public static final int L3_TXN_ONLINE = 5;//联机
    }

    public static final String CMD_CANCEL = "+++CANCEL";

    public class Aid{
        public static final int CONTACT = 1;
        public static final int CONTACTLESS = 2;
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
    }
}
