package com.newland.sdk.mtype.common;

/**
 * Universal constant table
 */
public class Const {
    /**
     * If instruction concentration involves data cache, the possible maximum length of such data cache
     * Maximum buffer area 3k.
     */
    public static final int CMD_MAXBUFFER_LEN = 3072;
    /**
     * Default character encoding format
     */
    public static final String DEFAULT_CHARSET = "GBK";

    public static final String N900 = "N900";
    public static final String IM81 = "IM81";
    public static final String N910 = "N910";
    public static final String TUSNFLAG_PATH = "/newland/factory/flag_sn_20";

    /**
     * According to emv 4.2
     *
     * @since v1.0
     */
    public static class EmvStandardReference {

        /**
         * Issuer Identification Number (IIN).The number that identifies the
         * major industry and the card issuer and that forms the first part of
         * the Primary Account Number (PAN)
         */
        public static final int ISSUER_IDENTIFICATION_NUMBER = 0x42;
        /**
         * Application Identifier (AID) - card.Identifies the application as
         * described in ISO/IEC 7816-5
         */
        public static final int AID_CARD = (int) 0x4f;
        /**
         * Application Label.Mnemonic associated with the AID according to
         * ISO/IEC 7816-5
         */
        public static final int APPLICATION_LABEL = 0x50;
        /**
         * ISO-7816 Path.
         */
        public static final int PATH = 0x51;
        /**
         * Track 2 Equivalent Data.Contains the data elements of track 2
         * according to ISO/IEC 7813, excluding start sentinel, end sentinel,
         * and Longitudinal Redundancy Check (LRC)
         */
        public static final int TRACK_2_EQV_DATA = 0x57;
        /**
         * Application Primary Account Number (PAN).Valid cardholder account
         * number
         */
        public static final int PAN = (int) 0x5a;
        /**
         * Application Template.Contains one or more data objects relevant to an
         * application directory entry according to ISO/IEC 7816-5
         */
        public static final int APPLICATION_TEMPLATE = 0x61;
        /**
         * File Control Information (FCI) Template.Identifies the FCI template
         * according to ISO/IEC 7816-4
         */
        public static final int FCI_TEMPLATE = (int) 0x6f;
        /**
         * Record Template (EMV Proprietary).Template proprietary to the EMV
         * specification
         */
        public static final int RECORD_TEMPLATE = 0x70;
        /**
         * Issuer Script Template 1.Contains proprietary issuer data for
         * transmission to the ICC before the second GENERATE AC command
         */
        public static final int ISSUER_SCRIPT_TEMPLATE_1 = 0x71;
        /**
         * Issuer Script Template 2.Contains proprietary issuer data for
         * transmission to the ICC after the second GENERATE AC command
         */
        public static final int ISSUER_SCRIPT_TEMPLATE_2 = 0x72;
        /**
         * Directory Discretionary Template.Issuer discretionary part of the
         * directory according to ISO/IEC 7816-5
         */
        public static final int DD_TEMPLATE = 0x73;
        /**
         * Response Message Template Format 2.Contains the data objects (with
         * tags and lengths) returned by the ICC in response to a command
         */
        public static final int RESPONSE_MESSAGE_TEMPLATE_2 = 0x77;
        /**
         * Response Message Template Format 1.Contains the data objects (without
         * tags and lengths) returned by the ICC in response to a command
         */
        public static final int RESPONSE_MESSAGE_TEMPLATE_1 = 0x80;
        /**
         * Amount, Authorised (Binary).Authorised amount of the transaction
         * (excluding adjustments)
         */
        public static final int AMOUNT_AUTHORISED_BINARY = 0x81;
        /**
         * Application Interchange Profile.Indicates the capabilities of the
         * card to support specific functions in the application
         */
        public static final int APPLICATION_INTERCHANGE_PROFILE = 0x82;
        /**
         * Command Template.Identifies the data field of a command message
         */
        public static final int COMMAND_TEMPLATE = 0x83;
        /**
         * Dedicated File (DF) Name.Identifies the name of the DF as described
         * in ISO/IEC 7816-4
         */
        public static final int DEDICATED_FILE_NAME = 0x84;
        /**
         * Issuer Script Command.Contains a command for transmission to the ICC
         */
        public static final int ISSUER_SCRIPT_COMMAND = 0x86;
        /**
         * Application Priority Indicator.Indicates the priority of a given
         * application or group of applications in a directory
         */
        public static final int APPLICATION_PRIORITY_INDICATOR = 0x87;
        /**
         * Short File Identifier (SFI).Identifies the SFI to be used in the
         * commands related to a given AEF or DDF. The SFI data object is a
         * binary field with the three high order bits set to zero
         */
        public static final int SFI = 0x88;
        /**
         * Authorisation Code.Value generated by the authorisation authority for
         * an approved transaction
         */
        public static final int AUTHORISATION_CODE = 0x89;
        /**
         * Authorisation Response Code.Code that defines the disposition of a
         * message
         */
        public static final int AUTHORISATION_RESPONSE_CODE = (int) 0x8a;
        /**
         * Card Risk Management Data Object List 1 (CDOL1).List of data objects
         * (tag and length) to be passed to the ICC in the first GENERATE AC
         * command
         */
        public static final int CDOL1 = (int) 0x8c;
        /**
         * Card Risk Management Data Object List 2 (CDOL2).List of data objects
         * (tag and length) to be passed to the ICC in the second GENERATE AC
         * command
         */
        public static final int CDOL2 = (int) 0x8d;
        /**
         * Cardholder Verification Method (CVM) List.Identifies a method of
         * verification of the cardholder supported by the application
         */
        public static final int CVM_LIST = (int) 0x8e;
        /**
         * Certification Authority Public Key Index - card.Identifies the
         * certification authority’s public key in conjunction with the RID
         */
        public static final int CA_PUBLIC_KEY_INDEX_CARD = (int) 0x8f;
        /**
         * Issuer Public Key Certificate.Issuer public key certified by a
         * certification authority
         */
        public static final int ISSUER_PUBLIC_KEY_CERT = 0x90;
        /**
         * Issuer Authentication Data.Data sent to the ICC for online issuer
         * authentication
         */
        public static final int ISSUER_AUTHENTICATION_DATA = 0x91;
        /**
         * Issuer Public Key Remainder.Remaining digits of the Issuer Public Key
         * Modulus
         */
        public static final int ISSUER_PUBLIC_KEY_REMAINDER = 0x92;
        /**
         * Signed Static Application Data.Digital signature on critical
         * application parameters for SDA
         */
        public static final int SIGNED_STATIC_APP_DATA = 0x93;
        /**
         * Application File Locator (AFL).Indicates the location (SFI, range of
         * records) of the AEFs related to a given application
         */
        public static final int APPLICATION_FILE_LOCATOR = 0x94;
        /**
         * Terminal Verification Results (TVR).Status of the different functions
         * as seen from the terminal
         */
        public static final int TERMINAL_VERIFICATION_RESULTS = 0x95;
        /**
         * Transaction Certificate Data Object List (TDOL).List of data objects
         * (tag and length) to be used by the terminal in generating the TC Hash
         * Value
         */
        public static final int TDOL = 0x97;
        /**
         * Transaction Certificate (TC) Hash Value.Result of a hash function
         * specified in Book 2, Annex B3.1
         */
        public static final int TC_HASH_VALUE = 0x98;
        /**
         * Transaction ExtPinpadKeyal Identification Number (PIN) Data.Data entered by
         * the cardholder for the purpose of the PIN verification
         */
        public static final int TRANSACTION_PIN_DATA = 0x99;
        /**
         * Transaction Date.Local date that the transaction was authorised
         */
        public static final int TRANSACTION_DATE = 0x9a;
        /**
         * Transaction Status Information.Indicates the functions performed in a
         * transaction
         */
        public static final int TRANSACTION_STATUS_INFORMATION = 0x9b;
        /**
         * Transaction Type.Indicates the type of financial transaction,
         * represented by the first two digits of ISO 8583:1987 Processing Code
         */
        public static final int TRANSACTION_TYPE = 0x9c;
        /**
         * Directory Definition File (DDF) Name.Identifies the name of a DF
         * associated with a directory
         */
        public static final int DDF_NAME = 0x9d;
        /**
         * File Control Information (FCI) Proprietary Template.Identifies the
         * data object proprietary to this specification in the FCI template
         * according to ISO/IEC 7816-4
         */
        public static final int FCI_PROPRIETARY_TEMPLATE = 0xa5;
        /**
         * Cardholder Name.Indicates cardholder name according to ISO 7813
         */
        public static final int CARDHOLDER_NAME = 0x5f20;
        /**
         * Application Expiration Date.Date after which application expires
         */
        public static final int APP_EXPIRATION_DATE = 0x5f24;
        /**
         * Application Effective Date.Date from which the application may be
         * used
         */
        public static final int APP_EFFECTIVE_DATE = 0x5f25;
        /**
         * Issuer Country Code.Indicates the country of the issuer according to
         * ISO 3166
         */
        public static final int ISSUER_COUNTRY_CODE = 0x5f28;
        /**
         * Transaction Currency Code.Indicates the currency code of the
         * transaction according to ISO 4217
         */
        public static final int TRANSACTION_CURRENCY_CODE = 0x5f2a;
        /**
         * Language Preference.1–4 languages stored in order of preference, each
         * represented by 2 alphabetical characters according to ISO 639
         */
        public static final int LANGUAGE_PREFERENCE = 0x5f2d;
        /**
         * Service Code.Service code as defined in ISO/IEC 7813 for track 1 and
         * track 2
         */
        public static final int SERVICE_CODE = 0x5f30;
        /**
         * Application Primary Account Number (PAN) Sequence Number.Identifies
         * and differentiates cards with the same PAN
         */
        // public static final int PAN_SEQUENCE_NUMBER = 0x5f34 ;
        /**
         * Transaction Currency Exponent.Indicates the implied position of the
         * decimal point from the right of the transaction amount represented
         * according to ISO 4217
         */
        public static final int TRANSACTION_CURRENCY_EXP = 0x5f36;
        /**
         * Issuer URL.The URL provides the location of the Issuer’s Library
         * Server on the Internet
         */
        public static final int ISSUER_URL = 0x5f50;
        /**
         * International Bank Account Number (IBAN).Uniquely identifies the
         * account of a customer at a financial institution as defined in ISO
         * 13616
         */
        public static final int IBAN = 0x5f53;
        /**
         * Bank Identifier Code (BIC).Uniquely identifies a bank as defined in
         * ISO 9362
         */
        public static final int BANK_IDENTIFIER_CODE = 0x5f54;
        /**
         * Issuer Country Code (alpha2 format).Indicates the country of the
         * issuer as defined in ISO 3166 (using a 2 character alphabetic code)
         */
        public static final int ISSUER_COUNTRY_CODE_ALPHA2 = 0x5f55;
        /**
         * Issuer Country Code (alpha3 format).Indicates the country of the
         * issuer as defined in ISO 3166 (using a 3 character alphabetic code)
         */
        public static final int ISSUER_COUNTRY_CODE_ALPHA3 = 0x5f56;
        /**
         * Acquirer Identifier.Uniquely identifies the acquirer within each
         * payment system
         */
        public static final int ACQUIRER_IDENTIFIER = 0x9f01;
        /**
         * Amount, Authorised (Numeric).Authorised amount of the transaction
         * (excluding adjustments)
         */
        public static final int AMOUNT_AUTHORISED_NUMERIC = 0x9f02;
        /**
         * Amount, Other (Numeric).Secondary amount associated with the
         * transaction representing a cashback amount
         */
        public static final int AMOUNT_OTHER_NUMERIC = 0x9f03;
        /**
         * Amount, Other (Binary).Secondary amount associated with the
         * transaction representing a cashback amount
         */
        public static final int AMOUNT_OTHER_BINARY = 0x9f04;
        /**
         * Application Discretionary Data.Issuer or payment system specified
         * data relating to the application
         */
        public static final int APP_DISCRETIONARY_DATA = 0x9f05;
        /**
         * Application Identifier (AID) - terminal.Identifies the application as
         * described in ISO/IEC 7816-5
         */
        public static final int AID_TERMINAL = 0x9f06;
        /**
         * Application Usage Control.Indicates issuer’s specified restrictions
         * on the geographic usage and services allowed for the application
         */
        public static final int APP_USAGE_CONTROL = 0x9f07;
        /**
         * Application Version Number - card.Version number assigned by the
         * payment system for the application
         */
        public static final int APP_VERSION_NUMBER_CARD = 0x9f08;
        /**
         * Application Version Number - terminal.Version number assigned by the
         * payment system for the application
         */
        public static final int APP_VERSION_NUMBER_TERMINAL = 0x9f09;
        /**
         * Cardholder Name Extended.Indicates the whole cardholder name when
         * greater than 26 characters using the same coding convention as in ISO
         * 7813
         */
        public static final int CARDHOLDER_NAME_EXTENDED = 0x9f0b;
        /**
         * Issuer Action Code - Default.Specifies the issuer’s conditions that
         * cause a transaction to be rejected if it might have been approved
         * online, but the terminal is unable to process the transaction online
         */
        public static final int ISSUER_ACTION_CODE_DEFAULT = 0x9f0d;
        /**
         * Issuer Action Code - Denial.Specifies the issuer’s conditions that
         * cause the denial of a transaction without attempt to go online
         */
        public static final int ISSUER_ACTION_CODE_DENIAL = 0x9f0e;
        /**
         * Issuer Action Code - Online.Specifies the issuer’s conditions that
         * cause a transaction to be transmitted online
         */
        public static final int ISSUER_ACTION_CODE_ONLINE = 0x9f0f;
        /**
         * Issuer Application Data.Contains proprietary application data for
         * transmission to the issuer in an online transaction
         */
        public static final int ISSUER_APPLICATION_DATA = 0x9f10;
        /**
         * Issuer Code Table Index.Indicates the code table according to ISO/IEC
         * 8859 for displaying the Application Preferred Name
         */
        public static final int ISSUER_CODE_TABLE_INDEX = 0x9f11;
        /**
         * Application Preferred Name.Preferred mnemonic associated with the AID
         */
        public static final int APP_PREFERRED_NAME = 0x9f12;
        /**
         * Last Online Application Transaction Counter (ATC) Register.ATC value
         * of the last transaction that went online
         */
        public static final int LAST_ONLINE_ATC_REGISTER = 0x9f13;
        /**
         * Lower Consecutive Offline Limit.Issuer-specified preference for the
         * maximum number of consecutive offline transactions for this ICC
         * application allowed in a terminal with online capability
         */
        public static final int LOWER_CONSEC_OFFLINE_LIMIT = 0x9f14;
        /**
         * Merchant Category Code.Classifies the type of business being done by
         * the merchant, represented according to ISO 8583:1993 for Card
         * Acceptor Business Code
         */
        public static final int MERCHANT_CATEGORY_CODE = 0x9f15;
        /**
         * Merchant Identifier.When concatenated with the Acquirer Identifier,
         * uniquely identifies a given merchant
         */
        public static final int MERCHANT_IDENTIFIER = 0x9f16;
        /**
         * ExtPinpadKeyal Identification Number (PIN) Try Counter.Number of PIN tries
         * remaining
         */
        public static final int PIN_TRY_COUNTER = 0x9f17;
        /**
         * Issuer Script Identifier.Identification of the Issuer Script
         */
        public static final int ISSUER_SCRIPT_IDENTIFIER = 0x9f18;
        /**
         * Terminal Country Code.Indicates the country of the terminal,
         * represented according to ISO 3166
         */
        public static final int TERMINAL_COUNTRY_CODE = 0x9f1a;
        /**
         * Terminal Floor Limit.Indicates the floor limit in the terminal in
         * conjunction with the AID
         */
        public static final int TERMINAL_FLOOR_LIMIT = 0x9f1b;
        /**
         * Terminal Identification.Designates the unique location of a terminal
         * at a merchant
         */
        public static final int TERMINAL_IDENTIFICATION = 0x9f1c;
        /**
         * Terminal Risk Management Data.Application-specific value used by the
         * card for risk management purposes
         */
        public static final int TERMINAL_RISK_MANAGEMENT_DATA = 0x9f1d;
        /**
         * Interface Device (IFD) Serial Number.Unique and permanent serial
         * number assigned to the IFD by the manufacturer
         */
        public static final int INTERFACE_DEVICE_SERIAL_NUMBER = 0x9f1e;

