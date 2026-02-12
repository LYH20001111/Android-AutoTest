package com.newland.sdk.module.emv;

import com.newland.sdk.mtype.common.Const.EmvSelfDefinedReference;
import com.newland.sdk.mtype.common.Const.EmvStandardReference;
import com.newland.sdk.me.module.emv.structure.AbstractEMVPackage;
import com.newland.sdk.me.module.emv.structure.EMVTagDefined;

/**
 * AID
 *
 * @since ver3.10.01
 */
public class AID extends AbstractEMVPackage {
    /**
     * Application Identifier (AID) – terminal(9F06)
     *
     * @see EmvStandardReference#AID_TERMINAL
     */
    @EMVTagDefined(tag = EmvStandardReference.AID_TERMINAL)
    protected byte[] aid;
    /**
     * KernelId(DF37)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.KERNELID)
    private byte[] kernelId;
    /**
     * Terminal performance (0x9f33)
     *
     * @see EmvStandardReference#TERMINAL_CAPABILITIES
     */
    @EMVTagDefined(tag = EmvStandardReference.TERMINAL_CAPABILITIES)
    private byte[] terminalCapabilities;
    /**
     * kernel configuration (DF48)<p>
     * (Terminal CVM Capabilities use this data when the transaction amount is less than or equal to cvm limit)<p>
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.CAP_NO_CVM)
    private byte[] cap_no_cvm;
    /**
     * Magstripe CVM Capability-CVM Required(DF42)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.MAGSCVM)
    private byte[] magStripeCvm;

    /**
     * Magstripe CVM Capability- NO CVM Required(DF47)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.MAGSNOCVM)
    private byte[] magStripeNoCvm;

    /**
     * kernel configuration (DF2F)‘DF811B’
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.KERNEL_CONFIGURATION)
    private byte[] kernelConfiguration;

    /**
     * Default Udol(DF2B)'DF811A'
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.DEUDOL)
    private byte[] defaultUdol;

    /**
     * Mobile Support Indicator (DF46)'9F7E’
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.MOBILE_SUPPORT_INDICATOR)
    private byte[] mobileSupportIndicator;
    /**
     * Electronic cash transaction limits(0x9F7B)
     * used for READER_CLSS_TRANS_LIMIT_ON_DEVICE_CVM(DF8125)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.EC_TRANS_LIMIT)
    private byte[] ecTransactionLimit;

    /**
     * Reader CVM Required Limit - 'DF8126’(DF21)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.NCICC_CVM_LIMIT)
    private byte[] cvmLimit;
    /**
     * TAC Denial (DF13)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.TAC_DENIAL)
    private byte[] tacDenial;
    /**
     * TAC online(DF12)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.TAC_ONLINE)
    private byte[] tacOnLine;
    /**
     * TAC default (DF11)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.TAC_DEFAULT)
    private byte[] tacDefault;
    /**
     * Application version number(9F09)
     * Version number of a particular application supported by the Terminal.
     */
    @EMVTagDefined(tag = EmvStandardReference.APP_VERSION_NUMBER_TERMINAL)
    private byte[] appVersionNumber;
    /**
     * Terminal Floor Limit.Indicates the floor limit in the terminal in conjunction with the AID(9F1B)
     */
    @EMVTagDefined(tag = EmvStandardReference.TERMINAL_FLOOR_LIMIT)
    private byte[] terminalFloorLimit;
    /**
     * Transaction Currency Code(5F2A)
     */
    @EMVTagDefined(tag = EmvStandardReference.TRANSACTION_CURRENCY_CODE)
    private String transactionCurrencyCode;
//--------------------------------------
    /**
     * Default DDOL
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.DEFAULT_DDOL)
    private byte[] ddol;

    /**
     * Application version number - card(9F08)
     * Version number assigned by the Issuer for the application.
     */
    @EMVTagDefined(tag = EmvStandardReference.APP_VERSION_NUMBER_CARD)
    private byte[] appVersionNumberIssuer;

