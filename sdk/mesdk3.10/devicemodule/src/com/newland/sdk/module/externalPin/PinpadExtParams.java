package com.newland.sdk.module.externalPin;

import com.newland.sdk.module.pin.AccountInputType;
import com.newland.sdk.module.pin.PinConfirmType;

/**
 * @description: Extra pinpad external parameters
 * @author: Lindan
 * @create: 2019/07/29
 */
public class PinpadExtParams {

    private AccountInputType acctInputType = AccountInputType.USE_ACCOUNT;
    private int inputMinLen = 0;
    private int inputMaxLen = 12;
    private byte[] pwdRange;
    private int encryMode = -1;
    private byte msgType;
    private boolean isUsePinKey;

    /*only for ME51*/
    private String firstLineMessage;
    private String secondLineMessage;
    private String thirdLineMessage;
    private String fourthLineMessage;
    private boolean isKeyPress;

    private InputMode inputMode = InputMode.ONLY_DIGIT;//V4海外指令集获取明文按键值使用
    private String message = "请输入密码"; // reserved.
    private boolean isNeedLoadKey = true;//是否需要每次重新装129的密钥密钥，针对V4海外指令集获取明文按键值使用
    private PinConfirmType pinConfirmType=PinConfirmType.ENABLE_ENTER;//是否需要按确认键，否的话，密码位数达到最大长度，自动返回

    private String messageEncode = "GB2312";

    private boolean cardInPinpad=false;

    public PinpadExtParams(AccountInputType acctInputType, int inputMinLen, int inputMaxLen) {
        this.acctInputType = acctInputType;
        this.inputMinLen = inputMinLen;
        this.inputMaxLen = inputMaxLen;
    }

    public PinpadExtParams(int inputMinLen, int inputMaxLen, byte[] pwdRange) {
        this.inputMinLen = inputMinLen;
        this.inputMaxLen = inputMaxLen;
        this.pwdRange = pwdRange;
    }

    public PinpadExtParams(int inputMinLen, int inputMaxLen) {
        this.inputMinLen = inputMinLen;
        this.inputMaxLen = inputMaxLen;
    }

    public PinpadExtParams() {
    }

    public byte[] getPwdRange() {
        return pwdRange;
    }

    /**
     * Get the account input type
     *
     * @return
     */
    public AccountInputType getAcctInputType() {
        return acctInputType;
    }

    /**
     * Set the account input type
     *
     * @param acctInputType
     */
    public void setAcctInputType(AccountInputType acctInputType) {
        this.acctInputType = acctInputType;
    }

    /**
     * Get the minimum password length
     *
     * @return
     */
    public int getInputMinLen() {
        return inputMinLen;
    }

    /**
     * Set the minimum password length(0-30)
     *
     * @param inputMinLen
     */
    public void setInputMinLen(int inputMinLen) {
        this.inputMinLen = inputMinLen;
    }

    /**
     * Get the maximum password length
     *
     * @return
     */
    public int getInputMaxLen() {
        return inputMaxLen;
    }

    /**
     * Set the maximum password length(0-30)
     *
     * @param inputMaxLen
     */
    public void setInputMaxLen(int inputMaxLen) {
        this.inputMaxLen = inputMaxLen;
    }

    /**
     * @return <p>SEC_PIN_ISO9564_0=3,encrypt with pan，pin padding with 'F' </p>
     * <p>SEC_PIN_ISO9564_1=4,<encrypt without pan，pin padding with radom data </p>
     * <p>SEC_PIN_ISO9564_2=5,<encrypt without pan，pin padding with 'F' </p>
     * <p>SEC_PIN_ISO9564_3=6,<encrypt with pan，pin padding with radom data </p>
     * <p>SEC_PIN_SM4_2=8,<filled with pan，pin padding with 'F' </p>
     * <p>SSEC_PIN_AES_FMT4=12，Aes </p>
     */
    public int getEncryMode() {
        return encryMode;
    }

