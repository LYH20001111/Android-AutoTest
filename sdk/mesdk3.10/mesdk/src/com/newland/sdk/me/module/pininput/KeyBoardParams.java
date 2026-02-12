package com.newland.sdk.me.module.pininput;

import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.pin.PinInputExtParams;
import com.newland.sdk.module.pin.PinInputListener;
import com.newland.sdk.module.pin.PinpadModule;

public class KeyBoardParams {
    private static KeyManagement keyManagement;
    private static AlgorithmMode algorithmMode;
    private static int keyIndex;
    private static String pan;
    private static int timeout;
    private static PinInputListener pinInputListener;
    private static PinInputExtParams pinInputExtParams;
    private static byte[] modulus;
    private static byte[] exponent;
    private static PinpadModule pinpadModule;

    public static byte[] getModulus() {
        return modulus;
    }

    public static void setModulus(byte[] modulus) {
        KeyBoardParams.modulus = modulus;
    }

    public static byte[] getExponent() {
        return exponent;
    }

    public static void setExponent(byte[] exponent) {
        KeyBoardParams.exponent = exponent;
    }

    public static KeyManagement getKeyManagement() {
        return keyManagement;
    }

    public static void setKeyManagement(KeyManagement keyManagement) {
        KeyBoardParams.keyManagement = keyManagement;
    }

    public static AlgorithmMode getAlgorithmMode() {
        return algorithmMode;
    }

    public static void setAlgorithmMode(AlgorithmMode algorithmMode) {
        KeyBoardParams.algorithmMode = algorithmMode;
    }

    public static int getKeyIndex() {
        return keyIndex;
    }

    public static void setKeyIndex(int keyIndex) {
        KeyBoardParams.keyIndex = keyIndex;
    }

    public static String getPan() {
        return pan;
    }

    public static void setPan(String pan) {
        KeyBoardParams.pan = pan;
    }

    public static int getTimeout() {
        return timeout;
    }

    public static void setTimeout(int timeout) {
        KeyBoardParams.timeout = timeout;
    }

    public static PinInputListener getPinInputListener() {
        return pinInputListener;
    }

    public static void setPinInputListener(PinInputListener pinInputListener) {
        KeyBoardParams.pinInputListener = pinInputListener;
    }

    public static PinInputExtParams getPinInputExtParams() {
        return pinInputExtParams;
    }

    public static void setPinInputExtParams(PinInputExtParams pinInputExtParams) {
        KeyBoardParams.pinInputExtParams = pinInputExtParams;
    }

    public static PinpadModule getPinpadModule() {
        return pinpadModule;
    }

    public static void setPinpadModule(PinpadModule pinpadModule) {
        KeyBoardParams.pinpadModule = pinpadModule;
    }
}