        /**
         * 卡序列号 Application Primary account Number(PAN) Sequence Number
         */
        public static final int CARD_SEQUENCE_NUMBER = 0x5F34;
        /**
         * [Magnetic Stripe] Track 1 Discretionary Data.Discretionary part of
         * track 1 according to ISO/IEC 7813
         */
        public static final int TRACK1_DISCRETIONARY_DATA = 0x9f1f;
        /**
         * [Magnetic Stripe] Track 2 Discretionary Data.Discretionary part of
         * track 2 according to ISO/IEC 7813
         */
        public static final int TRACK2_DISCRETIONARY_DATA = 0x9f20;
        /**
         * Transaction Time (HHMMSS).Local time that the transaction was
         * authorised
         */
        public static final int TRANSACTION_TIME = 0x9f21;
        /**
         * Certification Authority Public Key Index - Terminal.Identifies the
         * certification authority’s public key in conjunction with the RID
         */
        public static final int CA_PUBLIC_KEY_INDEX_TERMINAL = 0x9f22;
        /**
         * Upper Consecutive Offline Limit.Issuer-specified preference for the
         * maximum number of consecutive offline transactions for this ICC
         * application allowed in a terminal without online capability
         */
        public static final int UPPER_CONSEC_OFFLINE_LIMIT = 0x9f23;
        /**
         * Application Cryptogram.Cryptogram returned by the ICC in response of
         * the GENERATE AC command
         */
        public static final int APP_CRYPTOGRAM = 0x9f26;
        /**
         * Cryptogram Information Data.Indicates the type of cryptogram and the
         * actions to be performed by the terminal
         */
        public static final int CRYPTOGRAM_INFORMATION_DATA = 0x9f27;
        /**
         * ICC PIN Encipherment Public Key Certificate.ICC PIN Encipherment
         * Public Key certified by the issuer
         */
        public static final int ICC_PIN_ENCIPHERMENT_PUBLIC_KEY_CERT = 0x9f2d;
        /**
         * ICC PIN Encipherment Public Key Exponent.ICC PIN Encipherment Public
         * Key Exponent used for PIN encipherment
         */
        public static final int ICC_PIN_ENCIPHERMENT_PUBLIC_KEY_EXP = 0x9f2e;
        /**
         * ICC PIN Encipherment Public Key Remainder.Remaining digits of the ICC
         * PIN Encipherment Public Key Modulus
         */
        public static final int ICC_PIN_ENCIPHERMENT_PUBLIC_KEY_REM = 0x9f2f;
        /**
         * Issuer Public Key Exponent.Issuer public key exponent used for the
         * verification of the Signed Static Application Data and the ICC Public
         * Key Certificate
         */
        public static final int ISSUER_PUBLIC_KEY_EXP = 0x9f32;
        /**
         * Terminal Capabilities.Indicates the card data input, CVM, and
         * security capabilities of the terminal
         */
        public static final int TERMINAL_CAPABILITIES = 0x9f33;
        /**
         * Cardholder Verification (CVM) Results.Indicates the results of the
         * last CVM performed
         */
        public static final int CVM_RESULTS = 0x9f34;
        /**
         * Terminal Type.Indicates the environment of the terminal, its
         * communications capability, and its operational control
         */
        public static final int TERMINAL_TYPE = 0x9f35;
        /**
         * Application Transaction Counter (ATC).Counter maintained by the
         * application in the ICC (incrementing the ATC is managed by the ICC)
         */
        public static final int APP_TRANSACTION_COUNTER = 0x9f36;
        /**
         * Unpredictable Number.Value to provide variability and uniqueness to
         * the generation of a cryptogram
         */
        public static final int UNPREDICTABLE_NUMBER = 0x9f37;
        /**
         * Processing Options Data Object List (PDOL).Contains a list of
         * terminal resident data objects (tags and lengths) needed by the ICC
         * in processing the GET PROCESSING OPTIONS command
         */
        public static final int PDOL = 0x9f38;
        /**
         * Point-of-Service (POS) Entry Mode.Indicates the method by which the
         * PAN was entered, according to the first two digits of the ISO
         * 8583:1987 POS Entry Mode
         */
        public static final int POINT_OF_SERVICE_ENTRY_MODE = 0x9f39;
        /**
         * Amount, Reference Currency.Authorised amount expressed in the
         * reference currency
         */
        public static final int AMOUNT_REFERENCE_CURRENCY = 0x9f3a;
        /**
         * Application Reference Currency.1–4 currency codes used between the
         * terminal and the ICC when the Transaction Currency Code is different
         * from the Application Currency Code; each code is 3 digits according
         * to ISO 4217
         */
        public static final int APP_REFERENCE_CURRENCY = 0x9f3b;
        /**
         * Transaction Reference Currency Code.Code defining the common currency
         * used by the terminal in case the Transaction Currency Code is
         * different from the Application Currency Code
         */
        public static final int TRANSACTION_REFERENCE_CURRENCY_CODE = 0x9f3c;
        /**
         * Transaction Reference Currency Exponent.Indicates the implied
         * position of the decimal point from the right of the transaction
         * amount, with the Transaction Reference Currency Code represented
         * according to ISO 4217
         */
        public static final int TRANSACTION_REFERENCE_CURRENCY_EXP = 0x9f3d;
        /**
         * Additional Terminal Capabilities.Indicates the data input and output
         * capabilities of the terminal
         */
        public static final int ADDITIONAL_TERMINAL_CAPABILITIES = 0x9f40;
        /**
         * Transaction Sequence Counter.Counter maintained by the terminal that
         * is incremented by one for each transaction
         */
        public static final int TRANSACTION_SEQUENCE_COUNTER = 0x9f41;
        /**
         * Application Currency Code.Indicates the currency in which the account
         * is managed according to ISO 4217
         */
        public static final int APPLICATION_CURRENCY_CODE = 0x9f42;
        /**
         * Application Reference Currency Exponent.Indicates the implied
         * position of the decimal point from the right of the amount, for each
         * of the 1–4 reference currencies represented according to ISO 4217
         */
        public static final int APP_REFERENCE_CURRECY_EXPONENT = 0x9f43;
        /**
         * Application Currency Exponent.Indicates the implied position of the
         * decimal point from the right of the amount represented according to
         * ISO 4217
         */
        public static final int APP_CURRENCY_EXPONENT = 0x9f44;
        /**
         * Data Authentication Code.An issuer assigned value that is retained by
         * the terminal during the verification process of the Signed Static
         * Application Data
         */
        public static final int DATA_AUTHENTICATION_CODE = 0x9f45;
        /**
         * ICC Public Key Certificate.ICC Public Key certified by the issuer
         */
        public static final int ICC_PUBLIC_KEY_CERT = 0x9f46;
        /**
         * ICC Public Key Exponent.ICC Public Key Exponent used for the
         * verification of the Signed Dynamic Application Data
         */
        public static final int ICC_PUBLIC_KEY_EXP = 0x9f47;
        /**
         * ICC Public Key Remainder.Remaining digits of the ICC Public Key
         * Modulus
         */
        public static final int ICC_PUBLIC_KEY_REMAINDER = 0x9f48;
        /**
         * Dynamic Data Authentication Data Object List (DDOL).List of data
         * objects (tag and length) to be passed to the ICC in the INTERNAL
         * AUTHENTICATE command
         */
        public static final int DDOL = 0x9f49;
        /**
         * Static Data Authentication Tag List.List of tags of primitive data
         * objects defined in this specification whose value fields are to be
         * included in the Signed Static or Dynamic Application Data
         */
        public static final int SDA_TAG_LIST = 0x9f4a;
        /**
         * Signed Dynamic Application Data.Digital signature on critical
         * application parameters for DDA or CDA
         */
        public static final int SIGNED_DYNAMIC_APPLICATION_DATA = 0x9f4b;
        /**
         * ICC Dynamic Number.Time-variant number generated by the ICC, to be
         * captured by the terminal
         */
        public static final int ICC_DYNAMIC_NUMBER = 0x9f4c;
        /**
         * Log Entry.Provides the SFI of the Transaction Log file and its number
         * of records
         */
        public static final int LOG_ENTRY = 0x9f4d;
        /**
         * Merchant Name and Location.Indicates the name and location of the
         * merchant
         */
        public static final int MERCHANT_NAME_AND_LOCATION = 0x9f4e;
        /**
         * Log Format.List (in tag and length format) of data objects
         * representing the logged data elements that are passed to the terminal
         * when a transaction log record is read
         */
        public static final int LOG_FORMAT = 0x9f4f;

