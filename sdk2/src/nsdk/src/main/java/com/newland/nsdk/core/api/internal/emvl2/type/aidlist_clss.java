package com.newland.nsdk.core.api.internal.emvl2.type;

public class aidlist_clss {
    public byte[] usAid = new byte[16];
    public int ucAidLen;
    public byte ucPartialMatch;
    public byte[] usKernelId = new byte[8];
    public byte ucExtendAidSupport;
    public byte ucTerminalPriority;
    public byte ucKernelConfig;
    public byte ucTransType;
    public byte ucTransTypeCheckFlag;
    public byte ucZeroAmtFlag;
    public byte ucLimitExist;
    public byte[] usDF20_CLlimit = new byte[6];
    public byte ucRfu;

}
