package com.newland.nsdk.core.api.external.keyboard;

import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.keymanager.Key;

public class InputParameters {
    /**
     * Key used to encrypt data, see {@link Key}.
     */
    Key encryptKey;
    /**
     * Cipher type
     */
    CipherType cipherType;
    /**
     * initial vector, only available when cipher type is CBC. 8 or 16 bytes.
     */
    byte[] iv;
    /**
     * Prompt line displayed in pinpad.
     */
    int promptLine;
    /**
     * Text displayed line in pinpad.
     */
    int displayLine;
    /**
     * Whether to set bypass key. "0" means using "ENTER" key to bypass, "1" means using "CANCEL" key to bypass.
     */
    int bytePassKey;
    /**
     * The buttons parameters to be input, see {@link InputButtonParameters}.
     */
    InputButtonParameters[] buttons;

    public Key getEncryptKey() {
        return encryptKey;
    }

    public void setEncryptKey(Key encryptKey) {
        this.encryptKey = encryptKey;
    }

    public CipherType getCipherType() {
        return cipherType;
    }

    public void setCipherType(CipherType cipherType) {
        this.cipherType = cipherType;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    public int getDisplayLine() {
        return displayLine;
    }

    public void setDisplayLine(int displayLine) {
        this.displayLine = displayLine;
    }

    public int getPromptLine() {
        return promptLine;
    }

    public void setPromptLine(int promptLine) {
        this.promptLine = promptLine;
    }

    public int getBytePassKey() {
        return bytePassKey;
    }

    public void setBytePassKey(int bytePassKey) {
        this.bytePassKey = bytePassKey;
    }

    public InputButtonParameters[] getButtons() {
        return buttons;
    }

    public void setButtons(InputButtonParameters[] buttons) {
        this.buttons = buttons;
    }
}
