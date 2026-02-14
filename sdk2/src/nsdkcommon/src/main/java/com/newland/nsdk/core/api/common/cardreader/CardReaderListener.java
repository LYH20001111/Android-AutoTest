package com.newland.nsdk.core.api.common.cardreader;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardInfo;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardType;
import com.newland.nsdk.core.api.common.card.magcard.MagCardInfo;

/**
 * A listener used to monitor the result of card searching.
 */
public interface CardReaderListener {
    /**
     * Invoked when timeout.
     */
    void onTimeout();

    /**
     * Invoked when card searching is cancelled.
     */
    void onCancel();

    /**
     * Invoked when error occurs during card searching.
     *
     * @param errorCode Error code. See {@link ErrorCode}.
     * @param message   Error message
     */
    void onError(int errorCode, String message);

    /**
     * Invoked when magnetic stripe card is swiped.
     *
     * @param magCardInfo Magnetic card info when it is swiped successfully. See {@link MagCardInfo}
     */
    void onFindMagCard(MagCardInfo magCardInfo);

    /**
     * Invoked when only found  contact card is inserted in card slot IC1.
     */
    void onFindContactCard();

    /**
     * Invoked when contactless card is tapped.
     *
     * @param cardType Card type of tapped card, see {@link ContactlessCardType}.
     * @param cardInfo Card info of tapped card, see {@link ContactlessCardInfo}.
     */
    void onFindContactlessCard(ContactlessCardType cardType, ContactlessCardInfo cardInfo);
}