        /**
         * File Control Information (FCI) Issuer Discretionary Data.Issuer
         * discretionary part of the FCI (e.g. O/S Manufacturer proprietary
         * data)
         */
        public static final int FCI_ISSUER_DISCRETIONARY_DATA = 0xbf0c;

        /**
         * Card product identification information
         */
        public static final int CARD_PRODUCT_IDATIFICATION = 0x9f63;

        /**
         * Terminal Transaction Qualifiers.Provided by the reader in the GPO
         * command and used by the card to determine processing choices based on
         * reader functionality
         */
        public static final int TERMINAL_TRANSACTION_QUALIFIERS = 0x9f66;

        /**
         * Electronic cash issuer Authorization Code (by qpboc)
         */
        public static final int EC_ISSUER_AUTHORIZATION_CODE = 0x9F74;
        /**
         * Electronic cash balance upper limit
         */
        public static final int EC_BALANCE_LIMIT = 0x9F77;
        /**
         * Electronic cash single transaction limit (card limit), libemvjni.so doesn't support this tag
         */
        public static final int EC_SINGLE_TRANSACTION_LIMIT = 0x9F78;
        /**
         * Electronic cash balance
         */
        public static final int PBOC_CARD_FUNDS = 0x9F79;

        /**
         * QPBOC electronic cash balance
         */
        public static final int QPBOC_CARD_FUNDS = 0x9F5D;
        /**
         * Card holder certificate number
         */
        public static final int CARDHOLDER_CERT_NO = 0x9F61;
        /**
         * Card holder certificate type
         */
        public static final int CARDHOLDER_CERT_TYPE = 0x9F62;
        /**
         * Application currency cord
         */
        public static final int APP_CURRENCY_CODE = 0x9F51;
        /**
         * Card Transaction Qualifiers CTQ(0x9F6c)
         */
        public static final int CDCVM_DATA = 0x9F6C;

