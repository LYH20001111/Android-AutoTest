package com.newland.sdkdemo.fragment;

import android.content.Context;

import com.newland.sdk.module.light.IndicatorLightModule;
import com.newland.sdk.module.light.LightColor;
import com.newland.sdk.module.light.LightState;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.utils.MessageTag;

/**
 * @description:  Indicator light
 * @author: Lindan 
 * @create: 2019/8/6
 */
public class LightFragment extends BaseFragment {
    private IndicatorLightModule indicatorLight;
    private static final int INDEX_BLINK_LED = 1;
    private static final int INDEX_TURNON_LED = 2;
    private static final int INDEX_TURN_OFF_LED = 3;

    public LightFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_light_f);
    }

    @Override
    public void initData() {
        indicatorLight=moduleManage.getIndicatorLightModule();
    }

    @Override
    public Object getModule() {
        return LightFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_light_operate_more,functionid = INDEX_BLINK_LED)
    private void blinkLED(){
        try {
            Boolean operateMultLightResult = indicatorLight.blinkLight(new LightColor[] { LightColor.RED, LightColor.BLUE, LightColor.GREEN,LightColor.YELLOW},5,100);
            if (operateMultLightResult) {
                showMessage(context.getString(R.string.msg_mul_light_flash) + context.getString(R.string.msg_common_succ) + "\r\n", MessageTag.NORMAL);
            } else {
                showMessage(context.getString(R.string.msg_mul_light_flash) + context.getString(R.string.msg_common_failed) + "\r\n", MessageTag.ERROR);
            }

            //---------method 2: blink until invoking turnoff method-----------//
//            Boolean operateMultLightResult1 = indicatorLight.operateLight(new LightColor[] { LightColor.RED },LightState.BLINK);
//            if (operateMultLightResult1) {
//                showMessage(context.getString(R.string.msg_mul_light_flash) + context.getString(R.string.msg_common_succ) + "\r\n", MessageTag.NORMAL);
//            } else {
//                showMessage(context.getString(R.string.msg_mul_light_flash) + context.getString(R.string.msg_common_failed) + "\r\n", MessageTag.NORMAL);
//            }

        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_mul_light_flash) + context.getString(R.string.common_exception) + e + "\r\n", MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_light_poweron,functionid = INDEX_TURNON_LED)
    private void turnOnLED(){
        try {

            Boolean turnOnLightResult = indicatorLight.operateLight(new LightColor[] { LightColor.RED,
                    LightColor.GREEN, LightColor.YELLOW, LightColor.BLUE,LightColor.YELLOW}, LightState.TURNON);
            if (turnOnLightResult) {
                showMessage(context.getString(R.string.msg_byr_light_open) + context.getString(R.string.msg_common_succ) + "\r\n", MessageTag.NORMAL);
            } else {
                showMessage(context.getString(R.string.msg_byr_light_open) + context.getString(R.string.msg_common_failed) + "\r\n", MessageTag.ERROR);
            }

        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_byr_light_open) + context.getString(R.string.common_exception) + e + "\r\n", MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_light_powerof,functionid = INDEX_TURN_OFF_LED)
    private void tunOffLED(){
        try {
            Boolean trunOffLightResult = indicatorLight.operateLight(new LightColor[] { LightColor.BLUE,
                    LightColor.RED, LightColor.GREEN, LightColor.YELLOW,LightColor.YELLOW},LightState.TURNOFF);
            if (trunOffLightResult) {
                showMessage(context.getString(R.string.msg_light_off) + context.getString(R.string.msg_common_succ) + "\r\n", MessageTag.NORMAL);
            } else {
                showMessage(context.getString(R.string.msg_light_off) + context.getString(R.string.msg_common_failed) + "\r\n", MessageTag.ERROR);
            }

        } catch (Exception e) {
            showMessage(context.getString(R.string.msg_light_off) + context.getString(R.string.common_exception) + e + "\r\n", MessageTag.ERROR);
        }
    }
}
