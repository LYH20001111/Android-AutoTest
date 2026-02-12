package com.newland.nsdk.plugin.card.api.external.contactless;

import com.newland.nsdk.core.api.common.card.contactless.ActivationResult;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.external.card.contactless.ExtContactlessCard;
import com.newland.nsdk.plugin.card.api.common.contactless.ContactlessKeyMode;

/**
 * Provides M1 card operations.
 *
 * <p>How to create a ExtM1Card instance:</p>
 * <pre>
 *     ExtM1Card extM1Card = new ExtM1CardImpl()
 * </pre>
 */
public interface ExtM1Card extends ExtContactlessCard {
    /**
     * Authenticates M1 card with the specified key after the card is activated and before reading/writing the card..
     *
     * <p>Example:</p>
     * <pre>
     *     ActivationResult result;
     *     try{
     *         result = extM1Card.activate();
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
     *         extM1Card.m1Authenticate(keyMode, uid, blockNo, key);
     *     } catch (NSDKException e) {
     *         // Handle the exception.
     *     }
     * </pre>
     *
     * @param keyMode <b>[Required]</b> Key Mode. See {@link ContactlessKeyMode}
     * @param uid     <b>[Required]</b> Card serial number, 4 bytes of UID. It is returned when activating the card, see {@link ActivationResult#getUID()}.
     * @param blockNo <b>[Required]</b> The block to be authenticated, value range: [0-255].
     * @param key     <b>[Required]</b> The key used to authenticate, 6 bytes.
     * @throws NSDKException
     */
    void authenticate(ContactlessKeyMode keyMode, byte[] uid, byte blockNo, byte[] key) throws NSDKException;

    /**
     * Reads data from the specified block of M1 card after the block is authenticated.
     *
     * @param blockNo <b>[Required]</b> Block index, value range: [0-255].
     * @return Block data, 16 bytes.
     * @throws NSDKException
     */
    byte[] readBlockData(byte blockNo) throws NSDKException;

    /**
     * Writes data to the specified block of M1 card after the block is authenticated.
     *
     * <p>Example:</p>
     * <pre>
     *     int blockNo = 1;
     *     byte[] data = ISOUtils.hex2byte("01000000FEFFFFFF0100000001FE01FE");
     *
     *     try {
     *         extM1Card.writeBlockData(blockNo, data);
     *     } catch (NSDKException e) {
     *         // Handle the exception.
     *     }
     * </pre>
     *
     * @param blockNo <b>[Required]</b> Block index, value range: [0-255].
     * @param data    <b>[Required]</b> Data to write, 16 bytes.
     * @throws NSDKException
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
     *         extM1Card.increment(blockNo, data);
     *     } catch (NSDKException e) {
     *         // Handle the exception.
     *     }
     * </pre>
     *
     * @param blockNo <b>[Required]</b> Block index, value range: [0-255].
     * @param data    <b>[Required]</b> Value that will be added to the block, 4 bytes data.
     * @throws NSDKException
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
     *         extM1Card.decrement(blockNo, data);
     *     } catch (NSDKException e) {
     *         // Handle the exception.
     *     }
     * </pre>
     *
     * @param blockNo <b>[Required]</b> Block index, value range: [0-255].
     * @param data    <b>[Required]</b> Value that will be minus from the block, 4 bytes data.
     * @throws NSDKException
     */
    void decrement(byte blockNo, byte[] data) throws NSDKException;

    /**
     * Transfers M1 card block after increment/decrement.
     *
     * @param blockNo <b>[Required]</b> Block to transfer, value range: [0-255].
     * @throws NSDKException
     */
    void transfer(byte blockNo) throws NSDKException;

    /**
     * Restores M1 card register to invalidate increment/decrement.
     *
     * @param blockNo <b>[Required]</b> Block to restore, value range: [0-255].
     * @throws NSDKException
     */
    void restore(byte blockNo) throws NSDKException;
}
