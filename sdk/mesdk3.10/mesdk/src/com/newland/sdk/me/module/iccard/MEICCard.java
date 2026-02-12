package com.newland.sdk.me.module.iccard;

import com.newland.sdk.me.cmd.iccard.CmdICCardPowerOff;
import com.newland.sdk.me.cmd.iccard.CmdICCardPowerOn;
import com.newland.sdk.me.cmd.iccard.CmdICCardPowerOn.CmdICCardPowerOnResponse;
import com.newland.sdk.me.cmd.iccard.CmdICCardTest;
import com.newland.sdk.me.cmd.iccard.CmdICCardTest.CmdICCardTestResponse;
import com.newland.sdk.me.cmd.iccard.CmdICCardTransmit;
import com.newland.sdk.me.cmd.iccard.CmdICCardTransmit.CmdICCardTransmitResponse;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.module.iccard.ICCardModule;
import com.newland.sdk.module.iccard.ICCardSlot;
import com.newland.sdk.module.iccard.ICCardSlotState;
import com.newland.sdk.module.iccard.ICCardType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class MEICCard extends AbstractModule implements ICCardModule {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MEICCard");

    public MEICCard(AbstractDevice owner) {
        super(owner);
    }

    @Override
    public boolean isStandardModule() {
        return true;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return ModuleType.ICCARDREADER;
    }

    @Override
    public String getExModuleType() {
        return null;
    }


    @Override
    public Map<ICCardSlot, ICCardSlotState> checkSlotsState() {
        devicelogger.debug("[checkSlotsState]");
        CmdICCardTestResponse response = (CmdICCardTestResponse) invoke(new CmdICCardTest());

        return response.getICCardState();
    }

    @Override
    public byte[] powerOn(ICCardSlot slot, ICCardType cardType) {
        devicelogger.debug("[powerOn] slot:"+slot+";cardType:"+cardType);

        CmdICCardPowerOnResponse response = (CmdICCardPowerOnResponse) invoke(new CmdICCardPowerOn(slot, cardType));

        return response.getATR();
    }

    @Override
    public void powerOff(ICCardSlot slot, ICCardType cardType) {
        devicelogger.debug("[powerOff] slot:"+slot+"; cardType:"+cardType);
        invoke(new CmdICCardPowerOff(slot, cardType));

    }

    @Override
    public byte[] transmit(ICCardSlot icCardSlot, ICCardType icCardType, byte[] command, int timeout) {
        devicelogger.debug("[transmit] icCardSlot:"+icCardSlot+"; icCardType:"+icCardType+";timeout:"+timeout);
        long posTimeout = timeout / 1000;
        long invokeTimeout = timeout + 3;

        CmdICCardTransmitResponse response = (CmdICCardTransmitResponse) invoke(new CmdICCardTransmit(icCardSlot, icCardType, command), invokeTimeout, TimeUnit.SECONDS);

        return response.getResponse();
    }
}
