package com.newland.sdk.module.externalCardreader;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.newland.sdk.module.cardreader.CardReaderExtParams;
import com.newland.sdk.module.cardreader.CardReaderListener;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.mtype.Module;

import java.util.List;

public interface ExtCardReaderModule extends Module {

    /**
     * Initialize the pinpad port
     *
     * @param params Pinpad initialization extension parameters.
     * @since 3.10.20
     */
    public boolean init(@NonNull PinpadInitExtParams params);

    /**
     * Open the card reader by non-blocking way<p>
     *
     * @param cardTypes <p>Expected card reader listening type{@link CardType}</p>
     * @param timeout timeout(second)(0-255),
     * @param cardReaderListener Non-blocked listener{@link CardReaderListener}
     * @param cardReaderExtParams can set RF card type.TYPE-A or TYPE-B 0r TYPE-A||TYPE-B
     */
    public void openCardReader(@NonNull CardType[] cardTypes, @IntRange(from = 0, to =255)int timeout, @NonNull CardReaderListener cardReaderListener,CardReaderExtParams cardReaderExtParams);

    /**
     * Cancel the current card reading <p>
     **/
    public void cancelCardReader();
}
