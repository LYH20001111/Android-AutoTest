package com.newland.nsdk.core.api.common.card.contact;


/**
 * Contact card type.
 */
public enum ContactCardType {
    /**
     * CPU card.
     */
    CPU(0x00);
//    /**
//     * SLE44X2 card.
//     */
//    SLE44X2(0x06),
//    /**
//     * SLE44X8 card.
//     */
//    SLE44X8(0x07),
//    /**
//     * AT88SC102 card.
//     */
//    AT88SC102(0x08),
//    /**
//     * AT88SC1604 card.
//     */
//    AT88SC1604(0x09),
//    /**
//     * AT88SC1608 card.
//     */
//    AT88SC1608(0x0a),
//    /**
//     * ISO7816 card.
//     */
//    ISO7816(0x0b),
//    /**
//     * AT88SC153 card.
//     */
//    AT88SC153(0x0c),
//    /**
//     * AT24C01 card.
//     */
//    AT24C01(0x0d),
//    /**
//     * AT24C02 card.
//     */
//    AT24C02(0x0e),
//    /**
//     * AT24C04 card.
//     */
//    AT24C04(0x0f),
//    /**
//     * AT24C08 card.
//     */
//    AT24C08(0x10),
//    /**
//     * AT24C16 card.
//     */
//    AT24C16(0x11),
//    /**
//     * AT24C32 card.
//     */
//    AT24C32(0x12),
//    /**
//     * AT24C64 card.
//     */
//    AT24C64(0x13);

    private int code;

    ContactCardType(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
