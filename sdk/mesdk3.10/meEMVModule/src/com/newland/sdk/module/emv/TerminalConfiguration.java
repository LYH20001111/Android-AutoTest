package com.newland.sdk.module.emv;

import com.newland.sdk.mtype.common.Const.EmvSelfDefinedReference;
import com.newland.sdk.mtype.common.Const.EmvStandardReference;
import com.newland.sdk.me.module.emv.structure.AbstractEMVPackage;
import com.newland.sdk.me.module.emv.structure.EMVTagDefined;

/**
 * Terminal parameter configuration object
 *
 * @since ver3.10.01
 */
public class TerminalConfiguration extends AbstractEMVPackage {

    /**
     * Terminal ICS configuration(0xDF24)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.ICS)
    private byte[] ICS;

    /**
     * Transaction Currency Code (0x5f2a)
     *
     * @see EmvStandardReference#TRANSACTION_CURRENCY_CODE
     */
    @EMVTagDefined(tag = EmvStandardReference.TRANSACTION_CURRENCY_CODE)
    private String transactionCurrencyCode;

//    /**
//     * Transaction Reference Currency Conversion
//     * <item tag="DF22" value="00000000" /> L3目前有定义，但目前内核没有使用到
//     */
//    private byte[] transactionRefCurCNV;
    /**
     * Transaction reference currency code (0x9f3c)
     *
     * @see EmvStandardReference#TRANSACTION_REFERENCE_CURRENCY_CODE
     */
    @EMVTagDefined(tag = EmvStandardReference.TRANSACTION_REFERENCE_CURRENCY_CODE)
    private String transationReferenceCurrencyCode;

    /**
     * EC Terminal Support Indicator(0x9F7A)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.EC_SUPPORT_INDICATOR)
    private String ecSupportIndicator;
    /**
     * Terminal type(0x9f35)
     *
     * @see EmvStandardReference#TERMINAL_TYPE
     */
    @EMVTagDefined(tag = EmvStandardReference.TERMINAL_TYPE)
    private Integer terminalType;

    /**
     * Application selection indicator（DF01）
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.APP_SELECT_INDICATOR)
    private Integer appSelectIndicator;


    /**
     * Terminal capabilties(0x9f33)
     *
     * @see EmvStandardReference#TERMINAL_CAPABILITIES
     */
    @EMVTagDefined(tag = EmvStandardReference.TERMINAL_CAPABILITIES)
    private byte[] terminalCapabilities;

    /**
     * Additional terminal capabilites(0x9f40)
     *
     * @see EmvStandardReference#ADDITIONAL_TERMINAL_CAPABILITIES
     */
    @EMVTagDefined(tag = EmvStandardReference.ADDITIONAL_TERMINAL_CAPABILITIES)
    private byte[] additionalTerminalCapabilities;
    /**
     * Acquirer Identifier(0x9f01)
     *
     * @see EmvStandardReference#ACQUIRER_IDENTIFIER
     */
    @EMVTagDefined(tag = EmvStandardReference.ACQUIRER_IDENTIFIER)
    private String acquirerIdentifier;

    /**
     * Merchant category code(0x9f15)
     *
     * @see EmvStandardReference#MERCHANT_CATEGORY_CODE
     */
    @EMVTagDefined(tag = EmvStandardReference.MERCHANT_CATEGORY_CODE)
    private String merchantCategoryCode;
    /**
     * Merchant number(0x9f16)
     *
     * @see EmvStandardReference#MERCHANT_IDENTIFIER
     */
    @EMVTagDefined(tag = EmvStandardReference.MERCHANT_IDENTIFIER)
    private String merchantIdentifier;


    /**
     * Transaction currency exponent(0x5f36)
     *
     * @see EmvStandardReference#TRANSACTION_CURRENCY_EXP
     */
    @EMVTagDefined(tag = EmvStandardReference.TRANSACTION_CURRENCY_EXP)
    private String transactionCurrencyExp;


    /**
     * Transaction reference currency exponent (0x9f3d)
     *
     * @see EmvStandardReference#TRANSACTION_REFERENCE_CURRENCY_EXP
     */
    @EMVTagDefined(tag = EmvStandardReference.TRANSACTION_REFERENCE_CURRENCY_EXP)
    private String transationReferenceCurrencyExp;

    /**
     * Terminal country code (0x9f1a)
     *
     * @see EmvStandardReference#TERMINAL_COUNTRY_CODE
     */
    @EMVTagDefined(tag = EmvStandardReference.TERMINAL_COUNTRY_CODE)
    private byte[] terminalCountryCode;

