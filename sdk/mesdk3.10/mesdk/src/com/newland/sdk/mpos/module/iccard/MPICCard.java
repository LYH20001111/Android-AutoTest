package com.newland.sdk.mpos.module.iccard;

import android.content.Context;
import android.util.Log;

import com.newland.sdk.me.module.externaliccard.MEExtICCard;
import com.newland.sdk.module.externaliccard.ExtICCardModule;
import com.newland.sdk.module.externaliccard.TransmitExtParams;
import com.newland.sdk.module.iccard.ICCardModule;
import com.newland.sdk.module.iccard.ICCardSlot;
import com.newland.sdk.module.iccard.ICCardSlotState;
import com.newland.sdk.module.iccard.ICCardType;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.utils.ISOUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/16
 */
public class MPICCard implements ICCardModule {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MPICCard");
    private ExtICCardModule mExtICCardModule;

    public MPICCard(AbstractDevice device, Context context){
        mExtICCardModule = new MEExtICCard(device,context);
    }

    @Override
    public byte[] powerOn(ICCardSlot icCardSlot, ICCardType icCardType) {
        devicelogger.debug("[powerOn] icCardSlot="+icCardSlot+" icCardType="+icCardType);
        return mExtICCardModule.powerOn();
    }

    @Override
    public void powerOff(ICCardSlot icCardSlot, ICCardType icCardType) {
        devicelogger.debug("[powerOff] icCardSlot="+icCardSlot+" icCardType="+icCardType);
        mExtICCardModule.powerOff();
    }

    @Override
    public byte[] transmit(ICCardSlot icCardSlot, ICCardType icCardType, byte[] command, int timeout) {
        devicelogger.debug("[transmit] icCardSlot="+icCardSlot+" icCardType="+icCardType+" command="+hexString(command)+" timeout="+timeout);
        TransmitExtParams transmitExtParams = new TransmitExtParams();
        byte[] respData = mExtICCardModule.transmit(command,null);
        devicelogger.debug("[transmit] respData="+hexString(respData));
        return respData;
    }

    @Override
    public Map<ICCardSlot, ICCardSlotState> checkSlotsState() {
        Map<ICCardSlot, ICCardSlotState> states = new HashMap<ICCardSlot, ICCardSlotState>();
        for (int i = 0; i < ICCardSlot.values().length; i++) {
            states.put(ICCardSlot.values()[i],ICCardSlotState.NO_CARD);
        }
        boolean isExits = mExtICCardModule.isCardIn();
        devicelogger.debug("[checkSlotsState] isExits="+isExits);
        if(isExits){
            states.put(ICCardSlot.IC1,ICCardSlotState.CARD_INSERTED);
        }
        return states;

    }

    private String hexString(byte[] data) {
        return (data == null ? "null" : ISOUtils.hexString(data));
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
