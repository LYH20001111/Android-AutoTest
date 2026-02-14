package com.newland.nsdk.core.api.internal.emvl2.type;

public class capk {
    public byte[] usKeyModulus = new byte[248];
    public int ucModLen;
    public byte[] usExponent = new byte[3];
    public byte[] usHashValue = new byte[20];
    public byte[] usExpiredDate = new byte[4];
    public byte[] usRid = new byte[5];
    public int ucIndex;
    public byte ucPkAlgorithm;
    public byte ucHashAlgorithm;
    public byte ucDisable;
    public byte[] usResv = new byte[3];
}
