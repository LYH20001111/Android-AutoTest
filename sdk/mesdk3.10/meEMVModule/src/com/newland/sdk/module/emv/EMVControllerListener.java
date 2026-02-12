package com.newland.sdk.module.emv;


import java.math.BigDecimal;
import java.util.List;

/**
 * Emv process control listener<p>
 *
 * @since v1.0
 */
public interface EMVControllerListener {

    /**
     * <p>It will be triggered when the transaction requires the app to response an application selection.</p>
     * <p>Invoke the setSelectedApplication method{@link EMVTransController#setSelectedApplication(int)} to continue the EMV process.</p>
     *
     * @param controller    Emv Transaction controller{@link EMVControllerListener}
     * @param aidEntityList A collection of available applications {@link AIDEntity}
     * @param times         Number of times you have tried to select.
     * @throws Exception
     */
    public void onRequestSelectApplication(EMVTransController controller, List<AIDEntity> aidEntityList, int times);

    /**
     * <p>It will be triggered when the transaction requires the app to response a customer confirmation of transaction information.</p>
     * <p>Invoke the confirmInformation method{@link EMVTransController#confirmInformation(boolean)} to continue the EMV process.</p>
     *
     * @param controller Emv Transaction controller{@link EMVControllerListener}
     */
    public void onRequestConfirmCardInfo(EMVTransController controller);

    /**
     * <p>It will be triggered when the transaction requires a transaction amount and it doesn't come with the startEMV method.</p>
     * <p>Invoke the setTransactionAmount method{@link EMVTransController#setTransactionAmount(BigDecimal)} to continue the EMV process.</p>
     *
     * @param controller Emv Transaction controller{@link EMVControllerListener}
     */
    public void onRequestInputAmount(EMVTransController controller);

    /**
     * <p>It will be triggered when the transaction requires to verify the card PIN.<p>
     * <p>you can call the PinpadModule to get the encrypted/clear PIN.{@link com.newland.sdk.module.pin.PinpadModule#startPinInput}{@link com.newland.sdk.module.pin.PinpadModule#startOfflinePinInput}</p>
     * <p>and then invoke the setPIN method{@link EMVTransController#setPIN(byte[])} to continue the EMV process.</p>
     *
     * @param controller    Emv Transaction controller{@link EMVControllerListener}
     * @param requireOnline <p>Whether a transaction is required online PIN.</p>
     *                      <p>true:require online PIN.false:require offline PIN</p>
     * @param pinEntity     PIN information,Contains module and exponent required for offline PIN.{@link PINEntity}
     * @throws Exception
     */
    public void onRequestInputPIN(EMVTransController controller, boolean requireOnline, PINEntity pinEntity);

    /**
     * <p>It will be triggered when the transaction requires online transaction. </p>
     * <p>Invoke the completeEMVProcess method{@link EMVTransController#completeEMVProcess(OnlineTransactionData)}</p>
     * <p>{@link EMVTransController#completeEMVProcess(OnlineTransactionData)}to end the EMV process.</p>
     *
     * @param controller Emv Transaction controller{@link EMVControllerListener}
     */
    public void onRequestOnlineProcess(EMVTransController controller);


    /**
     * <p>The final callback for EMV transaction.</p>
     * <p>It will be triggered when  completeEMVProcess are called{@link EMVTransController#completeEMVProcess(OnlineTransactionData)} </p>
     * <p>{@link EMVTransController#completeEMVProcess(OnlineTransactionData)} {@link EMVTransController#cancelEMVProcess()} </p>
     */
    public void onEmvFinished(boolean isSuccess, EMVTransController controller);

    /**
     * <p>The final callback for EMV transaction.</p>
     * <p>It will be triggered  when the emv process requires a fallback transaction</p>
     *
     * @param controller Emv Transaction controller{@link EMVControllerListener}
     *                   、
     */
    public void onFallback(EMVTransController controller);

    /**
     * <p>The final callback for EMV transaction.</p>
     * <p>It will be triggered when transaction meet any RuntimeException.</p>
     *
     * @param controller Emv Transaction controller{@link EMVControllerListener}
     * @param e          Exception
     */
    public void onError(EMVTransController controller, Exception e);

    /**
     * It will be triggered when the EMV kernel requests confirmation of the account type selection.</p>
     *
     * @param controller  Emv Transaction controller{@link EMVTransController#setSelectedAccountType(AccountType)}
     * @param accountType the account type{@link AccountType}
     */
    public void onRequestSelectAccountType(EMVTransController controller, AccountType[] accountType);

    /**
     * It will be triggered when the EMV kernel requests confirmation of the cardholder ID selection.<p>
     *
     * @param controller Emv Transaction controller{@link EMVTransController#confirmID(boolean)}
     * @param cardType   Card holder certificate type{@link IDCardType}
     * @param IDNo       ID number
     * @return true: The ID confirmation is correct.
     * false: The ID confirmation is incorrect.
     */
    public void onRequestConfirmID(EMVTransController controller, IDCardType cardType, String IDNo);

    /**
     * It will be triggered when the EMV kernel requests confirmation of the e-cash selection.<p>
     */
    public void onRequestConfirmEC(EMVTransController controller);

    /**
     * It will be triggered when the EMV kernel requests to display or confirm a message.
     *
     * @param controller Emv Transaction controller{@link EMVTransController#confirmMessage(boolean)}
     * @param title      Title
     * @param msg        Message content
     * @param isConfirm  Whether to confirm by user<p>
     *                   true：The message prompt requires confirmation(Confirm、Cancel)<p>
     *                   false: just show message,the message prompt does not require confirmation<p>
     * @param timeOut    Waiting time(s)
     */
    public void onRequestShowMessage(EMVTransController controller, String title, String msg, boolean isConfirm, int timeOut);

    /**
     * It will be triggered when the EMV kernel requests a language selection
     *
     * @param controller Emv Transaction controller{@link EMVTransController#setSelectedLanguage(String)}
     * @param language   language information
     */
    public void onRequestSelectLanguage(EMVTransController controller, String[] language);

    /**
     * It will be triggered After the application selection<p>
     * User can set data here to EMV kernel {@link EMVTransController#setEmvData(int, byte[])}  and then invoke the confirmInformation method to continue(true) or cancel(false) the EMV transaction.
     *
     * @param controller Emv Transaction controller{@link EMVTransController#confirmInformation(boolean)}
     * @throws Exception
     */
    public void onRequestConfirmFinalAppSelection(EMVTransController controller);
}
