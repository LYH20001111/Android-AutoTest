package com.newland.nsdk.core.external;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.external.beeper.ExtBeeper;
import com.newland.nsdk.core.api.external.devicemanager.BeeperTone;
import com.newland.nsdk.core.external.command.common.ExternalCommonModule;

public class ExtBeeperImpl implements ExtBeeper {
    private ExternalCommonModule externalCommonModule;
    private volatile static ExtBeeperImpl instance;
    public static ExtBeeperImpl getInstance() {
        if (instance == null) {
            synchronized (ExtBeeperImpl.class) {
                if (instance == null) {
                    instance = new ExtBeeperImpl();
                }
            }
        }
        return instance;
    }
    private ExtBeeperImpl(){
        externalCommonModule = new ExternalCommonModule();
    }

    @Override
    public void beep(BeeperTone tone, int duration) throws NSDKException {
        if (duration <= 0) {
            throw new NSDKIllegalParameterException("Duration should be above 0!");
        }

        // 指令集时间单位是 10ms
        if (duration < 10) {
            duration = 10;
        } else {
            duration /= 10;
        }
        externalCommonModule.beep(tone, duration);
    }

    @Override
    public void beep(int frequency, int duration) throws NSDKException {
        if (frequency < 1 || frequency > 4000) {
            throw new NSDKIllegalParameterException("Frequency shall be range from 1 to 4000 Hz.");
        }
        if (duration <= 0) {
            throw new NSDKIllegalParameterException("Duration shall be >= 0.");
        }
        externalCommonModule.beep(frequency, duration);
    }
}
