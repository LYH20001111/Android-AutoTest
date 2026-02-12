package com.newland.sdk.module.externalrfcard;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.light.LightColor;
import com.newland.sdk.module.light.LightState;
import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.rfcard.RFKeyMode;
import com.newland.sdk.module.rfcard.RFResult;

/**
 * @description: External RF card reader(SP10/SP100).
 * @author: Suyuming
 * @create: 2019/7/28
 */
public interface ExtRFCardModule {

    /**
     * Initialize the card reader.
     *
     * @param params extra parameters.
     * @return true if success, false if error.
     * @since 3.10.01
     */
    boolean init(@NonNull PinpadInitExtParams params);

    /**
     * Power on the external card reader.
     *
     * @param rfCardTypes The array of RF Card Type. {@link RFCardType}
     * @param timeout     timeout:s(if 0 means search once time)
     * @return RF Card Type {@link RFResult}
     * @since 3.10.01
     */
    RFResult powerOn(@NonNull RFCardType[] rfCardTypes, @IntRange(from = 0, to = 0xFFFF) int timeout);

    /**
     * A command APDU is sent by the reader to the card.
     *
     * @param command command APDUs.
     * @return response APDUs.
     * @since 3.10.01
     */
    byte[] transmit(@NonNull byte[] command);

    /**
     * Power off the external card reader.
     *
     * @return true if success, false if error.
     * @since 3.10.01
     */
    boolean powerOff();

    /**
     * Reset operation
     *
     * @since 3.10.01
     */
    void reset();

    /**
     * Read the block data
     *
     * @param blockNo Block number
     * @return Read result
     * @since 3.10.01
     */
    byte[] readBlockData(int blockNo);

    /**
     * Write the block data
     *
     * @param blockNo Block number
     * @param data    Block data
     * @return Write result {@code true} or {@code false}
     * @since 3.10.01
     */
    boolean writeBlockData(int blockNo, byte[] data);

    /**
     * Use the external key for authentication
     *
     * @param rfKeyMode Key Mode {@link RFKeyMode}
     * @param SNR       4 bytes
     * @param blockNo   (0-255) Number of block to be authenticated
     * @param key       External key
     * @return Authenticate result {@code true} or {@code false}
     * @since 3.10.01
     */
    boolean m1Authenticate(RFKeyMode rfKeyMode, byte[] SNR, @IntRange(from = 0, to = 255) int blockNo, byte[] key);

    /**
     * Increment operation.
     *
     * @param blockNo Block number
     * @param data    4 bytes value
     * @return Increment result {@code true} or {@code false}
     * @since 3.10.01
     */
    boolean m1Increment(int blockNo, byte[] data);

    /**
     * Decrement operation.
     *
     * @param blockNo Block number
     * @param data    4 bytes value
     * @return Decrement result {@code true} or {@code false}
     * @since 3.10.01
     */
    boolean m1Decrement(int blockNo, byte[] data);

    /**
     * Check the card whether exist or not.
     *
     * @return RFCard is exist
     * @since 3.10.01
     */
    boolean isCardExist();

    /**
     * Get the ATS.
     *
     * @return ats
     * @since 3.10.01
     */
    byte[] getCardATS();

    /**
     * Control indicator light.(non-blocking mode)
     *
     * @param lightColor The type of indicator light {@link LightColor#BLUE}
     * @param lightState The state of indicator light  {@link LightState#TURNON}
     * @return
     * @since 3.10.01
     */
    boolean operateLight(LightColor[] lightColor, LightState lightState);
}
