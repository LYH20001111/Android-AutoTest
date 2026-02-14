package com.newland.nsdk.core.internal.led;

import android.newland.os.NlBuild;
import android.util.Log;

import com.newland.nsdk.core.api.common.ErrorCode;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.exception.NSDKIllegalParameterException;
import com.newland.nsdk.core.api.common.led.LEDColor;
import com.newland.nsdk.core.api.common.led.LEDState;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.exception.NSDKNDKException;
import com.newland.nsdk.core.api.internal.led.DisplayParameters;
import com.newland.nsdk.core.api.internal.led.LED;
import com.newland.nsdk.core.api.internal.led.LEDLight;
import com.newland.nsdk.core.internal.jni.NSDKJni;

import java.util.Locale;

/**
 * : Operate the indicator light<p>
 * Author by liudan, Date on 2020/1/19.
 */
public class LEDImpl implements LED {

    public static final byte LIGHT_TURN_ON = 0x01;
    public static final byte LIGHT_TURN_OFF = 0x00;
    public static final byte LIGHT_BLINK = 0x02;

    private static final int LIGHT_BLUE = 0x01;
    private static final int LIGHT_GREEN = 0x02;
    private static final int LIGHT_YELLOW = 0x04;
    private static final int LIGHT_RED = 0x08;

    public boolean isSupported;
    private volatile static LEDImpl instance;
    private int x = -1;
    private int y = -1;
    private boolean isHorizontal = true;
    private boolean isBackgroundAlwaysDisplayed = false;

    public static LEDImpl getInstance(boolean isSupported) {
        if (instance == null) {
            synchronized (LEDImpl.class) {
                if (instance == null || instance.isSupported != isSupported) {
                    instance = new LEDImpl(isSupported);
                }
            }
        } else {
            if (instance.isSupported != isSupported) {
                instance = new LEDImpl(isSupported);
            }
        }
        return instance;
    }


    private LEDImpl(){
        this.isSupported = true;
    }

    private LEDImpl(boolean isSupported){
        this.isSupported = isSupported;
    }

    private void isSupported() throws NSDKException {
        if(!isSupported){
            throw new NSDKException(ErrorCode.UNSUPPORTED, "UnSupported LED Module");
        }
    }

    @Override
    public void setDisplayParameters(DisplayParameters parameters) throws NSDKException {
        if (parameters == null) {
            throw new NSDKIllegalParameterException("Display parameters shall not be null");
        }
        if (parameters.getX() >=0 && parameters.getY() >= 0) {
            this.x = parameters.getX();
            this.y = parameters.getY();
        }
        this.isHorizontal = parameters.isHorizontal();
        this.isBackgroundAlwaysDisplayed = parameters.isBackgroundAlwaysDisplayed();
    }

