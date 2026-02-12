package com.newland.sdk.mtypex.module.common.emv;

import com.newland.sdk.module.emv.EmvPackager;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.common.Const;
import com.newland.sdk.mtype.common.Const.EmvSelfDefinedReference;
import com.newland.sdk.mtype.common.Const.EmvStandardReference;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.me.module.emv.structure.AbstractEMVPackage;
import com.newland.sdk.me.module.emv.structure.EMVTagDefined;
import com.newland.sdk.me.module.emv.structure.EMVTagRef;
import com.newland.sdk.me.module.emv.structure.EMVTagValueType;
import com.newland.sdk.utils.TLVMsg;
import com.newland.sdk.utils.TLVPackage;
import com.newland.sdk.mtype.util.Dump;
import com.newland.sdk.mtype.util.InnerUtils;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @since v1.0
 */
public class SimpleEmvPackager implements EmvPackager {

    private static List<EmvTag> emvTagsList = new ArrayList<EmvTag>();
    private static HashMap<Integer, EmvTag> emvTagsContext = new HashMap<Integer, EmvTag>();

    private static Map<String, Map<Field, EMVTagRef>> fieldsContext = new HashMap<String, Map<Field, EMVTagRef>>();
    private static Map<String, Map<Integer, Field>> tagsContext = new HashMap<String, Map<Integer, Field>>();

    private final DeviceLogger logger = DeviceLoggerFactory.getLogger("SimpleEmvPackager");

    static {
        emvTagsList.add(new EmvTag(EmvStandardReference.AID_TERMINAL, "AID", 5, 16, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.CA_PUBLIC_KEY_INDEX_TERMINAL, "Authentication center public key index", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.CA_PK_EXPIRATION_DATE, "Valid period of public key", 4, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.CA_PK_HASH_ALGORITHM_INDICATOR, "Public key signature hash algorithm", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.CA_PK_ALGORITHM_INDICATOR, "Public key signature algorithm", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.CAPK_MODULUS, "N modules of public key", 0, 248, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.CAPK_EXPONENT, "The e exponent of public key", 0, 3, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.CAPK_SHA1CHECKSUM, "Public Key Thumbprint", 0, 248, EMVTagValueType.BINARY));

        /************************************************ AID setting *********************************************/
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.APP_SELECT_INDICATOR, "Application selection indicator（ASI）", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.TAC_DEFAULT, "TAC default", 5, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.TAC_ONLINE, "TAC online", 5, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.TAC_DENIAL, "TAC refuse", 5, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.TERMINAL_FLOOR_LIMIT, "Terminal minimum limits", 4, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.THRESHOLD_VALUE_FOR_BIASED_RANDOM_SELECTION, "Threshold of bias random selection", 4, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.MAX_TARGET_PERCENTAGE_FOR_BIASED_RANDOM_SELECTION, "Maximum target percentage of biased random selection", 1, EMVTagValueType.COMPRESSED_NUMBERIC));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.TARGET_PERCENTAGE_FOR_RANDOM_SELECTION, "Target percentage of random selection", 1, EMVTagValueType.COMPRESSED_NUMBERIC));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.DEFAULT_DDOL, " Default DDOL", EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.EC_TRANS_LIMIT, "EC trade limit", 6, EMVTagValueType.COMPRESSED_NUMBERIC));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.NCICC_OFFLINE_FLOOR_LIMIT, "Electronic cash transaction limit", 6, EMVTagValueType.COMPRESSED_NUMBERIC));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.NCICC_TRANS_LIMIT, "RF card transaction limit", 6, EMVTagValueType.COMPRESSED_NUMBERIC));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.NCICC_CVM_LIMIT, "RF triggering CVM trading quotas", 6, EMVTagValueType.COMPRESSED_NUMBERIC));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.PP1F8101, "RF card transaction limit", 1, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.DF7D, "RF triggering CVM trading quotas", 1, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.EMVSELECTKERNEL, "EMV Select Kernel", 1, EMVTagValueType.NUMERIC));

