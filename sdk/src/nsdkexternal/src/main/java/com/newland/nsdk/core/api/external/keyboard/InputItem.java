package com.newland.nsdk.core.api.external.keyboard;

public class InputItem {
    /**
     * Request input data type
     */
    InputType type;
    /**
     * Settings about the input data procedure. Bit 0: 0-display actual digit, 1-display "*", Bit1: 0-Only numbers, 1- Any ASCII characters allowed. When Bit0 is 1, Bit 1 will be ineffective.
     */
    byte inputSettings;
    /**
     *     The format of the input data. Only when InputType is PHONE_NUMBER and EXPIRY_DATE, this is effective.
     *     For PHONE_NUMBER, 00 means normal String and 01 means American Format 3-3-4(10 digits).
     *     For EXPIRE_DATE, 00 means MMYY, 01 means YYMM, 02 means DDMMYY, 03 means MMDDYY, 04 means YYMMDD and 05 means YYDDMM.
     */
    byte formatCode;
    /**
     * The minimum input digits length.
     */
    int minDigits;
    /**
     * The maximum input digits length.
     */
    int maxDigits;
    /**
     * The timeout of the input data procedure.
     */
    int timeout;
    /**
     * The pinpad entry data, this is only available when input finished.
     */
    byte[] value;
    /**
     * The code of the selected button in pinpad, only available when input finished.
     */
    int buttonCode;
    /**
     * Different from value length only when it is in crypto mode, only available when input finished.
     */
    int actualLen;

    public InputType getType() {
        return type;
    }

    public void setType(InputType type) {
        this.type = type;
    }

    public byte getInputSettings() {
        return inputSettings;
    }

    public void setInputSettings(byte inputSettings) {
        this.inputSettings = inputSettings;
    }

    public byte getFormatCode() {
        return formatCode;
    }

    public void setFormatCode(byte formatCode) {
        this.formatCode = formatCode;
    }

    public int getMinDigits() {
        return minDigits;
    }

    public void setMinDigits(int minDigits) {
        this.minDigits = minDigits;
    }

    public int getMaxDigits() {
        return maxDigits;
    }

    public void setMaxDigits(int maxDigits) {
        this.maxDigits = maxDigits;
    }

    public int getTimeout() {
        return timeout;
    }

    public void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public byte[] getValue() {
        return value;
    }

    public void setValue(byte[] value) {
        this.value = value;
    }

    public int getButtonCode() {
        return buttonCode;
    }

    public void setButtonCode(int buttonCode) {
        this.buttonCode = buttonCode;
    }

    public int getActualLen() {
        return actualLen;
    }

    public void setActualLen(int actualLen) {
        this.actualLen = actualLen;
    }
}