    /**
     * Application selection indicator（ASI）(DF01)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.APP_SELECT_INDICATOR)
    private Integer appSelectIndicator;
    /**
     * Threshold of random selection(DF15)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.THRESHOLD_VALUE_FOR_BIASED_RANDOM_SELECTION)
    private byte[] thresholdValue;

    /**
     * Maximum Target Percentage to be used for Biased Random Selection(0xDF16)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.MAX_TARGET_PERCENTAGE_FOR_BIASED_RANDOM_SELECTION)
    private Integer maxTargetPercentage;

    /**
     * Target percentage of random selection(DF17)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.TARGET_PERCENTAGE_FOR_RANDOM_SELECTION)
    private Integer targetPercentage;

    /**
     * Indicate whether using the Limit Amount(DF27)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.LIMIT_EXIST)
    private byte[] amountLimitIndicator;
    /**
     * Reader Contactless offline Floor Limit (DF19)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.NCICC_OFFLINE_FLOOR_LIMIT)
    private byte[] offlineFloorLimit;

    /**
     * Reader Contactless Transaction Limit(DF20)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.NCICC_TRANS_LIMIT)
    private byte[] transactionLimit;


    /**
     * EC Terminal Support Indicator(0x9F7A)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.EC_SUPPORT_INDICATOR)
    private Integer ecSupportIndicator;

    /**
     * Merchant category code(9f15)
     */
    @EMVTagDefined(tag = EmvStandardReference.MERCHANT_CATEGORY_CODE)
    private String merchantCategoryCode;


    /**
     * Country code(9F1A)
     */
    @EMVTagDefined(tag = EmvStandardReference.TERMINAL_COUNTRY_CODE)
    private byte[] terminalCountryCode;

    /**
     * Transaction currency index(0x5f36)
     *
     * @see EmvStandardReference#TRANSACTION_CURRENCY_EXP
     */
    @EMVTagDefined(tag = EmvStandardReference.TRANSACTION_CURRENCY_EXP)
    private String transactionCurrencyExp;

    /**
     * Terminal type(0x9f35)
     *
     * @see EmvStandardReference#TERMINAL_TYPE
     */
    @EMVTagDefined(tag = EmvStandardReference.TERMINAL_TYPE)
    private Integer terminalType;


    /**
     * Terminal additional performance (0x9f40)
     *
     * @see EmvStandardReference#ADDITIONAL_TERMINAL_CAPABILITIES
     */
    @EMVTagDefined(tag = EmvStandardReference.ADDITIONAL_TERMINAL_CAPABILITIES)
    private byte[] additionalTerminalCapabilities;

    /**
     * Terminal transaction attributes(9f66/9f6e)
     * it's the value of 9f6e tag when the card is ExpressPay
     */
    @EMVTagDefined(tag = EmvStandardReference.TERMINAL_TRANSACTION_QUALIFIERS)
    private byte[] terminalTransProp;
    /**
     * Tdol(DF45)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.DEFAULT_TDOL)
    private byte[] tdol;
    /**
     * Acquirer identification(9f01)
     *
     * @see EmvStandardReference#ACQUIRER_IDENTIFIER
     */
    @EMVTagDefined(tag = EmvStandardReference.ACQUIRER_IDENTIFIER)
    private String acquirerIdentifier;

    /**
     * (JCB)Terminal Interchange Profile((9F53)
     */
    @EMVTagDefined(tag = EmvStandardReference.TIP)
    private byte[] terminalInterchangeProfile;
    /**
     * (EXPRESSPAY)Contactless Reader Capabilities(DF49)'9F6D'
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.EX_Terminal_CAP)
    private byte[] expTerminalCap;

    /**
     * statusCheckSupport(DF39) 0x01 supported;0x00 Not supported
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.STATUSCHECK)
    private byte[] statusCheckSupport;


    /**
     * kernel extended params.(DF52)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.PPTLV)
    private byte[] kernelExtendedTLV;

    /**
     * Mag-stripe Application Version Number(DF2D)'9F6D'
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.MAGAPPVER)
    private byte[] magAppVer;


    /**
     * paywave config(DF34)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.PWCONFIG)
    private byte[] pwConfig;

    /**
     * Zero Amount Allowed flag(DF3A)<p>
     * 1 :allowed，0:not allowed<p>
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.ZEROALLOW)
    private byte[] zeroAmountAllow;
    /**
     * (JCB)Combination Options(DF60)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.COMBINATIONOPT)
    private byte[] combinationOP;


    /**
     * (PAYWAVE)Dynamic Reader Limits(DF3D)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.DRLSTATUS)
    private byte[] drlStatus;

    /**
     * (PAYWAVE)DrlData(DF3F)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.DRLDATA)
    private byte[] drlData;

    /**
     * (Expresspay)DrlData(DF53)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.DRLDATA_EXP)
    private byte[] drlDataExp;

    /**
     * (PAYPASS)Max Lifetime of Torn Transaction Log Record(DF43)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.MEXLTTORN)
    private byte[] maxLifetimeTornLog;


    /**
     * (Expresspay)Unpredictable Number Range(DF4A)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.EXRANDOM)
    private byte[] exRandomScope;

    /**
     * Terminal Risk Management Data(9f1d)
     * Application-specific value used by the card for risk management purposes
     *
     * @see EmvStandardReference#TERMINAL_RISK_MANAGEMENT_DATA
     */
    @EMVTagDefined(tag = EmvStandardReference.TERMINAL_RISK_MANAGEMENT_DATA)
    private byte[] riskManagementData;
    /**
     * Transaction currency index(0x5f36)
     *
     * @see EmvStandardReference#TRANSACTION_CURRENCY_EXP
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.DF7D)
    private Integer transactionType;
    /**
     * Transaction currency index(0x5f36)
     *
     * @see EmvStandardReference#TRANSACTION_CURRENCY_EXP
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.PP1F8101)
    private Integer transactionTypeCheckFlag;


    /**
     * DF25
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.EMVSELECTKERNEL)
    private Integer emvSelectKernel;

    /**
     * (EXPRESSPAY)Contactless Reader Capabilities - Tag ‘9F6D’(DF49)
     *
     * @return
     */
    public byte[] getExpTerminalCap() {
        return expTerminalCap;
    }

