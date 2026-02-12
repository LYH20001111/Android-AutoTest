package com.newland.nsdkdemo.external.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.View;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.newland.nsdk.core.api.common.ModuleType;
import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.led.LEDColor;
import com.newland.nsdk.core.api.common.led.LEDState;
import com.newland.nsdk.core.api.external.led.ExtLED;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.adapter.LayoutMode;
import com.newland.nsdkdemo.common.annotation.MethodGridEntity;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ExtLEDFragment extends ExtBaseFragment {

    private ExtLED mLED;
    private static final int INDEX_BLINK_LED = 1;
    private static final int INDEX_TURNON_LED = 2;
    private static final int INDEX_TURN_OFF_LED = 3;
    private static final int INDEX_LED_TEST = 4;
    private static final int INDEX_LED_AUTO_TEST = 5;

    private LEDState ledState;
    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor mEditor;



    public ExtLEDFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_extlight_f);
    }

    @Override
    public void initData() {
        mLED = (ExtLED) moduleManager.getModule(ModuleType.EXT_LED);
        sharedPreferences = context.getSharedPreferences("ExtLED", Context.MODE_PRIVATE);
        mEditor = sharedPreferences.edit();
    }

    @Override
    public Object getModule() {
        return ExtLEDFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_light_operate_more, functionid = INDEX_BLINK_LED)
    private void blinkLED() {
        try {
            mLED.setState(new LEDColor[]{LEDColor.RED, LEDColor.BLUE, LEDColor.GREEN, LEDColor.YELLOW}, LEDState.BLINK);
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
    private void LEDTest() {
        DialogUtils.createCustomDialog(context, R.string.msg_light_test, null, R.layout.dialog_led, new DialogUtils.CustomDialogCallback2() {
            @Override
            public void onInit(View view) {
                CheckBox cbRed = view.findViewById(R.id.cb_led_color_red);
                CheckBox cbBlue = view.findViewById(R.id.cb_led_color_blue);
                CheckBox cbYellow = view.findViewById(R.id.cb_led_color_yellow);
                CheckBox cbGreen = view.findViewById(R.id.cb_led_color_green);
                RadioGroup rgFunctionSeletion = view.findViewById(R.id.led_function_select_radioGroup);
                RadioButton rbBlink = view.findViewById(R.id.led_blink_radio);
                RadioButton rbOn = view.findViewById(R.id.led_on_radio);
                RadioButton rbOff = view.findViewById(R.id.led_off_radio);
                LinearLayout llBlinkParams = view.findViewById(R.id.linear_led_blink_params);
                llBlinkParams.setVisibility(View.GONE);
                rgFunctionSeletion.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(checkedId == rbBlink.getId()) {
                            llBlinkParams.setVisibility(View.GONE);
                        }
                        if(checkedId == rbOff.getId()) {
                            ledState = LEDState.OFF;
                            llBlinkParams.setVisibility(View.GONE);
                        }
                        if(checkedId == rbOn.getId()) {
                            ledState = LEDState.ON;
                            llBlinkParams.setVisibility(View.GONE);
                        }
                    }
                });

                Set<String> ledStates = sharedPreferences.getStringSet("ExtLEDState", null);
                if(ledStates != null) {
                    Object[] objLedStates = ledStates.toArray();
                    String[] ledStatus = new String[4];
                    for (int i = 0; i < objLedStates.length; i++) {
                        ledStatus[i] = objLedStates[i].toString();
                        switch (ledStatus[i]) {
                            case "RED":
                                cbRed.setChecked(true);
                                break;
                            case "BLUE":
                                cbBlue.setChecked(true);
                                break;
                            case "YELLOW":
                                cbYellow.setChecked(true);
                                break;
                            case "GREEN":
                                cbGreen.setChecked(true);
                                break;
                            default:
                                break;
                        }
                    }
                }



            }

            @Override
            public void onResult(int id, View view) {
                LEDColor[] ledColors;
                List<LEDColor> colorList = new ArrayList<>();


                CheckBox cbRed = view.findViewById(R.id.cb_led_color_red);
                CheckBox cbBlue = view.findViewById(R.id.cb_led_color_blue);
                CheckBox cbYellow = view.findViewById(R.id.cb_led_color_yellow);
                CheckBox cbGreen = view.findViewById(R.id.cb_led_color_green);
                RadioGroup rgFunctionSelection = view.findViewById(R.id.led_function_select_radioGroup);
                RadioButton rbBlink = view.findViewById(R.id.led_blink_radio);
                RadioButton rbOn = view.findViewById(R.id.led_on_radio);
                RadioButton rbOff = view.findViewById(R.id.led_off_radio);
                LinearLayout llBlinkParams= view.findViewById(R.id.linear_led_blink_params);
                llBlinkParams.setVisibility(View.GONE);
                rgFunctionSelection.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(RadioGroup group, int checkedId) {
                        if(checkedId == rbBlink.getId()) {
                            llBlinkParams.setVisibility(View.GONE);
                        }
                        if(checkedId == rbOff.getId()) {
                            ledState = LEDState.OFF;
                            llBlinkParams.setVisibility(View.GONE);
                        }
                        if(checkedId == rbOn.getId()) {
                            ledState = LEDState.ON;
                            llBlinkParams.setVisibility(View.GONE);
                        }
                    }
                });

                Set<String> ledStates = new HashSet<>();
                if (cbRed.isChecked()) {
                    colorList.add(LEDColor.RED);
                    ledStates.add("RED");

                }
                if (cbBlue.isChecked()) {
                    colorList.add(LEDColor.BLUE);
                    ledStates.add("BLUE");
                }
                if (cbYellow.isChecked()) {
                    colorList.add(LEDColor.YELLOW);
                    ledStates.add("YELLOW");
                }
                if (cbGreen.isChecked()) {
                    colorList.add(LEDColor.GREEN);
                    ledStates.add("GREEN");
                }


                ledColors = new LEDColor[colorList.size()];
                for(int i = 0; i < colorList.size(); i++) {
                    ledColors[i] = colorList.get(i);
                }

                try {
                    mLED.setState(ledColors, ledState);
                    showMessage(context.getString(R.string.msg_light_test) + context.getString(R.string.msg_common_succ));
                }catch (NSDKException e) {
                    showErrorMessage(e, context.getString(R.string.msg_light_test));
                }

                mEditor.putStringSet("ExtLEDState", ledStates);
                mEditor.commit();

                colorList.clear();
                ledColors = null;
                }
        });
    }

    @MethodGridEntity(btnnameid = R.string.msg_light_test_auto, functionid = INDEX_LED_AUTO_TEST)
    private void LEDAutoTest() {
        try {
            mLED.setState(new LEDColor[]{LEDColor.FIRST}, LEDState.ON);
            showMessage("First Led light ON.");
            Thread.sleep(1000);
            mLED.setState(new LEDColor[]{LEDColor.SECOND}, LEDState.ON);
            showMessage("Second Led light ON.");
            Thread.sleep(1000);
            mLED.setState(new LEDColor[]{LEDColor.THIRD}, LEDState.ON);
            showMessage("Third Led light ON.");
            Thread.sleep(1000);
            mLED.setState(new LEDColor[]{LEDColor.FORTH}, LEDState.ON);
            showMessage("Forth Led light ON.");
            Thread.sleep(1000);
            mLED.setState(new LEDColor[]{LEDColor.FIRST}, LEDState.BLINK);
            showMessage("First Led light Blink.");
            Thread.sleep(1000);
            mLED.setState(new LEDColor[]{LEDColor.SECOND}, LEDState.BLINK);
            showMessage("Second Led light Blink.");
            Thread.sleep(1000);
            mLED.setState(new LEDColor[]{LEDColor.THIRD}, LEDState.BLINK);
            showMessage("Third Led light Blink.");
            Thread.sleep(1000);
            mLED.setState(new LEDColor[]{LEDColor.FORTH}, LEDState.BLINK);
            showMessage("Forth Led light Blink.");
            Thread.sleep(1000);
            mLED.setState(new LEDColor[]{LEDColor.FIRST}, LEDState.OFF);
            showMessage("First Led light Off.");
            Thread.sleep(1000);
            mLED.setState(new LEDColor[]{LEDColor.SECOND}, LEDState.OFF);
            showMessage("Second Led light Off.");
            Thread.sleep(1000);
            mLED.setState(new LEDColor[]{LEDColor.THIRD}, LEDState.OFF);
            showMessage("Third Led light Off.");
            Thread.sleep(1000);
            mLED.setState(new LEDColor[]{LEDColor.FORTH}, LEDState.OFF);
            showMessage("Forth Led light Off.");
            Thread.sleep(1000);
            showMessage("LED Auto Test End");
        } catch (Exception e) {
            showErrorMessage(e, "LED Auto Test Error");
        }
    }
}
