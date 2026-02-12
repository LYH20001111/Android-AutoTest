package com.newland.nsdk.core.internal.beeper;

import android.newland.os.NlBuild;
import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.internal.beeper.Beeper;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.internal.jni.NSDKJni;

import java.util.Locale;

public class BeeperImpl implements Beeper {

    public boolean isSupported;

    private volatile static BeeperImpl instance;

    public static BeeperImpl getInstance(boolean isSupported) {
        if (instance == null) {
            synchronized (BeeperImpl.class) {
                if (instance == null || instance.isSupported != isSupported) {
                    instance = new BeeperImpl(isSupported);
                }
            }
        } else {
            if (instance.isSupported != isSupported) {
                instance = new BeeperImpl(isSupported);
            }
        }
        return instance;
    }

    private BeeperImpl(){
        this.isSupported = true;
    }

    private BeeperImpl(boolean isSupported){
        this.isSupported = isSupported;
    }

    private void isSupported() throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported Beeper Module");
        }
    }

    @Override
    public void beep(int frequency, int duration) throws NSDKException {
        isSupported();

        if (frequency <= 0 || frequency > 4000) {
            throw new NSDKIllegalParameterException("Frequency shall be >0 and <= 4000.");
        }
        if (duration < 0) {
            throw new NSDKIllegalParameterException("Duration shall be >0.");
        }
        int ret = -1;

        ret = NSDKJni.getInstance().NAPI_Beep(frequency, duration);
        
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to beep, result code = %d", ret));
        }
    }

    @Override
    public void setVolume(int volume) throws NSDKException {
        if (volume < 1 || volume > 5) {
            throw new NSDKIllegalParameterException("Volume range is [1,5]");
        }
        int ret = NSDKJni.getInstance().NDK_SysSetBeepVol(volume);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to set volume, ret = %d", ret));
        }
    }

    private boolean isHardwareBeeper() {
        String hardwareConfig = NlBuild.VERSION.NL_HARDWARE_CONFIG;
        if (hardwareConfig.length() >= 50) {
            String cfg = hardwareConfig.substring(48, 50);
            return "02".equals(cfg);
        }
        return false;
    }
}
