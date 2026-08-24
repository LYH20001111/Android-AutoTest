package com.hudou.autotest.base.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewStub;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.hudou.autotest.R;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 启动页抽象类。
 * <p>
 * 宿主应用通过继承此类并实现 {@link #getTargetActivity()} 来启用启动加载反馈。
 * 每次冷/温启动必经启动页，显示品牌画面 + 加载动画，后台预热关键配置，
 * 预热完成后自动跳转宿主主界面。
 * </p>
 */
public abstract class AutoTestSplashActivity extends AppCompatActivity implements IAutoTestSplash {

    /** 预热完成标志 */
    private final AtomicBoolean preloadDone = new AtomicBoolean(false);

    /** 预热异常标志 */
    private final AtomicBoolean preloadError = new AtomicBoolean(false);

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 在 super.onCreate() 之前安装 SplashScreen，冷启动时与系统启动画面衔接
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);
        splashScreen.setKeepOnScreenCondition(() -> !isPreloadDone());

        super.onCreate(savedInstanceState);

        setTheme(R.style.Theme_AutoTest_SplashScreen);
        setContentView(R.layout.auto_test_splash_layout);

        // 处理自定义加载布局
        ViewStub loadingContainer = findViewById(R.id.splash_loading_container);
        int customLayoutId = getSplashLoadingLayoutResId();
        if (customLayoutId != 0) {
            // 使用自定义加载布局
            loadingContainer.setLayoutResource(customLayoutId);
        }
        // 无论自定义还是默认布局，都 inflate 到 ViewStub
        loadingContainer.inflate();

        // 仅当使用默认布局时设置品牌图标
        if (customLayoutId == 0) {
            ImageView splashIcon = findViewById(R.id.splash_icon);
            splashIcon.setImageResource(getSplashIconResId());
        }

        // 启动后台预热线程
        startPreload();
    }

    private void startPreload() {
        new Thread(() -> {
            try {
                onPreloadData();
            } catch (Exception e) {
                preloadError.set(true);
                e.printStackTrace();
            } finally {
                preloadDone.set(true);
            }
        }).start();

        // 主线程等待预热完成且满足最小展示时长
        waitForPreload();
    }

    private void waitForPreload() {
        new Thread(() -> {
            long startTime = System.currentTimeMillis();
            long minDuration = getMinDisplayDuration();

            // 等待预热完成
            while (!isPreloadDone()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            // 确保最小展示时长
            long elapsed = System.currentTimeMillis() - startTime;
            if (elapsed < minDuration) {
                try {
                    Thread.sleep(minDuration - elapsed);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }

            // 在主线程跳转目标 Activity
            mainHandler.post(this::navigateToTarget);
        }).start();
    }

    private void navigateToTarget() {
        if (isFinishing()) return;

        Class<?> targetClass = getTargetActivity();
        if (targetClass != null) {
            Intent intent = new Intent(this, targetClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        finish();
    }

    @Override
    public void onPreloadData() {

    }

    @Override
    public int getSplashIconResId() {
        return R.drawable.auto_test_ic_launcher_foreground;
    }

    @Override
    public int getSplashLoadingLayoutResId() {
        return 0;
    }

    @Override
    public long getMinDisplayDuration() {
        return 1200L;
    }

    /**
     * 查询预热是否完成。
     * <p>
     * 宿主可重写此方法添加额外的完成条件。
     * </p>
     *
     * @return 预热完成返回 true
     */
    protected boolean isPreloadDone() {
        return preloadDone.get();
    }

    /**
     * 查询预热是否发生异常。
     *
     * @return 预热异常返回 true
     */
    protected boolean isPreloadError() {
        return preloadError.get();
    }
}