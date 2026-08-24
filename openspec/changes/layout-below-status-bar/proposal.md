## Why

当前调用 auto-test 模块的应用（如 app 模块）在 Android 15+ 设备上运行时，由于 Material 3 NoActionBar 主题默认启用 edge-to-edge 渲染，主界面布局（`auto_test_activity_main.xml`）会绘制在系统状态栏下方，导致顶部内容被状态栏遮挡，用户体验不佳。需要修复布局使其正确显示在状态栏下方。

## What Changes

- 在主布局 `auto_test_activity_main.xml` 的根 `FrameLayout` 上保留 `android:fitsSystemWindows="true"`，作为布局接收 insets 的标记
- 在 `AutoTestMainActivity.onCreate()` 中 `setContentView()` 之前调用 `WindowCompat.setDecorFitsSystemWindows(getWindow(), true)`，显式禁用 edge-to-edge 渲染，让系统自动将内容绘制在状态栏下方

## Capabilities

### New Capabilities
- `layout/status-bar-insets`: 定义 auto-test 模块主界面布局在系统状态栏下方的显示行为——根布局通过 `fitsSystemWindows` 自动适配系统栏 insets，确保内容不被状态栏遮挡

### Modified Capabilities
<!-- 无现有 spec 变更 -->

## Impact

- 修改文件：`auto-test/src/main/res/layout/auto_test_activity_main.xml`、`AutoTestMainActivity.java`
- 影响范围：`AutoTestMainActivity` 的 `onCreate()` 方法，所有继承该类的宿主应用均受益
- 兼容性：`WindowCompat.setDecorFitsSystemWindows` 在 Android 5.0+ 支持良好，向后兼容；低版本设备默认即为 true，调用无副作用