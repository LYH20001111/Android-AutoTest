package com.newland.nsdkdemo.internal.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Switch;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.led.LEDColor;
import com.newland.nsdk.core.api.common.led.LEDState;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.internal.led.DisplayParameters;
import com.newland.nsdk.core.api.internal.led.LED;
import com.newland.nsdk.core.api.internal.led.LEDLight;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.AppConfig;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class LEDFragment extends InternalBaseFragment {

    private LED mLED;
    private LEDState ledState = null;


    private static final int INDEX_BLINK_LED = 1;
    private static final int INDEX_TURNON_LED = 2;
    private static final int INDEX_TURN_OFF_LED = 3;
    private static final int INDEX_LED_TEST = 4;
    private int ledStatusFlag = -1;
    private static final int LED_STATUS_BLINK = 0;
    private static final int LED_STATUS_ON = 1;
    private static final int LED_STATUS_OFF = 2;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor mEditor;
    public LEDFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_light_f);
    }

    @Override
    public void initData() {
        mLED = (LED) moduleManager.getModule(ModuleType.LED);
        sharedPreferences = context.getSharedPreferences("ledState", Context.MODE_PRIVATE);
         mEditor = sharedPreferences.edit();
    }

    @Override
    public Object getModule() {
        return LEDFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_light_operate_more, functionid = INDEX_BLINK_LED)
    private void blinkLED() {
        try {
            mLED.blink(new LEDColor[]{LEDColor.RED, LEDColor.BLUE, LEDColor.GREEN, LEDColor.YELLOW}, 5, 100);
            showMessage(context.getString(R.string.msg_mul_light_flash) + context.getString(R.string.msg_common_succ) + "\r\n", MessageTag.NORMAL);
        } catch (Exception e) {
            showErrorMessage(e, context.getString(R.string.msg_mul_light_flash));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_light_poweron, functionid = INDEX_TURNON_LED)
    private void turnOnLED() {
        try {

            mLED.setState(new LEDColor[]{LEDColor.RED,
                    LEDColor.GREEN, LEDColor.YELLOW, LEDColor.BLUE, LEDColor.YELLOW}, LEDState.ON);
            showMessage(context.getString(R.string.msg_byr_light_open) + context.getString(R.string.msg_common_succ) + "\r\n", MessageTag.NORMAL);

        } catch (Exception e) {
            showErrorMessage(e, context.getString(R.string.msg_byr_light_open));
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_light_powerof, functionid = INDEX_TURN_OFF_LED)
    private void tunOffLED() {
        try {
            mLED.setState(new LEDColor[]{LEDColor.BLUE,
                    LEDColor.RED, LEDColor.GREEN, LEDColor.YELLOW, LEDColor.YELLOW}, LEDState.OFF);
            showMessage(context.getString(R.string.msg_light_off) + context.getString(R.string.msg_common_succ) + "\r\n", MessageTag.NORMAL);

        } catch (Exception e) {
            showErrorMessage(e, context.getString(R.string.msg_light_off));
        }
    }

    @MethodGridEntity(btnnameid = R.string.msg_light_test, functionid = INDEX_LED_TEST)
    private void ledTest() {

        DialogUtils.createCustomDialog(context, R.string.msg_light_test, null, R.layout.dialog_led, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                RadioGroup rgLedFunctionSelection = view.findViewById(R.id.led_function_select_radioGroup);
                RadioButton rbLedBlink = view.findViewById(R.id.led_blink_radio);
                RadioButton rbLedOn = view.findViewById(R.id.led_on_radio);
                RadioButton rbLedOff = view.findViewById(R.id.led_off_radio);
                CheckBox cbLedRed = view.findViewById(R.id.cb_led_color_red);
                CheckBox cbLedBlue = view.findViewById(R.id.cb_led_color_blue);
                CheckBox cbLedYellow = view.findViewById(R.id.cb_led_color_yellow);
                CheckBox cbLedGreen = view.findViewById(R.id.cb_led_color_green);
                LinearLayout llLedBlinkParams = view.findViewById(R.id.linear_led_blink_params);
                llLedBlinkParams.setVisibility(View.GONE);
                rgLedFunctionSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(checkedId == rbLedBlink.getId()) {
                            ledStatusFlag = LED_STATUS_BLINK;
                            llLedBlinkParams.setVisibility(View.VISIBLE);
                        }
                        if(checkedId == rbLedOff.getId()) {
                            ledStatusFlag = LED_STATUS_OFF;
                            ledState = LEDState.OFF;
                            llLedBlinkParams.setVisibility(View.GONE);
                        }
                        if(checkedId == rbLedOn.getId()) {
                            ledStatusFlag = LED_STATUS_ON;
                            ledState = LEDState.ON;
                            llLedBlinkParams.setVisibility(View.GONE);
                        }
                        mEditor.putInt(AppConfig.SharedPreferenceConfig.LED_OPERATEION_SELECTION, checkedId);
                        mEditor.commit();
                    }
                });
                int ledOperationSelection = sharedPreferences.getInt(AppConfig.SharedPreferenceConfig.LED_OPERATEION_SELECTION, 0);
                if (ledOperationSelection == rbLedOn.getId()) {
                    rbLedOn.setChecked(true);
                } else if (ledOperationSelection == rbLedOff.getId()) {
                    //Set rbLedBlink true When open the dialog after LED_OFF
                    rbLedBlink.setChecked(true);
                } else {
                    rbLedBlink.setChecked(true );
                }

                Set<String> ledStates = sharedPreferences.getStringSet("ledState", null);
                if(ledStates != null && ledStatusFlag == LED_STATUS_ON) {
                    Object[] objLedStates = ledStates.toArray();
                    String[] ledstates = new String[4];
                    for (int i = 0; i < objLedStates.length; i++) {
                        ledstates[i] = objLedStates[i].toString();
                        switch (ledstates[i]) {
                            case "RED":
                                cbLedRed.setChecked(true);
                                break;
                            case "BLUE":
                                cbLedBlue.setChecked(true);
                                break;
                            case "YELLOW":
                                cbLedYellow.setChecked(true);
                                break;
                            case "GREEN":
                                cbLedGreen.setChecked(true);
                                break;
                            default:
                                break;
                        }
                    }
                }



            }

            @Override
            public void onResult(int id, View view) {
                Set<String> ledStates = new HashSet<>();
                int ledBlinkTimes;
                int ledBlinkTimeInterval;
                LEDColor[] ledColors;
                List<LEDColor> colorList = new ArrayList<>();
                Map<String, LEDColor> colorMap = new HashMap<>();

                EditText etLedBlinkTimes = view.findViewById(R.id.et_led_blink_times);

                EditText etLedBlinkTimeInterval = view.findViewById(R.id.et_led_blink_time_interval);
                
                CheckBox cbLedRed = view.findViewById(R.id.cb_led_color_red);
                CheckBox cbLedBlue = view.findViewById(R.id.cb_led_color_blue);
                CheckBox cbLedYellow = view.findViewById(R.id.cb_led_color_yellow);
                CheckBox cbLedGreen = view.findViewById(R.id.cb_led_color_green);
                RadioButton rbLedBlink = view.findViewById(R.id.led_blink_radio);
                LinearLayout llLedBlinkParams = view.findViewById(R.id.linear_led_blink_params);
                llLedBlinkParams.setVisibility(View.GONE);


                if (cbLedRed.isChecked()) {
                    colorList.add(LEDColor.RED);
                    ledStates.add("RED");

                }
                if (cbLedBlue.isChecked()) {
                    colorList.add(LEDColor.BLUE);
                    ledStates.add("BLUE");
                }
                if (cbLedYellow.isChecked()) {
                    colorList.add(LEDColor.YELLOW);
                    ledStates.add("YELLOW");
                }
                if (cbLedGreen.isChecked()) {
                    colorList.add(LEDColor.GREEN);
                    ledStates.add("GREEN");
                }


                ledColors = new LEDColor[colorList.size()];
                for(int i = 0; i < colorList.size(); i++) {
                    ledColors[i] = colorList.get(i);
                }

                if(rbLedBlink.isChecked()) {
                    ledBlinkTimes = Integer.parseInt(etLedBlinkTimes.getText().toString());
                    ledBlinkTimeInterval = Integer.parseInt(etLedBlinkTimeInterval.getText().toString());
                    if(ledBlinkTimes > 0 && ledBlinkTimeInterval > 0) {
                        try {
                            mLED.blink(ledColors, ledBlinkTimes, ledBlinkTimeInterval);
                            showMessage(context.getString(R.string.msg_mul_light_flash) + context.getString(R.string.msg_common_succ) + "\r\n", MessageTag.NORMAL);
                        }catch (NSDKException e) {
                            showErrorMessage(e, context.getString(R.string.msg_mul_light_flash));
                        }
                    }
                }else {
                    try {
                        mLED.setState(ledColors, ledState);
                        showMessage(context.getString(R.string.msg_light_open) + context.getString(R.string.msg_common_succ));
                    }catch (NSDKException e) {
                        showErrorMessage(e, context.getString(R.string.msg_light_test));
                    }
                }



                Object[] oLedStates = ledStates.toArray();
                for (int i = 0; i < oLedStates.length; i++) {
                    LogUtils.d("led", oLedStates[i].toString());
                }

                mEditor.putStringSet("ledState", ledStates);
                mEditor.commit();
                //reset ledColor List
                colorList.clear();
                ledColors = null;

            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.mag_p300_light_test, functionid = 5)
    private void p300LedTest() {
        LEDLight[] lights = new LEDLight[15];
        lights[0] = new LEDLight(1, LEDColor.RED, LEDState.ON);
        lights[1] = new LEDLight(1, LEDColor.GREEN, LEDState.ON);
        lights[2] = new LEDLight(1, LEDColor.BLUE, LEDState.ON);
        lights[3] = new LEDLight(2, LEDColor.RED, LEDState.ON);
        lights[4] = new LEDLight(2, LEDColor.GREEN, LEDState.ON);
        lights[5] = new LEDLight(2, LEDColor.BLUE, LEDState.ON);
        lights[6] = new LEDLight(3, LEDColor.RED, LEDState.ON);
        lights[7] = new LEDLight(3, LEDColor.GREEN, LEDState.ON);
        lights[8] = new LEDLight(3, LEDColor.BLUE, LEDState.ON);
        lights[9] = new LEDLight(4, LEDColor.RED, LEDState.ON);
        lights[10] = new LEDLight(4, LEDColor.GREEN, LEDState.ON);
        lights[11] = new LEDLight(4, LEDColor.BLUE, LEDState.ON);
        lights[12] = new LEDLight(5, LEDColor.RED, LEDState.ON);
        lights[13] = new LEDLight(5, LEDColor.GREEN, LEDState.ON);
        lights[14] = new LEDLight(5, LEDColor.BLUE, LEDState.ON);
        try {
            mLED.setState(lights);
            showMessage(context.getString(R.string.mag_p300_light_test));
        } catch (NSDKException e) {
            showErrorMessage(e, context.getString(R.string.mag_p300_light_test));
        }

    }

    @MethodGridEntity(btnnameid = R.string.msg_light_display_parameters, functionid = 6)
    private void setBlinkDisplayParameters() {
        DialogUtils.createCustomDialog(context, context.getString(R.string.msg_light_display_parameters), null, R.layout.dialog_light_display_parameters, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                RadioButton rgBlue = view.findViewById(R.id.rg_light_blue);
                RadioButton rgRed = view.findViewById(R.id.rg_light_red);
                RadioButton rgGreen = view.findViewById(R.id.rg_light_green);
                RadioButton rgYellow = view.findViewById(R.id.rg_light_yellow);
                List<LEDLight> ledLightsList = new ArrayList<>();
                if (rgBlue.isChecked()) {
                    ledLightsList.add(new LEDLight(LEDColor.BLUE));
                }
                if (rgRed.isChecked()) {
                    ledLightsList.add(new LEDLight(LEDColor.RED));
                }
                if (rgGreen.isChecked()) {
                    ledLightsList.add(new LEDLight(LEDColor.GREEN));
                }
                if (rgYellow.isChecked()) {
                    ledLightsList.add(new LEDLight(LEDColor.YELLOW));
                }
                LEDLight[] ledLight = new LEDLight[ledLightsList.size()];
                for (int i = 0; i < ledLightsList.size(); i++) {
                    ledLight[i] = ledLightsList.get(i);
                }
                DisplayParameters parameters = new DisplayParameters();
                EditText editX = view.findViewById(R.id.edit_light_setDisplayParams_x);
                EditText editY = view.findViewById(R.id.edit_light_setDisplayParams_y);
                Switch swHorizontal = view.findViewById(R.id.sw_light_setDisplayParams_isHorizontal);
                Switch swIsAlwaysDisplayBackground = view.findViewById(R.id.sw_light_setDisplayParams_isAlwaysDisplayBackground);
                parameters.setX(editX.getText().toString().isEmpty() ? -1 : Integer.parseInt(editX.getText().toString()));
                parameters.setY(editY.getText().toString().isEmpty() ? -1 : Integer.parseInt(editY.getText().toString()));
                parameters.setBackgroundAlwaysDisplayed(swIsAlwaysDisplayBackground.isChecked());
                parameters.setHorizontal(swHorizontal.isChecked());
                int onDuration = 2;
                int offDuration = 50;
                EditText editOnDuration = view.findViewById(R.id.edit_light_setDisplayParams_onDuration);
                EditText editOffDuration = view.findViewById(R.id.edit_light_setDisplayParams_offDuration);
                if (!TextUtils.isEmpty(editOnDuration.getText().toString())) {
                    onDuration = Integer.parseInt(editOnDuration.getText().toString());
                }
                if (!TextUtils.isEmpty(editOffDuration.getText().toString())) {
                    offDuration = Integer.parseInt(editOffDuration.getText().toString());
                }
                EditText editCount = view.findViewById(R.id.edit_light_setDisplayParams_count);
                int count = editCount.getText().toString().isEmpty() ? -1 : Integer.parseInt(editCount.getText().toString());
                try {
                    mLED.setDisplayParameters(parameters);
                    showMessage("Set display parameters success.");
                    mLED.blink(ledLight, count, onDuration, offDuration);
                } catch (NSDKException e) {
                    showErrorMessage(e, e.getMessage());
                }
            }
        });

    }

}
