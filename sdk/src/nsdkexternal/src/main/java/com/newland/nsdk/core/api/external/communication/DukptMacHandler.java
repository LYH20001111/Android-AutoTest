package com.newland.nsdk.core.api.external.communication;

import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * This handler helps to handle DUKPT MAC verification.
 */
public interface DukptMacHandler {
    /**
     * Gets current KSN.
     *
     * @return Current KSN.
     * @throws NSDKException
     */
    byte[] getKsn() throws NSDKException;

    /**
     * Increases KSN.
     *
     * @throws NSDKException
     */
    void increaseKsn() throws NSDKException;

    /**
     * Generates MAC.
     *
     * @param data <b>[Required]</b> Data used to generate MAC.
     * @return MAC.
     * @throws NSDKException
     */
    byte[] generateMac(byte[] data) throws NSDKException;

    /**
     * Checks if response MAC is correct.
     *
     * @param responseData        <b>[Required]</b> Response data used to calculate MAC.
     * @param responseMac         <b>[Required]</b> Response MAC.
     * @param responseMessageType <b>[Required]</b> Response message type.
     * @throws NSDKException
     */
    void checkMac(byte[] responseData, byte[] responseMac, String responseMessageType) throws NSDKException;

    /**
     * Checks if response KSN is correct.
     *
     * @param responseKsn         <b>[Required]</b> Response KSN.
     * @param responseMessageType <b>[Required]</b> Response message type.
     * @throws NSDKException
     */
    void checkKsn(byte[] responseKsn, String responseMessageType) throws NSDKException;
}
