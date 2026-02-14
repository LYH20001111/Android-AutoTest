package com.newland.nsdk.core.api.internal.pinentry;

import com.newland.nsdk.core.api.common.pinentry.PINBlockMode;

public class PINConvertParameters {
    String PAN;//Account for PINBlock encryption.
    PINBlockMode sessionPinBlockMode;//PINBlock encryption mode with sessionKey.
    PINBlockMode convertPinBlockMode;//PINBlock encryption mode with convert pinKey.
    PINConvertMode pinConvertMode = PINConvertMode.ONLY_CONVERT;//PINBlock convert mode.
    RSAKey offlinePinKey; //Only used for PINConvertMode.CONVERT_VERIFY.

    public String getPAN() {
        return PAN;
    }

    public void setPAN(String PAN) {
        this.PAN = PAN;
    }

    public PINBlockMode getSessionPinBlockMode() {
        return sessionPinBlockMode;
    }

    public void setSessionPinBlockMode(PINBlockMode sessionPinBlockMode) {
        this.sessionPinBlockMode = sessionPinBlockMode;
    }

    public PINConvertMode getPinConvertMode() {
        return pinConvertMode;
    }

    public void setPinConvertMode(PINConvertMode pinConvertMode) {
        this.pinConvertMode = pinConvertMode;
    }

    public PINBlockMode getConvertPinBlockMode() {
        return convertPinBlockMode;
    }

    public void setConvertPinBlockMode(PINBlockMode convertPinBlockMode) {
        this.convertPinBlockMode = convertPinBlockMode;
    }

    public RSAKey getOfflinePinKey() {
        return offlinePinKey;
    }

    public void setOfflinePinKey(RSAKey offlinePinKey) {
        this.offlinePinKey = offlinePinKey;
    }
}
