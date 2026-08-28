package com.hudou.autotest;

import com.hudou.autotest.base.activity.AutoTestSplashActivity;

/**
 * 示例宿主启动页。
 * <p>
 * 继承 {@link AutoTestSplashActivity}，作为应用 Launcher 入口。
 * 显示品牌启动页 + 加载动画，预热完成后跳转 {@link MainActivity}。
 * </p>
 */
public class SplashActivity extends AutoTestSplashActivity {

    @Override
    public Class<?> getTargetActivity() {
        return MainActivity.class;
    }

    @Override
    public void onPreloadData() {
        super.onPreloadData();
        try {
            Thread.sleep(7000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public long getMinDisplayDuration() {
        return 10000;
    }

//    @Override
//    public String getSplashTitle() {
//        return "PaymentService";
//    }

    @Override
    public int getSplashIconResId() {
        return super.getSplashIconResId();
    }

//    @Override
//    public int getSplashLoadingLayoutResId() {
//        return R.layout.activity_main;
//    }
}