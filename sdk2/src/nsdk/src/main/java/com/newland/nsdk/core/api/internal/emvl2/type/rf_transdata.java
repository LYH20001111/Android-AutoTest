package com.newland.nsdk.core.api.internal.emvl2.type;

public class rf_transdata {
    public long nAmount;
    public long nAmountOther;
    public byte[] usDate = new byte[7];
    public byte[] usAid = new byte[16];
    public byte nAidLen;
    public byte[] usKernelId = new byte[8];
    public int nFileOffSet;
    public byte[] pusFinalAidFci = new byte[512];
    public int nFciLen;
    public byte[] usSW12 = new byte[2];

    /*Entry_Point_PreProcess_indicators*/
    public byte[] pusPreProcessIndicator = new byte[100];
    public int ucPreProcessIndicatorLen;
    public byte ucNoAmount;
    public byte[] usResv = new byte[4];

}

