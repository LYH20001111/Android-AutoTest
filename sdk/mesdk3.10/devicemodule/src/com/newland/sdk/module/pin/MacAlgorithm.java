package com.newland.sdk.module.pin;

/**
 * MAC digest algorithm
 *
 *
 * @since ver3.10.01
 */
public class MacAlgorithm {

    public static class DES{
        public static final int X99 = 0x00;
        public static final int X919 = 0x01;
        public static final int ECB = 0x02;
        public static final int M9606 = 0x03;
        public static final int CBC = 0x04;
        public static final int X919_3DES = 0x08;
    }

    public static class SM4{
        public static final int X99 = 0x05;
        public static final int SM4_UNIONPAY = 0x06;
        public static final int SM4_ECB = 0x09;
        public static final int M9606 = 0x0A;
    }

    public static class AES{
        public static final int X99 = 0x07;
    }

}
