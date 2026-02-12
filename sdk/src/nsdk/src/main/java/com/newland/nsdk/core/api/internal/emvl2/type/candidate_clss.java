package com.newland.nsdk.core.api.internal.emvl2.type;

public class candidate_clss {
    public byte[] usAid = new byte[16];
    public byte ucAidLen;
    /**
     * < length of AID
     */
    public byte[] usLable = new byte[20];
    public byte ucLableLen;
    /**
     * < length of lable
     */
    public byte[] usPreferredName = new byte[20];
    public byte ucPreferredNameLen;
    /**
     * < length of preferred name
     */
    public byte ucPriority;
    public byte ucEnable;

    public byte ucLimitFlag;
    public byte[] usKernelId = new byte[8];
    public byte[] usExtendAid = new byte[16];
    public byte ucExtendAidLen;
    public byte ucStatusType;
    public byte ucTerminalAidNum;
    public byte ucTerminalPriority;
    public byte uc9F2Aexist;
    public byte ucKernelConfig;
    public byte[] us9F0A = new byte[255];
    public int uc9F0ALen;
    public int unFileOffset;
    /**
     * < the offset of this AID in the parameters file
     */
    public byte[] usResv = new byte[7];
}
