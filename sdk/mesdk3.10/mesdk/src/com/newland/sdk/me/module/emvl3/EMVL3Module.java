package com.newland.sdk.me.module.emvl3;

import com.newland.sdk.me.module.emvl3.impl.EmvL3Usage;
import com.newland.sdk.module.emv.AID;
import com.newland.sdk.module.emv.CAPK;
import com.newland.sdk.module.emv.EMVModule;
import com.newland.sdk.me.module.emvl3.listener.MEEmvL3Listener;
import com.newland.sdk.module.emv.EmvExtParams;

import java.util.ArrayList;

/**
 * @Description EMVL3指令接口.
 * @Author wuhh
 * @Date 2019/12/30
 */
public interface EMVL3Module {
    /**
     *
     * @return
     */
    public boolean extInit(EmvExtParams emvExtParams);
    /**
     *
     * @param configuration
     * @param listener
     * @return
     */
    public boolean l3init(EmvL3Usage l3Usage, byte[] configuration, MEEmvL3Listener listener);

    /**
     *
     * @param fileName
     * @return
     */
    public boolean loadConfiguration(String fileName,EMVModule emvModule);

    /**
     *
     * @param cardIntf
     * @param tlvList
     * @return
     */
    public boolean updateTerminalConfig(CardContactMode cardIntf, byte[] tlvList);

    /**
     *
     * @param cardIntf
     * @return
     */
    public byte[] getTerminalConfig(CardContactMode cardIntf);

    /**
     *
     * @param cardIntf
     * @param tlvList
     * @return
     */
    public boolean addAID(CardContactMode cardIntf, byte[] tlvList);

    /**
     *
     * @param cardIntf
     * @param aid
     * @return
     */
    public AID getAID(CardContactMode cardIntf, byte[] aid);

    /**
     *
     * @param cardIntf
     * @param aid
     * @return
     */
    public boolean deleteAID(CardContactMode cardIntf, byte[] aid);

    public byte[] getAIDCount(CardContactMode cardIntf);
    /**
     *
     * @param capk
     * @return
     */
    public boolean addCAPublicKey(byte[] capk);

    /**
     *
     * @param rid
     * @param index
     * @return
     */
    public CAPK getCAPublicKey(byte[] rid, int index);

    /**
     *
     * @param rid
     * @param index
     * @return
     */
    public boolean deleteCAPublicKey(byte[] rid, int index);

    /**
     *
     * @return
     */
    public boolean deleteAllCAPublicKey();

    public byte[] getCAPublicKeyCount();

//    public boolean loadRevocationList(EntryCRL crl, ConfigMode mode);
//    public boolean loadExceptionList(EntryException exceptionList, ConfigMode mode);

    /**
     *
     * @param data
     * @return
     */
    public int preProcessTransaction(byte[] data);

    /**
     *
     * @param data
     * @return
     */
    public TransactionResult performTransaction(byte[] data);

    /**
     *
     * @param data
     * @return
     */
    public TransactionResult completeTransaction(byte[] data);

    /**
     *
     * @return
     */
    public boolean terminateTransaction();

    /**
     *
     * @param tag
     * @param data
     * @return
     */
    public boolean setData(int tag, byte[] data);

    /**
     *
     * @param tag
     * @return
     */
    public byte[] getData(int tag);

    /**
     *
     * @param tlvList
     * @return
     */
    public boolean setTLVData(byte[] tlvList);

    /**
     *
     * @param tagList
     * @param isPackZeroLen
     * @return
     */
    public byte[] getTlvData(ArrayList<Integer> tagList, boolean isPackZeroLen);

    /**
     *
     * @return
     */
    public boolean isSignature();

    public boolean setDebugMode(int level);

    /**
     *finish emv, poweroff iccard,turn off led
     * @return
     */
    public void finishEMV();
}
