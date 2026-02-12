package com.newland.nsdk.core.internal.pinentry;

/**
 * PIN session type.
 */
public enum PINSessionType {
    /**
     * Online DUKPT session, does not iterate DUKPT key.
     */
    DUKPT(1),
    /**
     * Online Master Session.
     */
    MASTER_SESSION(2),
    /**
     * EMV off-line with clear PIN.
     */
    EMV_OFFLINE_CLEARPIN(3),
    /**
     * EMV off-line with encrypted password.
     */
    EMV_OFFLINE_ENCPIN(4),
    /**
     * EMV off-line with clear verification using a PIN block.
     */
    EMV_PIN_VERIFY_CLEARPIN(5),
    /**
     * EMV off-line with encrypted password using a PIN block.
     */
    EMV_PIN_VERIFY_ENCPIN(6),
    /**
     * Online Master Session, using a diversified key generated in accordance to the Spanish requirements.
     */
    MS_DIVERSIFYKEY(7),
    /**
     * For parameter testing purposes
     */
    INVALID_SESSION(8);

    int code;

    PINSessionType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
