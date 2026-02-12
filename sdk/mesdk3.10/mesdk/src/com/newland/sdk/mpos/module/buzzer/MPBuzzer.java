package com.newland.sdk.mpos.module.buzzer;

import android.content.Context;

import com.newland.sdk.me.module.cardreader.SDKExecutors;
import com.newland.sdk.me.module.externalbuzzer.MEExtBuzzer;
import com.newland.sdk.module.buzzer.BuzzerModule;
import com.newland.sdk.module.externalbuzzer.ExtBuzzerModule;
import com.newland.sdk.mtype.Device;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.module.common.emv.SoundPoolImpl;

/**
 * @Description
 * @Author wuhh
 * @Date 2021/3/8
 */
public class MPBuzzer implements BuzzerModule {

    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MPBuzzer");
    private boolean isStop;//只考虑单线程的停止;
    private ExtBuzzerModule mExtBuzzerModule;

    public MPBuzzer(AbstractDevice device, Context context){
        mExtBuzzerModule = new MEExtBuzzer(device,context);
    }
    @Override
    public boolean play(final int count, final int time, final int interval) {
        devicelogger.debug("[play] count="+count+" time="+time+" interval="+interval);
        isStop = false;
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    for (int i = 0; i < count; i++) {
                        if(isStop){
                           return;
                        }
                        mExtBuzzerModule.play(0,time);
                        Thread.sleep(interval);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        ).start();

        return false;
    }

    @Override
    public boolean stop() {
        devicelogger.debug("[stop]");
        isStop = true;
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
