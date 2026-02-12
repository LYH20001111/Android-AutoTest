package com.newland.sdk.module.externaliccard;

import com.newland.sdk.module.externalPin.ExtPowerOnExtParams;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.iccard.ICCardType;

/**
 * @author youjf
 * @description
 * @date 2020/6/9
 * @since V3.10.20
 */
public interface ExtICCardModule {
    /**
     * init external pinpad
     * @param params {@link PinpadInitExtParams}
     * @return
     */
    public boolean init(PinpadInitExtParams params);

    /**
     * ic card power on
     * @return
     */
    public byte[] powerOn();

    /**
     * Set IC Card power on card type, before power on
     * @param cardType
     * @return
     */
    boolean setICCardType(ICCardType cardType, ExtPowerOnExtParams params);

    /**
     * IC card poweroff
     */
    public void powerOff();

    /**
     * @param reqApdu
     * @param transmitExtParams transmit params
     * @return
     */
    public byte[] transmit(byte[] reqApdu,TransmitExtParams transmitExtParams);

    /**
     * is IC card in card slot
     * @return
     */
    public boolean isCardIn();
}
