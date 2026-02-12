package com.newland.forth.spi.crypto.keystore;

public enum KEY_USE {
    KEY_USE_KEK(0),
    KEY_USE_PIN_KEK(1),
    KEY_USE_MAC_KEK(2),
    KEY_USE_DATA_KEK(3),
    KEY_USE_DATA_ENC_KEK(4),
    KEY_USE_TR31_KEK(5),
    KEY_USE_PIN(6),
    KEY_USE_MAC(7),
    KEY_USE_DATA(8),
    KEY_USE_DATA_ENC_ONLY(9),
    KEY_USE_DUKPT(16),

    KEY_USE_ASYM_AUTH(0x20),
    KEY_USE_ASYM_DATA(0x21),
    KEY_USE_ASYM_ANY(0x22),
    KEY_USE_ASYM_KEY_DISTRIBUTION(0x23);

    int code;
    KEY_USE(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
