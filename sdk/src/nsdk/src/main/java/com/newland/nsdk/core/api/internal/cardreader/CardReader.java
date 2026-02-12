package com.newland.nsdk.core.api.internal.cardreader;

import com.newland.nsdk.core.api.common.Module;
import com.newland.nsdk.core.api.common.card.contact.ContactCardSlot;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardType;
import com.newland.nsdk.core.api.common.cardreader.CardReaderListener;
import com.newland.nsdk.core.api.common.cardreader.CardReaderParameters;
import com.newland.nsdk.core.api.common.cardreader.CardType;
import com.newland.nsdk.core.api.common.cardreader.ExtendedCardReaderListener;
import com.newland.nsdk.core.api.common.exception.NSDKException;

/**
 * Provides the ability to search for mag card, contact card and contactless card at the same time.
 *
 * <p>How to get this module:</p>
 * <pre>
 *     CardReader cardReader = (CardReader)NSDKModuleManagerImpl.getInstance().getModule(ModuleType.CARD_READER);
 * </pre>
 */
public interface CardReader extends Module {
    /**
     * Opens card readers and waits for cards.
     *
     * <p>It will only return the first card that swiped/inserted/tapped and stop waiting for other cards.</p>
     *
     * <p>Example:</p>
     * <pre>
     *     CardType[] cardTypes = new CardType[]{CardType.CONTACT_CARD, CardType.CONTACTLESS_CARD, CardType.MAG_CARD};
     *     int timeout = 30;
     *     CardReaderParameters parameters = new CardReaderParameters();
     *     // If nothing is set to parameters, default values for parameters will be applied.
     *     // If you need to specify parameters, set as below according to your needs.
     *     parameters.setContactlessCardTypes(new ContactlessCardType[]{ContactlessCardType.TYPE_A, ContactlessCardType.TYPE_F});
     *     parameters.setVerifyTrack(false);
     *
     *     CardReaderListener cardReaderListener = new CardReaderListener(){
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
     *              CPUContactCard cpuContactCard = new CPUContactCardImpl(ContactCardSlot.IC1);
     *              // If you can power up the card successfully, that means you have newed a correct card instance for the inserted card.
     *              try {
     *                  cpuContactCard.powerUp();
     *              } catch(NSDKException e) {
     *                  // This could happen in two cases:
     *                  // 1. The inserted card is CPU card, but error occurred when powering up the card.
     *                  // 2. The card is not a CPU card.
     *              }
     *         }
     *
     *        {@code @Override}
     *         public void onFindContactlessCard(ContactlessCardType cardType, ContactlessCardInfo cardInfo) {
     *            // Contactless card tapped. New different contactless card instances according to the tapped card type and your needs.
     *            if (cardType == ContactlessCardType.TYPE_A) {
     *                CPUContactlessCard cpuContactlessCard = new CPUContactlessCardImpl();
     *                // If you can activate the card successfully, that means you have newed a correct card instance for the tapped card.
     *                try {
     *                    cpuContactlessCard.activate();
     *                } catch(NSDKException e) {
     *                    // This could happen in two cases:
     *                    // 1. The tapped card is CPU Type A card, but error occurred when activating the card.
     *                    // 2. The card is not a CPU Type A card.
     *                }
     *            } else if (cardType == ContactlessCardType.TYPE_F) {
     *                // Handle card info
     *            }
     *         }
     *     };
     *
     *     try{
     *         cardReader.openCardReader(cardTypes, timeout, parameters, cardReaderListener);
     *     } catch(NSDKException e) {
     *         // Handle the exception.
     *     }
     * </pre>
     *
     * @param cardTypes            <b>[Required]</b> Expected card types. See {@link CardType}
     * @param timeout              <b>[Required]</b> Timeout for waiting cards, shall be >0. Unit: seconds.
     * @param cardReaderParameters <b>[Optional]</b> Card reader parameters. See {@link CardReaderParameters}.
     *                             <ul>
     *                             <li>When contactless card is expected, target contactless card types are required. If no contactless card types set, it will search for type A and type B cards by default.</li>
     *                             <li>When mag card is expected:
     *                                 <ul>
     *                                 <li>It will verify track data by default.</li>
     *                                 <li>Default length of first clear part of masked PAN: 6.</li>
     *                                 <li>Default length of last clear part of masked PAN: 4.</li>
     *                                 </ul>
     *                             </li>
     *                             <li>When contact card in IC2 slot is expected, {@link CardReaderParameters#cardSlots} will include {@link ContactCardSlot#IC2}</li>
     *                             </ul>
     * @param cardReaderListener   <b>[Required]</b> Listener which will be called to give the result. See {@link CardReaderListener}. If {@link ContactCardSlot#IC2} is expected, this shall be {@link ExtendedCardReaderListener}.
     * @throws NSDKException
     */
    void openCardReader(CardType[] cardTypes, int timeout, CardReaderParameters cardReaderParameters, CardReaderListener cardReaderListener) throws NSDKException;

    /**
     * Cancels card reader.
     *
     * <p>If it is searching cards, this will trigger {@link CardReaderListener#onCancel()} callback of {@link CardReader#openCardReader} method. Otherwise, this will only close card reader.</p>
     *
     * @throws NSDKException
     */
    void cancelCardReader() throws NSDKException;

    /**
     * Gets the card type that obtained by {@link CardReader#openCardReader} method last time.
     *
     * <p>It is null if {@link CardReader#openCardReader} method has not been called.</p>
     *
     * @return Card type, see {@link CardType}
     * @throws NSDKException
     */
    CardType getLastReaderType() throws NSDKException;

    /**
     * Checks if there is a contactless card presented.
     *
     * @return If there is a contactless card presented.
     * @throws NSDKException
     */
    boolean isCardPresent() throws NSDKException;

    /**
     * Checks if there is a target contactless card presented.
     * @param cardTypes    <b>[Required]</b> The contactless card types to be checked, see {@link ContactlessCardType}.
     * @return If there is a target contactless card presented.
     * @throws NSDKException
     */
    boolean isCardPresent(ContactlessCardType[] cardTypes) throws NSDKException;

    /**
     * Checks if there is a card inserted in {@link ContactCardSlot#IC1}.
     *
     * @return If there is a card inserted.
     * @throws NSDKException
     * @deprecated This interface can be replaced by {@link CardReader#checkCardSlotStatus(ContactCardSlot)}.
     */
    boolean isCardInserted() throws NSDKException;

    /**
     * Checks if there is a card inserted in target card slot.
     * @param cardSlot    <b>[Required]</b> The target card slot to be checked, which could only be {@link ContactCardSlot#IC1} or {@link ContactCardSlot#IC2}.
     * @return If there is a card inserted in target card slot.
     * @throws NSDKException
     */
    boolean checkCardSlotStatus(ContactCardSlot cardSlot) throws NSDKException;

    /**
     * Initialize and open Rf.
     * @throws NSDKException
     */
    void openRf() throws NSDKException;

    /**
     * Close Rf.
     * @throws NSDKException
     */
    void closeRf() throws NSDKException;

//    /**
//     * Whether to display rfid logo or not.
//     * @param isDisplayed      <b>[Required]</b> Whether to display rfid logo.
//     * @throws NSDKException
//     */
//    void displayRfidLogo(boolean isDisplayed) throws NSDKException;
}
