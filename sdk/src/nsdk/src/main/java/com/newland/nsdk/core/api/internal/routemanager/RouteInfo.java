package com.newland.nsdk.core.api.internal.routemanager;

public class RouteInfo {
    private String address;
    private int networkType;
    public RouteInfo() {}
    public RouteInfo(String address, int networkType) {
        this.address = address;
        this.networkType = networkType;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setNetworkType(int networkType) {
        this.networkType = networkType;
    }

    public String getAddress() {
        return address;
    }

    public int getNetworkType() {
        return networkType;
    }
}
