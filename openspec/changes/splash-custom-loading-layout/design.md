## Context

当前 `AutoTestSplashActivity` 的加载区域固定在 `auto_test_splash_layout.xml` 中——品牌图标 ImageView + 圆形 ProgressBar + 加载文案 TextView。`getSplashIconResId()` 仅能替换图标资源，无法改变整体布局结构。宿主如需展示自定义加载动画（如 Lottie、逐帧动画、品牌品牌定制进度条等），需要能够完全替换加载区域。参见 proposal.md - Why。

源文件 `AutoTestSplashActivity.java` 为 TSZ 加密格式，无法直接修改源码，需要通过解密后修改或字节码插桩方式实现。

## Goals / Non-Goals

**Goals:**
- 新增 `getSplashLoadingLayoutResId()` 可重写方法，宿主返回自定义布局资源 ID 以替换默认加载区域
- 默认行为完全不变（返回 0 或未重写时使用现有布局）
- 自定义布局能正常显示在启动页中央区域
- 不影响启动页生命周期、预热流程、跳转逻辑

**Non-Goals:**
- 不改变 `getSplashIconResId()` 的现有行为
- 不修改启动页整体容器（白底 FrameLayout）——仅替换加载内容区域
- 不引入第三方动画库依赖

## Decisions

### D1: 新增 `getSplashLoadingLayoutResId()` 方法

在 `IAutoTestSplash` 接口中声明 `getSplashLoadingLayoutResId()` 方法，`AutoTestSplashActivity` 实现并提供默认行为。

- 接口声明：`int getSplashLoadingLayoutResId()`（`IAutoTestSplash` 中）
- 实现：`@Override public int getSplashLoadingLayoutResId()`，默认返回 0（由于 R8 混淆问题，需声明为 `public` 以保持与现有接口一致的混淆策略，参见 D5: 可重写接口设计）
- 默认返回值：0，表示使用默认加载布局
- 宿主重写时返回自定义布局资源 ID，如 `R.layout.custom_splash_loading`

### D2: 布局结构调整

将 `auto_test_splash_layout.xml` 中的加载内容区域（`LinearLayout` 及其子视图）提取为独立的 `include` 布局 `auto_test_splash_loading_default.xml`，并在外层 `FrameLayout` 中使用 `ViewStub` 或 `FrameLayout` 容器来承载加载区域：

- 方案 A（ViewStub）：外层 FrameLayout 中放置 `ViewStub`，`onCreate` 时根据 `getSplashLoadingLayoutResId()` 返回值决定 inflate 默认布局还是自定义布局
- 方案 B（FrameLayout 容器 + 动态替换）：外层 FrameLayout 中放置一个空的 `FrameLayout` 作为加载区域容器，`onCreate` 时动态 inflate 并添加默认或自定义布局

**选择方案 A（ViewStub）**，理由：
- 默认情况下 ViewStub 不占用布局层级，性能更优
- 只需一次 inflate，不存在替换 Fragment 的复杂度
- 布局文件结构更清晰

### D3: 修改 `onCreate` 流程

在 `onCreate` 中扩展现有逻辑：

1. 原有流程：`setContentView(启动页布局)` → `findViewById(R.id.splash_icon).setImageResource(getSplashIconResId())`
2. 新增流程：在 `setContentView` 之后，检查 `getSplashLoadingLayoutResId()` 返回值：
   - 返回非 0 → inflate 自定义布局作为加载区域，替换默认的 `ViewStub`
   - 返回 0 → inflate 默认加载布局 `auto_test_splash_loading_default.xml` 到 `ViewStub`
   - 如果使用自定义布局，跳过 `splash_icon` 的 `setImageResource` 调用（因为自定义布局中可能没有该 ID）

### D4: 与现有重写方法的关系

| 方法 | 与自定义加载布局的关系 |
|------|----------------------|
| `getSplashIconResId()` | 仅在未使用自定义布局时生效；使用自定义布局时，宿主自行在自定义布局中管理图标 |
| `onPreloadData(Context)` | 无影响，预热流程不变 |
| `getMinDisplayDuration()` | 无影响，最小展示时长逻辑不变 |
| `isPreloadDone()` | 无影响，完成条件判断不变 |

## Risks / Trade-offs

- [自定义布局中缺少 `splash_icon`/`splash_progress`/`splash_text` 资源 ID] → 使用自定义布局时跳过默认控件的查找与赋值，由宿主完全控制自定义布局中的视图
- [ViewStub 只能 inflate 一次] → 符合设计意图：启动页只展示一次加载过程，不存在动态切换布局的场景
- [AAR 打包兼容性] → 新增方法与布局文件随 AAR 发布，宿主需重新打包 AAR 以获取新方法；使用自定义布局的宿主需同步更新 AAR 版本
- [R8 混淆] → 新方法需与现有接口保持一致的混淆策略（`public` 而非 `protected`），否则宿主无法覆盖