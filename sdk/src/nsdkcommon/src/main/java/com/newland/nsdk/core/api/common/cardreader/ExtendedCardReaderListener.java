package com.newland.nsdk.core.api.common.cardreader;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.card.contact.ContactCardSlot;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardInfo;
import com.newland.nsdk.core.api.common.card.contactless.ContactlessCardType;
import com.newland.nsdk.core.api.common.card.magcard.MagCardInfo;

public interface ExtendedCardReaderListener extends CardReaderListener{

    /**
     * Invoked when searched more than one card or searched card except in card slot IC1.
     */
    void onFindContactCards(ContactCardSlot[] cardSlots);

}
