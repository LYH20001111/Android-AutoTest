package com.newland.sdk.mpos.module.magcard;

import android.content.Context;

import com.newland.sdk.me.module.externalmagiccard.MEExtMagStripeCard;
import com.newland.sdk.module.externalmagic.ExtMagicCardModule;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.CipherMode;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.swiper.MagStripeCardModule;
import com.newland.sdk.module.swiper.SwipExtParams;
import com.newland.sdk.module.swiper.SwipResult;
import com.newland.sdk.module.swiper.SwiperReadModel;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.utils.ISOUtils;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/16
 */
public class MPMagStripeCard implements MagStripeCardModule {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MPMag");
    private ExtMagicCardModule mExtMagicCardModule;
    public MPMagStripeCard(AbstractDevice device, Context context){
        mExtMagicCardModule = new MEExtMagStripeCard(device,context);
    }

    @Override
    public SwipResult readPlainResult(SwiperReadModel[] readModel) {
        devicelogger.debug("[readPlainResult] readModel="+readModel.toString());
        SwipResult swipResult = mExtMagicCardModule.readPlainResult(readModel);
        if(swipResult != null){
            devicelogger.debug("[readPlainResult] "+ ISOUtils.hexString(swipResult.getFirstTrackData()));
            devicelogger.debug("[readPlainResult] "+ ISOUtils.hexString(swipResult.getSecondTrackData()));
            devicelogger.debug("[readPlainResult] "+ ISOUtils.hexString(swipResult.getThirdTrackData()));
        }
        return swipResult;
    }

    @Override
    public SwipResult readEncryptResult(KeyManagement keyManagement, int keyIndex, SwipExtParams swipExtParams) {
        SwipResult swipResult = mExtMagicCardModule.readEncryptResult(keyManagement, keyIndex,
                new SwiperReadModel[]{SwiperReadModel.FIRST_TRACK,SwiperReadModel.SECOND_TRACK,SwiperReadModel.THIRD_TRACK},
                AlgorithmMode.DES, CipherMode.ECB,swipExtParams);
        if(swipResult != null){
            devicelogger.debug("[readPlainResult] "+ ISOUtils.hexString(swipResult.getFirstTrackData()));
            devicelogger.debug("[readPlainResult] "+ ISOUtils.hexString(swipResult.getSecondTrackData()));
            devicelogger.debug("[readPlainResult] "+ ISOUtils.hexString(swipResult.getThirdTrackData()));
        }
        return swipResult;
    }

    @Override
    public SwipResult calcTrackData(KeyManagement keyManagement, String firstTrackData, String secondTrackData, String thirdTrackData, int keyIndex, SwipExtParams swipExtParams) {
        devicelogger.debug("[calcTrackData]");
        return null;
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

    @Override
    public Device getOwner() {
        return null;
    }

    @Override
    public String getDescription() {
        return null;
    }
}
