package com.newland.sdk.module.swiper;

import android.support.annotation.IntRange;

import com.newland.sdk.mtype.Module;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.module.cardreader.CardReaderModule;
import com.newland.sdk.module.pin.KeyManagement;

/**
 * Magnetic stripe card reading interface<p>
 * When {@link CardReaderModule#openCardReader} is called, if the return type is {@link ModuleType#MAGCARDREADER}, the method of this interface may be called to read the magnetic track data.<p>
 *
 *
 * @since ver3.10.01
 */
public interface MagStripeCardModule extends Module{

	/**
	 *  After security verification, use plain text to return the swiping result. <p>
	 *  The return magnetic information of the card swiping result is of plain text form. <p>
	 *
	 * @param readModel  Magnetic track reading model
	 *
	 * @return swiping result
	 * @since ver3.10.01
	 */
	public SwipResult readPlainResult(SwiperReadModel[] readModel);

	/**
	 * Read encrypted magnetic track information <p>
	 * This method supports mask model and allows the customer to set mask for controlling the returned account number format so as to avoid the account number return in plain text. </p>
	 * Refer to {@link Account#getAcctNo()}
	 *
	 * @param keyIndex  (1-200)if the keyIndex is a working key index,it isn't need to set the workingKeyData in swipExtParams</p>
	 *                   if the keyIndex is a master key index, it is need to set the workingKeyData in swipExtParams</p>
	 * @param swipExtParams params used in get swipresult
	 * @return swip result{@link SwipResult}
	 * @since ver3.10.01
	 */
	public SwipResult readEncryptResult(KeyManagement keyManagement, @IntRange(from = 1, to =200)int keyIndex, SwipExtParams swipExtParams);

	/**
	 * Magnetic track information encrypted by track plain text computing
	 * @param firstTrackData The first track plaintext
	 * @param secondTrackData second track plaintext
	 * @param thirdTrackData  Third tack plaintext
	 * @param keyIndex  if the keyIndex is a working key index,it isn't need to set the workingKeyData in swipExtParams<p>
	 *                   if the keyIndex is a master key Index, it is need to set the workingKeyData in swipExtParams<p>
	 * @param swipExtParams the params used in get swip result.
	 * @return Returned track data result
	 * @since ver3.10.01
	 */
	public SwipResult calcTrackData(KeyManagement keyManagement, String firstTrackData, String secondTrackData, String thirdTrackData, @IntRange(from = 1, to =200)int keyIndex, SwipExtParams swipExtParams);
}