    /**
     * Control indicator light.(non-blocking mode)
     *
     * @param colors The type of indicator light {@link LEDColor}
     * @param state  The state of indicator light  {@link LEDState}
     * @return true  if success, false if error.
     */
    @Override
    public void setState(LEDColor[] colors, LEDState state) throws NSDKException {
        isSupported();

        if (colors == null || state == null) {
            throw new NSDKIllegalParameterException("Light colors and state shall not be null !");
        }
        int status = 0, lightColors = 0;
        if (state == LEDState.ON) {
            status = LIGHT_TURN_ON;
        } else if (state == LEDState.OFF) {
            status = LIGHT_TURN_OFF;
        } else if (state == LEDState.BLINK) {
            status = LIGHT_BLINK;
        }
        for (LEDColor color : colors) {
            if (color == LEDColor.BLUE) {
                lightColors |= LIGHT_BLUE;
            } else if (color == LEDColor.GREEN) {
                lightColors |= LIGHT_GREEN;
            } else if (color == LEDColor.YELLOW) {
                lightColors |= LIGHT_YELLOW;
            } else if (color == LEDColor.RED) {
                lightColors |= LIGHT_RED;
            }
        }
        int ret = NSDKJni.getInstance().operateLight(status, lightColors);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to set led status, result code = %d", ret));
        }
    }

    @Override
    public void setState(LEDLight[] lights) throws NSDKException {
        if (lights == null || lights.length == 0) {
            throw new NSDKIllegalParameterException("No light to set.");
        }
        int[] allParams = new int[lights.length * 3];
        LogUtils.d("LEDImpl", "Light count: " + lights.length);
        int offset  = 0;
        int count = 0;
        for (LEDLight light :  lights) {
            if (light == null) {
                continue;
            }
            if (light.getNumber() < 1 || light.getNumber() > 5) {
                throw new NSDKIllegalParameterException("Light number is from 1 to 5.");
            }
            allParams[offset] = light.getNumber();
            offset ++;

            if (light.getColor() == null) {
                throw new NSDKIllegalParameterException(String.format(Locale.US, "Please set color of Light %d.", light.getNumber()));
            }
            allParams[offset] = light.getColor().ordinal();
            offset ++;

            if (light.getState() == null) {
                throw new NSDKIllegalParameterException(String.format(Locale.US, "Please set state of Light %s.", light.getState()));
            }
            allParams[offset] = light.getState().ordinal();
            offset ++;

            count ++;
        }

        int ret = NSDKJni.getInstance().operateLightLT1118(allParams, allParams.length, count);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to set led status, result code = %d", ret));
        }
    }

    /**
     * Control indicator light flashing.(blocking mode)
     *
     * @param colors       The type of indicator light {@link LEDColor#BLUE}
     * @param count        The count of blink
     * @param timeInterval Time interval(units:ms), The total time is not more than 3 seconds.
     * @return true  if success, false if error.
     */
    @Override
    public void blink(LEDColor[] colors, int count, int timeInterval) throws NSDKException {
        isSupported();

        if (colors == null || count <= 0 || timeInterval <= 0) {
            throw new NSDKIllegalParameterException();
        }
        int lightColors = 0;
        for (LEDColor color : colors) {
            if (color == LEDColor.BLUE) {
                lightColors |= LIGHT_BLUE;
            } else if (color == LEDColor.GREEN) {
                lightColors |= LIGHT_GREEN;
            } else if (color == LEDColor.YELLOW) {
                lightColors |= LIGHT_YELLOW;
            } else if (color == LEDColor.RED) {
                lightColors |= LIGHT_RED;
            }
        }
        int ret = -1;
        if (!isVirtualLED()) {
            ret = NSDKJni.getInstance().blinkLight(count, lightColors, timeInterval);
        } else {
            ret = NSDKJni.getInstance().blinkVirtualLight(count, lightColors, timeInterval);
        }

        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }

        if (ret != ErrorCode.OK) {
            throw new NSDKNDKException(ret, String.format(Locale.US, "Failed to set flick parameter, result code = %d", ret));
        }
    }

    @Override
    public void blink(LEDLight[] ledLights, int count, int onDuration, int offDuration) throws NSDKException {
        if (ledLights == null || ledLights.length == 0) {
            throw new NSDKIllegalParameterException("LEDLights shall not be null.");
        }
        if (onDuration <= 0 || offDuration <= 0) {
            throw new NSDKIllegalParameterException("LED light on or off duration shall be > 0.");
        }
        if (onDuration > 127 || offDuration > 127) {
            throw new NSDKIllegalParameterException("LED light on or off duration shall not be more than 127.");
        }
        int ledColors = getLEDColors(ledLights);
        int ret = NSDKJni.getInstance().blinkVirtual(x, y, isHorizontal ? 1 : 0, isBackgroundAlwaysDisplayed ? 1 : 0, count, ledColors, onDuration, offDuration);
        if (ret == ErrorCode.PARAM_ERROR) {
            throw new NSDKIllegalParameterException();
        }
        if (ret != ErrorCode.OK) {
            throw new NSDKException(ret, String.format(Locale.US, "Failed to blink, ret = %d", ret));
        }
    }

    private boolean isVirtualLED() {
        String CONFIG = NlBuild.VERSION.NL_HARDWARE_CONFIG;
        String ledConfig = null;
        if (CONFIG.length() > 54) {
            ledConfig = CONFIG.substring(54, 56);
        }
        return "11".equals(ledConfig) || "12".equals(ledConfig);
    }

    private int getLEDColors(LEDLight[] ledLights) throws NSDKException {
        int ledColors = 0;
        for (LEDLight ledLight : ledLights) {
            Integer number = ledLight.getNumber();
            LEDColor color = ledLight.getColor();
            if (number != null && color != null) {
                if (number < 0) {
                    throw new NSDKIllegalParameterException("The number of light shall be > 0.");
                }
                switch (number) {
                    case 1:
                        if (color != LEDColor.BLUE) {
                            throw new NSDKIllegalParameterException("The color of the first light shall be blue. 1-> Blue, 2-> Yellow, 3-> Green, 4-> RED.");
                        }
                        ledColors |= 0xc0;
                        break;
                    case 2:
                        if (color != LEDColor.YELLOW) {
                            throw new NSDKIllegalParameterException("The color of the second light shall be yellow. 1-> Blue, 2-> Yellow, 3-> Green, 4-> RED.");
                        }
                        ledColors |= 0x0c;
                        break;
                    case 3:
                        if (color != LEDColor.GREEN) {
                            throw new NSDKIllegalParameterException("The color of the second light shall be green. 1-> Blue, 2-> Yellow, 3-> Green, 4-> RED.");
                        }
                        ledColors |= 0x30;
                        break;
                    case 4:
                        if (color != LEDColor.RED) {
                            throw new NSDKIllegalParameterException("The color of the second light shall be red. 1-> Blue, 2-> Yellow, 3-> Green, 4-> RED.");
                        }
                        ledColors |= 0x03;
                        break;
                    default:
                        throw new NSDKIllegalParameterException("The number of the light shall be range from 1 to 4.");
                }
            } else if (number != null) {
                switch (number) {
                    case 1:
                        ledColors |= 0xc0;
                        break;
                    case 2:
                        ledColors |= 0x0c;
                        break;
                    case 3:
                        ledColors |= 0x30;
                        break;
                    case 4:
                        ledColors |= 0x03;
                        break;
                    default:
                        throw new NSDKIllegalParameterException("The number of the light shall be range from 1 to 4.");
                }
            } else if (color != null) {
                switch (color) {
                    case BLUE:
                        ledColors |= 0xc0;
                        break;
                    case GREEN:
                        ledColors |= 0x30;
                        break;
                    case YELLOW:
                        ledColors |= 0x0c;
                        break;
                    case RED:
                        ledColors |= 0x03;
                        break;
                }
            }
        }
        return ledColors;
    }
}
