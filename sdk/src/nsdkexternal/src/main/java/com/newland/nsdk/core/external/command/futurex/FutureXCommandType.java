package com.newland.nsdk.core.external.command.futurex;

public enum FutureXCommandType {
    PEDI((byte)1),
    PEDK((byte)2),
    PEDV((byte)3);
    private byte code;
    FutureXCommandType(byte code) {
        this.code = code;
    }
    public byte getCode(){
        return this.code;
    }
}
