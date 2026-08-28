package com.hudou.autotest.base.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

import com.hudou.autotest.R;
import com.hudou.autotest.databinding.AutoTestSplashLayoutBinding;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * 启动页抽象类。
 * <p>
 * 宿主应用通过继承此类并实现 {@link #getTargetActivity()} 来启用启动加载反馈。
 * 每次冷/温启动必经启动页，显示品牌画面 + 加载动画，后台预热关键配置，
 * 预热完成后自动跳转宿主主界面。
 * </p>
 * <p>
 * 集成要求：宿主必须在 AndroidManifest 中为继承本类的 Activity 声明
 * {@code android:theme="@style/Theme.AutoTest.SplashScreen"}，
 * 否则系统启动窗口无 splash 背景，冷启动会先出现空白窗口。
 * </p>
 */
public abstract class AutoTestSplashActivity extends AppCompatActivity implements IAutoTestSplash {

    /** 预热完成标志 */
    private final AtomicBoolean preloadDone = new AtomicBoolean(false);

    /** 预热异常标志 */
    private final AtomicBoolean preloadError = new AtomicBoolean(false);

    private final Handler mainHandler = new Handler(Looper.getMainLooper());

    /** 预热开始时间戳，用于计算最小展示时长的剩余部分 */
    private long preloadStartTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // 在 super.onCreate() 之前安装 SplashScreen，冷启动时与系统启动画面衔接。
        // 前提是宿主已在 Manifest 声明 Theme.AutoTest.SplashScreen，此处不再做 setTheme 切换
        SplashScreen splashScreen = SplashScreen.installSplashScreen(this);

        super.onCreate(savedInstanceState);
        AutoTestSplashLayoutBinding binding = AutoTestSplashLayoutBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        // 处理自定义加载布局
        int customLayoutId = getSplashLoadingLayoutResId();
        if (customLayoutId != 0) {
            // 使用自定义加载布局
            binding.splashLoadingContainer.setLayoutResource(customLayoutId);
        }
        // 无论自定义还是默认布局，都 inflate 到 ViewStub
        binding.splashLoadingContainer.inflate();

        // 仅当使用默认布局时设置品牌图标
        if (customLayoutId == 0) {
            TextView splashTitle = findViewById(R.id.splash_title);
            splashTitle.setText(getSplashTitle());
            ImageView splashIcon = findViewById(R.id.splash_icon);
            splashIcon.setImageResource(getSplashIconResId());
        }

        // 启动后台预热线程
        startPreload();
    }

    private void startPreload() {
        preloadStartTime = SystemClock.uptimeMillis();
        new Thread(() -> {
            try {
                onPreloadData();
            } catch (Exception e) {
                preloadError.set(true);
                e.printStackTrace();
            } finally {
                preloadDone.set(true);
                mainHandler.post(this::scheduleNavigation);
            }
        }, "AutoTestSplashPreload").start();
    }

    /**
     * 在主线程计算剩余最小展示时长并调度跳转，替代原先的独立线程轮询等待。
     */
    private void scheduleNavigation() {
        if (isFinishing() || isDestroyed()) return;
        long elapsed = SystemClock.uptimeMillis() - preloadStartTime;
        long delay = Math.max(0L, getMinDisplayDuration() - elapsed);
        mainHandler.postDelayed(this::navigateToTarget, delay);
    }

    private void navigateToTarget() {
        if (isFinishing() || isDestroyed()) return;

        Class<?> targetClass = getTargetActivity();
        if (targetClass != null) {
            Intent intent = new Intent(this, targetClass);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }
        finish();
    }

    @Override
    protected void onDestroy() {
        mainHandler.removeCallbacksAndMessages(null);
        super.onDestroy();
    }

    @Override
    public void onPreloadData() {

    }

    @Override
    public String getSplashTitle() {
        return this.getString(R.string.auto_test);
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