package com.newland.sdk.module.externalmagic;

import android.support.annotation.IntRange;

import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.swiper.SwipExtParams;
import com.newland.sdk.module.swiper.SwipResult;
import com.newland.sdk.module.swiper.SwiperReadModel;

import java.util.List;

/**
 * @author youjf
 * @description
 * @date 2020/6/9
 * @since V3.10.01
 */
public interface ExtMagicCardModule {
    /**
     * init external pinpad
     * @param params {@link PinpadInitExtParams}
     * @return
     */
    public boolean init(PinpadInitExtParams params);
    /**
     *  After security verification, use plain text to return the swiping result. <p>
     *  The return magnetic information of the card swiping result is of plain text form. <p>
     *
     * @param readModel  Magnetic track reading model
     * @return swiping result
     * @since ver3.10.20
     */
    public SwipResult readPlainResult(SwiperReadModel[] readModel);

    /**
     * @param timeout second.
     * @param readModel  Magnetic track reading model
     * @return swiping result
     * @since ver3.10.20
     */
    public SwipResult readPlainResultWithoutOpen(int timeout, SwiperReadModel[] readModel);


    /**
     * read encrypted magic track data
     * @param keyManagement just support {@link KeyManagement#MKSK}
     * @param keyIndex 129-255
     * @param readModel {@link SwiperReadModel}
     * @param algorithmMode unsupport {@link AlgorithmMode#SM4} for now.
     * @param cipherMode
     * @param swipExtParams
     * @return
     */
    public SwipResult readEncryptResult(KeyManagement keyManagement, @IntRange(from = 1, to =200)int keyIndex, SwiperReadModel[] readModel,AlgorithmMode algorithmMode,CipherMode cipherMode,SwipExtParams swipExtParams);
}
