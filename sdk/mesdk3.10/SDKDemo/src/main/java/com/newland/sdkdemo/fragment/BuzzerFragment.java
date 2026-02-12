package com.newland.sdkdemo.fragment;

import android.content.Context;

import com.newland.sdk.module.buzzer.BuzzerModule;
import com.newland.sdkdemo.R;
import com.newland.sdkdemo.adapter.LayoutMode;
import com.newland.sdkdemo.annotation.MethodGridEntity;

/**
 * Author by bxy, Date on 2019/12/19.
 */
public class BuzzerFragment extends BaseFragment{

    private BuzzerModule buzzerModule;
    public BuzzerFragment(Context context) {
        super(context, LayoutMode.GRID);
    }

    @Override
    public String title() {
        return context.getString(R.string.tv_buzzer_f);
    }

    @Override
    public void initData() {
        buzzerModule = moduleManage.getBuzzerModule();
    }

    @Override
    public Object getModule() {
        return BuzzerFragment.this;
    }

    @Override
    public int getSpanCount() {
        return 2;
    }

    private static final int INDEX_START = 1;
    private static final int INDEX_STOP = 2;

    @MethodGridEntity(btnnameid = R.string.buzzer_play, functionid = INDEX_START)
    private void start(){
        showMessage(context.getString(R.string.buzzer_play));
        buzzerModule.play(2,200,500);
    }

    @MethodGridEntity(btnnameid = R.string.buzzer_stop, functionid = INDEX_STOP)
    private void stop(){
        showMessage(context.getString(R.string.buzzer_stop));
        buzzerModule.stop();
    }
}
