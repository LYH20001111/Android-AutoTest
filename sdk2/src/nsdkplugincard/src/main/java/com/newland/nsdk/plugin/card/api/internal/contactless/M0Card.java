package com.newland.nsdk.plugin.card.api.internal.contactless;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.internal.card.contactless.ContactlessCard;

/**
 * Provides M0 card operations.
 * <p>Note:This module is deprecated, if you want to perform mifare related interfaces, please use Newland RFIC module instead.</p>
 * <p>How to create a M0Card instance:</p>
 * <pre>
 *     M1Card m1Card = new M0CardImpl()
 * </pre>
 * @deprecated Replaced by "MifareUltraLightCard" in Newland NSDK RFIC library.
 */
public interface M0Card extends ContactlessCard {
    /**
     * Authenticates M0 card after the card is activated and before reading/writing the card.
     *
     * @param key <b>[Required]</b> Authentication key, the key is 16 bytes.
     * @throws NSDKException
     * @deprecated Replaced by "MifareUltraLightCard.authenticate()" in Newland NSDK RFIC library.
     */
    void authenticate(byte[] key) throws NSDKException;

    /**
     * Reads data from the specified block of M0 card.
     *
     * @param blockNo <b>[Required]</b> Block index, value range: [0-255].
     * @return Data of the block, 16 bytes.
     * @throws NSDKException
     * @deprecated Replaced by "MifareUltraLightCard.read()" in Newland NSDK RFIC library.
     */
    byte[] readBlockData(byte blockNo) throws NSDKException;

    /**
     * Writes data to the specified block of M0 card.
     *
     * <p>Example:</p>
     * <pre>
     *     int blockNo = 4;
     *     byte[] data = new byte[]{0x01, 0x02, 0x03, 0x04};
     *     try{
     *         m1Card.writeBlockData(blockNo, data);
     *     } catch(NSDKException e) {
     *         // Handle the exception.
     *     }
     * </pre>
     *
     * @param blockNo <b>[Required]</b> Block index, value range: [0-255].
     * @param data    <b>[Required]</b> Data to write. Data length can be 4 or 16, but actually only 4 bytes will be taken.
     * @throws NSDKException
     * @deprecated Replaced by "MifareUltraLightCard.write()" in Newland NSDK RFIC library.
     */
    void writeBlockData(byte blockNo, byte[] data) throws NSDKException;
}
