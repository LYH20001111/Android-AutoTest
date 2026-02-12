package com.newland.sdk.module.cardreader;

import android.support.annotation.IntRange;
import android.support.annotation.NonNull;

import com.newland.sdk.mtype.Module;

/**
 * Device terminal card reader module<p>
 * @author youjf
 *
 * @since ver3.10.01
 */
public interface CardReaderModule extends Module {
    /**
     * Open the card reader by non-blocking way<p>
     *
     * @param cardTypes <p>Expected card reader listening type{@link CardType}</p>
     * @param timeout timeout(second)(0-255),
     * @param cardReaderListener Non-blocked listener{@link CardReaderListener}
     * @param cardReaderExternalParams <p>cardreader external params{@link CardReaderExtParams}</p>
     *                                 <p>e.g. opening the UnionPay card magnetic track cheking, setting card searching rule</p>
     * @since 3.10.01
     */
    public void openCardReader(@NonNull CardType[] cardTypes, @IntRange(from = 0, to =255)int timeout, @NonNull CardReaderListener cardReaderListener, CardReaderExtParams cardReaderExternalParams);

    /**
     * Cancel the current card reading <p>
     *
     * @since 3.10.01
     */
    public void cancelCardReader();

    /**
     * Get the card reader module type trigged last time<p>
     *
     * @return card reader module type ,include following type: <ol>
     * <li>{@link CardType#MSGCARD}</li>
     * <li>{@link CardType#ICCARD}</li>
     * <Li>{@link CardType#RFCARD}</li></ol>
     * @since 3.10.01
     */
    public CardType[]  getLastReaderTypes();

    public void setLastReaderTypes(CardType[] cardTypes);
}
