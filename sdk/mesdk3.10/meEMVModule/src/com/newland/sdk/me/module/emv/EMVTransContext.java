package com.newland.sdk.me.module.emv;

import com.newland.emv.jni.type.emv_opt;
import com.newland.emv.jni.type.ep_opt;
import com.newland.emv.jni.type.rf_transdata;
import com.newland.sdk.mtype.common.Const.EmvSelfDefinedReference;
import com.newland.sdk.mtype.common.Const.EmvStandardReference;
import com.newland.sdk.me.module.emv.structure.AbstractEMVPackage;
import com.newland.sdk.me.module.emv.structure.EMVTagDefined;

public class EMVTransContext extends AbstractEMVPackage {

    public static final int _EMV_PROC_NOT_FORCEONLINE = 0x00;
    public static final int _EMV_PRCO_FORCEONLINE = 0x01;
    public static final int _EMV_MEDIATYPE_ICCARD = 0x00;
    public static final int _EMV_MEDIATYPE_RFCARD = 0x01;

    /**
     * 授权金额
     */
    @EMVTagDefined(tag = EmvStandardReference.AMOUNT_AUTHORISED_NUMERIC)
    private String amountAuthorisedNumeric;

    /**
     * 授权金额（其他）
     */
    @EMVTagDefined(tag = EmvStandardReference.AMOUNT_OTHER_NUMERIC)
    private String amountOtherNumeric;

    /**
     * 交易类型
     * <p>
     *
     * @see EmvStandardReference#TRANSACTION_TYPE
     */
    @EMVTagDefined(tag = EmvStandardReference.TRANSACTION_TYPE)
    private int transactionType;

    /**
     * 交易类型
     * <p>
     *
     * @see EmvStandardReference#TRANSACTION_TYPE
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.INNER_TRANSACTION_TYPE)
    private int innerTransactionType;

    @EMVTagDefined(tag = EmvSelfDefinedReference.MEDIATYPE)
    private int mediaType = _EMV_MEDIATYPE_ICCARD;

    @EMVTagDefined(tag = EmvSelfDefinedReference.PBOC_TRANS_STEP)
    private int pbocTransStep;

    @EMVTagDefined(tag = EmvSelfDefinedReference.FORCE_ONLINE)
    private int forceOnline = _EMV_PRCO_FORCEONLINE;

    @EMVTagDefined(tag = EmvSelfDefinedReference.ACCTSELECTED_INDICATOR)
    private int acctSelectedIndicator;

    private boolean isUseExtCardReader;
    private int kernelID;
    private ep_opt epOpt;
    private rf_transdata rfTransData;
    private emv_opt emvOpt;
    private boolean isSimpleProcess;
    private boolean isEpProcess;
    private byte[] pusCtrl;
    private byte[] processData;
    private boolean NDKEMVProcess = false;
    private boolean donePreProcess;

    private boolean isGetUnionSpecialTag = true;
    public String getAmountAuthorisedNumeric() {
        return amountAuthorisedNumeric;
    }

    public void setAmountAuthorisedNumeric(String amountAuthorisedNumeric) {
        this.amountAuthorisedNumeric = amountAuthorisedNumeric;
    }

    public String getAmountOtherNumeric() {
        return amountOtherNumeric;
    }

    public void setAmountOtherNumeric(String amountOtherNumeric) {
        this.amountOtherNumeric = amountOtherNumeric;
    }

    public int getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(int transactionType) {
        this.transactionType = transactionType;
    }

    public int getMediaType() {
        return mediaType;
    }

    public void setMediaType(int mediaType) {
        this.mediaType = mediaType;
    }

    public int getPbocTransStep() {
        return pbocTransStep;
    }

    public void setPbocTransStep(int pbocTransStep) {
        this.pbocTransStep = pbocTransStep;
    }

    public boolean getForceOnline() {
        return this.forceOnline == _EMV_PRCO_FORCEONLINE;
    }

    public void setForceOnline(boolean forceOnline) {
        if (forceOnline)
            this.forceOnline = _EMV_PRCO_FORCEONLINE;
        else
            this.forceOnline = _EMV_PROC_NOT_FORCEONLINE;
    }

    public int getAcctSelectedIndicator() {
        return acctSelectedIndicator;
    }

    public void setAcctSelectedIndicator(int acctSelectedIndicator) {
        this.acctSelectedIndicator = acctSelectedIndicator;
    }

    public int getInnerTransactionType() {
        return innerTransactionType;
    }

    public void setInnerTransactionType(int innerTransactionType) {
        this.innerTransactionType = innerTransactionType;
    }

    public boolean isUseExtCardReader() {
        return isUseExtCardReader;
    }

    public void setUseExtCardReader(boolean isUseExtCardReader) {
        this.isUseExtCardReader = isUseExtCardReader;
    }

    public int getKernelID() {
        return kernelID;
    }

    public void setKernelID(int kernelID) {
        this.kernelID = kernelID;
    }

    public ep_opt getEpOpt() {
        return epOpt;
    }

    public void setEpOpt(ep_opt epOpt) {
        this.epOpt = epOpt;
    }

    public rf_transdata getRfTransData() {
        return rfTransData;
    }

    public void setRfTransData(rf_transdata rfTransData) {
        this.rfTransData = rfTransData;
    }

    public emv_opt getEmvOpt() {
        return emvOpt;
    }

    public void setEmvOpt(emv_opt emvOpt) {
        this.emvOpt = emvOpt;
    }

    public boolean isSimpleProcess() {
        return isSimpleProcess;
    }

    public void setSimpleProcess(boolean isSimpleProcess) {
        this.isSimpleProcess = isSimpleProcess;
    }

    public boolean isEpProcess() {
        return isEpProcess;
    }

    public void setEpProcess(boolean isEpProcess) {
        this.isEpProcess = isEpProcess;
    }

    public byte[] getPusCtrl() {
        return pusCtrl;
    }

    /**
     * set some ctrl params used in NDK EMV
     *
     * @param pusCtrl
     */
    public void setPusCtrl(byte[] pusCtrl) {
        this.pusCtrl = pusCtrl;
    }

    /**
     * get NDK EMV process data, used in final select process
     *
     * @return
     */
    public byte[] getProcessData() {
        return processData;
    }

    /**
     * set NDK EMV process data, used in fianl select process
     *
     * @param processData
     */
    public void setProcessData(byte[] processData) {
        this.processData = processData;
    }

    public boolean isNDKEMVProcess() {
        return NDKEMVProcess;
    }

    public void setNDKEMVProcess(boolean NDKEMVProcess) {
        this.NDKEMVProcess = NDKEMVProcess;
    }

    public boolean isDonePreProcess() {
        return donePreProcess;
    }

    public void setDonePreProcess(boolean donePreProcess) {
        this.donePreProcess = donePreProcess;
    }

    public boolean isGetUnionSpecialTag() {
        return isGetUnionSpecialTag;
    }

    public void setGetUnionSpecialTag(boolean getUnionSpecialTag) {
        isGetUnionSpecialTag = getUnionSpecialTag;
    }
}