        /**
         * TIP just for JCB
         */
        public static final int TIP = 0x9F53;
    }

    /**
     * EMV application self-defined tag extension
     *
     * @since v1.0
     */
    public static final class EmvSelfDefinedReference {

        /**** CA param setting ****/
        /**
         * CA param setting<p>
         * CA public key expiration date<p>
         */
        public static final int CA_PK_EXPIRATION_DATE = 0xDF05;

        /**
         * CA param setting<p>
         * Public key signature hash algorithem
         */
        public static final int CA_PK_HASH_ALGORITHM_INDICATOR = 0xDF06;

        /**
         * CA param setting<p>
         * Public key signature algorithm
         */
        public static final int CA_PK_ALGORITHM_INDICATOR = 0xDF07;

        /**
         * CA param setting<p>
         * Public key n modulus
         */
        public static final int CAPK_MODULUS = 0xDF02;

        /**
         * CA param setting<p>
         * Public key exponent
         */
        public static final int CAPK_EXPONENT = 0xDF04;

        /**
         * CA param setting<p>
         * Public key fingerprint
         */
        public static final int CAPK_SHA1CHECKSUM = 0xDF03;

        /**** AID setting ****/
        /**
         * AID setting<p>
         * Application selection indicator (ASI)
         */
        public static final int APP_SELECT_INDICATOR = 0xDF01;

