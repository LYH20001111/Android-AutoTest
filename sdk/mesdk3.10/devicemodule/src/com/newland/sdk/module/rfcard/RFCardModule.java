package com.newland.sdk.module.rfcard;

import android.support.annotation.Nullable;

import com.newland.sdk.mtype.Module;

/**
 * RFCard related functions
 *
 * @author linsi
 * @since V3.10.01
 */
public interface RFCardModule extends Module {
    /**
     * RFCard power on
     *
     * @param rfCardType       Card types{@link RFCardType#ACARD}
     * @param timeout          <p>Timeout(s)</p>
     *                         <p>if the value is 0x0000, it means search for the card once and return immediately.</p>
     * @param powerOnExtParams The extended params for RFCard power on.{@link RFCardPowerOnExtParams}
     * @return The result of RFCard power on{@link RFResult}.
     * @since V3.10.01
     */
    public RFResult powerOn(RFCardType[] rfCardType, int timeout, @Nullable RFCardPowerOnExtParams powerOnExtParams);

    /**
     * RFCard power off
     * @return The result of power off
     * @since V3.10.01
     */
    public boolean powerOff();

    /**
     * <p>Judge a contactless card in the induction zone.</p>
     *
     * @return True: the card is inducted.False: the card is not inducted.
     * @since V3.10.01
     */
    public boolean isCardExist();

    /**
     * The communication of the A-Card or B-Card type of RFCard.
     *
     * @param command Command data
     * @param timeout Timeout(ms)
     * @return resp   Return response data.
     * @since V3.10.01
     */
    public byte[] transmit(byte[] command, long timeout);

    /**
     * The communication of the Felica-Card type of RFCard.
     *
     * @param command Command data
     * @param timeout Timeout(ms)
     * @return resp   Return response data.
     * @since V3.10.01
     */
    public byte[] felicaTransmit(byte[] command, long timeout);

    /**
     * M0 Card authentication
     *
     * @param command Command data
     * @return The result of M0 card authenticate
     * @since V3.10.01
     */
    public boolean m0Authenticate(byte[] command);

    /**
     * Read the block data of M0 Card.
     *
     * @param blockNo The block index
     * @return
     * @since V3.10.01
     */
    public byte[] m0ReadBlockData(int blockNo);

    /**
     * Write the block data of M0 Card.
     *
     * @param blockNo Block index
     * @param data    Block data
     * @return The result of M0 card write block
     * @since V3.10.01
     */
    public boolean m0WriteBlockData(int blockNo, byte[] data);

    /**
     * Use the external key for M1 Card authentication
     *
     * @param rfKeyMode Key Mode{@link RFKeyMode#KEYA_0X00}
     * @param snr       Card serial number.it come from the result of power on{@link RFResult#getSNR()}.
     * @param blockNo   Number of block to be authenticated
     * @param key       The external key
     * @return The result of M1 card authenticate
     * @since V3.10.01
     */

    public boolean m1Authenticate(RFKeyMode rfKeyMode, byte[] snr, int blockNo, byte[] key);

    /**
     * Read the block data of M1 Card.
     *
     * @param blockNo Block index
     * @return
     * @since V3.10.01
     */
    public byte[] m1ReadBlockData(int blockNo);

    /**
     * Write the block data of M1 Card.
     *
     * @param blockNo Block index
     * @param data    Block data
     * @return The result of M1 card write block
     * @since V3.10.01
     */
    public boolean m1WriteBlockData(int blockNo, byte[] data);

    /**
     * Increment operation of M1 Card.
     *
     * @param blockNo Block index
     * @param data    Value
     * @return The result of M1 card Increment
     * @since V3.10.01
     */
    public boolean m1Increment(int blockNo, byte[] data);

    /**
     * Decrement operation of M1 Card.
     *
     * @param blockNo Block index
     * @param data    Value
     * @return The result of M1 card decrement
     * @since V3.10.01
     */

    public boolean m1Decrement(int blockNo, byte[] data);

    /**
     * get RFCard ATS<p>
     * @since 3.10.24
     * @return
     */
    public byte[] getCardATS();

    /**
     * Pass custom data through to the card.
     * communicate with the card according to the card protocol
     * @param sendData
     * @param timeOut
     * @return
     */
    public byte[] communication(byte[] sendData,int timeOut);

    /**
     * Set RF mode before using {@link #powerOn} ()} (Optional).
     *
     * @param rfCardMode The RF card mode to configure.
     *                   When set to {@link RFCardMode#RING_M1}, 
     *                   Improve the priority of searching for ring M1 cards.
     *                   Valid modes: {@link RFCardMode}
     *                   The mode will only reset to default mode after a terminal reboot.
     */
    public boolean setRFMode(RFCardMode rfCardMode);
}
