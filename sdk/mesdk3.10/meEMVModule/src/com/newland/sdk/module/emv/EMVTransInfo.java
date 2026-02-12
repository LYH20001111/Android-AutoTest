package com.newland.sdk.module.emv;

import com.newland.emv.jni.service.EmvJNIService;
import com.newland.emv.jni.type.EmvConst;
import com.newland.emv.jni.type.emv_opt;
import com.newland.emv.jni.type.ep_opt;
import com.newland.sdk.mtype.common.Const.EmvSelfDefinedReference;
import com.newland.sdk.mtype.common.Const.EmvStandardReference;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.me.module.emv.structure.AbstractEMVPackage;
import com.newland.sdk.me.module.emv.structure.EMVTagDefined;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;

/**
 * EMV transaction information package
 *
 * @since v1.0
 */
public class EMVTransInfo extends AbstractEMVPackage {
    private DeviceLogger deviceLogger = DeviceLoggerFactory.getLogger(EMVTransInfo.class);


    /**
     * Card number（0x5a）
     */
    @EMVTagDefined(tag = EmvStandardReference.PAN)
    private String cardNo;

    /**
     * Card sequence number（0x5F34）
     */
    @EMVTagDefined(tag = EmvStandardReference.CARD_SEQUENCE_NUMBER)
    private String cardSequenceNumber;

    /**
     * Card expiration date（0x5f24）
     */
    @EMVTagDefined(tag = EmvStandardReference.APP_EXPIRATION_DATE)
    private String cardExpirationDate;


    /**
     * Pboc implementation result（0xDF75）
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.PBOC_PROCESS_RSLT)
    private Integer executeRslt;
    /**
     * PBOC Electronic cash balance (0x9F79)
     */
    @EMVTagDefined(tag = EmvStandardReference.PBOC_CARD_FUNDS)
    private String pbocCardFunds;
//
    /**
     * QPBOC Electronic cash balance(0x9F5D)
     */
    @EMVTagDefined(tag = EmvStandardReference.QPBOC_CARD_FUNDS)
    private String qpbocCardFunds;
    /**
     * (0x57)
     */
    @EMVTagDefined(tag = EmvStandardReference.TRACK_2_EQV_DATA)
    private byte[] track_2_eqv_data;

    /**
     * (0xDF76)
     */
    private int errorcode;
    /**
     * Electronic cash balance upper limit (9f77)
     */
    @EMVTagDefined(tag = EmvStandardReference.EC_BALANCE_LIMIT)
    private byte[] ec_balance_limit;

    /**
     * Card holder name (5F20)
     */
    @EMVTagDefined(tag = EmvStandardReference.CARDHOLDER_NAME)
    private byte[] cardHolderName;

    /**
     * 0x9f51）1st currency cod of electronic cash transaction
     *
     * @see EmvStandardReference#APP_CURRENCY_CODE
     */
    @EMVTagDefined(tag = EmvStandardReference.APP_CURRENCY_CODE)
    private String appCurrencyCode;
    /**
     * Second currency cod of electronic cash transaction (0xdf71)
     *
     * @see EmvSelfDefinedReference#PBOC_TRANS_STEP
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.PBOC_TRANS_STEP)
    private String secondCurrencyCode;

    /**
     * Card Transaction Qualifiers(0x9F6c)
     *
     * @see EmvStandardReference#CDCVM_DATA
     */
    @EMVTagDefined(tag = EmvStandardReference.CDCVM_DATA)
    private byte[] cdcvm;
    /**
     * Emvr result Code
     */
    private int emvrsltCode;
    /**
     * cvm（Only for non - UnionPay cards）
     * <p>
     * cvm value
     */
    private byte cvm;
    private EntryPointType entryPointType;
    /**
     * kernelID
     * <p>
     * cvm value
     */
    private byte kernelId;
    private CardType openCardType;

    public byte[] onlinePin;        //< out, string with '\0' if online pin is entered
    public byte[] issScriptRes;//< out, if issuer script result exists */
    public Integer adviceReq;                    //< out, if advice is required (must be supported by ics)
    public Integer signatureReq;                    //< out, if the CVM finally request a signature
    public ep_opt epOpt;
    public emv_opt emvOpt;
    public emv_opt emvOpt2;//用于在回调步骤由用户修改内核参数用.因为原来每个步骤都是重新new emv_opt,如果使用emvOpt应该是有问题的.
    /**
     *Mobile CVM Results（9F71第三字节）
     */
    private byte mobileCVMResult;

