package com.newland.sdk.module.emv;


import com.newland.emv.jni.type.emv_opt;
import com.newland.emv.jni.type.ep_opt;

import java.math.BigDecimal;

/**
 * Extra emv parameters
 *
 * @author: Lindan
 * @create: 2019/7/29
 */
public class TransactionExtParams {

    private int processingCode = ProcessingCode.GOODS_AND_SERVICE;
    private boolean supportSM = false;
    private BigDecimal otherAmount = new BigDecimal("0");
    private int[] customerTag;
    private ep_opt epOpt;
    private boolean ppseAppSel = true;
    private byte[] pusCtrl;
    private byte[] processData;
    private Integer cardInterfaces;//期望寻卡上电的类型，默认3种都寻，sp100只能传非接
    private Integer currentCardInterfaces;//当前已经上电的卡类型

    private boolean isGetUnionSpecialTag = true;
    private boolean  enablePreParam = false;

    private Byte[] extEmvCallBack;

    private boolean isSupportEmvDuration = false;//是否支持获取EMV SelectPPSE，SelectAID，GPO总耗时
    private boolean internalTvrOnlinePinBit = false;//只针对DPAS内核

    public TransactionExtParams() {
    }

    public TransactionExtParams(int processingCode, boolean isSupportSM, BigDecimal otherAmount, int[] customerTag, ep_opt epOpt) {
        this.processingCode = processingCode;
        this.supportSM = isSupportSM;
        this.otherAmount = otherAmount;
        this.customerTag = customerTag;
        this.epOpt = epOpt;
    }

    /**
     * Get the processing code
     *
     * @return
     */
    public int getProcessingCode() {
        return processingCode;
    }

    /**
     * <p>Set the processing code,tag:'9C'</p>
     * <p>Indicates the type of financial transaction, represented by the first two digits of [ISO8583: 1993] Processing Code.</p>
     *
     * @param processingCode {@link ProcessingCode}
     */
    public void setProcessingCode(int processingCode) {
        this.processingCode = processingCode;
    }

    /**
     * Get the Secondary amount associated with the transaction representing a cashback amount.
     *
     * @return
     */
    public BigDecimal getOtherAmount() {
        return otherAmount;
    }

    /**
     * Set the Secondary amount associated with the transaction representing a cashback amount.
     *
     * @param otherAmount (Unit:yuan)
     */
    public void setOtherAmount(BigDecimal otherAmount) {
        this.otherAmount = otherAmount;
    }


    /**
     * support SM or not
     *
     * @return
     */
    public boolean isSupportSM() {
        return supportSM;
    }

    /**
     * support SM or not (defalut is false)
     *
     * @param isSupportSM
     */
    public void setSupportSM(boolean isSupportSM) {
        this.supportSM = isSupportSM;
    }


    /**
     * Get the special tags which are out of the EMV protocol.
     *
     * @return
     */
    public int[] getCustomerTag() {
        return customerTag;
    }

    /**
     * Set the special tags which are out of the EMV protocol.
     *
     * @param customerTag
     */
    public void setCustomerTag(int[] customerTag) {
        this.customerTag = customerTag;
    }

    /**
     * get entrypoint params
     *
     * @return
     */
    public ep_opt getEpOpt() {
        return epOpt;
    }

    /**
     * set entrypoint params
     *
     * @param epOpt
     */
    public void setEpOpt(ep_opt epOpt) {
        this.epOpt = epOpt;
    }

    public boolean isPpseAppSel() {
        return ppseAppSel;
    }

    /**
     * <p>Select by AID supported</p>
     * <p>For some contactless kernel, such as Amex, if PPSE failed, will try to select by AID.</p>
     * <p>true:Select by AID is supported.(default)</p>
     * <p>false: Select by AID is not supported</p>
     *
     * @param ppseAppSel
     */
    public void setPpseAppSel(boolean ppseAppSel) {
        this.ppseAppSel = ppseAppSel;
    }

    /**
     * @return
     */
    public byte[] getPusCtrl() {
        return pusCtrl;
    }

