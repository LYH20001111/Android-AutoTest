package com.newland.sdk.me.module.externalLight;

import android.content.Context;

import com.newland.sdk.me.module.externalPininput.PinpadPackage;
import com.newland.sdk.module.externalLight.ExtIndicatorLightModule;
import com.newland.sdk.module.light.LightColor;
import com.newland.sdk.module.light.LightState;
import com.newland.sdk.module.serialport.PinpadModel;
import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.mtype.util.InnerUtils;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;
import com.newland.sdk.utils.ISOUtils;

import java.util.Arrays;

public class MEExtLight extends AbstractModule implements ExtIndicatorLightModule {

    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MEExtLight");
    private PinpadPackage mPinpadPackage;
    private static final int TIMEOUT = 5000;
    private static final byte NAK = 0x15;

    private int mLEDStatus = 0;
    private static final int LIGHT_BLUE = 0x10;
    private static final int LIGHT_YELLOW = 0x20;
    private static final int LIGHT_RED = 0x40;
    private static final int LIGHT_GREEN = 0x80;

    public MEExtLight(AbstractDevice device, Context context) {
        super(device);
        mPinpadPackage= PinpadPackage.getInstance(device,context);
    }

    @Override
    public boolean operateLight(LightColor[] lightColors, LightState lightState, int timeout) {
        devicelogger.debug("[operateLight] lightColors:"+lightColors+"; lightState:"+lightState+"; timeout:"+timeout);
        if(mPinpadPackage.getModel() == PinpadModel.SP){
            if (lightState == LightState.TURNON || lightState == LightState.BLINK) {
                for (LightColor type : lightColors) {
                    if (type == LightColor.BLUE) {
                        mLEDStatus |= LIGHT_BLUE;
                    } else if (type == LightColor.YELLOW) {
                        mLEDStatus |= LIGHT_YELLOW;
                    } else if (type == LightColor.RED) {
                        mLEDStatus |= LIGHT_RED;
                    } else if (type == LightColor.GREEN) {
                        mLEDStatus |= LIGHT_GREEN;
                    }
                }
                if (lightState == LightState.TURNON) {
                    return spTurnOnOff();
                } else {
                    return spBlink();
                }
            } else if (lightState == LightState.TURNOFF) {
                for (LightColor type : lightColors) {
                    if (type == LightColor.BLUE) {
                        mLEDStatus &= ~LIGHT_BLUE;
                    } else if (type == LightColor.YELLOW) {
                        mLEDStatus &= ~LIGHT_YELLOW;
                    } else if (type == LightColor.RED) {
                        mLEDStatus &= ~LIGHT_RED;
                    } else if (type == LightColor.GREEN) {
                        mLEDStatus &= ~LIGHT_GREEN;
                    }
                }
                return spTurnOnOff();
            }
        }else{
            try {
                byte[] messageType = new byte[]{0x41,0x38}; //"A8"
                byte[] reqData = new byte[4];
                byte ligthtType = 0x00;//0x01 - red 0x02-yellow 0x04 - green 0x08 blue,可组合
                byte operType = 0x00;//0x01-开；0x02-关; 0x03-闪烁
                switch (lightState){
                    case TURNON:
                        operType = 0x01;
                        for (LightColor lightColor : lightColors) {
                            if (lightColor == LightColor.BLUE) {
                                ligthtType |=  0x08;
                            } else if (lightColor == LightColor.GREEN) {
                                ligthtType |=  0x04;
                            } else if (lightColor == LightColor.YELLOW) {
                                ligthtType |=  0x02;
                            } else if (lightColor == LightColor.RED) {
                                ligthtType |=  0x01;
                            }
                        }
                        break;
                    case TURNOFF:
                        operType = 0x02;
                        for (LightColor lightColor : lightColors) {
                            if (lightColor == LightColor.BLUE) {
                                ligthtType |=  0x08;
                            } else if (lightColor == LightColor.GREEN) {
                                ligthtType |=  0x04;
                            } else if (lightColor == LightColor.YELLOW) {
                                ligthtType |=  0x02;
                            } else if (lightColor == LightColor.RED) {
                                ligthtType |=  0x01;
                            }
                        }
                        break;
                    case BLINK:
                        operType = 0x03;
                        for (LightColor lightColor : lightColors) {
                            if (lightColor == LightColor.BLUE) {
                                ligthtType |=  0x08;
                            } else if (lightColor == LightColor.GREEN) {
                                ligthtType |=  0x04;
                            } else if (lightColor == LightColor.YELLOW) {
                                ligthtType |=  0x02;
                            } else if (lightColor == LightColor.RED) {
                                ligthtType |=  0x01;
                            }
                        }
                        break;
                }

                reqData[0]=ligthtType;
                reqData[1]=operType;
                byte[] timeoutByte = InnerUtils.intToBytes(timeout, 2, true);
                System.arraycopy(timeoutByte, 0, reqData, 2, timeoutByte.length);
                devicelogger.debug("[operateLight]reqData:"+(reqData==null?null:ISOUtils.hexString(reqData)));
                byte[] resp = mPinpadPackage.sendPinpadCmd(messageType,reqData,PinpadPackage.EXTCMD_TIMEOUT_MS,true);
                devicelogger.debug("[operateLight]operateLight result:" + (resp == null ? "null" : ISOUtils.hexString(resp)));
                //指示灯，只返回06，没有响应数据
                if (resp != null && resp[0] == 0x06) { //ACK
                    return true;
                }
                if (resp != null && resp.length >= 5) {
                    if(Arrays.equals("A9".getBytes(), new byte[]{resp[0], resp[1]})){
                        return Arrays.equals(new byte[]{0x30, 0x30}, new byte[]{resp[3], resp[4]});
                    }
                }
                return false;
            }catch (Exception e){
                e.printStackTrace();
            }
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
        return ExModuleType.LIGHT;
    }

    private boolean spTurnOnOff() {

        devicelogger.debug("[spTurnOnOff]LED data: " + mLEDStatus);
        byte[] resp = mPinpadPackage.getPinpadRspCode( new byte[]{0x31, 0x14, (byte) mLEDStatus}, PinpadPackage.EXTCMD_TIMEOUT_MS);
        devicelogger.debug("[spTurnOnOff] operateLight result:" + (resp == null ? "null" : ISOUtils.hexString(resp)));
        if (resp != null && resp.length >= 2) {
            if (Arrays.equals(new byte[]{0x00, 0x00}, new byte[]{resp[0], resp[1]})) {
                return true;
            } else if (Arrays.equals(new byte[]{0x00, 0x01}, new byte[]{resp[0], resp[1]})) {
                devicelogger.error("[spTurnOnOff] The device have not LED");
                return false;
            } else {
                devicelogger.error("[spTurnOnOff] Unknown error");
            }
        }
        return false;
    }

    private boolean spBlink() {
        devicelogger.debug("[spBlink] LED data: " + mLEDStatus);
        byte[] resp = mPinpadPackage.getPinpadRspCode(new byte[]{0x31, 0x16, (byte) mLEDStatus}, PinpadPackage.EXTCMD_TIMEOUT_MS);
        devicelogger.debug("[spBlink] blinkLight result:" + (resp == null ? "null" : ISOUtils.hexString(resp)));
        if (resp != null && resp.length >= 2) {
            if (Arrays.equals(new byte[]{0x00, 0x00}, new byte[]{resp[0], resp[1]})) {
                return true;
            } else if (Arrays.equals(new byte[]{0x00, 0x01}, new byte[]{resp[0], resp[1]})) {
                devicelogger.error("[spBlink]The device have not LED");
                return false;
            } else {
                devicelogger.error("[spBlink] Unknown error");
            }
        }
        return false;
    }
}
