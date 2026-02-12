package com.newland.sdk.mpos.module.cardreader;

import android.content.Context;
import android.support.annotation.NonNull;

import com.newland.sdk.me.module.externalCardreader.MEExtCardReader;
import com.newland.sdk.module.cardreader.CardReaderExtParams;
import com.newland.sdk.module.cardreader.CardReaderListener;
import com.newland.sdk.module.cardreader.CardReaderModule;
import com.newland.sdk.module.cardreader.CardType;
import com.newland.sdk.module.externalCardreader.ExtCardReaderModule;
import com.newland.sdk.module.pin.MacAlgorithm;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/16
 */
public class MPCardReader implements CardReaderModule {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MPCardReader");
    private ExtCardReaderModule mExtCardReaderModule;

    public MPCardReader(AbstractDevice device, Context context){
        mExtCardReaderModule = new MEExtCardReader(device,context);
    }
    @Override
    public void openCardReader(@NonNull CardType[] cardTypes, int timeout, @NonNull CardReaderListener cardReaderListener, CardReaderExtParams cardReaderExternalParams) {
        devicelogger.debug("[openCardReader] cardTypes="+cardTypes+" timeout="+timeout);
        mExtCardReaderModule.openCardReader(cardTypes,timeout,cardReaderListener,cardReaderExternalParams);
    }

    @Override
    public void cancelCardReader() {
        devicelogger.debug("[cancelCardReader]");
        mExtCardReaderModule.cancelCardReader();
    }

    @Override
    public CardType[] getLastReaderTypes() {
        CardType[] cardTypes = ((MEExtCardReader)mExtCardReaderModule).getLastReaderTypes();
        devicelogger.debug("[getLastReaderTypes] cardTypes="+cardTypes[0]);
        return cardTypes;
    }

    @Override
    public void setLastReaderTypes(CardType[] cardTypes) {
        //lastReaderTypes = cardTypes;
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
