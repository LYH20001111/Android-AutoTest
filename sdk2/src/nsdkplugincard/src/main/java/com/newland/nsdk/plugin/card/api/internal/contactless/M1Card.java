package com.newland.nsdk.plugin.card.api.internal.contactless;

import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.internal.card.contactless.ContactlessCard;
import com.newland.nsdk.plugin.card.api.common.contactless.ContactlessKeyMode;

/**
 * Provides M1 card operations.
 * <p>Note:This module is deprecated, if you want to perform mifare related interfaces, please use Newland RFIC module instead.</p>
 * <p>How to create a M1Card instance:</p>
 * <pre>
 *     M1Card m1Card = new M1CardImpl()
 * </pre>
 * @deprecated Replaced by "MifareClassicCard" in Newland NSDK RFIC library.
 */

public interface M1Card extends ContactlessCard {
    /**
     * Authenticates M1 card with the specified key after the card is activated and before reading/writing the card.
     *
     * <p>Example:</p>
     * <pre>
     *     ActivationResult result;
     *     try{
     *         result = m1Card.activate();
     *     } catch(NSDKException e) {
     *         // Handle the exception.
     *     }
     *
     *     ContactlessKeyMode keyMode = ContactlessKeyMode.KEYA_0X00;
     *     byte[] uid = result.getUID();
     *     int blockNo = 1;
     *     byte[] key = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
     *
     *     try {
     *         m1Card.m1Authenticate(keyMode, uid, blockNo, key);
     *     } catch (NSDKException e) {
     *         // Handle the exception.
     *     }
     * </pre>
     *
     * @param keyMode <b>[kRequired]</b> Key Mode. See {@link ContactlessKeyMode}
     * @param uid     <b>[Required]</b> Card serial number, 4 bytes of UID. It is returned when activating the card, see {@link ActivationResult#getUID()}.
     * @param blockNo <b>[Required]</b> The block to be authenticated, value range: [0-255].
     * @param key     <b>[Required]</b> The key used to authenticate, 6 bytes.
     * @throws NSDKException
     * @deprecated Replaced by "MifareClassicCard.authenticate()" in Newland NSDK RFIC library.
     */
    void authenticate(ContactlessKeyMode keyMode, byte[] uid, byte blockNo, byte[] key) throws NSDKException;

    /**
     * Reads data from the specified block of M1 card after the card is authenticated.
     *
     * @param blockNo <b>[Required]</b> Block index, value range: [0-255].
     * @return Block data.
     * @throws NSDKException
     * @deprecated Replaced by "MifareClassicCard.readBlockData()" in Newland NSDK RFIC library.
     */
    byte[] readBlockData(byte blockNo) throws NSDKException;

    /**
     * Writes data to the specified block of M1 card after the card is authenticated.
     *
     * <p>Example:</p>
     * <pre>
     *     int blockNo = 1;
     *     byte[] data = ISOUtils.hex2byte("01000000FEFFFFFF0100000001FE01FE");
     *
     *     try {
     *         m1Card.writeBlockData(blockNo, data);
     *     } catch (NSDKException e) {
     *         // Handle the exception.
     *     }
     * </pre>
     *
     * @param blockNo <b>[Required]</b> Block index, value range: [0-255].
     * @param data    <b>[Required]</b> Data to write.
     * @throws NSDKException
     * @deprecated Replaced by "MifareClassicCard.writeBlockData()" in Newland NSDK RFIC library.
     */
    void writeBlockData(byte blockNo, byte[] data) throws NSDKException;

    /**
     * Increment operation of M1 Card.
     *
     * <p>Example:</p>
     * <pre>
     *     int blockNo = 1;
     *     byte[] data = ISOUtils.hex2byte("01000000");
     *
     *     try {
     *         m1Card.increment(blockNo, data);
     *     } catch (NSDKException e) {
     *         // Handle the exception.
     *     }
     * </pre>
     *
     * @param blockNo <b>[Required]</b> Block index, value range: [0-255].
     * @param data    <b>[Required]</b> Value that will be added to the block, 4 bytes data.
     * @throws NSDKException
     * @deprecated Replaced by "MifareClassicCard.increment()" in Newland NSDK RFIC library.
     */
    void increment(byte blockNo, byte[] data) throws NSDKException;

    /**
     * Decrement operation of M1 Card.
     *
     * <p>Example:</p>
     * <pre>
     *     int blockNo = 1;
     *     byte[] data = ISOUtils.hex2byte("01000000");
     *
     *     try {
     *         m1Card.decrement(blockNo, data);
     *     } catch (NSDKException e) {
     *         // Handle the exception.
     *     }
     * </pre>
     *
     * @param blockNo <b>[Required]</b> Block index, value range: [0-255].
     * @param data    <b>[Required]</b> Value that will be minus from the block, 4 bytes data.
     * @throws NSDKException
     * @deprecated Replaced by "MifareClassicCard.decrement()" in Newland NSDK RFIC library.
     */
    void decrement(byte blockNo, byte[] data) throws NSDKException;

    /**
     * Transfers M1 card block after increment/decrement.
     *
     * @param blockNo <b>[Required]</b> Block to transfer, value range: [0-255].
     * @throws NSDKException
     * @deprecated Replaced by "MifareClassicCard.transfer()" in Newland NSDK RFIC library.
     */
    void transfer(byte blockNo) throws NSDKException;

    /**
     * Restores M1 card register to invalidate increment/decrement.
     *
     * @param blockNo <b>[Required]</b> Block to restore, value range: [0-255].
     * @throws NSDKException
     * @deprecated Replaced by "MifareClassicCard.restore()" in Newland NSDK RFIC library.
     */
    void restore(byte blockNo) throws NSDKException;
}