    /**
     * (EXPRESSPAY)Contactless Reader Capabilities - Tag ‘9F6D’(DF49)
     *
     * @param expTerminalCap
     */
    public void setExpTerminalCap(byte[] expTerminalCap) {
        this.expTerminalCap = expTerminalCap;
    }

    /**
     * Get Mobile Support Indicator (DF46)
     *
     * @return
     */
    public byte[] getMoblieSupportIndicator() {
        return mobileSupportIndicator;
    }

    /**
     * Set Mobile Support Indicator (DF46)
     *
     * @param mobileSupportIndicator
     */
    public void setMobileSupportIndicator(byte[] mobileSupportIndicator) {
        this.mobileSupportIndicator = mobileSupportIndicator;
    }

    /**
     * Get application Identifier (AID) – terminal(0x9f06)
     *
     * @return byte[]
     */
    public byte[] getAid() {
        return aid;
    }

    /**
     * Set application Identifier (AID) – terminal(0x9f06)
     *
     * @param aid
     */
    public void setAid(byte[] aid) {
        this.aid = aid;
    }

    /**
     * Get Application selection indicator-ASI(0xDF01)
     *
     * @return integer
     */
    public Integer getAppSelectIndicator() {
        return appSelectIndicator;
    }

    /**
     * Set Application selection indicator-ASI (0xDF01)
     *
     * @param appSelectIndicator
     */
    public void setAppSelectIndicator(Integer appSelectIndicator) {
        this.appSelectIndicator = appSelectIndicator;
    }

    /**
     * Get the app version number(0x9f09)
     * Version number of a particular application supported by the Terminal.
     *
     * @return byte[]
     */
    public byte[] getAppVersionNumber() {
        return appVersionNumber;
    }

    /**
     * Set the app version number(0x9f09)
     * Version number of a particular application supported by the Terminal.
     *
     * @param appVersionNumber
     */
    public void setAppVersionNumber(byte[] appVersionNumber) {
        this.appVersionNumber = appVersionNumber;
    }

    /**
     * Terminal Action Code - Default(0xDF11)
     *
     * @return byte[]
     */
    public byte[] getTacDefault() {
        return tacDefault;
    }

    /**
     * Terminal Action Code - Default(0xDF11)
     *
     * @param tacDefault
     */
    public void setTacDefault(byte[] tacDefault) {
        this.tacDefault = tacDefault;
    }

    /**
     * Terminal Action Code - Online(0xDF12)
     *
     * @return byte[]
     */
    public byte[] getTacOnLine() {
        return tacOnLine;
    }

    /**
     * Terminal Action Code - Online(0xDF12)
     *
     * @param tacOnLine
     */
    public void setTacOnLine(byte[] tacOnLine) {
        this.tacOnLine = tacOnLine;
    }

    /**
     * Terminal Action Code - Denial(0xDF13)
     *
     * @return byte[]
     */
    public byte[] getTacDenial() {
        return tacDenial;
    }

    /**
     * Terminal Action Code - Denial(0xDF13)
     *
     * @param tacDenial
     */
    public void setTacDenial(byte[] tacDenial) {
        this.tacDenial = tacDenial;
    }

    /**
     * <p>Get the terminal Floor Limit(0x9f1b)</p>
     * <p>Indicates the floor limit in the terminal in conjunction with the AID.</p>
     *
     * @return byte[]
     */
    public byte[] getTerminalFloorLimit() {
        return terminalFloorLimit;
    }

