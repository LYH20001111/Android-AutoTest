package com.newland.sdk.me.module.externalbuzzer;

import android.content.Context;

import com.newland.sdk.me.module.externalPininput.PinpadPackage;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.externalbuzzer.ExtBuzzerModule;
import com.newland.sdk.module.serialport.PinpadModel;
import com.newland.sdk.mtype.DeviceRTException;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.common.ErrorCode;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.utils.ISOUtils;

/**
 * @author youjf
 * @description
 * @date 2020/6/10
 * @since V3.10.20
 */
public class MEExtBuzzer extends AbstractModule implements ExtBuzzerModule {
    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MEExtBuzzer");
    private PinpadPackage pinpadPackage;
    private Context context;
    private static final byte ACK = 0x06;
    private static final byte NAK = 0x15;

    private PinpadModel pinpadModel = PinpadModel.SP_OVERSEAS;

    public MEExtBuzzer(AbstractDevice device, Context context) {
        super(device);
        this.context = context;
        pinpadPackage = PinpadPackage.getInstance(device, context);
        pinpadModel = pinpadPackage.getModel();
    }

    @Override
    public boolean init(PinpadInitExtParams params) {
        try {
            devicelogger.debug("------init----params:" + params);
            boolean rs = pinpadPackage.init(params, false);
            pinpadModel = pinpadPackage.getModel();
            return rs;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean play(int buzzerTone, int time) {
        try {
                devicelogger.debug("[play],buzzerTone:" + buzzerTone + ";time");
                byte[] messageType = new byte[]{0x33, 0x39};
                byte[] req = new byte[3];
                req[0] = (byte) buzzerTone;
                time = time / 10;//ME51是10毫秒一个单位
                devicelogger.debug("[play]time:" + time);
                byte[] timeData = InnerUtils.intToBytes(time, 2, true);
                devicelogger.debug("[play]timeData:" + (timeData == null ? null : ISOUtils.hexString(timeData)));
                req[1] = timeData[0];
                req[2] = timeData[1];
                devicelogger.debug("[play]reqData:" + (InnerUtils.hexString(req)));
                byte[] rspCode = pinpadPackage.sendPinpadCmd(messageType, req, (time * 10) + PinpadPackage.EXTCMD_OFFSETTIME_MS, true);
                devicelogger.debug("[play]rspCode:" + (rspCode == null ? null : ISOUtils.hexString(rspCode)));
                if (rspCode == null || rspCode[0] == NAK) {
                    throw new DeviceRTException(ErrorCode.DEVICE_INVOKE_FAILED, "read magic track failed" + ",res=" + (rspCode == null ? "null" : InnerUtils.hexString(rspCode)));

                } else {
                    if (rspCode[0] == ACK) {
                        return true;
                    }
                    pinpadPackage.getPinpadRspCode();

                }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
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
        return ExModuleType.BUZZER;
    }
}
