package com.hudou.autotest.base.item;

import static com.hudou.autotest.base.activity.AutoTestMainActivity.resultData;
import static com.hudou.autotest.base.activity.AutoTestMainActivity.resultItemList;

import android.graphics.Color;

import androidx.annotation.Nullable;

import com.hudou.autotest.constant.ResultData;
import com.hudou.autotest.constant.ResultItem;
import com.hudou.autotest.constant.SetMode;
import com.hudou.autotest.util.SharedPreferencesUtil;

import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Method;


public class AutoTestTestItem extends BaseTestCase{

    /**
     * 记录测试通过
     */
    public void recordPass(){
        recordPass(null);
    }

    public void recordPass(@Nullable String message){
        postValue(Color.GREEN, "测试通过" + (message == null ? "" : message));
        resultData.setResult("测试通过");
    }

    /**
     * 记录测试失败
     */
    public void recordFail(){
        recordFail(null);
    }

    public void recordFail(@Nullable String message){
        postValue(Color.RED, "测试失败" + (message == null ? "" : message));
        resultData.setResult("测试失败");
    }

    /**
     * 记录测试记录信息
     * @param message
     */
    public void recordMessage(@NotNull String message){
//        if (isDebugMode()) {
//            postValue(Color.BLACK, message);
//        }
        postValue(isDebugMode(), Color.BLACK, message);
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


    /**
     * 该方法只有在onCaseEnd时调用是才生效
     * @param method 案例方法
     * @param englishDescription 需要设置的英文描述
     * @param setMode 设置模式
     */
    public void setEnDes(Method method, String englishDescription, SetMode setMode){
        for (ResultItem resultItem : resultItemList) {
            if (resultItem.getClz().equals(this.getClass())) {
                for (ResultData resultData : resultItem.getResultDataList()){
                    if (resultData.getId().equals(method.getName())){
                        switch (setMode) {
                            case EMPTY_ADD://TestCase设置时，使用设置的；未设置时，否则使用setEnDes设置的
                                if (resultData.getEnglishDescription() == null
                                        || resultData.getEnglishDescription().equals("")) {
                                    resultData.setEnglishDescription(englishDescription);
                                }
                                break;
                            case ALWAYS_REPLACE://无论TestCase是否设置enDes，直接替换
                                resultData.setEnglishDescription(englishDescription);
                                break;
                        }
                        break;
                    }
                }
            }
        }
    }



    @Override
    public void onCaseStart(Method method) {

    }

    @Override
    public void onCaseFinish(Method method) {

    }

    @Override
    public void onCaseEnd(Method method) {

    }
}