    /**
     * <p>Set the terminal Floor Limit(0x9f1b)</p>
     * <p>Indicates the floor limit in the terminal in conjunction with the AID.</p>
     *
     * @param terminalFloorLimit
     */
    public void setTerminalFloorLimit(byte[] terminalFloorLimit) {
        this.terminalFloorLimit = terminalFloorLimit;
    }

    /**
     * Get the threshold of bias random selection(0xDF15)
     *
     * @return byte[]
     */
    public byte[] getThresholdValue() {
        return thresholdValue;
    }

    /**
     * Set the threshold of bias random selection(0xDF15)
     *
     * @param thresholdValue
     */
    public void setThresholdValue(byte[] thresholdValue) {
        this.thresholdValue = thresholdValue;
    }

    /**
     * Get the maximum target percentage of the bias random selection(0xDF16)
     *
     * @return Integer
     */
    public Integer getMaxTargetPercentage() {
        return maxTargetPercentage;
    }

    /**
     * Set maximum target percentage for bias random selection(0xDF16)
     *
     * @param maxTargetPercentage
     */
    public void setMaxTargetPercentage(Integer maxTargetPercentage) {
        this.maxTargetPercentage = maxTargetPercentage;
    }

    /**
     * Get the percentage of the target of random selection(0xDF17)
     *
     * @return Integer
     */
    public Integer getTargetPercentage() {
        return targetPercentage;
    }

    /**
     * Set the percentage of the random selection of the target(0xDF17)
     *
     * @param targetPercentage
     */
    public void setTargetPercentage(Integer targetPercentage) {
        this.targetPercentage = targetPercentage;
    }

    /**
     * <p>Get the DDOL value(0xDF44)</p>
     * <p>DDOL-Dynamic Data Authentication Data Object List</p>
     *
     * @return byte[]
     */
    public byte[] getDdol() {
        return ddol;
    }

    /**
     * Set the DDOL value(0xDF44)
     * <p>DDOL-Dynamic Data Authentication Data Object List</p>
     *
     * @param ddol
     */
    public void setDdol(byte[] ddol) {
        this.ddol = ddol;
    }

    /**
     * Get terminal electronic cash transaction limits(0x9F7B)
     *
     * @return byte[]
     */
    public byte[] getEcTransactionLimit() {
        return ecTransactionLimit;
    }

    /**
     * Set end of terminal electronic cash transaction limit(0x9F7B)
     *
     * @param ecTransactionLimit
     */
    public void setEcTransactionLimit(byte[] ecTransactionLimit) {
        this.ecTransactionLimit = ecTransactionLimit;
    }

    /**
     * Reader Contactless Floor Limit – the amount limit at which online authorization is requested(0xDF19).
     *
     * @return
     */
    public byte[] getOfflineFloorLimit() {
        return offlineFloorLimit;
    }

    /**
     * Reader Contactless Floor Limit – the amount limit at which online authorization is requested.(0xDF19).
     *
     * @param offlineFloorLimit
     */
    public void setOfflineFloorLimit(byte[] offlineFloorLimit) {
        this.offlineFloorLimit = offlineFloorLimit;
    }


    /**
     * Reader Contactless Transaction Limit – the amount limit allowed for contactless transactions(0xDF20).
     *
     * @return byte[]
     */
    public byte[] getTransactionLimit() {
        return transactionLimit;
    }

    /**
     * Reader Contactless Transaction Limit – the amount limit allowed for contactless transactions(0xDF20).
     *
     * @param transactionLimit
     */
    public void setTransactionLimit(byte[] transactionLimit) {
        this.transactionLimit = transactionLimit;
    }

    /**
     * Reader CVM Required Limit – the amount limit at which cardholder verification is requested(0xDF21)'DF8126’.
     *
     * @return
     */
    public byte[] getCvmLimit() {
        return cvmLimit;
    }

    /**
     * Reader CVM Required Limit – the amount limit at which cardholder verification is requested(0xDF21)'DF8126’.
     *
     * @param cvmLimit
     */
    public void setCvmLimit(byte[] cvmLimit) {
        this.cvmLimit = cvmLimit;
    }

    /**
     * Get the  EC Terminal Support Indicator(0x9F7A)
     *
     * @return
     */
    public Integer getEcSupportIndicator() {
        return ecSupportIndicator;
    }

