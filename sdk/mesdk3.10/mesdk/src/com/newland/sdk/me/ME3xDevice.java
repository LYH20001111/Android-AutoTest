package com.newland.sdk.me;
import com.newland.sdk.me.module.emv.MEEMVL2;
import com.newland.sdk.me.module.externalLight.MEExtLight;
import com.newland.sdk.me.module.externalPininput.ReMEExternalPininput;
import com.newland.sdk.me.module.externalbuzzer.MEExtBuzzer;
import com.newland.sdk.me.module.externaliccard.MEExtICCard;
import com.newland.sdk.me.module.externalmagiccard.MEExtMagStripeCard;
import com.newland.sdk.me.module.externalrfcard.MeExternalRFCard;
import com.newland.sdk.mpos.module.buzzer.MPBuzzer;
import com.newland.sdk.mpos.module.cardreader.MPCardReader;
import com.newland.sdk.mpos.module.iccard.MPICCard;
import com.newland.sdk.mpos.module.light.MPLight;
import com.newland.sdk.mpos.module.magcard.MPMagStripeCard;
import com.newland.sdk.mpos.module.pininput.MPPinpad;
import com.newland.sdk.mpos.module.rfcard.MPRFCard;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtypex.conn.DeviceExecutor;

public class ME3xDevice extends AbstractMESeriesDevice {

    public ME3xDevice(DeviceExecutor deviceExecutor) {
        super(deviceExecutor);
    }

    @Override
    protected void initModule() {
        standardModules.put(ModuleType.COMMON_CARDREADER, new MPCardReader(this, deviceExecutor.getContext()));
        standardModules.put(ModuleType.MAGCARDREADER, new MPMagStripeCard(this, deviceExecutor.getContext()));
        standardModules.put(ModuleType.ICCARDREADER, new MPICCard(this, deviceExecutor.getContext()));
        standardModules.put(ModuleType.RFCARDREADER, new MPRFCard(this, deviceExecutor.getContext()));
        standardModules.put(ModuleType.INDICATOR_LIGHT, new MPLight(this, deviceExecutor.getContext()));
        standardModules.put(ModuleType.BUZZER, new MPBuzzer(this, deviceExecutor.getContext()));
        standardModules.put(ModuleType.PINPAD, new MPPinpad(this, deviceExecutor.getContext()));
        standardModules.put(ModuleType.EMV, new MEEMVL2(this, deviceExecutor.getContext()));
    }

    @Override
    protected void setStandardModules(ModuleType moduleType) {

    }

    @Override
    protected void setExModule(String moduleType) {

    }
}
