package com.newland.sdkdemo.fragment.dock.emv;

/**
 * Author by wuhh, Date on 2020/3/19.
 */
public class EmvL3TransConstant {
    /**
     * Transaction process
     */
    public static class TransStep{
        public static final int INPUT_AMOUNT = 1;
        public static final int TRANS_PERFORMED = 2;
        public static final int TRANS_ONLINE_PIN = 3;
        public static final int TRANS_NO_PIN = 4;
        public static final int TRANS_ONLINE = 5;
        public static final int TRANS_COMPLETE = 6;
        public static final int TRANS_RESULT = 7;
    }

    public final static int TRANS_SUCC =  0;
    public final static int TRANS_FAIL = -1;

    /**
     * Transaction Type Indicates the type of financial transaction,<p>
     * represented by the first two digits of ISO8583, Processing Code.
     */
    public static class TransType{
        public static final int SALE = 0x00;
        public static final int CASHBACK = 0x09;
        public static final int REFUND = 0x20;
    }


    public static final int CALLBACK_ENABLE_NOTIFICATION = 0x80;
    public static final int CALLBACK_ENABLE_SELECT_CANDIDATE_LIST = 0x40;
    public static final int CALLBACK_ENABLE_CHECK_CREDENTIALS = 0x20;
    public static final int CALLBACK_ENABLE_AFTER_FINAL_SELECT = 0x10;
    public static final int CALLBACK_ENABLE_GET_PIN = 0x08;
}
