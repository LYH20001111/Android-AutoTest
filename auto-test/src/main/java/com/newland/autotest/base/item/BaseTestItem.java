package com.newland.autotest.base.item;

import android.graphics.Color;

//import com.newland.autotest.MainActivity;
import com.newland.autotest.base.activity.BaseMainActivity;
import com.newland.autotest.constant.ShowMessage;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;

import javax.annotation.Nullable;

public class BaseTestItem extends BaseTestCase{


    public void recordPass(@Nullable String message){
        BaseMainActivity.mShowMessage.postValue(new ShowMessage(Color.GREEN, "测试通过" + (message == null ? "" : message)));
        waitMessage();
    }

    public void recordFail(@Nullable String message){
        BaseMainActivity.mShowMessage.postValue(new ShowMessage(Color.RED, "测试失败" + (message == null ? "" : message)));
        waitMessage();
    }

    public void recordNormal(@NotNull String message){
        BaseMainActivity.mShowMessage.postValue(new ShowMessage(Color.BLACK, message));
        waitMessage();
    }

    private void waitMessage(){
        try {
            Thread.sleep(15);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public void onCaseStart(Method method) {

    }

    @Override
    public void onCaseFinish(Method method) {

    }

}
