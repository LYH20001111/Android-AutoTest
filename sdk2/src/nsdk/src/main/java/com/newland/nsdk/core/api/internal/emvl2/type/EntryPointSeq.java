package com.newland.nsdk.core.api.internal.emvl2.type;

public class EntryPointSeq {
    /**
     * pre_processing
     */
    public static final int START_A = 0x00;
    /**
     * protocol activation
     */
    public static final int START_B = 0x01;
    public static final int START_PPSE_SEL = 0x02;
    /**
     * select the combination
     */
    public static final int START_C = 0x03;
    public static final int START_FINAL_APP = 0x04;
    /**
     * kernel activation
     */
    public static final int START_D = 0x05;
    /**
     * outcome processing
     */
    public static final int START_OUTCOME = 0x06;
    /**
     * Deal with the end of the Entry Point does not require
     */
    public static final int START_END = 0xFF;
}