//        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.RF_STATUS_CHECK, "RF state examination", 1, EMVTagValueType.BINARY));

        /*************************************Pboc terminal property setting ************************************/
        emvTagsList.add(new EmvTag(EmvStandardReference.AID_CARD, "Selected AID", 5, 16, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.ICS, "Teminal ICS setting", 7, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.TERMINAL_TYPE, "Terminal type", 1, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.TERMINAL_CAPABILITIES, "Terminal performance", 3, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.ADDITIONAL_TERMINAL_CAPABILITIES, "Terminal additional performance", 5, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.POINT_OF_SERVICE_ENTRY_MODE, "Pos entry", 1, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.ACQUIRER_IDENTIFIER, "Purchaser logo", 6, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.MERCHANT_CATEGORY_CODE, "Merchant type code", 2, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.MERCHANT_IDENTIFIER, "Merchant id", 15, EMVTagValueType.TEXT));
        emvTagsList.add(new EmvTag(EmvStandardReference.TRANSACTION_CURRENCY_CODE, " Transaction currency Code", 2, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.TRANSACTION_CURRENCY_EXP, "Transaction currency index", 1, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.TRANSACTION_REFERENCE_CURRENCY_CODE, "Transaction reference currency code", 2, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.TRANSACTION_REFERENCE_CURRENCY_EXP, "Transaction reference currency index", 1, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.TERMINAL_COUNTRY_CODE, "Terminal country code", 2, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.INTERFACE_DEVICE_SERIAL_NUMBER, "IFD serial number", 8, EMVTagValueType.TEXT));
        emvTagsList.add(new EmvTag(EmvStandardReference.CARD_SEQUENCE_NUMBER, "Card sequence number", 1, 2, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.TERMINAL_IDENTIFICATION, "Terminal number ", 8, EMVTagValueType.TEXT));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.DEFAULT_TDOL, "Default TDOL", EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.FALLBACK_POSENTRY, "Fallback posentry", EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.TRACK_2_EQV_DATA, "IC card second magnetic track data", 0, 1024, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.EC_SUPPORT_INDICATOR, " EC Terminal Support Indicator", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.MERCHANT_NAME_AND_LOCATION, "Merchant Name and Location", 0, 20, EMVTagValueType.TEXT));

        /********************************************** Acquisition of related transaction data ******************************************/
        emvTagsList.add(new EmvTag(EmvStandardReference.PAN, "卡号", EMVTagValueType.COMPRESSED_NUMBERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.APP_EXPIRATION_DATE, "Expiration date", 3, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.PBOC_PROCESS_RSLT, "PBOC transaction processing results", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.PBOC_CARD_FUNDS, "PBOC balance", 6, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.QPBOC_CARD_FUNDS, "QPBOC balance", 6, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.TRANSACTION_TIME, "Transaction time", 3, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.EC_BALANCE_LIMIT, " Electronic cash balance limit ", 6, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.EC_SINGLE_TRANSACTION_LIMIT, "EC Single Transaction Limit", 6, EMVTagValueType.NUMERIC));

        emvTagsList.add(new EmvTag(EmvStandardReference.TRANSACTION_STATUS_INFORMATION, "Transaction status information", 2, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.APP_PREFERRED_NAME, "Application preference", 0, 16, EMVTagValueType.TEXT));
        emvTagsList.add(new EmvTag(EmvStandardReference.APPLICATION_LABEL, "Applied label", 0, 20, EMVTagValueType.TEXT));

        /********************************************** Pboc standard process parameters ******************************************/
        emvTagsList.add(new EmvTag(EmvStandardReference.AMOUNT_AUTHORISED_NUMERIC, "Authed Amount", 6, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.AMOUNT_OTHER_NUMERIC, "Authed Amount(new EmvTag(others));", 6, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.TRANSACTION_TYPE, "Transaction type", 1, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.TERMINAL_TRANSACTION_QUALIFIERS, "Transaction Attribute", 4, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.MEDIATYPE, "Current card media", 1, EMVTagValueType.BINARY));
        // emvTagsList.add(new EmvTag(EmvSelfDefinedReference.PBOC_TRANS_STEP,
        // "pboc交易步骤", 1, EMVTagValueType.BINARY));由于银联征用df71用于第二币种，与新大陆自定义冲突
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.FORCE_ONLINE, "PBOC Forced online logo", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.ACCTSELECTED_INDICATOR, "PBOC account selection sign", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.INNER_TRANSACTION_TYPE, "Custom transaction type", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.APP_CURRENCY_CODE, "First currency electronic cash application currency code", 2, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.CDCVM_DATA, "Card Transaction Qualifiers CTQ", 2, EMVTagValueType.BINARY));
        /********************************************** PBOC transaction process data reading extension******************************************/
        emvTagsList.add(new EmvTag(EmvStandardReference.APP_CRYPTOGRAM, "Application Cryptogram", 8, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.CRYPTOGRAM_INFORMATION_DATA, "Cryptogram information data", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.ISSUER_APPLICATION_DATA, "Issuer application Data", 0, 32, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.UNPREDICTABLE_NUMBER, "Unpredictable number", 4, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.APP_TRANSACTION_COUNTER, "Application Transaction Counter", 2, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.TERMINAL_VERIFICATION_RESULTS, "Terminal verification result", 5, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.TRANSACTION_DATE, "Cryptogram information data", 3, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.APPLICATION_INTERCHANGE_PROFILE, "Application interactive features", 2, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.EC_ISSUER_AUTHORIZATION_CODE, "Electronic cash authorization code", 6, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.KSN, "KSN", 10, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.CARDHOLDER_NAME, "Name of cardholder", 2, 26, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.CARDHOLDER_NAME_EXTENDED, "Cardholder's name extension", 27, 45, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.CARDHOLDER_CERT_NO, "Cardholder's ID number", 1, 40, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.CARDHOLDER_CERT_TYPE, " Cert type of cardholder", 1, EMVTagValueType.BINARY));
        /********************************************** Optional information subdomain list***************************************************/
        emvTagsList.add(new EmvTag(EmvStandardReference.CVM_RESULTS, "Cardholder certification results", 3, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.TERMINAL_TYPE, "Terminal type", 1, EMVTagValueType.NUMERIC));
        emvTagsList.add(new EmvTag(EmvStandardReference.INTERFACE_DEVICE_SERIAL_NUMBER, "Interface device serial number", 8, EMVTagValueType.TEXT));
        emvTagsList.add(new EmvTag(EmvStandardReference.DEDICATED_FILE_NAME, "Special file name", 5, 16, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.APP_VERSION_NUMBER_TERMINAL, "Soft version", 2, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.TRANSACTION_SEQUENCE_COUNTER, "Transaction sequence counter", EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.ISSUER_AUTHENTICATION_DATA, "Issurer authentication data", EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.ISSUER_SCRIPT_TEMPLATE_1, "Issurer script 1", EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.ISSUER_SCRIPT_TEMPLATE_2, "Issurer script 2", EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.SCRIPT_EXECUTE_RSLT, "Script execution results", EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.CARD_PRODUCT_IDATIFICATION, "Card product identification information", 16, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.AUTHORISATION_RESPONSE_CODE, "Authorization response code", 2, EMVTagValueType.TEXT));
        emvTagsList.add(new EmvTag(EmvStandardReference.AUTHORISATION_CODE, "Authorization code", 6, EMVTagValueType.TEXT));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.KERNEL_CONFIGURATION, "kernel configuration", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.LIMIT_EXIST, "limit exist", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.CAP_NO_CVM, "cap no cvm", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.TERMINAL_RISK_MANAGEMENT_DATA, "TERMINAL_RISK_MANAGEMENT_DATA", 0, 1024, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.MOBILE_SUPPORT_INDICATOR, "MOBILE_SUPPORT_INDICATOR", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.EX_Terminal_CAP, "EX_Terminal_CAP", 1, EMVTagValueType.BINARY));

        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.PPTLV, "PPTLV", 0, 1024, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.DEUDOL, "DEUDOL", 0, 1024, EMVTagValueType.BINARY));

        /********************************************** 针对paypass ***************************************************/
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.MAGAPPVER, "MAGAPPVER", 2, EMVTagValueType.BINARY));
//        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.MNUMTORN, "MNUMTORN", 1, EMVTagValueType.BINARY));
//        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.BALANFLAG, "BALANFLAG", 1, EMVTagValueType.BINARY));

        /********************************************** 针对paywave ***************************************************/
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.PWCONFIG, "PWCONFIG", 2, EMVTagValueType.BINARY));
//        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.CVMREQ, "CVMREQ", 1, EMVTagValueType.BINARY));
//        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.DDAVER, "DDAVER", 1, EMVTagValueType.BINARY));

        /*******Entry Point Configuration Data per Combination A-Table 5-2* ***/
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.KERNELID, "KERNELID", 8, EMVTagValueType.BINARY));
//        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.VISATTQ, "VISATTQ", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.STATUSCHECK, "STATUSCHECK", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.ZEROALLOW, "ZEROALLOW", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.EXAIDSUPP, "EXAIDSUPP", 1, EMVTagValueType.BINARY));
//        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.CLSSCVA, "CLSSCVA", 1, EMVTagValueType.BINARY));

        /******** qVSDC support DRL application ID[4]**********/
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.DRLSTATUS, "DRLSTATUS", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.DRLDATA, "DRLDATA", 0, 1024, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.DRLDATA_EXP, "DRLDATA_EXP", 0, 1024, EMVTagValueType.BINARY));
        /******************** 以下为 Paypass 版本的结构体 ***********************/
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.MAGSCVM, "MAGSCVM", 1, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.MEXLTTORN, "MEXLTTORN", 2, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.MAGSNOCVM, "MAGSNOCVM", 1, EMVTagValueType.BINARY));

        /******************** 以下为 ExpressPay 版本的结构体 ********************/
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.EXRANDOM, "EXRandomScope", 1, EMVTagValueType.BINARY));
//        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.EXTIMEEX, "EXTIMEEX", 1, EMVTagValueType.BINARY));

        /******************** 以下为 JCB 版本的结构体 ********************/
        emvTagsList.add(new EmvTag(EmvSelfDefinedReference.COMBINATIONOPT, "COMBINATIONOPT", 2, EMVTagValueType.BINARY));
        emvTagsList.add(new EmvTag(EmvStandardReference.TIP, "TIP", 3, EMVTagValueType.BINARY));

        initEmvTagsContext();
    }

    private static void initEmvTagsContext() {
        for (EmvTag tag : emvTagsList) {
            emvTagsContext.put(tag.getTag(), tag);
        }
    }

    private Map<Field, EMVTagRef> getSupportTagRefs(Class<? extends AbstractEMVPackage> clazz) {
        String classKey = clazz.getName();
        synchronized (fieldsContext) {
            if (fieldsContext.get(classKey) != null) {
                return fieldsContext.get(classKey);
            }
            Map<Field, EMVTagRef> tagRefs = new HashMap<Field, EMVTagRef>();
            Field[] fields = clazz.getDeclaredFields();
            emvTagsContext.put(EmvSelfDefinedReference.PBOC_TRANS_STEP, new EmvTag(EmvSelfDefinedReference.PBOC_TRANS_STEP, "pboc交易步骤", 1, EMVTagValueType.BINARY));
            for (Field field : fields) {
                EMVTagDefined defined = field.getAnnotation(EMVTagDefined.class);
                if (defined == null)
                    continue;
                EMVTagRef tagRef = emvTagsContext.get(defined.tag());
                if (tagRef == null) {
                    logger.warn("[getSupportTagRefs] tag ref is null!not support by system!" + defined.tag());
                    continue;
                }
                tagRefs.put(field, tagRef);
            }

            fieldsContext.put(clazz.getName(), tagRefs);
            return tagRefs;
        }
    }

    private Map<Integer, Field> getSupportFieldRefs(Class<? extends AbstractEMVPackage> clazz) {
        String classKey = clazz.getName();
        synchronized (tagsContext) {
            if (tagsContext.get(classKey) != null) {
                return tagsContext.get(classKey);
            }
            Map<Integer, Field> fieldRefs = new HashMap<Integer, Field>();
            Field[] fields = clazz.getDeclaredFields();
            emvTagsContext.put(EmvSelfDefinedReference.PBOC_TRANS_STEP, new EmvTag(EmvSelfDefinedReference.PBOC_TRANS_STEP, "第二币种电子现金应用货币代码", 2, EMVTagValueType.NUMERIC));
            for (Field field : fields) {
                EMVTagDefined defined = field.getAnnotation(EMVTagDefined.class);
                if (defined == null)
                    continue;
                EMVTagRef tagRef = emvTagsContext.get(defined.tag());
                if (tagRef == null) {
                    logger.warn("[getSupportFieldRefs] tag ref is null!not support by system!" + defined.tag());
                    continue;
                }
                fieldRefs.put(tagRef.getTag(), field);
            }

            tagsContext.put(clazz.getName(), fieldRefs);
            return fieldRefs;
        }
    }

    /**
     * Packaging process, only for the length of nc,and the rest do not do the length judge
     */
    @Override
    public <T extends AbstractEMVPackage> byte[] pack(T pckg) {
        TLVPackage tlvpackage = InnerUtils.newTlvPackage();
        Class<T> pckgClazz = (Class<T>) pckg.getClass();
        Map<Field, EMVTagRef> tagRefs = getSupportTagRefs(pckgClazz);
        for (Field field : pckgClazz.getDeclaredFields()) {
            EMVTagRef tagRef = tagRefs.get(field);
            if (tagRef != null) {
                packTag(pckg, field, tagRef, tlvpackage);
            }
        }
        // Get the message objects of all extended TLV packages
        Enumeration tlvmsgs = pckg.getExternalPackage().elements();
        while (tlvmsgs.hasMoreElements()) {
            TLVMsg tlvmsg = (TLVMsg) tlvmsgs.nextElement();
            if (tlvpackage.hasTag(tlvmsg.getTag())) { // 如果在打包过程中,已经设置了对应的数据包,则提示
                logger.debug("[pack] msg in " + pckg.getClass() + " has defined tag:" + tlvmsg.getTag() + ", but external defined it too! so it will be covered by external defined!");
                tlvpackage.deleteByTag(tlvmsg.getTag());
            }
            tlvpackage.append(tlvmsg); // 将扩展包的内容设置进来
        }
        return tlvpackage.pack();
    }

    /**
     * Standard domain-specific packaging process
     * <p>
     * <p>
     * All packaged length checks only limit the length of the bytes, and do not control the specific business level.
     *
     * @param tgt        Packaging raw data objects
     * @param field      The domain that needs to be packaged
     * @param tagRef     Corresponding label description
     * @param tlvpackage Incoming packets
     */
    private void packTag(Object tgt, Field field, EMVTagRef tagRef, TLVPackage tlvpackage) {
        Object o = null;
        try {
            field.setAccessible(true);
            o = field.get(tgt);
        } catch (Exception e) {
            throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "get field value failed,when pack up " + tgt.getClass() + "!" + field.getName(), e);
        }
        if (o == null) { // 该数据项为空
            return;
        }
        if (ClassUtils.isByteArrays(o.getClass())) {// 如果是字节数组,则直接设置后返回（相当于不做任何序列化操作）
            msgSetting(tlvpackage, tagRef, (byte[]) o);
            return;
        }

        switch (tagRef.getTagValueType()) {// 根据具体的类型进行分开处理
            case BINARY:
                packBin(o, tagRef, tlvpackage);
                break;
            case COMPRESSED_NUMBERIC:
                packCn(o, tagRef, tlvpackage);
                break;
            case NUMERIC:
                packNum(o, tagRef, tlvpackage);
                break;
            case TEXT:
                packText(o, tagRef, tlvpackage);
                break;
            default:
                throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "not support emv tag type!" + tagRef.getTagValueType());
        }
    }

    private void msgSetting(TLVPackage tlvpckg, EMVTagRef tagRef, byte[] value) {
        int tag = tagRef.getTag();
        if (tagRef.isModelFixedLen()) {
            if (value.length != tagRef.getFixedLen())
                throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "fixed len not match(" + Integer.toHexString(tag) + ")!expected:" + tagRef.getFixedLen() + ",but is:" + value.length);
        } else if (tagRef.isModelScopeLen()) {
            if (value.length > tagRef.getMaxLen() || value.length < tagRef.getMinLen())
                throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "scope len not match(" + Integer.toHexString(tag) + ")!expected:[" + tagRef.getMinLen() + "," + tagRef.getMaxLen() + "],but is:" + value.length);
        }

        if (logger.isDebugEnabled())
            logger.debug("[msgSetting] set msg into tlvpackge,tag:[" + Integer.toHexString(tag) + "],value:" + Dump.getHexDump(value));
        tlvpckg.append(tag, value);
    }

    private boolean isTypeString(Class<?> clz) {
        return String.class.equals(clz);
    }

    private boolean isTypeInteger(Class<?> clz) {
        return Integer.class.equals(clz) || clz.getName().equals("int");
    }

    private boolean isTypeByte(Class<?> clz) {
        return Byte.class.equals(clz) || clz.getName().equals("byte");
    }

    /**
     * Processing text type packaging<p>
     * Support <tt>String,byte[],Byte[]</tt><p>
     * <p>
     * The packing process does not restrict and judge the length of any length
     *
     * @param tgt
     * @param tagRef
     * @param tlvpackage
     */
    private void packText(Object tgt, EMVTagRef tagRef, TLVPackage tlvpackage) {
        if (tgt instanceof String) { // 如果是字符串类型
            try {
                msgSetting(tlvpackage, tagRef, ((String) tgt).getBytes(Const.DEFAULT_CHARSET));
            } catch (UnsupportedEncodingException e) {
                throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, e.getMessage(), e);
            }
            return;
        }
        throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "unsupport type![" + Integer.toHexString(tagRef.getTag()) + "]" + tgt.getClass());
    }

    /**
     * Processing type is{@link EMVTagValueType#NUMERIC}.
     * <p>
     * This setting only supports fixed-length processing.
     * <p>
     * Supporting type only<tt>String,Integer</tt>
     *
     * @param tgt
     * @param tagRef
     * @param tlvpackage
     */
    private void packNum(Object tgt, EMVTagRef tagRef, TLVPackage tlvpackage) {
        /**
         * For non - fixed data, take the current data filling length
         */
        // if(!tagRef.isModelFixedLen())//只支持定长长度设置
        // throw new DeviceRTException(ExCode.SERIALIZE_OR_UNSERIALIZE_FAILED,
        // "numberic only support fixed length model!");

        String value = null;
        if (tgt instanceof String) {
            value = (String) tgt;
        }
        if (tgt instanceof Integer) {
            value = tgt.toString();
        }
        if (value == null) // 不支持的序列化类型
            throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "unsupport type![" + Integer.toHexString(tagRef.getTag()) + "]" + tgt.getClass());
        int fixedLen = -1;
        if (tagRef.isModelFixedLen())
            fixedLen = tagRef.getFixedLen();
        else
            fixedLen = (int) ((value.length() + 1) / 2);

        byte[] rslt = null;
        if (tgt instanceof Integer) {
            rslt = InnerUtils.intToBytes((Integer) tgt, fixedLen, true);
        } else {
            value = InnerUtils.padleft(value, fixedLen * 2, '0'); // 左补充0到定长的2倍，因为定长定的是字节总长。
            rslt = InnerUtils.str2bcd(value, true); // 其实无所谓这里补长的数据，因为实际必定为2的倍数
        }
        msgSetting(tlvpackage, tagRef, rslt);
    }

    /**
     * Processing type is{@link EMVTagValueType#COMPRESSED_NUMBERIC}.
     * <p>
     * This setting only supports fixed length processing.Supporttiing type only <tt>String,Integer</tt>
     *
     * @param tgt
     * @param tagRef
     * @param tlvpackage
     */
    private void packCn(Object tgt, EMVTagRef tagRef, TLVPackage tlvpackage) {
        /**
         * For non - fixed data, take the current data filling length
         */
        // if(!tagRef.isModelFixedLen())
        // throw new DeviceRTException(ExCode.SERIALIZE_OR_UNSERIALIZE_FAILED,
        // "numberic not support unfixed length!");

        String value = null;
        if (tgt instanceof String) {
            value = (String) tgt;
        }
        if (tgt instanceof Integer) {
            value = tgt.toString();
        }
        if (value == null)
            throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "unsupport type![" + Integer.toHexString(tagRef.getTag()) + "]" + tgt.getClass());

        int fixedLen = -1;
        if (tagRef.isModelFixedLen())
            fixedLen = tagRef.getFixedLen();
        else
            fixedLen = (int) ((value.length() + 1) / 2);

        value = InnerUtils.padright(value, fixedLen * 2, 'F');// 右补充F到定长的2倍，因为定长定的是字节总长
        byte[] rslt = InnerUtils.str2bcd(value, false);

        msgSetting(tlvpackage, tagRef, rslt);
    }

    /**
     * Supporting<tt>Byte,byte,Integer,int</tt>
     *
     * @param tgt
     * @param tagRef
     * @param tlvpackage
     */
    private void packBin(Object tgt, EMVTagRef tagRef, TLVPackage tlvpackage) {

        byte[] rslt = null;

        if (tgt instanceof Byte) {
            rslt = new byte[]{(Byte) tgt};
        }
        if (tgt instanceof Integer) {
            rslt = new byte[]{(byte) (((Integer) tgt).intValue() & 0xff)};
        }

        if (rslt == null)// 不支持的类型
            throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "unsupport type![" + Integer.toHexString(tagRef.getTag()) + "]" + tgt.getClass());

        msgSetting(tlvpackage, tagRef, rslt);
    }

    /**
     * Unpacking
     */
    @Override
    public <T extends AbstractEMVPackage> T unpack(byte[] payload, Class<T> content, T context) {
        Map<Integer, Field> fieldRefs = getSupportFieldRefs(content); // 根据包装类型获得所有的tag->Field映射
        T pckg = null;
        try {// Trying to construct a wrapper class
            Constructor<T> constructor = content.getDeclaredConstructor();
            constructor.setAccessible(true);
            if (context != null && content.isAssignableFrom(context.getClass())) {
                pckg = context;
            } else
                pckg = constructor.newInstance();
        } catch (Exception e) {
            throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "failed to create new instance of:" + content.getName(), e);
        }
        TLVPackage tlvpackage = InnerUtils.newTlvPackage();
        tlvpackage.unpack(payload);// 解包
        Enumeration elements = tlvpackage.elements();
        while (elements.hasMoreElements()) {// 根据解包数据进行处理
            TLVMsg tlvmsg = (TLVMsg) elements.nextElement();
            int tag = tlvmsg.getTag();
            Field field = null;
            logger.debug("[unpack] tag [" + Integer.toHexString(tag) + "] set into extenal!");
            pckg.setExternal(tlvmsg.getTag(), tlvmsg.getValue());
            if ((field = fieldRefs.get(tag)) != null) {// 尝试是否能获取到声明的tag
                byte[] tagValue = tlvpackage.getValue(tag);
                try {
                    if (tag == EmvSelfDefinedReference.CA_PK_EXPIRATION_DATE) {
                        if (tagValue != null && tagValue.length >= 3) {
                            if (tagValue.length == 8) {
                                String expDate = new String(tagValue);
                                tagValue = InnerUtils.hex2byte(expDate);
                            }
                            tagValue = InnerUtils.padLeft(tagValue, 4, (byte) 0x20);
                        }
                    }
                    unpackTag(pckg, field, emvTagsContext.get(tag), tagValue);
                } catch (Exception e) {
                    logger.warn("[unpack] unpack emv field failed!" + field.getName() + " at " + content.getName());
                }
                continue;
            }

        }

        return pckg;
    }

    private void fieldSetting(AbstractEMVPackage pckg, Field tgtField, EMVTagRef tagRef, Object value) {
        int tag = tagRef.getTag();
        if (logger.isDebugEnabled())
            logger.debug("[fieldSetting] set msg into field:" + tgtField.getName() + "(" + pckg.getClass().getName() + "),tag:[" + Integer.toHexString(tagRef.getTag()) + "],value:" + value.getClass().getName());
        try {
            tgtField.setAccessible(true);
            tgtField.set(pckg, value);
        } catch (Exception e) {
            throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "set msg into field:" + tgtField.getName() + "(" + pckg.getClass().getName() + "),tag:[" + Integer.toHexString(tagRef.getTag()) + "] failed!", e);
        }
    }

    private void unpackTag(AbstractEMVPackage pckg, Field tgtField, EMVTagRef tagRef, byte[] value) {
        /*** Length check ***/

        if (tagRef.isModelFixedLen()) {
            if (value.length != tagRef.getFixedLen())
                throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "fixed len not match!expected:" + tagRef.getFixedLen() + ",but is:" + value.length);
        } else if (tagRef.isModelScopeLen()) {
            if (value.length > tagRef.getMaxLen() || value.length < tagRef.getMinLen())
                throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "scope len not match!expected:[" + tagRef.getMinLen() + "," + tagRef.getMaxLen() + "],but is:" + value.length);
        }

        if (ClassUtils.isByteArrays(tgtField.getType())) {// 如果原始类型就是字节数组
            fieldSetting(pckg, tgtField, tagRef, value); // 直接设置
            return;
        }

        switch (tagRef.getTagValueType()) {
            case BINARY:
                unpackBin(pckg, tgtField, tagRef, value);
                break;
            case COMPRESSED_NUMBERIC:
                unpackCn(pckg, tgtField, tagRef, value);
                break;
            case NUMERIC:
                unpackNum(pckg, tgtField, tagRef, value);
                break;
            case TEXT:
                unpackText(pckg, tgtField, tagRef, value);
                break;
            default:
                throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "not support emv tag type!" + tagRef.getTagValueType());
        }

    }

    private void unpackText(AbstractEMVPackage pckg, Field tgtField, EMVTagRef tagRef, byte[] rslt) {
        if (isTypeString(tgtField.getType())) {
            try {
                fieldSetting(pckg, tgtField, tagRef, new String(rslt, Const.DEFAULT_CHARSET));
            } catch (UnsupportedEncodingException e) {
                throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, e.getMessage(), e);
            }
            return;
        }
        throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "not support field type![" + Integer.toHexString(tagRef.getTag()) + "]" + tgtField.getName() + "(" + tgtField.getType().getName() + ")");
    }

    private void unpackNum(AbstractEMVPackage pckg, Field tgtField, EMVTagRef tagRef, byte[] rslt) {
        // if(!tagRef.isModelFixedLen())
        // throw new DeviceRTException(ExCode.SERIALIZE_OR_UNSERIALIZE_FAILED,
        // "numberic not support unfixed length!");

        String str = InnerUtils.bcd2str(rslt, 0, rslt.length * 2, true); // 期待长度为目标解析长度的2倍(定长)
        String value = InnerUtils.unPadLeft(str, '0'); // 去掉左边的0,这里存在一个
        // bug,因为没有交易期望的数据长度控制,如果数据本身0开头,就可能会多删除掉数据.

        if (isTypeInteger(tgtField.getType())) {
            fieldSetting(pckg, tgtField, tagRef, Integer.valueOf(value));
            return;
        }
        if (isTypeString(tgtField.getType())) {
            fieldSetting(pckg, tgtField, tagRef, value);
            return;
        }
        throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "not support field type![" + Integer.toHexString(tagRef.getTag()) + "]" + tgtField.getName() + "," + tgtField.getType().getName());
    }

    private void unpackCn(AbstractEMVPackage pckg, Field tgtField, EMVTagRef tagRef, byte[] rslt) {
        // if(!tagRef.isModelFixedLen())
        // throw new DeviceRTException(ExCode.SERIALIZE_OR_UNSERIALIZE_FAILED,
        // "numberic not support unfixed length!");

        String str = InnerUtils.bcd2str(rslt, 0, rslt.length * 2, true);// 期待长度为目标解析长度的2倍(定长)
        String value = InnerUtils.unPadRight(str, 'F');// 去掉右边的F

        if (isTypeInteger(tgtField.getType())) {
            fieldSetting(pckg, tgtField, tagRef, Integer.valueOf(value));
            return;
        }
        if (isTypeString(tgtField.getType())) {
            fieldSetting(pckg, tgtField, tagRef, value);
            return;
        }

        throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "not support field type![" + Integer.toHexString(tagRef.getTag()) + "]" + tgtField.getName() + "," + tgtField.getType().getName());
    }

    private void unpackBin(AbstractEMVPackage pckg, Field tgtField, EMVTagRef tagRef, byte[] rslt) {
        if (isTypeByte(tgtField.getType())) {
            if (rslt.length > 1)
                logger.warn("[unpackBin] length is up to 1!but only set byte[0] ," + tgtField.getName());

            fieldSetting(pckg, tgtField, tagRef, rslt[0]);
            return;
        }
        if (isTypeInteger(tgtField.getType())) {
            if (rslt.length > 1)
                logger.warn("[unpackBin] length is up to 1!but only set byte[0] ,(tgt type is Integer)," + tgtField.getName());

            fieldSetting(pckg, tgtField, tagRef, (int) (rslt[0] & 0xff));
            return;
        }

        throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "not support field type![" + Integer.toHexString(tagRef.getTag()) + "]" + tgtField.getName() + "," + tgtField.getType().getName());

    }

    @Override
    public Map<Integer, EMVTagRef> getSupportTagMapping() {
        return (Map<Integer, EMVTagRef>) emvTagsContext.clone();
    }

    @Override
    public <T extends AbstractEMVPackage> List<T> unpackList(byte[] payload, Class<T> content, int reIndex) {
        Map<Integer, Field> fieldRefs = getSupportFieldRefs(content); // 根据包装类型获得所有的tag->Field映射
        List<T> tList = new ArrayList<T>();
        T pckg = null;
        Constructor<T> constructor = null;
        try {// 尝试构造一个包装类
            constructor = content.getDeclaredConstructor();
            constructor.setAccessible(true);
            pckg = constructor.newInstance();
        } catch (Exception e) {
            throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "failed to create new instance of:" + content.getName(), e);
        }
        TLVPackage tlvpackage = InnerUtils.newTlvPackage();
        if (null == payload) {
            return tList;
        }
        tlvpackage.unpack(payload);// 解包
        Enumeration elements = tlvpackage.elements();
        int i = 1;
        while (elements.hasMoreElements()) {// 根据解包数据进行处理
            TLVMsg tlvmsg = (TLVMsg) elements.nextElement();
            int tag = tlvmsg.getTag();
            Field field = null;
            logger.debug("[unpackList] tag [" + Integer.toHexString(tag) + "] set into extenal!");
            pckg.setExternal(tlvmsg.getTag(), tlvmsg.getValue());
            if ((field = fieldRefs.get(tag)) != null) {// 尝试是否能获取到声明的tag
                try {
                    unpackTag(pckg, field, emvTagsContext.get(tag), tlvmsg.getValue());
                } catch (Exception e) {
                    logger.warn("[unpackList] unpack emv field failed!" + field.getName() + " at " + content.getName());
                }
                // 重新构造对象
                if (i % reIndex == 0) {
                    try {
                        tList.add(pckg);
                        pckg = constructor.newInstance();
                    } catch (Exception e) {
                        throw new DeviceRTException(ErrorCode.SERIALIZE_OR_UNSERIALIZE_FAILED, "failed to create new instance of:" + content.getName(), e);
                    }
                }
                i++;
                continue;
            }
        }
        return tList;
    }

}