    /**
     * Set the  EC Terminal Support Indicator(0x9F7A)
     * 0x01:support;0x00:unsupport
     *
     * @param ecSupportIndicator
     */
    public void setEcSupportIndicator(Integer ecSupportIndicator) {
        this.ecSupportIndicator = ecSupportIndicator;
    }

    /**
     * Get the code of the merchant category(9F15)
     *
     * @return
     */
    public String getMerchantCategoryCode() {
        return merchantCategoryCode;
    }

    /**
     * Set the code of the merchant category(9F15)
     * Classifies the type of business being done by the merchant, represented according to ISO 8583:1993 for Card Acceptor Business Code.
     *
     * @param merchantCategoryCode
     */
    public void setMerchantCategoryCode(String merchantCategoryCode) {
        this.merchantCategoryCode = merchantCategoryCode;
    }

    /**
     * Get transaction Currency Code(5F2A)
     *
     * @return
     */
    public String getTransactionCurrencyCode() {
        return transactionCurrencyCode;
    }

    /**
     * Set transaction Currency Code(5F2A)
     *
     * @param transactionCurrencyCode
     */
    public void setTransactionCurrencyCode(String transactionCurrencyCode) {
        this.transactionCurrencyCode = transactionCurrencyCode;
    }

    /**
     * Get the terminal country code (0x9f1a)
     *
     * @return byte[]
     */
    public byte[] getTerminalCountryCode() {
        return terminalCountryCode;
    }

    /**
     * Set the terminal country code (0x9f1a)
     * Indicates the country of the terminal, represented according to ISO 3166.
     *
     * @param terminalCountryCode
     */
    public void setTerminalCountryCode(byte[] terminalCountryCode) {
        this.terminalCountryCode = terminalCountryCode;
    }

    /**
     * Get the transaction currency exponent (0x5f36)
     *
     * @return String
     */
    public String getTransactionCurrencyExp() {
        return transactionCurrencyExp;
    }

    /**
     * set the transaction currency exponent (0x5f36)
     * Indicates the implied position of the decimal point from the right of the transaction amount represented according to ISO 4217.
     *
     * @param transactionCurrencyExp
     */
    public void setTransactionCurrencyExp(String transactionCurrencyExp) {
        this.transactionCurrencyExp = transactionCurrencyExp;
    }

    /**
     * Get the terminal type (0x9f35)
     *
     * @return Integer
     */
    public Integer getTerminalType() {
        return terminalType;
    }

    /**
     * Set the terminal type (0x9f35)
     * Indicates the environment of the terminal, its communications capability, and its operational control.
     *
     * @param terminalType
     */
    public void setTerminalType(Integer terminalType) {
        this.terminalType = terminalType;
    }

    /**
     * Get terminal performance(0x9f33)
     *
     * @return
     */
    public byte[] getTerminalCapabilities() {
        return terminalCapabilities;
    }

    /**
     * Set terminal performance(0x9f33)
     *
     * @param terminalCapabilities
     */
    public void setTerminalCapabilities(byte[] terminalCapabilities) {
        this.terminalCapabilities = terminalCapabilities;
    }

    /**
     * Get the additional terminal capabilities (0x9f40)
     *
     * @return byte[]
     */
    public byte[] getAdditionalTerminalCapabilities() {
        return additionalTerminalCapabilities;
    }

    /**
     * Set the additional terminal capabilities (0x9f40)
     * Indicates the data input and output capabilities of the terminal.
     *
     * @param additionalTerminalCapabilities
     */
    public void setAdditionalTerminalCapabilities(byte[] additionalTerminalCapabilities) {
        this.additionalTerminalCapabilities = additionalTerminalCapabilities;
    }

    /**
     * Enhanced Contactless Reader Capabilities.(0x9f66)
     */
    public byte[] getTerminalTransProp() {
        return terminalTransProp;
    }

    /**
     * Enhanced Contactless Reader Capabilities.(0x9f66)
     *
     * @param terminalTransProp
     */
    public void setTerminalTransProp(byte[] terminalTransProp) {
        this.terminalTransProp = terminalTransProp;
    }

    /**
     * <p>Get the Tdol value.(DF45)</p>
     * <p>TDOL-Transaction Certificate Data Object List</p>
     *
     * @return
     */
    public byte[] getTdol() {
        return tdol;
    }

    /**
     * Set the Tdol value.(DF45)(DF45)
     * <p>TDOL-Transaction Certificate Data Object List</p>
     *
     * @param tdol
     */
    public void setTdol(byte[] tdol) {
        this.tdol = tdol;
    }

