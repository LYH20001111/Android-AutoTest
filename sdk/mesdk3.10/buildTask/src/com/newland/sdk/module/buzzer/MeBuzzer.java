package com.newland.sdk.module.buzzer;

import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtypex.module.common.emv.SoundPoolImpl;

public class MeBuzzer implements BuzzerModule {
    @Override
    public boolean play(int count, int time, int interval) {
        SoundPoolImpl.getInstance(1).play(count,time,interval);
        return true;
    }

    @Override
    public boolean stop() {
        SoundPoolImpl.getInstance(1).stop();
        return true;
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
