package com.newland.nsdk.plugin.card.api.external.contactless;


import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.external.card.contactless.ExtContactlessCard;

/**
 * Provides Felica card operations.
 *
 * <p>How to create an ExtFelicaCard instance:</p>
 * <pre>
 *     ExtFelicaCard extFelicaCard = new ExtFelicaCardImpl()
 * </pre>
 */
public interface ExtFelicaCard extends ExtContactlessCard {
    /**
     * Executes APDU command with Felica card after the card is detected.
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
}