    /**
     * Get the acquirer Identifier.(0x9f01)
     *
     * @return String
     */
    public String getAcquirerIdentifier() {
        return acquirerIdentifier;
    }

    /**
     * Set the acquirer Identifier.(0x9f01)
     * Uniquely identifies the acquirer within each payment system.
     *
     * @param acquirerIdentifier
     */
    public void setAcquirerIdentifier(String acquirerIdentifier) {
        this.acquirerIdentifier = acquirerIdentifier;
    }


    /**
     * Get the application version number.(0x9f08)
     * <p>Version number assigned by the Issuer for the application.</p>
     *
     * @return byte[]
     */
    public byte[] getAppVersionNumberIssuer() {
        return appVersionNumberIssuer;
    }

    /**
     * set Application version number - card.(9F08)
     * <p>Version number assigned by the Issuer for the application.</p>
     *
     * @param appVersionNumberIssuer
     */
    public void setAppVersionNumberIssuer(byte[] appVersionNumberIssuer) {
        this.appVersionNumberIssuer = appVersionNumberIssuer;
    }

    /**
     * Get the kernel Config(DF811B).
     *
     * @return
     */
    public byte[] getKernelConfiguration() {
        return kernelConfiguration;
    }

    /**
     * Set the kernel Config(DF811B).
     *
     * @param kernelConfiguration
     */
    public void setKernelConfiguration(byte[] kernelConfiguration) {
        this.kernelConfiguration = kernelConfiguration;
    }

    /**
     * Get the indicator of the Contactless Transaction amount limit.(DF27)
     * Indicate whether using the Limit Amount.
     *
     * @return
     */
    public byte[] getAmountLimitIndicator() {
        return amountLimitIndicator;
    }

    /**
     * Set the indicator of the Contactless Transaction amount limit.(DF27)
     * Indicate whether using the Limit Amount.
     *
     * @param amountLimitIndicator
     */
    public void setAmountLimitIndicator(byte[] amountLimitIndicator) {
        this.amountLimitIndicator = amountLimitIndicator;
    }

    /**
     * get kernel configuration (DF48)<p>
     * (Terminal CVM Capabilities use this data when the transaction amount is less than or equal to cvm limit)<p>
     *
     * @return
     */
    public byte[] getCap_no_cvm() {
        return cap_no_cvm;
    }

    /**
     * Set kernel configuration (DF48)<p>
     * (Terminal CVM Capabilities use this data when the transaction amount is less than or equal to cvm limit)<p>
     *
     * @param cap_no_cvm
     */
    public void setCap_no_cvm(byte[] cap_no_cvm) {
        this.cap_no_cvm = cap_no_cvm;
    }

    /**
     * get the kernel extended params.(DF52)
     *
     * @return
     */
    public byte[] getKernelExtendedTLV() {
        return kernelExtendedTLV;
    }

    /**
     * set the kernel extended params.(BER-TLV format).(DF52)
     * <p>You can setup other configuration into this item. Such as configuration for Data Exchange.</p>
     * <p>e.g,‘DF8132’\‘DF8133’\‘DF8134’\‘DF8135’\‘DF8136’\‘DF8137’</p>
     *
     * @param kernelExtendedTLV
     */
    public void setKernelExtendedTLV(byte[] kernelExtendedTLV) {
        this.kernelExtendedTLV = kernelExtendedTLV;
    }

    /**
     * get the default Udol.(DF2B)
     *
     * @return
     */
    public byte[] getDefaultUdol() {
        return defaultUdol;
    }

    /**
     * set the default Udol.(DF2B)
     *
     * @param defaultUdol
     */
    public void setDefaultUdol(byte[] defaultUdol) {
        this.defaultUdol = defaultUdol;
    }

    /**
     * (PAYPASS)Mag-stripe Application Version Number(DF2D)'9F6D'
     * Version number assigned by the payment system for the specific mag-stripe mode functionality of the Kernel.
     *
     * @return
     */
    public byte[] getMagAppVer() {
        return magAppVer;
    }

    /**
     * (PAYPASS)Mag-stripe Application Version Number(DF2D)'9F6D'
     * Version number assigned by the payment system for the specific mag-stripe mode functionality of the Kernel.
     *
     * @param magAppVer
     */
    public void setMagAppVer(byte[] magAppVer) {
        this.magAppVer = magAppVer;
    }

    /**
     * get paywave config(DF34)
     *
     * @return
     */
    public byte[] getPwConfig() {
        return pwConfig;
    }

    /**
     * set paywave config(DF34)
     *
     * @param pwConfig
     */
    public void setPwConfig(byte[] pwConfig) {
        this.pwConfig = pwConfig;
    }