    /**
     * emv L3,外接键盘，默认取所有tlv数据
     */
    private byte[] tlvData;

    /**
     * (9f06)
     */
    @EMVTagDefined(tag = EmvStandardReference.AID_TERMINAL)
    private byte[] aid;
    /**
     * EMV耗时，PPSE到GPO的APDU耗时,单位毫秒
     *
     * */
    public long emvDuration =0;

    /**
     * execution result（0xDF75）
     *
     * @return Integer
     */
    public Integer getExecuteRslt() {
        return executeRslt;
    }

    public void setExecuteRslt(Integer executeRslt) {
        this.executeRslt = executeRslt;
    }


    /**
     * Card number （0x5a）
     *
     * @return String
     */
    public String getCardNo() {
        if (null == cardNo || cardNo.equals("")) {
            if (null != track_2_eqv_data) {
                String track2 = InnerUtils.hexString(track_2_eqv_data);
                String[] trackArr = track2.split("D");
                if (null != trackArr && trackArr.length > 0) {
                    String panStr = trackArr[0];
                    if (panStr.length() > 19) {
                        cardNo = panStr.substring(panStr.length() - 19, panStr.length());
                    } else {
                        cardNo = panStr;
                    }
                }
            }
        } else {
            if (cardNo.endsWith("F")) {
                cardNo = cardNo.substring(0, cardNo.length() - 1);
            }
        }
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }


    /**
     * Card serialnumber 23 field（0x5F34）
     *
     * @return String
     */
    public String getCardSequenceNumber() {
        return cardSequenceNumber;
    }

    public void setCardSequenceNumber(String cardSequenceNumber) {
        this.cardSequenceNumber = cardSequenceNumber;
    }


    /**
     * Card expiration date（0x5f24）
     *
     * @return String
     */
    public String getCardExpirationDate() {
        return cardExpirationDate;
    }

    public void setCardExpirationDate(String cardExpirationDate) {
        this.cardExpirationDate = cardExpirationDate;
    }

    /**
     * PBOC online balance (0x9F79)
     *
     * @return String
     */
    public String getPbocCardFunds() {
        return pbocCardFunds;
    }

    public void setPbocCardFunds(String pbocCardFunds) {
        this.pbocCardFunds = pbocCardFunds;
    }

    /**
     * QPBOC inquiry of balance (0x9F5D)
     *
     * @return String
     */
    public String getQpbocCardFunds() {
        return qpbocCardFunds;
    }

    public void setQpbocCardFunds(String qpbocCardFunds) {
        this.qpbocCardFunds = qpbocCardFunds;
    }

    /**
     * (0x57)
     *
     * @return byte[]
     */
    public byte[] getTrack_2_eqv_data() {
        return track_2_eqv_data;
    }

    public void setTrack_2_eqv_data(byte[] track_2_eqv_data) {
        this.track_2_eqv_data = track_2_eqv_data;
    }


    /**
     * (0xDF76)
     *
     * @return byte[]
     */
    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    /**
     * Electronic cash balance upper limit (0x9f77）
     *
     * @return
     */
    public byte[] getEc_balance_limit() {
        return ec_balance_limit;
    }

    public void setEc_balance_limit(byte[] ec_balance_limit) {
        this.ec_balance_limit = ec_balance_limit;
    }

    /**
     * Get the card holder name (5F20)
     *
     * @return
     */
    public byte[] getCardHolderName() {
        return cardHolderName;
    }

