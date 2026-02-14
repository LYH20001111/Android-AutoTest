package com.newland.nsdk.core.api.internal.routemanager;

public enum NetWorkType {
    /**
     * Mobile network communication mode.
     */
    NET_WORK_MOBILE(0),

    /**
     * WIFI network communication mode.
     */
    NET_WORK_WIFI(1),

    /**
     * Ethernet network communication mode.
     */
    NET_WORK_ETHERNET(9);

    private int code;

    NetWorkType(int code){this.code = code;};

    public int getCode() {return code;}
}
