package com.newland.nsdk.core.api.external.card.contact;

import com.newland.nsdk.core.api.common.crypto.AlgorithmParameters;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.keymanager.CipherMode;
import com.newland.nsdk.core.api.common.keymanager.Key;
import com.newland.nsdk.core.api.common.keymanager.KeyType;
import com.newland.nsdk.core.api.external.card.ExtAPDUOutput;

/**
 * Provides the ability to communicate with CPU card.
 *
 * <p>How to create a ExtCPUContactCard instance:</p>
 * <pre>
 *     // Contact card slot is required.
 *     // Note:
 *     // - For IC1 slot, you need to open card reader and wait the card to be inserted before you create the card instance.
 *     // - For SAM slot, card instance could be created directly.
 *     ExtCPUContactCard extCPUContactCard1 = new ExtCPUContactCardImpl(ContactCardSlot.IC1)
 *     ExtCPUContactCard extCPUContactCard2 = new ExtCPUContactCardImpl(ContactCardSlot.SAM1)
 * </pre>
 */
public interface ExtCPUContactCard extends ExtContactCard {
    /**
     * Performs APDU command after the card is activated.
     *
     * @param key                 <b>[Required]</b> Key used to encrypt the command data.
     *                            <ul>
     *                               <li>Set it to null or set key ID to 0 when the command data is clear.</li>
     *                               <li>When the command data is encrypted, the following are required:
     *                                   <ul>
     *                                       <li>Key index</li>
     *                                       <li>Key type, default value is {@link KeyType#DES}</li>
     *                                   </ul>
     *                               </li>
     *                            </ul>
     * @param algorithmParameters <b>[Optional]</b> Algorithm parameters, see {@link AlgorithmParameters}.
     *                            <ul>
     *                            <li>Default cipher mode: {@link CipherMode#ECB}</li>
     *                            <li>IV is required when cipher mode is {@link CipherMode#CBC}</li>
     *                            </ul>
     * @param command             <b>[Required]</b> Encrypted or clear APDU command data.
     * @return Encrypted APDU response data.
     * @throws NSDKException
     * @deprecated
     */
    ExtAPDUOutput performAPDU(Key key, AlgorithmParameters algorithmParameters, byte[] command) throws NSDKException;

    /**
     * Performs APDU command after the card is activated.
     *
     * @param key                 <b>[Required]</b> Key used to encrypt the command data.
     *                            <ul>
     *                               <li>Set it to null or set key ID to 0 when the command data is clear.</li>
     *                               <li>When the command data is encrypted, the following are required:
     *                                   <ul>
     *                                       <li>Key index</li>
     *                                       <li>Key type, default value is {@link KeyType#DES}</li>
     *                                   </ul>
     *                               </li>
     *                            </ul>
     * @param algorithmParameters <b>[Optional]</b> Algorithm parameters, see {@link AlgorithmParameters}.
     *                            <ul>
     *                            <li>Default cipher mode: {@link CipherMode#ECB}</li>
     *                            <li>IV is required when cipher mode is {@link CipherMode#CBC}</li>
     *                            </ul>
     * @param actualLen           <b>[Required]</b> The length of actual command data (without padding)
     * @param command             <b>[Required]</b> Encrypted or clear APDU command data.
     * @return Encrypted APDU response data.
     * @throws NSDKException
     */
    ExtAPDUOutput performAPDU(Key key, AlgorithmParameters algorithmParameters, int actualLen, byte[] command) throws NSDKException;
}
