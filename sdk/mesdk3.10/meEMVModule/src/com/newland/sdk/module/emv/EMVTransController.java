package com.newland.sdk.module.emv;

import android.support.annotation.NonNull;

import com.newland.sdk.utils.TLVPackage;

import java.math.BigDecimal;
import java.util.List;

/**
 * @description: Transaction  controller
 * @author: Lindan
 * @create: 2019/7/29
 */
public interface EMVTransController {


    /**
     * (conditional)Preprocessing before the contactless transaction.
     *
     * @param transactionType
     * @param amount
     * @param forceOnline
     * @param transactionExtParams
     * @return
     */
    public boolean preproccess(int transactionType, BigDecimal amount, boolean forceOnline, TransactionExtParams transactionExtParams);
    /**
     * start the EMV process <p>
     * If the authorized amount is in initialized, no entry is needed and it is null
     *
     * @param transactionType      Self-defined transaction processing code.see the TransactionType class.{@link TransactionType}
     * @param amount               Authorized amount (Unit:yuan)
     * @param forceOnline          Forced online
     * @param transactionExtParams emv extra parameters
     * @since 3.10.01
     */
    public void startEMV(int transactionType, BigDecimal amount, boolean forceOnline, TransactionExtParams transactionExtParams);

    /**
     * <p>Import the selected application into the EMV kernel When the onRequestSelectApplication callback is received.<p/>
     * <p>{@link EMVControllerListener#onRequestSelectApplication(EMVTransController, List, int)} <p/>
     *
     * @param index the index of Selected application{@link AIDEntity#getIndex()}
     * @since 3.10.01
     */
    public void setSelectedApplication(int index);

    /**
     * <p>Import the pin to EMV kernel When the onRequestInputPIN callback is received.<p/>
     * <p>{@link EMVInterceptListener#onRequestInputPIN(EMVTransController, boolean, PINEntity)}<p/>
     *
     * @param pinblock Input pin result(new byte[]{} means by pass)
     * @since 3.10.01
     */
    public void setPIN(byte[] pinblock);

    /**
     * Set the transaction amount when calling the setTransactionAmount method.{@link EMVControllerListener#onRequestInputAmount(EMVTransController)}
     *
     * @param amount Input transaction amount
     * @since 3.10.01
     */
    public void setTransactionAmount(BigDecimal amount);

    /**
     * <p>Confirm the transaction information when calling onRequestConfirmCardInfo or onRequestConfirmFinalAppSelection method.<p/>
     * <p>{@link EMVControllerListener#onRequestConfirmCardInfo(EMVTransController)}<p/>
     * <p>{@link EMVControllerListener#onRequestConfirmFinalAppSelection(EMVTransController)}<p/>
     *
     * @param confirm <p>true:continue the emv process<p/>
     *                <p>false:terminate the emv process<p/>
     * @since 3.10.01
     */
    public void confirmInformation(boolean confirm);


    /**
     * <p>Notify the kernel to end the EMV process.</p>
     * <p>This is an optional method.You can call this method to get the onEmvFinished{@link EMVControllerListener#onEmvFinished} callback or ignore it.</p>
     * <p>When the transaction completed the online step </p>
     * <p>
     * {@link EMVControllerListener#onEmvFinished(boolean, EMVTransController)} .
     *
     * @param inputData the authentication data.(ep:0x91,0x8a,0x89,0x71,0x72)
     */
    public void completeEMVProcess(@NonNull OnlineTransactionData inputData);

    public void cancelEMVProcess();

    /**
     * Set the data to the emv kernel.
     *
     * @param tag   Tag
     * @param value Value
     * @return
     */
    public boolean setEmvData(int tag, byte[] value);

    /**
     * Get the emv kernel data.
     *
     * @param emvTags Array of tags
     * @return {@link TLVPackage}
     */
    public TLVPackage getEmvData(int[] emvTags);

    /**
     * Get the emv kernel data.
     *
     * @param tag emv tag
     * @return emv data
     */
    byte[] getEmvData(int tag);

    /**
     * Use the GET DATA Command to get the value of the Tag.
     *
     * @param tag Tag
     * @return
     */
    public byte[] getICCdata(int tag);

    /**
     * get the EMV process data from EMVTransInfo.
     *
     * @return
     */
    public EMVTransInfo getEMVTransInfo();

    /**
     * Confirm the account type when calling onRequestSelectAccountType method{@link EMVControllerListener#onRequestSelectAccountType}
     *
     * @param accountType the account type{@link AccountType}
     */
    public void setSelectedAccountType(AccountType accountType);

    /**
     * Confirm the confirm ID when calling onRequestConfirmID method{@link EMVControllerListener#onRequestConfirmID}
     *
     * @param confirm
     */
    public void confirmID(boolean confirm);

    /**
     * Confirm the e-cash selection when calling onRequestConfirmEC method{@link EMVControllerListener#onRequestConfirmEC}
     *
     * @param isEC <p>true:electronic cash transaction </p>
     *             <p>false:online transaction </p>
     */
    public void confirmEC(boolean isEC);

    /**
     * Confirm the message returned by the EMV kernel when calling onRequestShowMessage method {@link EMVControllerListener#onRequestShowMessage}
     *
     * @param confirm <p>If the fourth parameter of the onRequestShowMessage is true,this param means:</p>
     *                <p>true: confirm. false:cancel</p>
     *                <p>If the fourth parameter of the onRequestShowMessage is false,This parameter is meaningless,Continue the process with true values.</p>
     */
    public void confirmMessage(boolean confirm);

    /**
     * Import the selected language.
     *
     * @param language <p>Chinese is "zh" or "ZH"，English is "en" or "EN".</p>
     */
    public void setSelectedLanguage(String language);

    /**
     * set timeout data used in callback methods {@link EMVControllerListener}
     *
     * @param timeout unit: second
     */
    public void setEMVTimeOut(int timeout);
}
