package com.newland.sdk.me.module.swiper;

import com.newland.sdk.me.cmd.swiper.CmdCalculateTrackData;
import com.newland.sdk.me.cmd.swiper.CmdReadEncryptTrackData;
import com.newland.sdk.me.cmd.swiper.CmdReadEncryptTrackData.CmdReadEncryptTrackDataResponse;
import com.newland.sdk.me.cmd.swiper.CmdReadPlainTrackData;
import com.newland.sdk.me.cmd.swiper.CmdReadPlainTrackData.CmdReadClearTrackDataResponse;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.swiper.MSDAlgorithmType;
import com.newland.sdk.module.swiper.MagStripeCardModule;
import com.newland.sdk.module.swiper.SwipExtParams;
import com.newland.sdk.module.swiper.SwipResult;
import com.newland.sdk.module.swiper.SwipResultCode;
import com.newland.sdk.module.swiper.SwiperReadModel;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;

import java.util.concurrent.TimeUnit;

public class MEMagStripeCard extends AbstractModule implements MagStripeCardModule {

    private static final int DEFAULT_TRACKREAD_TIMEOUT_SECOND = 15;

    public MEMagStripeCard(AbstractDevice owner) {
        super(owner);
    }

    @Override
    public boolean isStandardModule() {
        return true;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return ModuleType.MAGCARDREADER;
    }

    @Override
    public String getExModuleType() {
        return null;
    }

    @Override
    public SwipResult readPlainResult(SwiperReadModel[] readModel) {
        try {
            CmdReadClearTrackDataResponse response = (CmdReadClearTrackDataResponse) invoke(new CmdReadPlainTrackData(readModel), DEFAULT_TRACKREAD_TIMEOUT_SECOND, TimeUnit.SECONDS);//读卡可能需要一个确认过程.
            if (response != null) {
                if (response.getRsltType() == SwipResultCode.SUCCESS) {
                    return new SwipResult(response.getAccount(),
                            response.getReadModels(),
                            response.getFirstTrackData(),
                            response.getSecondTrackData(),
                            response.getThirdTrackData(),
                            response.getValidDate(),
                            response.getServiceCode(), null);
                } else {
                    return new SwipResult(response.getRsltType());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new SwipResult(SwipResultCode.SWIP_FAILED);
    }

    @Override
    public SwipResult readEncryptResult(KeyManagement keyManagement, int keyIndex, SwipExtParams swipExtParams) {
        try {
            SwiperReadModel[] readModel = new SwiperReadModel[]{SwiperReadModel.FIRST_TRACK, SwiperReadModel.SECOND_TRACK, SwiperReadModel.THIRD_TRACK};
            MSDAlgorithmType msdAlgorithmType = MSDAlgorithmType.UNIONPAY_MODEL;
            byte[] acctMask = null;
            byte[] externalKeyData = null;
            if (swipExtParams != null && swipExtParams.getMSDAlgorithmType() != null) {
                msdAlgorithmType = swipExtParams.getMSDAlgorithmType();
            }
            if (swipExtParams != null && swipExtParams.getWorkingKeyData() != null) {
                externalKeyData = swipExtParams.getWorkingKeyData();
            }
            if (swipExtParams != null && swipExtParams.getAcctMask() != null) {
                acctMask = swipExtParams.getAcctMask();
            }
            CmdReadEncryptTrackDataResponse response = (CmdReadEncryptTrackDataResponse) invoke(new CmdReadEncryptTrackData(keyManagement, msdAlgorithmType, readModel, keyIndex, acctMask, externalKeyData), DEFAULT_TRACKREAD_TIMEOUT_SECOND, TimeUnit.SECONDS);
            if (response != null) {
                if (response.getRsltType() == SwipResultCode.SUCCESS) {
                    return new SwipResult(response.getAccount(),
                            response.getReadModels(),
                            response.getFirstTrackData(),
                            response.getSecondTrackData(),
                            response.getThirdTrackData(),
                            response.getValidDate(),
                            response.getServiceCode(),
                            response.getKsn());
                } else {
                    return new SwipResult(response.getRsltType());
                }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return new SwipResult(SwipResultCode.SWIP_FAILED);
    }

    @Override
    public SwipResult calcTrackData(KeyManagement keyManagement, String firstTrackData, String secondTrackData, String thirdTrackData, int keyIndex, SwipExtParams swipExtParams) {
        if (null != secondTrackData) {
            if (secondTrackData.length() % 2 == 0 && secondTrackData.substring(secondTrackData.length() - 1).equals("F")) {
                secondTrackData = secondTrackData.substring(0, secondTrackData.length() - 1);
            }
        }
        MSDAlgorithmType msdAlgorithmType = MSDAlgorithmType.UNIONPAY_MODEL;
        byte[] externalKeyData = null;
        if (swipExtParams != null && swipExtParams.getMSDAlgorithmType() != null) {
            msdAlgorithmType = swipExtParams.getMSDAlgorithmType();
        }
        if (swipExtParams != null && swipExtParams.getWorkingKeyData() != null) {
            externalKeyData = swipExtParams.getWorkingKeyData();
        }
        CmdCalculateTrackData.CmdCalculateTrackDataResponse response = (CmdCalculateTrackData.CmdCalculateTrackDataResponse) invoke(new CmdCalculateTrackData(keyManagement, msdAlgorithmType, firstTrackData, secondTrackData, thirdTrackData, keyIndex, externalKeyData));
        if (response != null) {
            if (response.getRsltType() == SwipResultCode.SUCCESS) {
                return new SwipResult(null,
                        null,
                        response.getFirstTrackData(),
                        response.getSecondTrackData(),
                        response.getThirdTrackData(),
                        null, null, response.getKsn());
            } else {
                return new SwipResult(response.getRsltType());
            }
        }
        return new SwipResult(SwipResultCode.SWIP_FAILED);
    }

}