        /**
         * AID setting<p>
         * TAC default
         */
        public static final int TAC_DEFAULT = 0xDF11;
        /**
         * AID setting<p>
         * TAC online
         */
        public static final int TAC_ONLINE = 0xDF12;
        /**
         * AID setting<p>
         * TAC denial
         */
        public static final int TAC_DENIAL = 0xDF13;
        /**
         * AID setting<p>
         * Threshold value for biased random selection
         */
        public static final int THRESHOLD_VALUE_FOR_BIASED_RANDOM_SELECTION = 0xDF15;
        /**
         * AID setting<p>
         * Maximum target percentage for biased random selection
         */
        public static final int MAX_TARGET_PERCENTAGE_FOR_BIASED_RANDOM_SELECTION = 0xDF16;
        /**
         * AID setting<p>
         * Target percentage for random selection
         */
        public static final int TARGET_PERCENTAGE_FOR_RANDOM_SELECTION = 0xDF17;
        /**
         * AID setting<p>
         * Default Dynamic Data Authentication Data Object List(DDOL)
         */
        public static final int DEFAULT_DDOL = 0xDF44;
        /**
         * AID setting<p>
         * Electronic cash transaction limit (the electronic cash limit of terminal  )
         */
        public static final int EC_TRANS_LIMIT = 0x9F7B;

