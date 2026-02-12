package com.newland.sdk.module.emv;

/**
 * @description: EMV callback monitor
 * @author: Lindan
 * @create: 2019/7/29
 * @since 3.10.01
 */
public interface EMVInterceptListener extends EMVControllerListener {

    /**
     * Whether to activate the onRequestSelectAccountType method {@link EMVControllerListener#onRequestSelectAccountType(EMVTransController, AccountType[])}.
     *
     * @return true:yes<p>
     * false:no<p>
     */
    public boolean activateAccountTypeSelectInterceptor();

    /**
     * Whether to activate the onRequestSelectAccountType method {@link EMVControllerListener#onRequestConfirmID(EMVTransController, IDCardType, String)}.
     * @return true:yes<p>
     * false:no<p>
     */
    public boolean activateCertConfirmInterceptor();

    /**
     * Whether the electronic cash confirmation event is intercepted or not
     *
     * @return true:When the card support electronic cash，app choose whether or not to use e-cash in the onRequestConfirmEC callback method<p>
     * false:sdk do the selection<p>
     */
    public boolean activateECSwitchInterceptor();

    /**
     * Whether to activate the {@link EMVInterceptListener#increaseTransactionCount()} method and get the Transaction count.
     *
     * @return true:yes<p>
     * false:no<p>
     */
    public boolean activateTransactionCountInterceptor();

    /**
     * Whether or not the message display event is intercepted
     *
     * @return true: app can processing prompt messages in the onRequestShowMessage callback method<p>
     * false: sdk processing prompt messages<p>
     */
    public boolean activateTransactionMessageInterceptor();

    /**
     * Whether  the language select is intercepted or not
     *
     * @return true：the language select is intercepted，app select language by onRequestSelectLanguage call-back；<p>
     * false: the language select is not intercepted
     */
    public boolean activateLanguageSelectInterceptor();

    /**
     * Increase the transaction count and return When the value of the IsTransactionCountInterceptor (){@link EMVInterceptListener#activateTransactionCountInterceptor()} method is true
     *
     * @return
     */
    public int increaseTransactionCount();
}
