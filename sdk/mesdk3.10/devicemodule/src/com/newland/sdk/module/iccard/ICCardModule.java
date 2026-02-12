package com.newland.sdk.module.iccard;

import com.newland.sdk.mtype.Module;

import java.util.Map;

/**
 * ICCard related functions
 *
 * @author linsi
 * @since V3.10.01
 */
public interface ICCardModule extends Module {

    /**
     * Get the current state of all IC card slots
     *
     * @return Current IC card slot state
     * @since V3.10.01
     */
    public Map<ICCardSlot, ICCardSlotState> checkSlotsState();

    /**
     * Power on IC card in specified card slot
     *
     * @param icCardSlot Card slot {@link ICCardSlot#IC1}
     * @param icCardType IC card type {@link ICCardType#CPUCARD}
     * @return ATR
     * @since V3.10.01
     */
    public byte[] powerOn(ICCardSlot icCardSlot, ICCardType icCardType);

    /**
     * Power off IC card in specified card slot
     *
     * @param icCardSlot IC card slot {@link ICCardSlot#IC1}
     * @param icCardType IC card type {@link ICCardType#CPUCARD}
     * @since V3.10.01
     */
    public void powerOff(ICCardSlot icCardSlot, ICCardType icCardType);

    /**
     * Initiate an IC card communication request
     *
     * @param icCardSlot IC card slot {@link ICCardSlot#IC1}
     * @param icCardType IC card type {@link ICCardType#CPUCARD}
     * @param command    Command data
     * @param timeout    Timeout (s)
     * @return resp   Return post-calling IC card response
     * @since V3.10.01
     */
    public byte[] transmit(ICCardSlot icCardSlot, ICCardType icCardType, byte[] command, int timeout);
}
