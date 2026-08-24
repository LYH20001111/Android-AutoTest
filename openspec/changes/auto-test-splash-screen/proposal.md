## Why

使用 auto-test 模块的宿主应用在内部初始化时间较长时（如数据库实例化、历史结果缓存加载），打开应用后会出现一段长时间的空白界面，直到加载完成才显示 `auto_test_activity_main` 页面。用户无法区分是"正在加载"还是"应用卡死"，体验较差。

## What Changes

- auto-test 模块新增启动页 `AutoTestSplashActivity`（抽象类），作为宿主 launcher 的必经入口，替代空白等待期
- `AutoTestSplashActivity` 提供可重写的接口（如 `getTargetActivity()`、`onPreloadData()` 等），供宿主应用进行适配
- 启动页展示加载动画（品牌图标 + 旋转进度条 + 加载文案），让用户感知正在加载配置
- 在后台线程预热关键配置（数据库实例、历史结果缓存），预热完成后自动跳转宿主主界面 `MainActivity extends AutoTestMainActivity`
- 预热异常时兜底跳转，不阻塞启动流程
- 宿主应用通过继承 `AutoTestSplashActivity` 并实现相关接口来启用启动加载反馈

## Capabilities

### New Capabilities
- `launch-loading-indicator`：宿主应用启动期间的加载反馈行为——每次冷/温启动必经品牌启动页（图标 + 旋转进度条动画 + 文案）替代空白界面、启动页后台预热配置并在完成后自动跳转主界面、主界面复用预热缓存快速渲染、预热异常时兜底跳转、宿主通过继承启动页一行启用。

### Modified Capabilities

<!-- 无现有规格变更 -->

## Impact

- `auto-test/src/main/java/com/hudou/autotest/base/activity/AutoTestSplashActivity.java`：新增启动页抽象类，提供可重写接口（含 `getSplashIconResId()` 供宿主自定义图标）
- `auto-test/src/main/res/layout/auto_test_splash_layout.xml`：新增启动页布局（品牌图标 + 进度条动画 + 文案）
- `auto-test/src/main/java/com/hudou/autotest/base/activity/AutoTestMainActivity.java`：新增静态预热入口与预热缓存复用
- `app/src/main/java/com/hudou/autotest/SplashActivity.java`：新增示例宿主启动页
- `app/src/main/AndroidManifest.xml`：launcher 切换至宿主启动页