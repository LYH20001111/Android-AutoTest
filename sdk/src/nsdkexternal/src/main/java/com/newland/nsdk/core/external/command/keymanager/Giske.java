package com.newland.nsdk.core.external.command.keymanager;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.external.command.exception.ExternalErrorMessage;
import com.newland.nsdk.core.external.command.exception.ExternalMessageException;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Giske {
    private GiskeVersion versionNumber;
    private GiskeKeyUsage keyUsage;
    private GiskeAlgorithm algorithm;
    private GiskeMode mode;
    private byte[] keyData;
    private byte[] mac;

    public byte[] pack() throws NSDKException {
        if (versionNumber == null) {
            throw new ExternalMessageException("Please set version number.");
        }
        if (keyUsage == null) {
            throw new ExternalMessageException("Please set key usage.");
        }
        if (algorithm == null) {
            throw new ExternalMessageException("Please set algorithm.");
        }
        if (mode == null) {
            throw new ExternalMessageException("Please set mode.");
        }
        if (keyData == null || keyData.length == 0) {
            throw new ExternalMessageException("Please set key data.");
        }
        if (mac == null || mac.length == 0) {
            throw new ExternalMessageException("Please set mac.");
        }

        byte[] reserved1 = new byte[16];
        byte[] keyVersionNum = new byte[2];
        byte reserved2 = 0;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        int keyLen = keyData.length * 2;
        String keyLenStr = String.format("%4s", keyLen).replace(' ', '0');
        try {
            outputStream.write(keyUsage.getCode().getBytes());
            outputStream.write(algorithm.getCode().getBytes());
            outputStream.write(mode.getCode().getBytes());
            outputStream.write(reserved1);
            outputStream.write(keyVersionNum);
            outputStream.write(reserved2);
            outputStream.write(keyLenStr.getBytes());
            outputStream.write(keyData);
            outputStream.write(mac);
        } catch (IOException e) {
            throw new ExternalMessageException(ErrorCode.EXT_MESSAGE_BYTE_ARRAY_STREAM_IO_ERROR, ExternalErrorMessage.BYTE_ARRAY_STREAM_IO_ERROR, e);
        }

        byte[] giske = outputStream.toByteArray();
        int allLen = 5 + giske.length;
        String allLenStr = String.format("%4s", allLen).replace(' ', '0');
        byte[] result = new byte[allLen];
        result[0] = versionNumber.getCode().getBytes()[0];
        System.arraycopy(allLenStr.getBytes(), 0, result, 1, 4);
        System.arraycopy(giske, 0, result, 5, giske.length);

        return result;
    }

    /**
     * Get version number.
     *
     * @return Version number. See {@link GiskeVersion}
     */
    public GiskeVersion getVersionNumber() {
        return versionNumber;
    }

    /**
     * Set version number.
     *
     * @param versionNumber Version number. See {@link GiskeVersion}
     */
    public void setVersionNumber(GiskeVersion versionNumber) {
        this.versionNumber = versionNumber;
    }

    /**
     * Get key usage.
     *
     * @return Key usage. See {@link GiskeKeyUsage}
     */
    public GiskeKeyUsage getKeyUsage() {
        return keyUsage;
    }

    /**
     * Set key usage.
     *
     * @param keyUsage Key usage. See {@link GiskeKeyUsage}
     */
    public void setKeyUsage(GiskeKeyUsage keyUsage) {
        this.keyUsage = keyUsage;
    }

    /**
     * Get algorithm.
     *
     * @return Algorithm. See {@link GiskeAlgorithm}
     */
    public GiskeAlgorithm getAlgorithm() {
        return algorithm;
    }

    /**
     * Set algorithm.
     *
     * @param algorithm Algorithm. See {@link GiskeAlgorithm}
     */
    public void setAlgorithm(GiskeAlgorithm algorithm) {
        this.algorithm = algorithm;
    }

    /**
     * Get mode.
     *
     * @return Mode of use. See {@link GiskeMode}
     */
    public GiskeMode getMode() {
        return mode;
    }

    /**
     * Set mode.
     *
     * @param mode Mode of use. See {@link GiskeMode}
     */
    public void setMode(GiskeMode mode) {
        this.mode = mode;
    }

    /**
     * Get key data.
     *
     * @return Key data.
     */
    public byte[] getKeyData() {
        return keyData;
    }

    /**
     * Set key data.
     *
     * @param keyData Key data.
     */
    public void setKeyData(byte[] keyData) {
        this.keyData = keyData;
    }

    /**
     * Get MAC.
     *
     * @return MAC.
     */
    public byte[] getMac() {
        return mac;
    }

    /**
     * Set MAC.
     *
     * @param mac MAC.
     */
    public void setMac(byte[] mac) {
        this.mac = mac;
    }

    /**
     * Giske key usage.
     */
    public enum GiskeKeyUsage {
        /**
         * D0 - data key
         */
        USAGE_D0("D0"),

        /**
         * K0 - master key
         */
        USAGE_K0("K0"),

        /**
         * G0 - mac key
         */
        USAGE_G0("G0"),

        /**
         * M0 - mac key
         */
        USAGE_M0("M0"),

        /**
         * 00 - mac key
         */
        USAGE_00("00"),

        /**
         * 10 - mac key
         */
        USAGE_10("10"),

        /**
         * 20 - mac key
         */
        USAGE_20("20"),

        /**
         * 30 - mac key
         */
        USAGE_30("30"),

        /**
         * 40 - mac key
         */
        USAGE_40("40"),

        /**
         * 50 - mac key
         */
        USAGE_50("50"),

        /**
         * 60 - mac key
         */
        USAGE_60("60"),

        /**
         * P0 - pin key
         */
        USAGE_P0("P0"),

        /**
         * V0 - pin key
         */
        USAGE_V0("V0"),

        /**
         * B0 - dukpt key
         */
        USAGE_B0("B0");

        private String code;

        GiskeKeyUsage(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }
    }

    /**
     * Giske algorithm
     */
    public enum GiskeAlgorithm {
        /**
         * T
         */
        T("T"),

        /**
         * D, unused
         */
        D("D");

        private String code;

        GiskeAlgorithm(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }
    }

    /**
     * Giske mode.
     */
    public enum GiskeMode {
        /**
         * T
         */
        N("N"),

        /**
         * D, unused
         */
        E("E"),

        /**
         * D, unused
         */
        D("D"),

        /**
         * D, unused
         */
        G("G"),

        /**
         * D, unused
         */
        V("V");

        private String code;

        GiskeMode(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }
    }

    /**
     * Giske version number.
     */
    public enum GiskeVersion {
        /**
         * T
         */
        VERSION_2("2"),

        /**
         * D, unused
         */
        VERSION_A("A");

        private String code;

        GiskeVersion(String code) {
            this.code = code;
        }

        public String getCode() {
            return this.code;
        }
    }
}
