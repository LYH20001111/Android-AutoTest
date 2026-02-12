package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.util.Log;

import com.newland.sdk.module.displayScreen.DisplayScreenModule;
import com.newland.sdk.module.externalKeyboard.KeyBoardCode;
import com.newland.sdk.module.externalKeyboard.ExtKeyboardModule;
import com.newland.sdk.module.externalKeyboard.KeyboardListener;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;
import com.newland.sdkdemo.utils.MessageTag;

public class ExtKeyboardFragment extends BaseFragment {

    private ExtKeyboardModule extKeyboard;
    private DisplayScreenModule displayScreen;

    private String inputData = "";

    public ExtKeyboardFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.module_ext_keyboard);
    }

    @Override
    public void initData() {
        extKeyboard = moduleManage.getExtKeyboardModule();
        displayScreen = moduleManage.getDisplayScreenModule();
    }

    @Override
    public Object getModule() {
        return ExtKeyboardFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_gd_is_use,functionid = 1)
    private void isKeyboardVaild(){
        try {
            boolean result = extKeyboard.isValid();
            if (result) {
                showMessage(context.getString(R.string.msg_gd_external_pin_use), MessageTag.TIP);
            } else {
                showMessage(context.getString(R.string.msg_gd_external_pin_useless), MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error)+e,MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_gd_start_input,functionid = 2)
    private void startInput(){
        try {
            showMessage(context.getString(R.string.msg_gd_start_input), MessageTag.TIP);
            boolean bool  = extKeyboard.startKeyInput(30, new KeyboardListener() {
                @Override
                public void onStart() {
                    try {
                        extKeyboard.showMessage(context.getString(R.string.msg_gd_input_amount));
                        displayScreen.showMessage("0");
                        inputData = "";
                    } catch (Exception e) {
                        e.printStackTrace();
                        showMessage(context.getString(R.string.msg_error)+e,MessageTag.ERROR);
                    }
                }

                @Override
                public void onError() {
                    showMessage(context.getString(R.string.msg_gd_input_error), MessageTag.ERROR);
                    extKeyboard.stopInput();
                }

                @Override
                public void onTimeOut() {
                    showMessage(context.getString(R.string.msg_gd_overtime), MessageTag.DATA);
                    extKeyboard.showMessage(context.getString(R.string.msg_gd_overtime));
                    extKeyboard.stopInput();
                }

                @Override
                public void onKeyPress(KeyBoardCode keyCode, String currValue) {
                    Log.e("onKeyPress", keyCode.toString());
                    if(keyCode == KeyBoardCode.KEY_NUM){
                        if (inputData.equals("0")) {
                            inputData = currValue;
                        } else {
                            inputData = inputData + currValue;
                        }
                        showMessage(context.getString(R.string.msg_gd_input_number)+inputData, MessageTag.DATA);
                        extKeyboard.showMessage(inputData);
                    }else if(keyCode == KeyBoardCode.KEY_DOT){
                        inputData = inputData + currValue;
                        showMessage(context.getString(R.string.msg_gd_input_point)+inputData, MessageTag.DATA);
                        extKeyboard.showMessage(inputData);
                    }else if(keyCode == KeyBoardCode.KEY_INVALID){
                        showMessage(context.getString(R.string.msg_gd_input_useless)+currValue, MessageTag.DATA);
                    }else if(keyCode == KeyBoardCode.KEY_OK){
                        showMessage(context.getString(R.string.msg_gd_input_correct)+inputData, MessageTag.DATA);
                        try {
                            boolean bool =  extKeyboard.showMessage(context.getString(R.string.msg_gd_receipt_suc));
                            if(bool){
                                showMessage(context.getString(R.string.msg_gd_display_suc), MessageTag.DATA);
                            }else{
                                showMessage(context.getString(R.string.msg_gd_display_fail), MessageTag.DATA);
                            }
                        }  catch (Exception e) {
                            e.printStackTrace();
                        }
                        extKeyboard.setValidKeys(new KeyBoardCode[]{KeyBoardCode.KEY_OK});
                    }else if(keyCode == KeyBoardCode.KEY_CANCEL){
                        showMessage(context.getString(R.string.msg_gd_input_cancel_current_value) + inputData, MessageTag.DATA);
                        extKeyboard.stopInput();
                    }else if(keyCode == KeyBoardCode.KEY_BACKSPACE){
                        if (inputData.length() != 0) {
                            if (inputData.length() == 1) {
                                inputData = "0";
                            } else {
                                inputData = inputData.substring(0, inputData.length() - 1);
                            }
                        }
                        showMessage(context.getString(R.string.msg_gd_input_backspace)+inputData, MessageTag.DATA);
                        extKeyboard.showMessage(inputData);
                    }
                    if(keyCode != KeyBoardCode.KEY_FUN1 && keyCode != KeyBoardCode.KEY_FUN2 &&
                            keyCode != KeyBoardCode.KEY_FUN3 && keyCode != KeyBoardCode.KEY_FUN4){
                        displayScreen.showMessage(inputData);
                    }
                }
            });
            if(bool){
                showMessage(context.getString(R.string.msg_gd_start_input_suc), MessageTag.DATA);
            }else {
                showMessage(context.getString(R.string.msg_gd_start_input_fail), MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error)+e,MessageTag.ERROR);
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_gd_stop_input,functionid = 3)
    private void stopInput(){
        try {
            showMessage(context.getString(R.string.msg_gd_stop_input), MessageTag.TIP);
            boolean bool  = extKeyboard.stopInput();
            if(bool){
                showMessage(context.getString(R.string.msg_gd_stop_input_suc), MessageTag.DATA);
            }else {
                showMessage(context.getString(R.string.msg_gd_stop_input_fail), MessageTag.ERROR);
            }
        } catch (Exception e) {
            e.printStackTrace();
            showMessage(context.getString(R.string.msg_error)+e,MessageTag.ERROR);

        }
    }

}
