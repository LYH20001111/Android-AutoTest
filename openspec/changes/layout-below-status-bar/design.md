## Context

当前 `auto-test` 模块使用 `Theme.AppCompat.Light.NoActionBar`（app 模块使用 `Theme.Material3.DayNight.NoActionBar`），在 Android 15+ 设备上 NoActionBar 主题默认启用 edge-to-edge 渲染，导致布局内容绘制在系统状态栏下方，顶部内容被遮挡。代码中无任何 `fitsSystemWindows`、`OnApplyWindowInsetsListener` 或 `EdgeToEdge` 处理。

参见 proposal.md - Why。

## Goals / Non-Goals

**Goals:**
- 主布局 `auto_test_activity_main.xml` 内容正确显示在状态栏下方
- 修改最小化，保持布局和代码改动量小
- 向后兼容低版本 Android 设备，无副作用

**Non-Goals:**
- 不涉及导航栏（底部）的 insets 处理
- 不采用 `EdgeToEdge.enable()` + 手动 insets 方案——当前只需禁用 edge-to-edge，`setDecorFitsSystemWindows(true)` 最简洁
- 不改变布局结构或添加新视图

## Decisions

### D1: 使用 `WindowCompat.setDecorFitsSystemWindows(getWindow(), true)` + `fitsSystemWindows`

在 `AutoTestMainActivity.onCreate()` 中 `setContentView()` 之前调用 `WindowCompat.setDecorFitsSystemWindows(getWindow(), true)`，同时保留布局根 `FrameLayout` 上的 `android:fitsSystemWindows="true"`。

**理由：**
- Android 15+ 默认启用 edge-to-edge（`setDecorFitsSystemWindows(false)`），此时系统不会下发 insets，导致 `fitsSystemWindows` 无法生效
- `WindowCompat.setDecorFitsSystemWindows(window, true)` 显式恢复旧行为，让系统自动将内容限制在系统栏安全区域内
- 保留 `fitsSystemWindows` 作为布局接收 insets 的标记，双重保障

**替代方案：**
- 仅 `fitsSystemWindows`：已验证无效（Android 15+ 默认不下发 insets）
- `EdgeToEdge.enable(activity)` + `ViewCompat.setOnApplyWindowInsetsListener` 手动处理 padding：功能正确但过度设计，当前只需「避开状态栏」而非「全屏 + 自定义 insets」
- 仅 `setDecorFitsSystemWindows(true)` 不保留 `fitsSystemWindows`：可行，但保留 `fitsSystemWindows` 更安全

### D2: 仅修改根布局和 Activity，不修改 Fragment 布局

`fitsSystemWindows` 在根 `FrameLayout` 上，配合 `setDecorFitsSystemWindows(true)` 系统自动下发 insets。各 Fragment 布局无需修改。

## Risks / Trade-offs

- `setDecorFitsSystemWindows(true)` 全局禁用 edge-to-edge，若未来需要全屏沉浸模式（如视频播放），需针对该页面单独处理。当前无此需求。
- `fitsSystemWindows` 同时影响状态栏和导航栏 padding。当前布局底部有 `TabLayout`（50dp），导航栏 padding 可能导致底部间距略大，但影响可接受。