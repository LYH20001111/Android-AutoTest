package com.newland.nsdk.core.api.external.cardemulator;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.exception.NSDKException;

public interface ExtCardEmulator extends Module {
    /**
     * Initializes the card emulator mode.
     * @throws NSDKException
     */
    void init() throws NSDKException;

    /**
     * Starts the card emulator procedure. After starting successfully, the device will emulate to be a card, which can be identified by the external device.
     * @param cardType  <b>[Required]</b> The card type to be emulated as.
     * @throws NSDKException
     */
    void start(EmulateCardType cardType) throws NSDKException;

    /**
     * Gets the emulated card status.
     * @param cardType  <b>[Required]</b> The emulated card type.
     * @return The status of the emulated card.
     * @throws NSDKException
     */
    EmulateCardStatus getStatus(EmulateCardType cardType) throws NSDKException;

    /**
     * Sets the emulated card configurations, like UID and memory size.
     * @param cardType <b>[Required]</b> The emulated card type, see {@link EmulateCardType}.
     * @param config   <b>[Required]</b> The configuration information to be set to the emulated card file, see {@link EmulateConfig}
     * @throws NSDKException
     */
    void setConfig(EmulateCardType cardType, EmulateConfig config) throws NSDKException;

    /**
     * Writes data into the target emulated card file.
     * @param fileType  <b>[Required]</b> The target file, see {@link EmulateFileType}.
     * @param data      <b>[Required]</b> The data to be written to the target file.
     * @throws NSDKException
     */
    void writeData(EmulateFileType fileType, byte[] data) throws NSDKException;

    /**
     * Reads data from the target emulated card file with expected read length.
     * @param fileType    <b>[Required]</b> The target file, see {@link EmulateFileType}.
     * @param readLength  <b>[Required]</b> The expected read length.
     * @return The data with expected length from the target file.
     * @throws NSDKException
     */
    byte[] readData(EmulateFileType fileType, int readLength) throws NSDKException;

    /**
     * Gets the target emulated card configurations.
     * @param cardType  <b>[Required]</b> The target emulated card type, see {@link EmulateCardType}.
     * @return The configurations from the target card.
     * @throws NSDKException
     */
    EmulateConfig getConfig(EmulateCardType cardType) throws NSDKException;

    /**
     * Ends the performing card emulator procedure. After this, device will switch back to the reader mode.
     * @throws NSDKException
     */
    void finish() throws NSDKException;

    /**
     * Gets the related event status.
     * @param eventType  <b>[Required]</b> The related event to be checked status.
     * @return The status of the related event.
     * @throws NSDKException
     */
    byte[] getEvent(EmulateEventType eventType) throws NSDKException;

}
