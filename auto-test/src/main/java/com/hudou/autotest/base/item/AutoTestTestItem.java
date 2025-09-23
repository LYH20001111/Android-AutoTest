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


public class AutoTestTestItem extends BaseTestCase {
    private volatile boolean isResultRecorded = false;
    private final Object lock = new Object();

    /**
     * 记录测试通过
     */
    public void recordPass() {
        recordPass(null);
    }

    public void recordPass(@Nullable String message) {
        synchronized (resultData) { // 确保线程安全
            resultData.setResult("测试通过");
        }
        synchronized (lock) {
            isResultRecorded = true;
            lock.notify(); // 通知等待的线程
        }
        postValue(Color.GREEN, "测试通过" + (message == null ? "" : ("\n" + message)));
    }

    /**
     * 记录测试失败
     */
    public void recordFail() {
        recordFail(null);
    }

    public void recordFail(@Nullable String message) {
        synchronized (resultData) {
            resultData.setResult("测试失败");
        }
        synchronized (lock) {
            isResultRecorded = true;
            lock.notify(); // 通知等待的线程
        }
        postValue(Color.RED, "测试失败" + (message == null ? "" : ("\n" + message)));
    }

    /**
     * 记录测试记录信息
     *
     * @param message
     */
    public void recordMessage(@NotNull String message) {
        postValue(isDebugMode(), Color.BLACK, message);
    }

    public void recordMessage(int color, String message) {
        postValue(isDebugMode(), color, message);
    }

    /**
     * 设置调试模式
     *
     * @param debugMode
     */
    public void setDebugMode(boolean debugMode) {
        SharedPreferencesUtil.save(SharedPreferencesUtil.DEBUG_MODE, debugMode);
    }

    /**
     * 判断调试模式
     *
     * @return
     */
    private boolean isDebugMode() {
        return SharedPreferencesUtil.get(SharedPreferencesUtil.DEBUG_MODE, true);
    }


    /**
     * 该方法只有在onCaseEnd时调用是才生效
     *
     * @param method             案例方法
     * @param englishDescription 需要设置的英文描述
     * @param setMode            设置模式
     */
    public void setEnDes(Method method, String englishDescription, SetMode setMode) {
        for (ResultItem resultItem : resultItemList) {
            if (resultItem.getClz().equals(this.getClass())) {
                for (ResultData resultData : resultItem.getResultDataList()) {
                    if (resultData.getId().equals(method.getName())) {
                        switch (setMode) {
                            case EMPTY_ADD://TestCase设置时，使用设置的；未设置时，否则使用setEnDes设置的
                                if (resultData.getEnglishDescription() == null
                                        || "".equals(resultData.getEnglishDescription())) {
                                    resultData.setEnglishDescription(englishDescription);
                                }
                                break;
                            case ALWAYS_REPLACE://无论TestCase是否设置enDes，直接替换
                                resultData.setEnglishDescription(englishDescription);
                                break;
                            default:
                                break;
                        }
                        break;
                    }
                }
            }
        }
    }

    private void resetResultRecorded() {
        synchronized (lock) {
            isResultRecorded = false;
        }
    }


    @Override
    public void onCaseStart(Method method) {
        resetResultRecorded();
    }

    @Override
    public void onCaseFinish(Method method) {

    }

    @Override
    public void onCaseEnd(Method method) {

    }

    @Override
    protected void waitForResult(Method method) throws InterruptedException {
        synchronized (lock) {
            while (!isResultRecorded) {
                lock.wait(); // 阻塞当前线程，直到被notify
            }
            // 重置状态
            isResultRecorded = false;
        }
    }
}
