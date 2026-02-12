package com.newland.sdk.me2.module.swiper;

import com.newland.sdk.me2.cmd.swiper.CmdCalculateTrackData;
import com.newland.sdk.me2.cmd.swiper.CmdCalculateTrackData.CmdCalculateTrackDataResponse;
import com.newland.sdk.me2.cmd.swiper.CmdReadEncryptTrackData;
import com.newland.sdk.me2.cmd.swiper.CmdReadEncryptTrackData.CmdReadEncryptTrackDataResponse;
import com.newland.sdk.me2.cmd.swiper.CmdReadPlainResult;
import com.newland.sdk.me2.cmd.swiper.CmdReadPlainResult.CmdReadPlainResultResponse;
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

/**
 * @author youjf
 * @description
 * @date 2019/9/2
 * @since V3.10.01
 */
public class MEMagStripeCard extends AbstractModule implements MagStripeCardModule {
    private static final int DEFAULT_TRACKREAD_TIMEOUT_SECOND = 15;

    public MEMagStripeCard(AbstractDevice owner) {
        super(owner);
    }


    @Override
    public SwipResult readPlainResult(SwiperReadModel[] readModel) {
        try {
            CmdReadPlainResultResponse response = (CmdReadPlainResultResponse) invoke(new CmdReadPlainResult(readModel), DEFAULT_TRACKREAD_TIMEOUT_SECOND, TimeUnit.SECONDS);//读卡可能需要一个确认过程.
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
            int trackManageType = 0xFF;//mksk key manage type
            SwiperReadModel[] readModel = new SwiperReadModel[]{SwiperReadModel.THIRD_TRACK, SwiperReadModel.SECOND_TRACK, SwiperReadModel.THIRD_TRACK};
            if (keyManagement == KeyManagement.DUKPT) {
                trackManageType = 0x01;
            }
            int algorithmType = 0x01;
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
            if (msdAlgorithmType == MSDAlgorithmType.SM4_MODEL) {
                //todo
            }
            CmdReadEncryptTrackDataResponse response = (CmdReadEncryptTrackDataResponse) invoke(new CmdReadEncryptTrackData(readModel, keyIndex, externalKeyData, acctMask, algorithmType, trackManageType), DEFAULT_TRACKREAD_TIMEOUT_SECOND, TimeUnit.SECONDS);
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
        int trackManageType = 0xFF;//mksk key manage type
        if (keyManagement == KeyManagement.DUKPT) {
            trackManageType = 0x01;
        }
        int algorithmType = 0x01;
        MSDAlgorithmType msdAlgorithmType = MSDAlgorithmType.UNIONPAY_MODEL;
        byte[] externalKeyData = null;
        if (swipExtParams != null && swipExtParams.getMSDAlgorithmType() != null) {
            msdAlgorithmType = swipExtParams.getMSDAlgorithmType();
            if (msdAlgorithmType == MSDAlgorithmType.SM4_MODEL) {
                //algorithmType= todo
            }
        }
        if (swipExtParams != null && swipExtParams.getWorkingKeyData() != null) {
            externalKeyData = swipExtParams.getWorkingKeyData();
        }
        CmdCalculateTrackDataResponse response = (CmdCalculateTrackDataResponse) invoke(new CmdCalculateTrackData(secondTrackData, thirdTrackData, keyIndex, externalKeyData, algorithmType, trackManageType));
        if (response != null) {
            if (response.getRsltType() == SwipResultCode.SUCCESS) {
                return new SwipResult(null,
                        null,
                        null,
                        response.getTrackData(),
                        null,
                        null, null, response.getKsn());
            } else {
                return new SwipResult(response.getRsltType());
            }
        }
        return new SwipResult(SwipResultCode.SWIP_FAILED);
    }

    @Override
    public boolean isStandardModule() {
        return false;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return null;
    }

    @Override
    public String getExModuleType() {
        return null;
    }
}
