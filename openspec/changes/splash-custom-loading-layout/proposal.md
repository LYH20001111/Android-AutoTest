## Why

`AutoTestSplashActivity` 当前的启动页布局（`auto_test_splash_layout.xml`）是固定的——品牌图标 + 旋转进度条 + 文案。宿主应用若想展示自定义加载动画（如 Lottie 动画、品牌定制进度条、广告展示页等），只能通过 `getSplashIconResId()` 替换图标，无法替换整个加载区域。这限制了不同宿主对启动页加载阶段视觉风格的个性化需求。

## What Changes

- `IAutoTestSplash` 接口新增 `getSplashLoadingLayoutResId()` 方法声明，`AutoTestSplashActivity` 实现并提供默认行为（返回 0），宿主可重写该方法返回自定义布局资源 ID，替换默认的加载动画区域（品牌图标 + 进度条 + 文案）
- 当宿主重写 `getSplashLoadingLayoutResId()` 返回非 0 布局 ID 时，启动页使用该自定义布局替代默认加载区域；返回 0 或未重写时保持现有默认行为
- 不破坏现有接口，所有已有重写方法（`getSplashIconResId`、`onPreloadData`、`getMinDisplayDuration`、`isPreloadDone`）行为不变

## Capabilities

### New Capabilities
- `splash-screen`: 启动页能力，覆盖自定义加载布局接口

### Modified Capabilities
- 无现有规格变更

## Impact

- `auto-test/src/main/java/com/hudou/autotest/base/activity/IAutoTestSplash.java`：接口新增 `getSplashLoadingLayoutResId()` 方法声明
- `auto-test/src/main/java/com/hudou/autotest/base/activity/AutoTestSplashActivity.java`：实现 `getSplashLoadingLayoutResId()` 方法及对应逻辑
- `auto-test/src/main/res/layout/auto_test_splash_layout.xml`：布局结构调整，将加载区域提取为可替换的容器
- 宿主适配：可选重写 `getSplashLoadingLayoutResId()` 返回自定义布局