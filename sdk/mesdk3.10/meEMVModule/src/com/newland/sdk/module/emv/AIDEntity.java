package com.newland.sdk.module.emv;

public class AIDEntity {
    private int index;
    private byte[] aid;
    private String name;
    private byte[] appLable;
    private byte terminalPriority;
    private byte enable;
    private byte limitFlag;
    private byte[] kernelId;
    private byte apid;

    public AIDEntity(int index, byte[] aid, String name, byte[] appLable, byte terminalPriority, byte enable, byte limitFlag, byte[] kernelId, byte apid) {
        this.index = index;
        this.aid = aid;
        this.name = name;
        this.appLable = appLable;
        this.terminalPriority = terminalPriority;
        this.enable = enable;
        this.limitFlag = limitFlag;
        this.kernelId = kernelId;
        this.apid = apid;
    }

    /**
     * index
     *
     * @return
     */
    public int getIndex() {
        return index;
    }

    /**
     * application id
     *
     * @return
     */
    public byte[] getAid() {
        return aid;
    }

    /**
     * Application Preferred Name 9F12(ICC), ans, 1-16 bytes
     *
     * @return
     */
    public String getName() {
        return name;
    }

    /**
     * application lable 50(ICC)
     *
     * @return
     */
    public byte[] getAppLable() {
        return appLable;
    }

    /**
     * return the priority of this AID.
     *
     * @return
     */
    public byte getTerminalPriority() {
        return terminalPriority;
    }

    /**
     * indicate whether the STCANDIDATE is enabled
     *
     * @return
     */
    public byte getEnable() {
        return enable;
    }

    /**
     * contactless limit amount set flag
     *
     * @return
     */
    public byte getLimitFlag() {
        return limitFlag;
    }

    /**
     * kernelID
     *
     * @return
     */
    public byte[] getKernelId() {
        return kernelId;
    }

    /**
     * Application Priority Indicator 87(ICC)
     *
     * @return
     */
    public byte getApid() {
        return apid;
    }
}