    /**
     * get KernelId(DF37)
     *
     * @return
     */
    public byte[] getKernelId() {
        return kernelId;
    }

    /**
     * set KernelId(DF37)
     *
     * @param kernelId
     */
    public void setKernelId(byte[] kernelId) {
        this.kernelId = kernelId;
    }

    /**
     * Status check supported.(DF39)
     *
     * @return
     */
    public byte[] getStatusCheckSupport() {
        return statusCheckSupport;
    }

    /**
     * Status check supported.(DF39)
     * <p>0x01 supported;0x00 Not supported</p>
     *
     * @param statusCheckSupport
     */
    public void setStatusCheckSupport(byte[] statusCheckSupport) {
        this.statusCheckSupport = statusCheckSupport;
    }

    /**
     * get Zero Amount Allowed flag(DF3A)<p>
     * 1 :allowed，0:not allowed<p>
     *
     * @return
     */
    public byte[] getZeroAmountAllow() {
        return zeroAmountAllow;
    }

    /**
     * set Zero Amount Allowed flag(DF3A)<p>
     *
     * @param zeroAmountAllow 1 :allowed，0:not allowed<p>
     */
    public void setZeroAmountAllow(byte[] zeroAmountAllow) {
        this.zeroAmountAllow = zeroAmountAllow;
    }


    /**
     * (PAYWAVE)get Dynamic Reader Limits Status(DF3D)
     *
     * @return
     */
    public byte[] getDrlStatus() {
        return drlStatus;
    }

    /**
     * (PAYWAVE)set Dynamic Reader Limits Status(DF3D)
     *
     * @param drlStatus
     */
    public void setDrlStatus(byte[] drlStatus) {
        this.drlStatus = drlStatus;
    }

    /**
     * PAYWAVE(DF3F) get Dynamic Reader Limits Data
     *
     * @return
     */
    public byte[] getDrlData() {
        return drlData;
    }

    /**
     * PAYWAVE(DF3F) set Dynamic Reader Limits Data(DF3F)
     *
     * @param drlData
     */
    public void setDrlData(byte[] drlData) {
        this.drlData = drlData;
    }

    /**
     * EXPRESSPAY(DF53) get Dynamic Reader Limits Data
     * @return
     */
    public byte[] getDrlDataExp() {
        return drlDataExp;
    }

    /**
     * EXPRESSPAY(DF53) set Dynamic Reader Limits Data(DF53)
     * @param drlDataExp
     */
    public void setDrlDataExp(byte[] drlDataExp) {
        this.drlDataExp = drlDataExp;
    }

    /**
     * get Magstripe CVM Capability-CVM Required(DF42)
     *
     * @return
     */
    public byte[] getMagStripeCvm() {
        return magStripeCvm;
    }

    /**
     * set Magstripe CVM Capability-CVM Required(DF42)
     *
     * @param magStripeCvm
     */
    public void setMagStripeCvm(byte[] magStripeCvm) {
        this.magStripeCvm = magStripeCvm;
    }

    /**
     * <p>Max Lifetime of Torn Transaction Log Record(DF43)'DF811C'.</p>
     * <p>Maximum time, in seconds, that a record can remain in the Torn Transaction Log.</p>
     *
     * @return
     */
    public byte[] getMaxLifetimeTornLog() {
        return maxLifetimeTornLog;
    }

    /**
     * <p>Max Lifetime of Torn Transaction Log Record(DF43)'DF811C'.</p>
     * <p>Maximum time, in seconds, that a record can remain in the Torn Transaction Log.</p>
     *
     * @param maxLifetimeTornLog
     */
    public void setMaxLifetimeTornLog(byte[] maxLifetimeTornLog) {
        this.maxLifetimeTornLog = maxLifetimeTornLog;
    }

    /**
     * get Magstripe CVM Capability- NO CVM Required(DF47)
     *
     * @return
     */
    public byte[] getMagStripeNoCvm() {
        return magStripeNoCvm;
    }

    /**
     * set Magstripe CVM Capability- NO CVM Required(DF47)
     *
     * @param magStripeNoCvm
     */
    public void setMagStripeNoCvm(byte[] magStripeNoCvm) {
        this.magStripeNoCvm = magStripeNoCvm;
    }

    /**
     * (EXPRESSPAY)Unpredictable Number Range(DF4A)
     *
     * @return
     */
    public byte[] getExRandomScope() {
        return exRandomScope;
    }

