package com.newland.sdk.module.cardreader;

import android.support.annotation.Nullable;

import com.newland.sdk.module.rfcard.RFCardType;
import com.newland.sdk.module.rfcard.RFCardType;

/**
 *  a listen callback for Cardreader
 * Created by youjf on 2019/7/23 10:31
 *
 */
public interface CardReaderListener {
    /**
     * open card reader timeout
     */
    public void onTimeout();
    /**
     * cancel the operation
     */
    public void onCancel();
    /**
     * open card reader failed
     * @param errorCode error code
     * @param message error message
     */
    public void onError(int errorCode, String message);

    /**
     * find magnetic stripe card
     * @param isSuccessful whether swipping magnetic card is successful or not
     */
    public void onFindMagCard(boolean isSuccessful);

    /**
     * find contact IC card
     */
    public void onFindICCard();

    /**
     * find Contactless card
     * @param rfCardType <p>The rfcard type. </p>
     *                   If the mothod of openCardReader uses SearchCardRule.RFCARD_QUICKLY,this value will return null.
     * @param rfCardInfo <p>The rf card info. </p>
     *                   If the mothod of openCardReader uses SearchCardRule.RFCARD_QUICKLY,this value will return null.
     */
    public void onFindRFCard(@Nullable RFCardType rfCardType, @Nullable RFCardInfo rfCardInfo);

}