    /**
     * Set the card holder name (5F20)
     * <p>
     *
     * @param cardHolderName
     */
    public void setCardHolderName(byte[] cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    /**
     * Get the 1st currency cod of electronic cash transaction（0x9f51）
     * <p>
     *
     * @return
     */
    public String getAppCurrencyCode() {
        return appCurrencyCode;
    }

    /**
     * Set the 1st currency cod of electronic cash transaction（0x9f51）
     * <p>
     *
     * @param appCurrencyCode
     */
    public void setAppCurrencyCode(String appCurrencyCode) {
        if (null != appCurrencyCode) {
            super.setExternal(EmvStandardReference.APP_CURRENCY_CODE, InnerUtils.hex2byte(appCurrencyCode));
        }
        this.appCurrencyCode = appCurrencyCode;
    }

    /**
     * Get the 2nd currency cod of electronic cash transaction (0xdf71)
     * <p>
     *
     * @return
     */
    public String getSecondCurrencyCode() {
        return secondCurrencyCode;
    }

    /**
     * Set the 2nd currency cod of electronic cash transaction (0xdf71)
     * <p>
     */
    public void setSecondCurrencyCode(String secondCurrencyCode) {
        if (null != secondCurrencyCode) {
            super.setExternal(EmvSelfDefinedReference.PBOC_TRANS_STEP, InnerUtils.hex2byte(secondCurrencyCode));
        }
        this.secondCurrencyCode = secondCurrencyCode;
    }

    public CardType getOpenCardType() {
        return openCardType;
    }

    public void setOpenCardType(CardType openCardType) {
        this.openCardType = openCardType;
    }

    /**
     * Get the value of Card Transaction Qualifiers(0x9F6C)
     * <p>
     */
    public byte[] getCdcvm() {
        return cdcvm;
    }

    /**
     * Set the Card Transaction Qualifiers(0x9F6c)
     * <p>
     */
    public void setCdcvm(byte[] cdcvm) {
        this.cdcvm = cdcvm;
    }

    public int getEmvrsltCode() {
        return emvrsltCode;
    }

    public void setEmvrsltCode(int emvrsltCode) {
        this.emvrsltCode = emvrsltCode;
    }

    public EntryPointType getEntryPointType() {
        return entryPointType;
    }

    public void setEntryPointType(EntryPointType entryPointType) {
        this.entryPointType = entryPointType;
    }

    public enum EntryPointType {
        QPBOC, PAYPASS, PAYWAVE, EXPRESSPAY, DISCOVERPAY, JCB, MCCS, RUPAY, INTERAC, PURE, KAHROBA, GIRO
    }

    /**
     * get cvm result value（Only for non - UnionPay cards）
     * <p>
     * NO CVM:0x00; OBTAIN SIGNATURE:0x10;
     * <p>
     * ONLINE PIN:0x20;CONFIRMATION CODE VERIFIED:0x30;
     * <p>
     * CVM N/A = 0xF0
     * <p>
     * if kernel id is expresspay: 0x04, refer following meanings:
     * 0x00-Unknown (if Mobile CVM not performed)
     * <p>
     * 0x01-Mobile CVM failed
     * <p>
     * 0x02-Mobile CVM Successful
     * <p>
     * 0x03-Mobile CVM Blocked
     * @return
     *
     */
    public byte getCvm() {
        if(getKernelId() == EmvConst.KERNEL_ID_EXPRESSPAY && isMobileCVMPerformed()){//美运的mobile cvm 取9F71第三字节
            return getmoblileCVMResult();
        }else{
            return cvm;
        }

    }

    /**
     * set cvm value
     *
     * @param cvm
     */
    public void setCvm(byte cvm) {
        this.cvm = cvm;
    }

    /**
     * get kernelID
     *
     * @return
     */
    public byte getKernelId() {
        return kernelId;
    }

    /**
     * set kernelID
     *
     * @param kernelId
     */
    public void setKernelId(byte kernelId) {
        this.kernelId = kernelId;
    }

    /**
     * get issuer script result
     *
     * @return
     */
    public byte[] getIssScriptRes() {
        return issScriptRes;
    }

    /**
     * set issuer script result
     *
     * @param issScriptRes
     */
    public void setIssScriptRes(byte[] issScriptRes) {
        this.issScriptRes = issScriptRes;
    }

    public Integer getAdviceReq() {
        return adviceReq;
    }

    public void setAdviceReq(Integer adviceReq) {
        this.adviceReq = adviceReq;
    }


    /**
     * if the CVM finally request a signature
     *
     * @return 1: the CVM finally request a signature
     */
    public Integer getSignatureReq() {
        return signatureReq;
    }

    /**
     * @param signatureReq
     */
    public void setSignatureReq(Integer signatureReq) {
        this.signatureReq = signatureReq;
    }

    /**
     * get Online Pin
     *
     * @return
     */
    public byte[] getOnlinePin() {
        return onlinePin;
    }

    /**
     * @param onlinePin
     */
    public void setOnlinePin(byte[] onlinePin) {
        this.onlinePin = onlinePin;
    }

    /**
     * Get the entry point option.
     *
     * @return
     */
    public ep_opt getEpOpt() {
        return epOpt;
    }

    /**
     * Set the entry point option.
     *
     * @param epOpt
     */
    public void setEpOpt(ep_opt epOpt) {
        this.epOpt = epOpt;
    }

    /**
     * Get the EMV option.
     *
     * @return
     */
    public emv_opt getEmvOpt() {
        return emvOpt;
    }

    /**
     * Set the EMV option.
     *
     * @param emvOpt
     */
    public void setEmvOpt(emv_opt emvOpt) {
        this.emvOpt = emvOpt;
    }

    /**
     * get mobile CVM Result
     * @return 0x00-Unknown (if Mobile CVM not performed)
     *         0x01-Mobile CVM failed
     *         0x02-Mobile CVM Successful
     *         0x03-Mobile CVM Blocked
     */
    private byte getmoblileCVMResult() {
        try {
            EmvJNIService emvcore = new EmvJNIService();
            byte[] buffer = new byte[1024];
            int len =-1;
            if(getOpenCardType()==CardType.ICCARD){
                deviceLogger.debug("[getmoblileCVMResult] EmvJNIService->jniemvgetdata 0x9F71");
                len = emvcore.jniemvgetdata(0x9F71, buffer, buffer.length);
            }else{
                deviceLogger.debug("[getmoblileCVMResult] EmvJNIService->jniSDKEPGetData 0x9F71");
                len = emvcore.jniSDKEPGetData(0x9F71, buffer, buffer.length);
            }
            if (len > 0){
                byte[] temp = new byte[len];
                System.arraycopy(buffer, 0, temp, 0, len);
                deviceLogger.debug("[getmoblileCVMResult] EmvJNIService len="+len+InnerUtils.hexString(temp));
            }

            if (len >= 3) {
                byte[] value = new byte[len];
                System.arraycopy(buffer, 0, value, 0, len);
                deviceLogger.debug("-----9F71："+(InnerUtils.hexString(value)));
                mobileCVMResult = value[2];
            }else{
                deviceLogger.error("-------9F71 non-existent---len:"+len);
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return mobileCVMResult;
    }

    /**
     * 判断是否执行了mobile cvm
     * @return
     */
    private boolean isMobileCVMPerformed(){
        try {
            EmvJNIService emvcore = new EmvJNIService();
            byte[] buffer = new byte[1024];
            int len =-1;
            if(getOpenCardType()==CardType.ICCARD){
                deviceLogger.debug("[isMobileCVMPerformed] EmvJNIService->jniemvgetdata 0x9F71");
                len = emvcore.jniemvgetdata(0x9F71, buffer, buffer.length);
            }else{
                deviceLogger.debug("[isMobileCVMPerformed] EmvJNIService->jniSDKEPGetData 0x9F71");
                len = emvcore.jniSDKEPGetData(0x9F71, buffer, buffer.length);
            }

            if (len > 0){
                byte[] temp = new byte[len];
                System.arraycopy(buffer, 0, temp, 0, len);
                deviceLogger.debug("[isMobileCVMPerformed] EmvJNIService len="+len+InnerUtils.hexString(temp));
            }

            if (len >= 3) {
                byte[] value = new byte[len];
                System.arraycopy(buffer, 0, value, 0, len);
                deviceLogger.debug("-----9F71："+(InnerUtils.hexString(value)));
                byte isPerformed = value[0];
                if(isPerformed==0x01){
                   return true;
                }
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * get emv tlv data
     * @return
     */
    public byte[] getTlvData() {
        return tlvData;
    }

    public void setTlvData(byte[] tlvData) {
        this.tlvData = tlvData;
    }

    /**
     * @return 9F06
     */
    public byte[] getAid() {
        return aid;
    }

    /**
     * @param aid  9F06
     */
    public void setAid(byte[] aid) {
        this.aid = aid;
    }

    public void setEmvParam(emv_opt emvOpt2) {
        this.emvOpt2 = emvOpt2;
    }
    public emv_opt getEmvParam() {
        return this.emvOpt2;
    }

    /**
     * get SelectPPSE，SelectAID，GPO duration,unit ms
     *
     * @return
     */
    public long getEmvDuration() {
        return emvDuration;
    }

    public void setEmvDuration(long emvDuration) {
        this.emvDuration = emvDuration;
    }
}
