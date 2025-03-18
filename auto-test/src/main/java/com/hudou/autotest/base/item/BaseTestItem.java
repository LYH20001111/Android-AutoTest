package com.hudou.autotest.base.item;

import android.graphics.Color;

//import com.hudou.autotest.MainActivity;
import androidx.annotation.Nullable;

import com.hudou.autotest.base.activity.BaseMainActivity;
import com.hudou.autotest.constant.ShowMessage;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;



public class BaseTestItem extends BaseTestCase{


    public void recordPass(@Nullable String message){
        postValue(Color.GREEN, "测试通过" + (message == null ? "" : message));
    }

    public void recordFail(@Nullable String message){
        postValue(Color.RED, "测试失败" + (message == null ? "" : message));
    }

    public void recordNormal(@NotNull String message){
        postValue(Color.BLACK, message);
    }

    @Override
    public void onCaseStart(Method method) {

    }

    @Override
    public void onCaseFinish(Method method) {

    }

}