    /**
     * set ctrl params
     * @param pusCtrl 5 bytes
     *               <p>byte 1 seekcard flag</p>
     *                      <p>  SEEK CARD IN SERVER = 2</p>
     * 						<p> CTIVE CARD IN SERVER = 3 </p>
     * 				 <p> btye 2 qpboc getdata flag </p>
     * 						<p> QPBOC NONEED GET DATA = 0 </p>
     * 						<p>QPBOC NEED GET DATA = 1 </p>
     * 				<p> byte 3 process light Flag(see the below, it need set the all four lights status)</p>
     * 				<p> byte 4 card read ok light Flag(see the below, it need set the all four lights status)</p>
     * 						<p> bit 8-7 first light, LED_RFID_BLUE_ON = 0x40, LED_RFID_BLUE_OFF = 0x80,LED_RFID_BLUE_FLICK = 0xc0 </p>
     * 						<p> bit 6-5 second light, LED_RFID_GREEN_ON = 0x10, LED_RFID_GREEN_OFF = 0x20,LED_RFID_GREEN_FLICK = 0x30 </p>
     * 						<p> bit 4-3 third light, LED_RFID_YELLOW_ON = 0x04, LED_RFID_YELLOW_OFF = 0x08, LED_RFID_YELLOW_FLICK = 0x0c </p>
     * 						<p> bit 2-1 four light, LED_RFID_RED_ON = 0x01, LED_RFID_RED_OFF = 0x02,LED_RFID_RED_FLICK = 0x03 </p>
     * 				<p> byte 5 AfterFinalSelect callback Flag,NO NEED CALLBACK = 0, NEED CALLBACK = 1 </p>
     */
    public void setPusCtrl(byte[] pusCtrl) {
        this.pusCtrl = pusCtrl;
    }

    /**
     * get EMV process data
     * @return
     */
    public byte[] getProcessData() {
        return processData;
    }

    /**
     * set EMV process data, used in NDK EMV final select process
     * @param processData TLV data
     */
    public void setProcessData(byte[] processData) {
        this.processData = processData;
    }

    public Integer getCardInterfaces() {
        return cardInterfaces;
    }

    /**
     * @param cardInterfaces MSR = 0x01;
     *                       CONTACT = 0x02;
     *                       CONTACTLESS = 0x04;
     */
    public void setCardInterfaces(Integer cardInterfaces) {
        this.cardInterfaces = cardInterfaces;
    }

    /**
     * get current card interface that has been power on.
     * @return
     */
    public Integer getCurrentCardInterfaces() {
        return currentCardInterfaces;
    }

    /**
     * set current card interface that has been power on.
     * @param currentCardInterfaces  MSR = 0x01;
     *                             CONTACT = 0x02;
     *                            CONTACTLESS = 0x04;
     */
    public void setCurrentCardInterfaces(Integer currentCardInterfaces) {
        this.currentCardInterfaces = currentCardInterfaces;
    }

    public boolean isGetUnionSpecialTag() {
        return isGetUnionSpecialTag;
    }
    /**
     * if true,it can obtain getSecondCurrencyCode,getAppCurrencyCode,getEc_balance_limit,getQpbocCardFunds from EmvTransInfo.
     * otherwise,it is not.
     * @param getUnionSpecialTag
     */
    public void setGetUnionSpecialTag(boolean getUnionSpecialTag) {
        isGetUnionSpecialTag = getUnionSpecialTag;
    }

    public boolean isEnablePreParam() {
        return enablePreParam;
    }

    public void setEnablePreParam(boolean enablePreParam) {
        this.enablePreParam = enablePreParam;
    }

    public Byte[] getExtEmvCallBack() {
        return extEmvCallBack;
    }

    public void setExtEmvCallBack(Byte[] extEmvCallBack) {
        this.extEmvCallBack = extEmvCallBack;
    }

    public boolean isSupportEmvDuration() {
        return isSupportEmvDuration;
    }
    public void setSupportEmvDuration(boolean isSupportEmvDuration) {
        this.isSupportEmvDuration = isSupportEmvDuration;
    }

    public boolean isInternalTvrOnlinePinBit() {
        return internalTvrOnlinePinBit;
    }

    public void setInternalTvrOnlinePinBit(boolean internalTvrOnlinePinBit) {
        this.internalTvrOnlinePinBit = internalTvrOnlinePinBit;
    }
}
