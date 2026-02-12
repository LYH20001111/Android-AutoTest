package com.newland.sdkdemo.fragment.mdb;

public class ReturnValue {


    public static class CallbackResult {
        public static final int CALLBACK_OK =0;        /* the same to Vend Approved, called function complete success */
        public static final int CALLBACK_ERROR = 1;        /* general error code, e.g. unknown malfuntional/error */
        public static final int CALLBACK_TIMEOUT = 2;    /* read card timeout or communicate with Host timeout */
        public static final int CALLBACK_DISABLE = 3;  /* reader disable event triggered */
        public static final int CALLBACK_CANCEL = 4;    /* cancel event triggered */
        public static final int CALLBACK_DENIED = 5;    /* payment transaction decline */
        public static final int CALLBACK_OUTSIDE_LIMIT = 6;    /* vend price exceed max price limit or less than min price limit */
        public static final int CALLBACK_USER_CANCEL = 7;    /* vend price exceed max price limit or less than min price limit */
    }
}
