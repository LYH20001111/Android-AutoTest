package com.newland.nsdk.core.api.external.devicemanager;

public class FileInfo {
    String name;
    byte[] info;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public byte[] getInfo() {
        return info;
    }

    public void setInfo(byte[] info) {
        this.info = info;
    }
}