        /**
         * AID setting<p>
         * Contactless card offline floor limit (contactless card)
         */
        public static final int NCICC_OFFLINE_FLOOR_LIMIT = 0xDF19;

        /**
         * AID setting<p>
         * Contactless card transaction limit (contactless card)
         */
        public static final int NCICC_TRANS_LIMIT = 0xDF20;

        /**
         * AID setting<p>
         * Contactless transaction triggered CVM transaction limit (contactless card)
         */
        public static final int NCICC_CVM_LIMIT = 0xDF21;

//        /**
//         * AID setting<p>
//         * Contactless state inspection
//         */
//        public static final int RF_STATUS_CHECK = 0xDF29;

        /**** Pboc terminal properties setting ****/

        /**
         * <p>Pboc terminal properties setting</p>
         * Terminal ICS setting
         */
        public static final int ICS = 0xDF64;//emv内核为DF24,但与早期国内申明的DF24（是否支持电子现金冲突，因此重命名为DF64）

        /**
         * <p>terminal properties</p>
         * Transaction Reference Currency Conversion
         */
        public static final int REFERENCE_CURRENCY_CONVERSION = 0xDF22;


        /**
         * Pboc terminal properties setting<p>
         * Default transaction certificate data object list (TDOL)
         */
        public static final int DEFAULT_TDOL = 0xDF45;

