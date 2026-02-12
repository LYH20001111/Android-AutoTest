package com.newland.nsdk.core.external;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.led.LEDColor;
import com.newland.nsdk.core.api.common.led.LEDState;
import com.newland.nsdk.core.api.external.led.ExtLED;
import com.newland.nsdk.core.external.command.common.ExternalCommonModule;

public class ExtLEDImpl implements ExtLED {
    private ExternalCommonModule commonModule;
    private volatile static ExtLEDImpl instance;
    public static ExtLEDImpl getInstance() {
        if (instance == null) {
            synchronized (ExtLEDImpl.class) {
                if (instance == null) {
                    instance = new ExtLEDImpl();
                }
            }
        }
        return instance;
    }
    private ExtLEDImpl() {
        commonModule = new ExternalCommonModule();
    }

    @Override
    public void setState(LEDColor[] ledColors, LEDState ledState) throws NSDKException {
        if (ledColors == null || ledState == null) {
            throw new NSDKIllegalParameterException("Light colors and state shall not be null !");
        }

        commonModule.setLedState(ledColors, ledState);
    }
}
