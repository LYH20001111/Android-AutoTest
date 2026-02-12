package com.newland.nsdk.core.api.internal.devicemanager;

public enum TamperReason {
    //None security triggered.
    NONE(0),
    //K21 button battery is dead.
    K21_BUTTON_BATTERY_DEAD(0x2000000),
    //K21 tamper-5 triggered.
    K21_TAMPER_5_TRIGGERED(0x2200000),
    //K21 tamper-2 triggered.
    K21_TAMPER_2_TRIGGERED(0x2040000),
    //K21 tamper-0 triggered.
    K21_TAMPER_0_TRIGGERED(0x2010000),
    //K21 tamper-1 triggered.
    K21_TAMPER_1_TRIGGERED(0x2020000),
    //1902 tamper-2 triggered.
    CHIP_1902_TAMPER_2_TRIGGERED(0x80000004),
    //1902 tamper-0 triggered.
    CHIP_1902_TAMPER_0_TRIGGERED(0x80000001),
    //1902 tamper-1 triggered.
    CHIP_1902_TAMPER_1_TRIGGERED(0x80000002),
    //1902 tamper-3 triggered.
    CHIP_1902_TAMPER_3_TRIGGERED(0x80000008),
    //3682 tamper-4 triggered.
    CHIP_3682_TAMPER_4_TRIGGERED(0x80000010),
    //1902 tamper-5 triggered.
    CHIP_3682_TAMPER_5_TRIGGERED(0x80000020),
    //3652 button battery is dead.
    CHIP_3652_BUTTON_BATTERY_DEAD(0x80000000),
    //Anti-cutting machine authentication failed
    ANTI_CUTTING_MACHINE_AUTH_FAILED(0xbb8),
    //Anti-cutting machine authentication is not performed
    NO_ANTI_CUTTING_MACHINE_AUTH(0xbb9),
    //Pro switch to Dev authentication failed.
    P2D_AUTH_FAILED(0xbba),
    //Dev switch to Pro authentication failed
    D2P_AUTH_FAILED(0xbbb);

    int code;
    TamperReason(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
