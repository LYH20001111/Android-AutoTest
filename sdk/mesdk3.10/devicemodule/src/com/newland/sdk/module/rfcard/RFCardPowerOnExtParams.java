package com.newland.sdk.module.rfcard;

/**
 * <p>The extended params for RFCard power on.</p>
 * <p>You can power on a card by sak or power on cards by FelicaParams[].<p/>
 *
 * @author linsi
 * @since V3.10.01
 */
public class RFCardPowerOnExtParams {

    private byte sak = (byte) 0xFF;
    private FelicaParams[] felicaParams;

    /**
     * <p>Set the sak of RFCard.</p>
     * <p>According to the sak value of the card.</p>
     * <li>0x20-CPUCARD of A Card</li>
     * <li>0x08-S50 Card Card</li>
     * <li>0x18-S70 Card Card</li>
     * <li>0x28-Pro Card Card</li>
     * <li>0x00-M0 Card Card</li>
     *
     * @param sak The value can't be set 0xFF.
     */
    public void setSak(byte sak) {
        this.sak = sak;
    }

    /**
     * <p>Set the felica card params.</p>
     * <p>You can power on different felica types according to {@link FelicaParams#systemCode}</p>
     *
     * @param felicaParams
     */
    public void setFelicaParams(FelicaParams[] felicaParams) {
        this.felicaParams = felicaParams;
    }

    public byte getSak() {
        return sak;
    }

    public FelicaParams[] getFelicaParams() {
        return felicaParams;
    }
}
