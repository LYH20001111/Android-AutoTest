package com.newland.nsdk.plugin.card.api.internal.contactless;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.internal.card.contactless.ContactlessCard;

/**
 * Provides Felica card operations.
 *
 * <p>How to create a FelicaCard instance:</p>
 * <pre>
 *     FelicaCard felicaCard = new FelicaCardImpl()
 * </pre>
 */
public interface FelicaCard extends ContactlessCard {
    /**
     * Executes APDU command with Felica card.
     *
     * <ul>Note:
     * <li>No need to activate Felica card.</li>
     * <li>Deactivate card to close RF after communication is finished.</li>
     * </ul>
     *
     * @param command <b>[Required]</b> APDU command data.
     * @return APDU response data.
     * @throws NSDKException
     */
    byte[] transmit(byte[] command) throws NSDKException;

    /**
     * Executes APDU command with Felica card with assigned timeout.
     *
     * <ul>Note:
     * <li>This interface shall be performed after getting IDmPMm by "openCardReader" or "polling".</li>
     * <li>Timeout is only available in this time, if input 0 means using driver default communication timeout.</li>
     * <li>No need to activate Felica card.</li>
     * <li>Deactivate card to close RF after communication is finished.</li>
     * </ul>
     *
     * @param command  <b>[Required]</b> APDU command data. The command length shall be more than 10 bytes.
     * @param timeout  <b>[Required]</b> APDU timeout.
     * @return APDU response data.
     * @throws NSDKException
     */
    byte[] transmit(byte[] command, int timeout) throws NSDKException;

    /**
     * Executes APDU command with Felica card with assigned timeout. If timeout error occurred and the retryTime is not 0, it will retry the APDU command with the assigned timeout.
     *
     * <ul>Note:
     * <li>This interface shall be performed after getting IDmPMm by "openCardReader" or "polling".</li>
     * <li>Timeout is only available in this time, if input 0 means using driver default communication timeout.</li>
     * <li>No need to activate Felica card.</li>
     * <li>Deactivate card to close RF after communication is finished.</li>
     * </ul>
     *
     * @param command     <b>[Required]</b> APDU command data. The command length shall be more than 10 bytes.
     * @param timeout     <b>[Required]</b> APDU timeout.
     * @param retryTimes  <b>[Required]</b> Retry times. It shall be >=0.
     * @return APDU response data.
     * @throws NSDKException
     */
    byte[] transmit(byte[] command, int timeout, int retryTimes) throws NSDKException;

    /**
     * Polling Felica card.
     * @param systemCode    <b>[Required]</b> The system code, 2 bytes.
     * @param requestCode   <b>[Required]</b> Designation of request data as follow, which shall be 1 byte.
     * @param timeslot      <b>[Required]</b> Designation of maximum number of slots possible to respond. It can only be 00h, 01h, 03h, 07h, and 0Fh.
     * @return The response data of polling, which consist of (01h + IDmPMm(16 bytes) + request data).
     * @throws NSDKException
     */
    byte[] polling(byte[] systemCode, byte requestCode, byte timeslot) throws NSDKException;

    /**
     * Polling Felica card within timeout.
     * @param systemCode   <b>[Required]</b> The system code, 2 bytes.
     * @param requestCode  <b>[Required]</b> Designation of request data as follow, which shall be 1 byte.
     * @param timeslot     <b>[Required]</b> Designation of maximum number of slots possible to respond. It can only be 00h, 01h, 03h, 07h, and 0Fh.
     * @param timeout      <b>[Required]</b> The whole polling timeout. Unit: ms, value range:[0, 5000]. Timeout is only available in this time, if input 0 means using driver default communication timeout.
     * @return The response data of polling, which consist of (01h + IDmPMm(16 bytes) + request data).
     * @throws NSDKException
     */
    byte[] polling(byte[] systemCode, byte requestCode, byte timeslot, int timeout) throws NSDKException;
}
