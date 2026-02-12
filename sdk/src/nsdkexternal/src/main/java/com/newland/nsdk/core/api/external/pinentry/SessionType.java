package com.newland.nsdk.core.api.external.pinentry;

public enum SessionType {
    DUKPT(1),
    MASTER_SESSION(2);

    int code;
    SessionType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