    /**
     * @param encryMode <p>SEC_PIN_ISO9564_0=3,encrypt with pan，pin padding with 'F' </p>
     *                  <p>SEC_PIN_ISO9564_1=4,<encrypt without pan，pin padding with radom data </p>
     *                  <p>SEC_PIN_ISO9564_2=5,<encrypt without pan，pin padding with 'F' </p>
     *                  <p>SEC_PIN_ISO9564_3=6,<encrypt with pan，pin padding with radom data </p>
     *                  <p>SEC_PIN_SM4_2=8,<filled with pan ，pin padding with 'F' </p>
     *                  <p>SSEC_PIN_AES_FMT4=12，Aes </p>
     */
    public void setEncryMode(int encryMode) {
        this.encryMode = encryMode;
    }

    /**
     * get the tip message on Chinese SP100 pinpad
     * <p>0x30-show"请输入密码"，and have Voice prompt</p>
     * <p>0x31-show"请再输入密码"，and have Voice prompt</p>
     * <p>0x32-show"请输入密码"，and don't have Voice prompt</p>
     * <p>0x33-show"请再输入密码"，and don't haveVoice prompt</p>
     *
     * @return
     */
    public byte getMsgType() {
        return msgType;
    }

    /**
     * set the tip message on Chinese SP100 pinpad
     *
     * @param msgType <p>0x30-show"请输入密码"，and have Voice prompt</p>
     *                <p>0x31-show"请再输入密码"，and have Voice prompt</p>
     *                <p>0x32-show"请输入密码"，and don't have Voice prompt</p>
     *                <p>0x33-show"请再输入密码"，and don't haveVoice prompt</p>
     */
    public void setMsgType(byte msgType) {
        this.msgType = msgType;
    }

    public boolean getUsePinKey() {
        return isUsePinKey;
    }

    public void setUsePinKey(boolean usePinKey) {
        isUsePinKey = usePinKey;
    }

    public void setPwdRange(byte[] pwdRange) {
        this.pwdRange = pwdRange;
    }

    public String getFirstLineMessage() {
        return firstLineMessage;
    }

    public void setFirstLineMessage(String firstLineMessage) {
        this.firstLineMessage = firstLineMessage;
    }

    public String getSecondLineMessage() {
        return secondLineMessage;
    }

    public void setSecondLineMessage(String secondLineMessage) {
        this.secondLineMessage = secondLineMessage;
    }

    public String getThirdLineMessage() {
        return thirdLineMessage;
    }

    public void setThirdLineMessage(String thirdLineMessage) {
        this.thirdLineMessage = thirdLineMessage;
    }

    public String getFourthLineMessage() {
        return fourthLineMessage;
    }

    public void setFourthLineMessage(String fourthLineMessage) {
        this.fourthLineMessage = fourthLineMessage;
    }

    public boolean isKeyPress() {
        return isKeyPress;
    }

    public void setKeyPress(boolean keyPress) {
        isKeyPress = keyPress;
    }

    public InputMode getInputMode() {
        return inputMode;
    }

    /**
     * for external pinad
     * @param inputMode
     */
    public void setInputMode(InputMode inputMode) {
        this.inputMode = inputMode;
    }

    public String getMessage() {
        return message;
    }

    /**
     * set the tip message,for external pinad
     * @param message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * load key every time,for external pinad
     * @return
     */
    public boolean isNeedLoadKey() {
        return isNeedLoadKey;
    }

    public void setNeedLoadKey(boolean needLoadKey) {
        isNeedLoadKey = needLoadKey;
    }

    public PinConfirmType getPinConfirmType() {
        return pinConfirmType;
    }

    public void setPinConfirmType(PinConfirmType pinConfirmType) {
        this.pinConfirmType = pinConfirmType;
    }

    public String getMessageEncode() {
        return messageEncode;
    }

    public void setMessageEncode(String messageEncode) {
        this.messageEncode = messageEncode;
    }

    public boolean isCardInPinpad() {
        return cardInPinpad;
    }

    public void setCardInPinpad(boolean cardInPinpad) {
        this.cardInPinpad = cardInPinpad;
    }
}
