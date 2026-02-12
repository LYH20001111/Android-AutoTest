package com.newland.testscanner.util;

/**
 * @author youjf
 * @description
 * @date 2019/8/2
 * @since V3.10.01
 */
public class AppConfig {
    public static class ScanResult{
        public static final int SCAN_FINISH = 0;
        public static final int SCAN_RESPONSE = 1;
        public static final int SCAN_ERROR = 2;
        public static final int SCAN_TIMEOUT = 3;
        public static final int SCAN_CANCEL = 4;
    }
}
