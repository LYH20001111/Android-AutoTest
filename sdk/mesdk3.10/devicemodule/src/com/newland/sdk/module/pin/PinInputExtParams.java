package com.newland.sdk.module.pin;

/**
 * Created by youjf on 2019/7/26 15:44
 */
public class PinInputExtParams {
    /**
     * pin block mode {@link PinBlockMode}
     */
    private PinBlockMode mPinBlockMode;
    /**
     * account input type
     */
    private AccountInputType acctInputType;

    /**
     * the max length of password
     */
    private int inputMaxLen;

    /**
     * the length range of the password
     */
    private byte[] pwdLengthRange;

    /**
     * Configure password keyboard
     */
    private DefaultLayout mDefaultLayout;

    private PinConfirmType pinConfirmType=PinConfirmType.ENABLE_ENTER;//是否需要按确认键，还是达到最大位数直接确认结束



    /**
     * dukpt aes derivate usage
     */
    private DukptDerivateUsage mDukptDerivateUsage;

    /**
     * dukpt aes derivate key length
     */
    private int derivateKeyLen;


    public PinInputExtParams() {
        this.mDefaultLayout = null;
    }

    public PinInputExtParams(DefaultLayout defaultLayout) {
        this.mDefaultLayout = defaultLayout;
    }

    /**
     * get the inner pin layout entity.
     *
     * @return
     */
    public DefaultLayout getDefaultLayout() {
        return mDefaultLayout;
    }

    /**
     * set the inner pin layout entity.
     *
     * @param defaultLayout
     */
    public void setDefaultLayout(DefaultLayout defaultLayout) {
        this.mDefaultLayout = defaultLayout;
    }

    /**
     * get account input type
     *
     * @return {@linkAccountInputType}
     */
    public AccountInputType getAcctInputType() {
        return acctInputType;
    }

    /**
     * set account input type
     *
     * @param acctInputType account input type{@linkAccountInputType}
     */
    public void setAcctInputType(AccountInputType acctInputType) {
        this.acctInputType = acctInputType;
    }


    /**
     * get the max length of password
     *
     * @return
     */
    public int getInputMaxLen() {
        return inputMaxLen;
    }

    /**
     * set the max length of password,
     *
     * @param inputMaxLen the max length of password,it can be 4-12.
     */
    public void setInputMaxLen(int inputMaxLen) {
        this.inputMaxLen = inputMaxLen;
    }

    /**
     * get the password length range
     *
     * @return
     */
    public byte[] getPwdLengthRange() {
        return pwdLengthRange;
    }

    /**
     * (Optional) permissible input pin length set.<p>
     * For example, if the permissible input is 4-bit, 6-bit and 8-bit pins<p>
     * pwdLengthRange is [0x04,0x06,0x08]<p>
     * if the pwdLengthRange is null,the password length can be 0,4,5,6,7,8,9,10,11,12
     *
     * @param pwdLengthRange
     */
    public void setPwdLengthRange(byte[] pwdLengthRange) {
        this.pwdLengthRange = pwdLengthRange;
    }

    /**
     *
     * @return
     */
    public PinBlockMode getPinBlockMode() {
        return mPinBlockMode;
    }

    /**
     *
     * @param pinBlockMode
     */
    public void setPinBlockMode(PinBlockMode pinBlockMode) {
        mPinBlockMode = pinBlockMode;
    }

    public PinConfirmType getPinConfirmType() {
        return pinConfirmType;
    }

    public void setPinConfirmType(PinConfirmType pinConfirmType) {
        this.pinConfirmType = pinConfirmType;
    }

    /**
     *  set dukpt aes derivate usage
     * @param mDukptDerivateUsage {@link DukptDerivateUsage}
     */
    public void setDukptDerivateUsage(DukptDerivateUsage mDukptDerivateUsage) {
        this.mDukptDerivateUsage = mDukptDerivateUsage;
    }

    public DukptDerivateUsage getDukptDerivateUsage() {
        return mDukptDerivateUsage;
    }

    public int getDerivateKeyLen() {
        return derivateKeyLen;
    }

    /**
     * only for dukpt aes
     * @param derivateKeyLen
     */
    public void setDerivateKeyLen(int derivateKeyLen) {
        this.derivateKeyLen = derivateKeyLen;
    }
}