        /**
         * Pboc terminal properties setting<p>
         * Fallback posentry
         */
        public static final int FALLBACK_POSENTRY = 0xDF40;

        /**** Acquisition of relevant transaction data ****/
        /**
         * Pboc transaction processing result
         */
        public static final int PBOC_PROCESS_RSLT = 0xDF75;

        /**** Pboc standard process params****/

        /**
         * Pboc standard process params <p>
         * Current card medium
         */
        public static final int MEDIATYPE = 0xDF70;
        /**
         * Enter：pboc transaction step<p>
         * Output：2nd currency electronic cash application currency code
         */
        public static final int PBOC_TRANS_STEP = 0xDF71;

        /**
         * Pboc forced online indicator
         */
        public static final int FORCE_ONLINE = 0xDF72;

        /**
         * Pboc account selection indicated
         */
        public static final int ACCTSELECTED_INDICATOR = 0xDF73;

        /**
         * KSN
         */
        public static final int KSN = 0xDF79;

        /**** Secondary authentication related****/
        /**
         * Script execution result<p>
         */
        public static final int SCRIPT_EXECUTE_RSLT = 0xDF31;

        /**
         * Inner transaction type <p>
         */
        public static final int INNER_TRANSACTION_TYPE = 0xDF7C;

        /**
         * Error code<p>
         */
        public static final int ERROR_CODE = 0xDF76;

        /**
         * kernel configuration（it take effect when paypass）
         */
        public static final int KERNEL_CONFIGURATION = 0xDF2F;

        /**
         * limit_exist
         */
        public static final int LIMIT_EXIST = 0xDF27;

        /**
         * CAP_NO_CVM（it take effect when paypass）
         */
        public static final int CAP_NO_CVM = 0xDF48;

