package com.hudou.autotest.base.activity;

import android.content.Context;

import com.hudou.autotest.R;

public interface IAutoTestSplash {

    /**
     * 返回宿主主界面的 Activity 类。
     * <p>
     * 宿主必须实现此方法，返回主界面 Activity 的 {@code Class} 对象。
     * </p>
     *
     * @return 主界面 Activity 的 Class
     */
    Class<?> getTargetActivity();

    /**
     * 预处理数据。
     * <p>
     * 在后台线程中执行，用于预热关键配置。
     * 宿主可重写此方法添加自定义初始化逻辑。
     * 默认实现初始化数据库实例。
     * </p>
     */
    void onPreloadData();

    /**
     * 获取启动页品牌图标资源 ID。
     * <p>
     * 宿主可重写此方法返回自定义图标 drawable 资源 ID。
     * 默认返回 {@link R.drawable#auto_test_ic_launcher_foreground}。
     * </p>
     *
     * @return 图标 drawable 资源 ID
     */
    int getSplashIconResId();

    /**
     * 获取启动页自定义加载布局资源 ID。
     * <p>
     * 宿主可重写此方法返回自定义布局资源 ID，替换默认的品牌图标 + 进度条 + 文案加载区域。
     * 返回 0 或未重写时使用默认加载布局。
     * 使用自定义布局时，宿主需自行管理布局内的所有视图（如图标、动画等）。
     * </p>
     *
     * @return 自定义布局资源 ID，默认返回 0（使用默认加载布局）
     */
    int getSplashLoadingLayoutResId();

    /**
     * 获取最小展示时长。
     * <p>
     * 启动页至少展示此时长（毫秒），保证动画可见一轮。
     * 宿主可重写自定义时长。
     * </p>
     *
     * @return 最小展示时长（毫秒），默认 1200ms
     */
    long getMinDisplayDuration();
}
