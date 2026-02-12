package com.newland.nsdk.core.api.external.card.contactless;

import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.external.card.ExtAPDUOutput;

/**
 * Provides the ability to communicate with CPU card.
 *
 * <p>How to create a ExtCPUContactlessCard instance:</p>
 * <pre>
 *     // Note: You need to open card reader and wait the card to be tapped before you create the card instance.
 *     ExtCPUContactlessCard extCPUContactlessCard = new ExtCPUContactlessCardImpl()
 * </pre>
 */
public interface ExtCPUContactlessCard extends ExtContactlessCard {
    /**
     * Performs APDU command after the card is activated.
     *
     * @param key                 <b>[Optional]</b> Key used to encrypt the command data.
     *                            <ul>
     *                               <li>Set it to null or set key ID to 0 when the command data is clear.</li>
     *                               <li>When the command data is encrypted, key ID [0-255] is required and key type is DES.</li>
     *                            </ul>
     * @param algorithmParameters <b>[Optional]</b> Algorithm parameters, see {@link AlgorithmParameters}.This parameter is reserved for further use.
     * @param command             <b>[Required]</b> Encrypted or clear APDU command data.
     * @return Encrypted APDU response data.
     * @throws NSDKException
     * @deprecated
     */
    ExtAPDUOutput performAPDU(Key key, AlgorithmParameters algorithmParameters, byte[] command) throws NSDKException;

    /**
     * Performs APDU command after the card is activated.
     *
     * @param key                 <b>[Optional]</b> Key used to encrypt the command data.
     *                            <ul>
     *                               <li>Set it to null or set key ID to 0 when the command data is clear.</li>
     *                               <li>When the command data is encrypted, key ID [0-255] is required and key type is DES.</li>
     *                            </ul>
     * @param algorithmParameters <b>[Optional]</b> Algorithm parameters, see {@link AlgorithmParameters}.This parameter is reserved for further use.
     * @param actualLen           <b>[Required]</b> The length of actual command data (without padding)
     * @param command             <b>[Required]</b> Encrypted or clear APDU command data.
     * @return Encrypted APDU response data.
     * @throws NSDKException
     */
    ExtAPDUOutput performAPDU(Key key, AlgorithmParameters algorithmParameters, int actualLen, byte[] command) throws NSDKException;
}