        /**
         * MOBILE_SUPPORT_INDICATOR
         */
        public static final int MOBILE_SUPPORT_INDICATOR = 0xDF46;

        /**
         * EXTerminalCap
         * just for  ExpressPay
         */
        public static final int EX_Terminal_CAP = 0xDF49;

        /**
         * PPTLV
         */
        public static final int PPTLV = 0xDF52;

        /**
         * DEUDOL
         */
        public static final int DEUDOL = 0xDF2B;

        /**
         * MagAppVer just for paypass
         */
        public static final int MAGAPPVER = 0xDF2D;

//        /**
//         * MaxNumTornLog just for paypass
//         */
//        public static final int MNUMTORN = 0xDF32;
//
//        /**
//         * BalanceReadFlag just for paypass
//         */
//        public static final int BALANFLAG = 0xDF33;

        /**
         * PwConfig just for paywave
         */
        public static final int PWCONFIG = 0xDF34;

//        /**
//         * CvmReq just for paywave
//         */
//        public static final int CVMREQ = 0xDf35;
//
//        /**
//         * DdaVer just for paywave
//         */
//        public static final int DDAVER = 0xDf36;

        /**
         * KernelId
         */
        public static final int KERNELID = 0xDF37;

//        /**
//         * VisaTtqPresent
//         */
//        public static final int VISATTQ = 0xDf38;

        /**
         * StatusCheckSupport
         */
        public static final int STATUSCHECK = 0xDF39;

        /**
         * Zero Amount Allowed flag<p>
         * 1 :allowed，0:not allowed<p>
         */
        public static final int ZEROALLOW = 0xDF3A;

        /**
         * Extended election Support flag
         * 0x01 Support for Extendable<p>
         * just for contactless<p>
         * if card aid return 9F29 in PPSE, append the 9F29 data to the card aid<p>
         */
        public static final int EXAIDSUPP = 0xDF3B;

        /**
         * Contactlss Card holder select application Allowed flag(deprecated)
         */
        public static final int CLSSCVA = 0xDF3C;

        /**
         * Dynamic Reader Limits status
         */
        public static final int DRLSTATUS = 0xDF3D;

        /**
         * Dynamic Reader Limits DATA(paywave)
         */
        public static final int DRLDATA = 0xDF3F;
        /**
         * Dynamic Reader Limits DATA(expresspay)
         */
        public static final int DRLDATA_EXP = 0xDF53;
        /**
         * Magstripe CVM Capability-CVM Required (just for paypass)
         */
        public static final int MAGSCVM = 0xDF42;

        /**
         * MaxLifetimeTornLog just for paypass
         */
        public static final int MEXLTTORN = 0xDf43;

        /**
         * Magstripe CVM Capability- NO CVM Required(just for paypass)
         */
        public static final int MAGSNOCVM = 0xDF47;

        /**
         * EXRandomScope just for ExpressPay
         */
        public static final int EXRANDOM = 0xDF4A;

//        /**
//         * EXTimeExpire just for ExpressPay
//         */
//        public static final int EXTIMEEX = 0xDF4B;

        /**
         * CombinationOP just for JCB
         */
        public static final int COMBINATIONOPT = 0xDF60;

        /**
         * EC Terminal Support Indicator
         */
        public static final int EC_SUPPORT_INDICATOR = 0x9F7A;

        /**
         * Maximum Relay Resistance Grace Period(DF54)- ‘DF8133’
         */
        public static final int DF8133 = 0xDF54;

        /**
         * Minimum Relay Resistance Grace Period- ‘DF8132’
         */
        public static final int DF8132 = 0xDF55;

        /**
         * Relay Resistance Accuracy Threshold- ‘DF8136’
         */
        public static final int DF8136 = 0xDF56;

        /**
         * Relay Resistance Transmission Time Mismatch Threshold- ‘DF8137’
         */
        public static final int DF8137 = 0xDF57;

        /**
         * Terminal Expected Transmission Time For Relay Resistance C-APDU- ‘DF8134’
         */
        public static final int DF8134 = 0xDF58;

        /**
         * Terminal Expected Transmission Time For Relay Resistance R-APDU- ‘DF8135’
         */
        public static final int DF8135 = 0xDF59;
        /**
         * Indicate whether matching trans type when picking up this aid.paypass.
         * Newland custom tag.
         * 0x01-support
         */
        public static final int PP1F8101 = 0x1F8101;

        /**
         * Transaction type.
         * the value of 9C (00/01/09/20)
         * when 1F8101==0x01, only this item is equal to the value of current transaction type (0x9C) then we select this aid.
         */
        public static final int DF7D = 0xDF7D;

        /**
         * EMV SELECT KERNEL
         */
        public static final int EMVSELECTKERNEL = 0xDF25;
    }
}
