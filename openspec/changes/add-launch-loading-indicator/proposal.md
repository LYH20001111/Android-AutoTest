# Proposal: 启动加载指示

## Why

上版方案（AndroidX core-splashscreen）经实际验证仍不满足"每次打开都有加载反馈"：系统启动窗口（splash window）**只在冷启动（进程创建）时显示**。安装后首次打开是冷启动 → 有 Splash；此后 Home 键退出再打开是热启动（窗口直接恢复，系统不显示启动窗口）→ 无 Splash；划掉任务再打开是温启动（进程可能存活、Activity 重建）→ 同样无启动窗口，但 `initMainUi()` 重新执行耗时初始化 → **空白且无任何反馈**。

根因：core-splashscreen 的 `installSplashScreen()` 只能接管"冷启动的启动窗口阶段"；热/温启动时系统不创建启动窗口，它没有画面可保持，因此 Splash 只在第一次出现。

新方案：引入**独立启动页 `AutoTestSplashActivity`** 作为宿主 launcher。每次冷/温启动必经启动页：显示品牌画面 + 加载动画（品牌图标 + 旋转进度条 + 「正在加载配置」文案），后台线程预热关键配置（数据库实例、历史结果缓存），预热完成且满足最小展示时长后自动跳转宿主主界面。启动页替代"空白等待期"，每次打开应用都有 Splash 与加载反馈。

## What Changes

- auto-test 新增启动页 `AutoTestSplashActivity`（抽象类，宿主继承并返回主界面 Activity 类）：品牌画面 + 旋转进度条动画 + 加载文案；`onCreate` 中在 `super.onCreate()` 之前调用 `SplashScreen.installSplashScreen(this)` 与冷启动系统启动画面衔接；后台线程预热配置；预热完成且满足最小展示时长后跳转主界面并结束自身。
- auto-test 新增启动页布局 `auto_test_splash_layout.xml`（白底、品牌图标、圆形进度条、复用「正在加载配置，请稍候…」文案字符串）。
- `AutoTestMainActivity` 新增静态预热入口（数据库实例 + 历史结果静态缓存 + volatile 完成标志）；`initMainUi()` 检测预热缓存命中则跳过耗时历史结果读取、直接组装界面快速渲染；未命中（异常路径）保持原逻辑与 `loading_layout` 兜底。
- 保留上版成果：`Theme.AutoTest.SplashScreen` 主题（启动页与冷启动系统启动画面衔接共用）、SplashScreen API（冷启动系统级）、`loading_layout`（主界面初始化兜底）。
- **宿主适配**：继承 `AutoTestSplashActivity`（实现 `getTargetActivity()` 返回主界面类）→ manifest 将 launcher 切换为宿主启动页（主题用启动画面主题）。app 模块作为示例宿主一并适配。
- 使用方其余用法（继承 `AutoTestMainActivity`、`addNavigationFragment` 等）不变。

## Capabilities

### New Capabilities

- `launch-loading-indicator`（修订）：宿主应用启动期间的加载反馈行为——每次冷/温启动必经品牌启动页（图标 + 旋转进度条动画 + 文案）替代空白界面、启动页后台预热配置并在完成后自动跳转主界面、主界面复用预热缓存快速渲染、预热异常时兜底跳转、宿主通过继承启动页一行启用。

### Modified Capabilities

<!-- openspec/specs 现有能力（test-execution/*）与启动加载无关，本次无修改项 -->

## Impact

- `auto-test/src/main/java/com/hudou/autotest/base/activity/AutoTestSplashActivity.java`：新增启动页抽象类
- `auto-test/src/main/res/layout/auto_test_splash_layout.xml`：新增启动页布局（品牌图标 + 进度条动画 + 文案）
- `auto-test/src/main/java/com/hudou/autotest/base/activity/AutoTestMainActivity.java`：新增静态预热入口与预热缓存复用
- `auto-test/src/main/res/values/auto_test_styles.xml`：`Theme.AutoTest.SplashScreen`（上版已加，启动页共用，不改动）
- `app/src/main/java/com/hudou/autotest/SplashActivity.java`：新增示例宿主启动页
- `app/src/main/AndroidManifest.xml`：launcher 切换至宿主启动页，MainActivity 移除 launcher 声明
- `app/src/main/res/values/themes.xml`：`Theme.Autotest.Splash`（上版已加，保留）
