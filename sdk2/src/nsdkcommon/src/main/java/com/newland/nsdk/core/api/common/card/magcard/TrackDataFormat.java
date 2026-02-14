package com.newland.nsdk.core.api.common.card.magcard;

public enum TrackDataFormat {
    ISO ((byte) 0x00),
    JIS ((byte) 0x01),
    UNKNOWN ((byte) 0xFF);

    byte code;

    TrackDataFormat(byte code) {
        this.code = code;
    }

    public byte getCode() {
        return code;
    }
}
