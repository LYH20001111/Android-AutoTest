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
}