    /**
     * IFD serial number (0x9f1e)
     *
     * @see EmvStandardReference#INTERFACE_DEVICE_SERIAL_NUMBER
     */
    @EMVTagDefined(tag = EmvStandardReference.INTERFACE_DEVICE_SERIAL_NUMBER)
    private String IFDSerialNumber;

    /**
     * Terminal identification number (0x9f1c)
     *
     * @see EmvStandardReference#TERMINAL_IDENTIFICATION
     */
    @EMVTagDefined(tag = EmvStandardReference.TERMINAL_IDENTIFICATION)
    private String terminalIdentification;
    /**
     * Maximum Target Percentage to be used for Biased Random Selection(0xDF16)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.MAX_TARGET_PERCENTAGE_FOR_BIASED_RANDOM_SELECTION)
    private Integer maxTargetPercentage;
    /**
     * Target percentage of random selection(0xDF17)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.TARGET_PERCENTAGE_FOR_RANDOM_SELECTION)
    private Integer targetPercentage;
    /**
     * Threshold of random selection(0xDF15)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.THRESHOLD_VALUE_FOR_BIASED_RANDOM_SELECTION)
    private byte[] thresholdValue;
    /**
     * Terminal Floor Limit.Indicates the floor limit in the terminal in
     * conjunction with the AID
     * (0x9F1B)
     *
     * @see EmvStandardReference#TERMINAL_FLOOR_LIMIT
     */
    @EMVTagDefined(tag = EmvStandardReference.TERMINAL_FLOOR_LIMIT)
    private byte[] terminalFloorLimit;
    /**
     * Default DDOL(0xDF44)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.DEFAULT_DDOL)
    private byte[] defaultDDOL;
    /**
     * Default transaction certificate data object list (TDOL)(0xDF45)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.DEFAULT_TDOL)
    private byte[] defaultTDOL;
    /**
     * Application version number(0x9F09)
     */
    @EMVTagDefined(tag = EmvStandardReference.APP_VERSION_NUMBER_TERMINAL)
    private byte[] appVersionNumber;
    //-----------------------defalut---terminal-config--contact----end------

    /**
     * Indicate whether using the Limit Amount (DF27)
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
     * Reader CVM Required Limit - 'DF8126’(DF21)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.NCICC_CVM_LIMIT)
    private byte[] cvmLimit;
    /**
     * passpass 需要两个限额，9F7B作为非接限额，只在PAYPASS中出现。在国内卡仍然是电子现金限额。
     * Electronic cash transaction limits(9F7B)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.EC_TRANS_LIMIT)
    private byte[] ecTransactionLimit;
    /**
     * Zero Amount Allowed flag(DF3A)<p>
     * 1 :allowed，0:not allowed<p>
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.ZEROALLOW)
    private byte[] zeroAmountAllow;
    /**
     * statusCheckSupport(DF39)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.STATUSCHECK)
    private byte[] statusCheckSupport;
    //-----------------------default---terminal-config---contactless---end------
    /**
     * fallback posentry(0xDF40)
     */
    @EMVTagDefined(tag = EmvSelfDefinedReference.FALLBACK_POSENTRY)
    private byte[] fallbackPosentry;

    /**
     * Merchant name (0x9f4e)
     */
    @EMVTagDefined(tag = EmvStandardReference.MERCHANT_NAME_AND_LOCATION)
    private String merchantName;
    /**
     * Terminal transaction attribute(9f66)
     */
    @EMVTagDefined(tag = EmvStandardReference.TERMINAL_TRANSACTION_QUALIFIERS)
    private byte[] terminalTransProp;
    /**
     * Point-of-Service (POS) Entry Mode(0x9f39)
     *
     * @see EmvStandardReference#POINT_OF_SERVICE_ENTRY_MODE
     */
    @EMVTagDefined(tag = EmvStandardReference.POINT_OF_SERVICE_ENTRY_MODE)
    private Integer POSEntryMode;

    /**
     * get the terminal ICS configuration(0xDF24)
     *
     * @return byte[]
     */
    public byte[] getICS() {
        return ICS;
    }

    /**
     * Set the terminal ICS configuration(0xDF24)
     *
     * @param ICS
     */

