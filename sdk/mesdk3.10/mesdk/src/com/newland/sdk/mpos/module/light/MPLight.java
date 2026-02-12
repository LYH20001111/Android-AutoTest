package com.newland.sdk.mpos.module.light;

import android.content.Context;

import com.newland.sdk.me.module.externalLight.MEExtLight;
import com.newland.sdk.module.externalLight.ExtIndicatorLightModule;
import com.newland.sdk.module.light.IndicatorLightModule;
import com.newland.sdk.module.light.LightColor;
import com.newland.sdk.module.light.LightState;
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
public class MPLight implements IndicatorLightModule {

    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MPLight");

    private ExtIndicatorLightModule mExtLightModule;

    public MPLight(AbstractDevice device, Context context){
        mExtLightModule = new MEExtLight(device,context);
    }

    @Override
    public boolean operateLight(LightColor[] lightColor, LightState lightState) {
        devicelogger.debug("[operateLight] lightColor="+lightColor.toString()+" lightState="+lightState);
        return mExtLightModule.operateLight(lightColor,lightState,60*1000*5);
    }

    @Override
    public boolean blinkLight(LightColor[] lightColor, int count, int timeInterval) {
        devicelogger.debug("[operateLight] lightColor="+lightColor.toString()+" count="+count+" timeInterval="+timeInterval);
        return mExtLightModule.operateLight(lightColor,LightState.BLINK,timeInterval*count);
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
