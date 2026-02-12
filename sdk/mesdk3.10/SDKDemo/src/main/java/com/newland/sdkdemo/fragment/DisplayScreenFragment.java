package com.newland.sdkdemo.fragment;

import android.content.Context;
import android.view.View;
import android.widget.EditText;

import com.newland.sdk.module.displayScreen.DisplayScreenModule;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;

import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

/**
 * Author by bxy, Date on 2019/5/11 0011.
 */
public class DisplayScreenFragment extends BaseFragment {

    private DisplayScreenModule displayScreen;
    private int i = 0;

    public DisplayScreenFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_guest_display);
    }

    @Override
    public void initData() {
        displayScreen = moduleManage.getDisplayScreenModule();
    }

    @Override
    public Object getModule() {
        return DisplayScreenFragment.this;
    }

    @MethodGridEntity(btnnameid = R.string.tv_gd_set_bright,functionid = 1)
    private void setLedBrightness(){
        try {
            boolean bool  = displayScreen.setBrightness(i);
            if(bool){
                showMessage(context.getString(R.string.msg_gd_set_bright_suc) + "：" + i, MessageTag.DATA);
                if (i == 7) {
                    i = 0;
                } else {
                    i++;
                }
            }else {
                showMessage(context.getString(R.string.msg_gd_set_bright_fail), MessageTag.DATA);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @MethodGridEntity(btnnameid = R.string.tv_gd_show_amount,functionid = 2)
    private void showLedAmount(){
        DialogUtils.createCustomDialog(context, context.getString(R.string.msg_gd_please_input_amount), null, R.layout.dialog_amtinput, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View dialogView) {
                if(id == -1){//cancel
                    return;
                }
                if(id == 0){
                    EditText editText = dialogView.findViewById(R.id.edit_amt_input);
                    final String value = editText.getText().toString();
                    try {
                        boolean bool = displayScreen.showMessage(value);
                        if(bool){
                            showMessage(context.getString(R.string.msg_gd_display_suc_amount)+value, MessageTag.DATA);
                        }else {
                            showMessage(context.getString(R.string.msg_gd_display_fail), MessageTag.DATA);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    @MethodGridEntity(btnnameid = R.string.tv_gd_close,functionid = 3)
    private void closeLed(){
        try {
            showMessage(context.getString(R.string.msg_gd_close), MessageTag.TIP);
            boolean bool  = displayScreen.turnOffLed();
            if(bool){
                showMessage(context.getString(R.string.msg_gd_close_suc), MessageTag.DATA);
            }else {
                showMessage(context.getString(R.string.msg_gd_close_fail), MessageTag.DATA);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
