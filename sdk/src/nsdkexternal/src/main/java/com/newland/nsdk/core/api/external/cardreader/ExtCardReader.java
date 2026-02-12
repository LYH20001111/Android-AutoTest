package com.newland.nsdk.core.api.external.cardreader;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.cardreader.CardReaderListener;
import com.newland.nsdk.core.api.common.cardreader.CardReaderParameters;
import com.newland.nsdk.core.api.common.cardreader.CardType;
import com.newland.nsdk.core.api.common.crypto.CipherType;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * <b>[External Module]</b> Provides the ability to search for mag card, contact card and contactless card at the same time.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     ExtCardReader extCardReader = (ExtCardReader)ExtNSDKModuleManagerImpl.getInstance().getModule(ModuleType.EXT_CARD_READER);
 * </pre>
 */
public interface ExtCardReader extends Module {
    /**
     * Opens card reader and waits for cards.
     *
     * <p>It will only return the first card that swiped/inserted/tapped and stop waiting for other cards.</p>
     *
     * <p>Example:</p>
     * <pre>
     *     CardType[] cardTypes = new CardType[]{CardType.CONTACT_CARD, CardType.CONTACTLESS_CARD, CardType.MAG_CARD};
     *     int timeout = 30;
     *     ExtCardReaderParameters parameters = new ExtCardReaderParameters();
     *     parameters.setContactlessCardTypes(new ContactlessCardType[]{ContactlessCardType.TYPE_A});
     *     // Set PAN key index if need to encrypt track data
     *     parameters.setPANKeyIndex((byte)2);
     *     parameters.setDisplayMessages(new String[]{"Please insert/tap/swipe card..."});
     *
     *     CardReaderListener cardReaderListener = new CardReaderListener() {
     *        {@code @Override}
     *         public void onTimeout() {
     *             // Handle timeout
     *         }
     *
     *        {@code @Override}
     *         public void onCancel() {
     *             // Handle cancel
     *         }
     *
     *        {@code @Override}
     *         public void onError(int code, String message) {
     *             // Handle error
     *         }
     *
     *        {@code @Override}
     *         public void onFindMagCard(MagCardInfo magCardInfo) {
     *             // Mag card swiped. Handle mag card info.
     *         }
     *
     *        {@code @Override}
     *         public void onFindContactCard() {
     *              // Contact card inserted. New different contact card instances according to your needs.
     *              // For example, new a CPU card:
     *              ExtCPUContactCard cpuContactCard = new ExtCPUContactCardImpl(ContactCardSlot.IC1);
     *              // If you can power up the card successfully, that means you have newed a correct card instance for the inserted card.
     *              try {
     *                  cpuContactCard.powerUp();
     *              } catch(NSDKException e) {
     *                  // This could happen in two cases:
     *                  // 1. The inserted card is CPU card, but error occurred when powering up the card.
     *                  // 2. The card is not CPU card.
     *              }
     *         }
     *
     *        {@code @Override}
     *         public void onFindContactlessCard(ContactlessCardType cardType, ContactlessCardInfo cardInfo) {
     *            // Contactless card tapped. New different contactless card instances according to the tapped card type and your needs.
     *            if (cardType == ContactlessCardType.TYPE_A) {
     *                ExtCPUContactlessCard cpuContactlessCard = new ExtCPUContactlessCardImpl();
     *                // If you can activate the card successfully, that means you have newed a correct card instance for the tapped card.
     *                try {
     *                    cpuContactlessCard.activate();
     *                } catch(NSDKException e) {
     *                    // This could happen in two cases:
     *                    // 1. The tapped card is CPU card, but error occurred when activating the card.
     *                    // 2. The card is not CPU card.
     *                }
     *            }
     *         }
     *     };
     *
     *     try{
     *         extCardReader.openCardReader(cardTypes, timeout, parameters, cardReaderListener);
     *     } catch (NSDKException e) {
     *         // Handle the exception
     *     }
     * </pre>
     *
     * @param cardTypes          <b>[Required]</b> Expected card types. See {@link CardType}
     * @param timeout            <b>[Required]</b> Timeout for waiting cards, shall be >0. Unit: second.
     * @param parameter          <b>[Optional]</b> Parameters for searching cards.
     *                           <ul>
     *                           <li>When contactless card is expected, target contactless card types are required. If no contactless card types set, it will search for type A and type B cards by default.</li>
     *                           <li>When mag card is expected:
     *                               <ul>
     *                               <li>It will verify track data by default.</li>
     *                               <li>Default length of first clear part of masked PAN: 6.</li>
     *                               <li>Default length of last clear part of masked PAN: 4.</li>
     *                               <li>More track encryption parameters can be set by using {@link ExtCardReaderParameters} and default cipher type is {@link CipherType#DES_ECB}.</li>
     *                               </ul>
     *                           </li>
     *                           </ul>
     * @param cardReaderListener <b>[Required]</b> Callback for returning card result. See {@link CardReaderListener}.
     * @throws NSDKException
     */
    void openCardReader(CardType[] cardTypes, int timeout, CardReaderParameters parameter, CardReaderListener cardReaderListener) throws NSDKException;

    /**
     * Cancels current card searching.
     *
     * @throws NSDKException
     */
    void cancelCardReader() throws NSDKException;

    /**
     * Checks if there is a contactless card presented.
     *
     * @return If there is a contactless card presented.
     * @throws NSDKException
     */
    boolean isCardPresent() throws NSDKException;

    /**
     * Check if there is a card inserted.
     *
     * @return If there is a card inserted.
     * @throws NSDKException
     */
    boolean isCardInserted() throws NSDKException;
}