    public void setICS(byte[] ICS) {
        this.ICS = ICS;
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
     * Get the terminal capabilities (0x9f33)
     *
     * @return byte[]
     */
    public byte[] getTerminalCapabilities() {
        return terminalCapabilities;
    }

    /**
     * set the terminal capabilities (0x9f33)
     * Indicates the card data input, CVM, and security capabilities of the terminal.
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
     * Get the Point-of-Service (POS) Entry Mode (0x9f39)
     * Indicates the method by which the PAN was entered, according to the first two digits of the ISO 8583:1987 POS Entry Mode.
     *
     * @return Integer
     */
    public Integer getPOSEntryMode() {
        return POSEntryMode;
    }

    /**
     * Set the Point-of-Service (POS) Entry Mode (0x9f39)
     * Indicates the method by which the PAN was entered, according to the first two digits of the ISO 8583:1987 POS Entry Mode.
     *
     * @param POSEntryMode
     */
    public void setPOSEntryMode(Integer POSEntryMode) {
        this.POSEntryMode = POSEntryMode;
    }

    /**
     * Get the acquirer Identifier (0x9f01)
     *
     * @return String
     */
    public String getAcquirerIdentifier() {
        return acquirerIdentifier;
    }

    /**
     * Set the acquirer Identifier (0x9f01)
     * Uniquely identifies the acquirer within each payment system.
     *
     * @param acquirerIdentifier
     */
    public void setAcquirerIdentifier(String acquirerIdentifier) {
        this.acquirerIdentifier = acquirerIdentifier;
    }

    /**
     * Get the merchant category code (0x9f15)
     *
     * @return String
     */
    public String getMerchantCategoryCode() {
        return merchantCategoryCode;
    }

    /**
     * Set the merchant category code (0x9f15)
     * Classifies the type of business being done by the merchant, represented according to ISO 8583:1993 for Card Acceptor Business Code.
     *
     * @param merchantCategoryCode
     */
    public void setMerchantCategoryCode(String merchantCategoryCode) {
        this.merchantCategoryCode = merchantCategoryCode;
    }

    /**
     * Get the merchant identifier (0x9f16)
     *
     * @return String
     */
    public String getMerchantIdentifier() {
        return merchantIdentifier;
    }

    /**
     * Set the merchant identifier (0x9f16)
     * When concatenated with the Acquirer Identifier, uniquely identifies a given merchant.
     *
     * @param merchantIdentifier
     */
    public void setMerchantIdentifier(String merchantIdentifier) {
        this.merchantIdentifier = merchantIdentifier;
    }

    /**
     * Get the transaction currency code (0x5f2a)
     *
     * @return String
     */
    public String getTransactionCurrencyCode() {
        return transactionCurrencyCode;
    }

    /**
     * Set the transaction currency code (0x5f2a)
     *
     * @param transactionCurrencyCode
     */
    public void setTransactionCurrencyCode(String transactionCurrencyCode) {
        this.transactionCurrencyCode = transactionCurrencyCode;
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
     * Get the transaction reference currency code (0x9f3c)
     *
     * @return String
     */
    public String getTransationReferenceCurrencyCode() {
        return transationReferenceCurrencyCode;
    }

    /**
     * Set the transaction reference currency code (0x9f3c)
     * Code defining the common currency used by the terminal in case the Transaction Currency Code is different from the Application Currency Code.
     *
     * @param transationReferenceCurrencyCode
     */
    public void setTransationReferenceCurrencyCode(String transationReferenceCurrencyCode) {
        this.transationReferenceCurrencyCode = transationReferenceCurrencyCode;
    }

    /**
     * Get the transaction reference currency exponent (0x9f3d)
     *
     * @return String
     */
    public String getTransationReferenceCurrencyExp() {
        return transationReferenceCurrencyExp;
    }

    /**
     * Set the transaction reference currency exponent. (0x9f3d)
     * Indicates the implied position of the decimal point from the right of the transaction amount, with the Transaction Reference Currency Code represented according to ISO 4217.
     *
     * @param transationReferenceCurrencyExp
     */
    public void setTransationReferenceCurrencyExp(String transationReferenceCurrencyExp) {
        this.transationReferenceCurrencyExp = transationReferenceCurrencyExp;
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
     * Get theIFD serial number (0x9f1e)
     *
     * @return String
     */
    public String getIFDSerialNumber() {
        return IFDSerialNumber;
    }

    /**
     * Set theIFD serial number (0x9f1e)
     * Unique and permanent serial number assigned to the IFD by the manufacturer.
     *
     * @param IFDSerialNumber
     */
    public void setIFDSerialNumber(String IFDSerialNumber) {
        this.IFDSerialNumber = IFDSerialNumber;
    }

    /**
     * Get the terminal identification (0x9f1c)
     *
     * @return String
     */
    public String getTerminalIdentification() {
        return terminalIdentification;
    }

    /**
     * Set the terminal identification (0x9f1c)
     * Designates the unique location of a terminal at a merchant.
     *
     * @param terminalIdentification
     */
    public void setTerminalIdentification(String terminalIdentification) {
        this.terminalIdentification = terminalIdentification;
    }

    /**
     * Get the default transaction certificate data object list(TDOL)(0xDF45)
     *
     * @return byte[]
     */
    public byte[] getDefaultTDOL() {
        return defaultTDOL;
    }

    /**
     * Set the default transaction certificate data object list(TDOL)(0xDF45)
     *
     * @param defaultTDOL
     */
    public void setDefaultTDOL(byte[] defaultTDOL) {
        this.defaultTDOL = defaultTDOL;
    }

    /**
     * Get thefallback posentry(0xDF40)
     *
     * @return byte[]
     */
    public byte[] getFallbackPosentry() {
        return fallbackPosentry;
    }

    /**
     * Set the fallback posentry(0xDF40)
     *
     * @param fallbackPosentry
     */
    public void setFallbackPosentry(byte[] fallbackPosentry) {
        this.fallbackPosentry = fallbackPosentry;
    }

    /**
     * Get the merchant name(0x9f4e)
     *
     * @return
     */
    public String getMerchantName() {
        return merchantName;
    }

    /**
     * Set the merchant name(0x9f4e)
     *
     * @param merchantName
     */
    public void setMerchantName(String merchantName) {
        this.merchantName = merchantName;
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
     * Get the  EC Terminal Support Indicator(0x9F7A)
     *
     * @return
     */
    public String getEcSupportIndicator() {
        return ecSupportIndicator;
    }

    /**
     * Set the  EC Terminal Support Indicator(0x9F7A)
     * 0x01:support;0x00:unsupport
     *
     * @param ecSupportIndicator
     */
    public void setEcSupportIndicator(String ecSupportIndicator) {
        this.ecSupportIndicator = ecSupportIndicator;
    }

    /**
     * Get Application selection indicator(0xDF01)
     *
     * @return
     */
    public Integer getAppSelectIndicator() {
        return appSelectIndicator;
    }

    /**
     * Set Application selection indicator(0xDF01)
     *
     * @param appSelectIndicator
     */
    public void setAppSelectIndicator(Integer appSelectIndicator) {
        this.appSelectIndicator = appSelectIndicator;
    }


    //---------------------------

    /**
     * Maximum Target Percentage to be used for Biased Random Selection(0xDF16).
     *
     * @return
     */
    public Integer getMaxTargetPercentage() {
        return maxTargetPercentage;
    }

    /**
     * Maximum Target Percentage to be used for Biased Random Selection(0xDF16).
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
     * Get terminal minimum limits(0x9f1b)
     *
     * @return byte[]
     */
    public byte[] getTerminalFloorLimit() {
        return terminalFloorLimit;
    }

    /**
     * Set terminal minimum limits(0x9f1b)
     *
     * @param terminalFloorLimit
     */
    public void setTerminalFloorLimit(byte[] terminalFloorLimit) {
        this.terminalFloorLimit = terminalFloorLimit;
    }

    /**
     * Get the default DDOL(0xDF44)
     *
     * @return byte[]
     */
    public byte[] getDefaultDDOL() {
        return defaultDDOL;
    }

    /**
     * Set the default DDOL(0xDF44)
     *
     * @param defaultDDOL
     */
    public void setDefaultDDOL(byte[] defaultDDOL) {
        this.defaultDDOL = defaultDDOL;
    }

    /**
     * Application Version Number(0x9F09).
     * Version number assigned by the payment system for the application.
     *
     * @return
     */
    public byte[] getAppVersionNumber() {
        return appVersionNumber;
    }

    /**
     * Application Version Number(0x9F09).
     * Version number assigned by the payment system for the application.
     *
     * @param appVersionNumber
     */
    public void setAppVersionNumber(byte[] appVersionNumber) {
        this.appVersionNumber = appVersionNumber;
    }

    /**
     * Get the indicator of the Contactless Transaction amount limit(DF27).
     * Indicate whether using the Limit Amount.
     *
     * @return
     */
    public byte[] getAmountLimitIndicator() {
        return amountLimitIndicator;
    }

    /**
     * Set the indicator of the Contactless Transaction amount limit(DF27).
     * Indicate whether using the Limit Amount.
     *
     * @param amountLimitIndicator
     */
    public void setAmountLimitIndicator(byte[] amountLimitIndicator) {
        this.amountLimitIndicator = amountLimitIndicator;
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
     * @return
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
     * <p>Status check supported(DF39)</p>
     * <p>0x01 supported;0x00 Not supported</p>
     *
     * @return
     */
    public byte[] getStatusCheckSupport() {
        return statusCheckSupport;
    }

    /**
     * <p>Status check supported(DF39)</p>
     * <p>0x01 supported;0x00 Not supported</p>
     *
     * @param statusCheckSupport
     */
    public void setStatusCheckSupport(byte[] statusCheckSupport) {
        this.statusCheckSupport = statusCheckSupport;
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

}
