package com.newland.sdk.me.module.emv;

import com.newland.sdk.module.emv.ProcessingCode;
import com.newland.sdk.module.emv.TransactionType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;

public class ProcessingCodeAdaptor {

    public static Integer convertToInnerProcessingCode(int processingCode) {
        Integer innerProcessingCode = 0;
        switch (processingCode) {
            case ProcessingCode.GOODS_AND_SERVICE:
                innerProcessingCode = TransactionType.STANDARD;
                break;
//		case ProcessingCode.CASH:
//			innerProcessingCode = InnerProcessingCode.TRANS_CASH;
//			break;
            case ProcessingCode.DEBITS_ADJUSTMENT:
                innerProcessingCode = TransactionType.STANDARD;
                break;
            case ProcessingCode.CHEQUE_GUARANTEE:
                innerProcessingCode = TransactionType.STANDARD;
                break;
            case ProcessingCode.CHEQUE_VERIFICATION:
                innerProcessingCode = TransactionType.STANDARD;
                break;
            case ProcessingCode.EURO_CHEQUE:
                innerProcessingCode = TransactionType.STANDARD;
                break;
            case ProcessingCode.TRAVELLER_CHEQUE:
                innerProcessingCode = TransactionType.STANDARD;
                break;
            case ProcessingCode.LETTER_OF_CREDIT:
                innerProcessingCode = TransactionType.STANDARD;
                break;
            case ProcessingCode.GIRO_POSTAL_BANKING:
                innerProcessingCode = TransactionType.STANDARD;
                break;
            case ProcessingCode.GOODS_AND_SERVICE_WITH_CASH_DISBURSEMENT_TRANSFER:
                break;
            case ProcessingCode.RETURNS:
                innerProcessingCode = TransactionType.STANDARD;
                break;
//		case ProcessingCode.DEPOSITS:
//			innerProcessingCode = InnerProcessingCode.TRANS_CASHDEPOSIT;
//			break;
            case ProcessingCode.CREDITS_ADJUSTMENT:
                innerProcessingCode = TransactionType.STANDARD;
                break;
            case ProcessingCode.CHEQUE_DEPOSIT_GUARANTEE:
                innerProcessingCode = TransactionType.STANDARD;
                break;
            case ProcessingCode.CHEQUE_DEPOSIT:
                innerProcessingCode = TransactionType.STANDARD;
                break;
            case ProcessingCode.AVAILABLE_FUNDS_INQUIRY:
                innerProcessingCode = TransactionType.STANDARD;
                break;
//		case ProcessingCode.BALANCE_INQUIRY:
//			innerProcessingCode = InnerProcessingCode.TRANS_INQUIRY;
//			break;
            case ProcessingCode.RESERVED_FOR_ISO_USE:
                innerProcessingCode = TransactionType.STANDARD;
                break;
            case ProcessingCode.ACCOUNT_VERIFICATION:
                innerProcessingCode = TransactionType.STANDARD;
                break;
//		case ProcessingCode.CARDHOLDER_ACCOUNTS_TRANSFER:
//			innerProcessingCode = InnerProcessingCode.TRANS_ACCOUNT;
//			break;
            case ProcessingCode.LOAD:
                innerProcessingCode = TransactionType.EC_APPOINTED_LOAD_CONTACT;
                break;
            case ProcessingCode.NOT_APPOINTED_LOAD:
                innerProcessingCode = TransactionType.EC_NOT_APPOINTED_LOAD_CONTACT;
                break;
            case ProcessingCode.CASH_SAVING:
                innerProcessingCode = TransactionType.EC_CASH_LOAD_CONTACT;
                break;
            default:
                innerProcessingCode = TransactionType.STANDARD;
        }
        return innerProcessingCode;
    }
}
