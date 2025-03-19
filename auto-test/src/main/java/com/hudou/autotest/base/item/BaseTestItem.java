package com.hudou.autotest.base.item;

import android.graphics.Color;

//import com.hudou.autotest.MainActivity;
import androidx.annotation.Nullable;

import com.hudou.autotest.util.SharedPreferencesUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;



public class BaseTestItem extends BaseTestCase{

    /**
     * 记录测试通过
     */
    public void recordPass(){
        recordPass(null);
    }
    public void recordPass(@Nullable String message){
        postValue(Color.GREEN, "测试通过" + (message == null ? "" : message));
    }

    /**
     * 记录测试失败
     */
    public void recordFail(){
        recordFail(null);
    }
    public void recordFail(@Nullable String message){
        postValue(Color.RED, "测试失败" + (message == null ? "" : message));
    }

    /**
     * 记录测试记录信息
     * @param message
     */
    public void recordMessage(@NotNull String message){
        if (isDebugMode()) {
            postValue(Color.BLACK, message);
        }
    }

    /**
     * 设置调试模式
     * @param debugMode
     */
    public void setDebugMode(boolean debugMode){
        SharedPreferencesUtil.save(SharedPreferencesUtil.DEBUG_MODE, debugMode);
    }

    /**
     * 判断调试模式
     * @return
     */
    private boolean isDebugMode(){
        return SharedPreferencesUtil.get(SharedPreferencesUtil.DEBUG_MODE, true);
    }





    @Override
    public void onCaseStart(Method method) {

    }

    @Override
    public void onCaseFinish(Method method) {

    }

}
