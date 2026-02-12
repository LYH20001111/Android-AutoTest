package com.newland.sdk.me.module.guestDisplay;

import android.content.Context;

import com.newland.sdk.me.cmd.guestDisplay.CmdSetGuestDisplayBrightness;
import com.newland.sdk.me.cmd.guestDisplay.CmdShowGuestDisplay;
import com.newland.sdk.me.cmd.guestDisplay.CmdTurnOffGuestDisplay;
import com.newland.sdk.module.displayScreen.DisplayScreenModule;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MEDisplayScreen extends AbstractModule implements DisplayScreenModule {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MEDisplayScreen");

    private Context context;

    private Pattern pattern = Pattern.compile("([0-9]{1,5}\\.?[0-9]{1,2})|([0-9]{1,6})|([0-9]{1,5}\\.?)");

    public MEDisplayScreen(AbstractDevice owner, Context context) {
        super(owner);
        this.context = context;
    }

    @Override
    public boolean isStandardModule() {
        return true;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return ModuleType.DISPLAY_SCREEN;
    }

    @Override
    public String getExModuleType() {
        return null;
    }

    @Override
    public boolean setBrightness(int value) {
        try {
            devicelogger.debug("[setBrightness] value:"+value);
            if (value < 0 || value > 7) {
                devicelogger.error("[setBrightness] value isn't between 0 and 7");
                return false;
            }
            CmdSetGuestDisplayBrightness.CmdGuestDisplayBrightnessResponse response = (CmdSetGuestDisplayBrightness.CmdGuestDisplayBrightnessResponse) invoke(new CmdSetGuestDisplayBrightness((byte) value));
            return response.getResultCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean showMessage(String showValue) {
        try {
            devicelogger.debug("[showMessage] showValue=" + showValue);
            Matcher isValid = pattern.matcher(showValue);
            if (!isValid.matches()) {
                devicelogger.error("[showMessage] showValue is valid 1");
                return false;
            }
            CmdShowGuestDisplay.CmdShowGuestDisplayResponse response = (CmdShowGuestDisplay.CmdShowGuestDisplayResponse) invoke(new CmdShowGuestDisplay(showValue.getBytes()));
            return response.getResultCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean turnOffLed() {
        try {
            devicelogger.debug("[turnOffLed]");
            CmdTurnOffGuestDisplay.CmdTurnOffGuestDisplayResponse response = (CmdTurnOffGuestDisplay.CmdTurnOffGuestDisplayResponse) invoke(new CmdTurnOffGuestDisplay());
            return response.getResultCode();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}