    /**
     * (EXPRESSPAY)Unpredictable Number Range(DF4A)
     *
     * @param exRandomScope
     */
    public void setExRandomScope(byte[] exRandomScope) {
        this.exRandomScope = exRandomScope;
    }

    /**
     * (JCB)get Combination Options(DF60)
     *
     * @return
     */
    public byte[] getCombinationOP() {
        return combinationOP;
    }

    /**
     * (JCB)set Combination Options(DF60)
     *
     * @param combinationOP
     */
    public void setCombinationOP(byte[] combinationOP) {
        this.combinationOP = combinationOP;
    }

    /**
     * (JCB)get Terminal Interchange Profile((9F53)
     * Defines the reader CVM requirement and capabilities, as well as other reader capabilities (online capability, contact EMV capability) for the Transaction.
     *
     * @return
     */
    public byte[] getTerminalInterchangeProfile() {
        return terminalInterchangeProfile;
    }

    /**
     * (JCB)set Terminal Interchange Profile((9F53)
     * Defines the reader CVM requirement and capabilities, as well as other reader capabilities (online capability, contact EMV capability) for the Transaction.
     *
     * @param terminalInterchangeProfile
     */
    public void setTerminalInterchangeProfile(byte[] terminalInterchangeProfile) {
        this.terminalInterchangeProfile = terminalInterchangeProfile;
    }

//    /**
//     * MaxNumTornLog(DF32)
//       * Max Number of Torn Transaction Log Records
//     */
//    @EMVTagDefined(tag = EmvSelfDefinedReference.MNUMTORN)
//    private byte[] maxNumTornLog;
//
//    /**
//     * BalanceReadFlag(DF33)
//     */
//    @EMVTagDefined(tag = EmvSelfDefinedReference.BALANFLAG)
//    private byte[] balanceReadFlag;
//    /**
//     * CvmReq(DF35)
//     */
//    @EMVTagDefined(tag = EmvSelfDefinedReference.CVMREQ)
//    private byte[] cvmReq;
//    /**
//     * ddaVer(DF36)
//     */
//    @EMVTagDefined(tag = EmvSelfDefinedReference.DDAVER)
//    private byte[] ddaVer;
//    /**
//     * VisaTtqPresent(DF38)
//     */
//    @EMVTagDefined(tag = EmvSelfDefinedReference.VISATTQ)
//    private byte[] visaTtqPresent;
//
//    /**
//     * clssCardholderVerifyAllow(DF3C)
//     */
//    @EMVTagDefined(tag = EmvSelfDefinedReference.CLSSCVA)
//    private byte[] clssCardholderVerifyAllow;
//    /**
//     * EXTimeExpire(DF4B)
//     */
//    @EMVTagDefined(tag = EmvSelfDefinedReference.EXTIMEEX)
//    private byte[] exTimeExpire;

    /**
     * Extended Selection Support flag(DF3B)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.EXAIDSUPP)
    private byte[] extendAidSupport;


    /**
     * get Risk Management Data
     * (Application-specific value used by the card for risk management purposes)
     *
     * @return
     */
    public byte[] getRiskManagementData() {
        return riskManagementData;
    }

    /**
     * set Risk Management Data
     * (Application-specific value used by the card for risk management purposes)
     *
     * @param riskManagementData
     */
    public void setRiskManagementData(byte[] riskManagementData) {
        this.riskManagementData = riskManagementData;
    }

    public Integer getTransactionType() {
        return transactionType;
    }

    /**
     * <p>Transaction type.</p>
     * <p>the value of 9C (00/01/09/20)</p>
     * <p>when 1F8101==0x01, only this item is equal to the value of current transaction type (0x9C) then we select this aid.</p>
     *
     * @param transactionType
     */
    public void setTransactionType(Integer transactionType) {
        this.transactionType = transactionType;
    }

    public Integer getTransactionTypeCheckFlag() {
        return transactionTypeCheckFlag;
    }

    /**
     * Indicate whether matching trans type when picking up this aid.
     * 0x01-support
     *
     * @param transactionTypeCheckFlag
     */
    public void setTransactionTypeCheckFlag(Integer transactionTypeCheckFlag) {
        this.transactionTypeCheckFlag = transactionTypeCheckFlag;
    }

    /**
     * get DF25
     *
     * @return
     */
    public Integer getEmvSelectKernel() {
        return emvSelectKernel;
    }

    /**
     * set DF25
     *
     * @param emvSelectKernel
     */
    public void setEmvSelectKernel(Integer emvSelectKernel) {
        this.emvSelectKernel = emvSelectKernel;
    }
}
