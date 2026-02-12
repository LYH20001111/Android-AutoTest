package com.newland.sdk.module.extpinpademv;

import java.util.ArrayList;

/**
 * Author by bxy, Date on 2019/12/30.
 */
public interface ExtPinpadEmvModule {
    public boolean init(byte[] configuration,CommunicationListener listener);

    public  boolean loadTerminalConfig(CardInterface cardIntf, byte[] tlvList, ConfigMode mode);

    public boolean addAid(CardInterface cardIntf,byte[] tlvList);
    public byte[] getSpecifiedAid(CardInterface cardIntf,AIDEntry aidEntry);
    public boolean deleteSpecifiedAid(CardInterface cardIntf,AIDEntry aidEntry);
    public boolean deleteAid(CardInterface cardIntf);

    public boolean addCAPublicKey(CAPKEntry capk);
    public CAPKEntry getSpecifiedCAPublicKey(byte[] rid,byte index);
    public boolean deleteSpecifiedCAPublicKey(byte[] rid,byte index);
    public boolean deleteAllCAPublicKey();

    public boolean loadRevocationList(CRLEntry crl, ConfigMode mode);
    public boolean loadExceptionList(ExceptionEntry exceptionList, ConfigMode mode);

    public TransactionResult performTransaction(byte[] data);
    public TransactionResult completeTransaction(byte[] data);
    public boolean terminateTransaction();
    public boolean cancelTransaction();

    public boolean setData(int tag,byte[] data);
    public byte[] getData(EmvData type);
    public boolean setTLVData(byte[] tlvList);
    public byte[] getTlvData(ArrayList<Integer> tagList,boolean isPackZeroLen);
    public boolean setDebugMode(int level);
    public String getVersion(EmvModuleVersion module);
}
