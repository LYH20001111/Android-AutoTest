package com.newland.sdk.module.pin;

/**
 * Author by wuhh, Date on 2020/3/4.
 */
public enum PinBlockMode {
    /**
     * iso 9564 0 pin block mode.<p>
     * Encrypt with main account. Complement password with 'F'.
     */
    ISO9564_FORMAT_0(3),
    /**
     * iso 9564 1 pin block mode.<p>
     * Encrypt without main account. Complement password with radom number.
     */
    ISO9564_FORMAT_1(4),
    /**
     * iso 9564 2 pin block mode.<p>
     * Encrypt without main account. Complement password with 'F'.
     */
    ISO9564_FORMAT_2(5),
    /**
     * iso 9564 3 pin block mode.<p>
     * Encrypt with main account. Complement password with radom number.
     */
    ISO9564_FORMAT_3(6),
    /**
     * iso 9564 4 pin block mode.<p>
     * The 128-bit plain text PIN field is enciphered with key K and
     * the resulting intermediate block A is added modulo-2 (XOR'd) to the 128-bit plain text PAN field.
     * The resulting intermediate block B is enciphered with the same key K yielding the 128-bit enciphered PIN block.
     */
    ISO9564_FORMAT_4(12),

    /**
     * Do not use the main account, the password is insufficient to fill 'F'.
     */
    UNIONPAY_SM4_1(7),
    /**
     * Fill in method 1 with the main account, and fill in 'F' if the password is insufficient.
     */
    UNIONPAY_SM4_2(8);

    private int code;
    PinBlockMode(int code){
        this.code = code;
    }
    public int getCode(){
        return code;
    }
}
