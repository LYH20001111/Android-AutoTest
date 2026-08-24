# Design: 启动加载指示

## Context

上版方案（AndroidX core-splashscreen + `setKeepOnScreenCondition`）经实际验证只解决冷启动：系统启动窗口（splash window）仅在进程创建时出现，热启动（Home 切回）与温启动（划掉任务重开、Activity 重建）均不显示；其中温启动会重新执行 `initMainUi()` 耗时初始化，出现空白且无反馈。core-splashscreen 只能接管"冷启动的启动窗口阶段"，无法覆盖热/温启动。参见 proposal.md - Why。

本次修订引入**独立启动页 `AutoTestSplashActivity`**：每次冷/温启动必经启动页（品牌画面 + 加载动画），后台预热配置，完成后跳转主界面。

## Goals / Non-Goals

**Goals:**
- 每次冷/温启动（真正打开应用）必经品牌启动页，替代空白等待期
- 启动页展示加载动画（品牌图标 + 旋转进度条 + 文案），持续反馈加载状态
- 启动页后台预热关键配置，主界面复用预热缓存快速渲染
- 保留冷启动系统启动画面衔接（SplashScreen API）与主界面 `loading_layout` 兜底
- 宿主适配成本低：继承启动页 + launcher 切换

**Non-Goals:**
- 不缩短初始化本身的耗时（数据库迁移、分页策略等）——独立性能课题
- 不强制热启动（后台切回）显示启动页——内容立即可见、无空白（用户已确认接受）
- 不使用 Lottie 动画（用户已确认选择进度条动画；后续如需更炫效果可替换动画资源，独立课题）

## Decisions

### D1: 新增 AutoTestSplashActivity 作为启动页

抽象类（继承 `AppCompatActivity`），位于 auto-test `base/activity` 包：

- 抽象方法 `protected abstract Class<? extends Activity> getTargetActivity()`：宿主返回主界面 Activity 类。
- `onCreate` 流程：
  1. `SplashScreen.installSplashScreen(this)`（在 `super.onCreate()` 之前，冷启动时与系统启动画面衔接）；
  2. `setContentView(启动页布局)`；
  3. `setKeepOnScreenCondition(() -> !preloadDone)`——冷启动时保持系统启动画面直到预热线程真正开始；
  4. 启动后台预热线程执行 `AutoTestMainActivity.preloadData(context)`，完成置 `preloadDone = true`（volatile）；
  5. 主线程等待预热完成且满足最小展示时长（常量如 1200ms，保证动画可见），`startActivity(目标)` 并 `finish()`。
- 时序：冷启动 → 系统启动画面（Android 12+ 系统级动画）→ 启动页（图标 + 旋转进度条 + 文案）→ 预热完成 + 最短时长 → 主界面（缓存命中快速渲染）。温启动：直接创建启动页（无系统启动画面）→ 同上，无空白。
- 备选对比：仅 SplashScreen API（上版）无法覆盖温启动；windowBackground 静态画面无动画；启动页为每次打开必经入口，选定。

### D2: 预热缓存与主界面复用

`AutoTestMainActivity` 新增静态预热入口：

- `public static void preloadData(Context context)`：`db` 为空时 `AppDatabase.getInstance(context)`；后台读取历史结果（`getAllResultItems` + `getDataForItem` + `Class.forName` 反射还原 `ResultItem`）填充静态 `resultItemList`；完成后置 `preloaded = true`（volatile）。
- `initMainUi()` 检测 `preloaded`：命中则跳过历史结果读取线程，直接进入 Fragment 创建 / ViewPager 组装（剩余工作快）；未命中（异常路径或 MainActivity 被直接拉起）走原逻辑。
- Fragment 创建依赖宿主 `addNavigationFragment`（抽象），不纳入预热，留在主界面完成。
- 并发安全：`resultItemList` 为 `CopyOnWriteArrayList`、`db` 为 Room 单例幂等创建、标志位 volatile，预热线程与主界面无数据竞争。

### D3: 启动页布局与动画

`auto_test_splash_layout.xml`：白底 FrameLayout + 居中纵向 LinearLayout：

- 品牌图标 `ImageView`（复用 `@drawable/auto_test_ic_launcher_foreground`）
- 圆形 `ProgressBar`（加载动画，零新依赖）
- `TextView` 复用字符串 `loading_config_message`（「正在加载配置，请稍候…」）

视觉与主界面 `loading_layout` 一致（白底 + 同文案），跳转衔接无感知。

### D4: 宿主适配（示例 app 模块）

- app 新增 `SplashActivity extends AutoTestSplashActivity`，`getTargetActivity()` 返回 `MainActivity.class`。
- manifest：launcher intent-filter 移至 `SplashActivity`（`android:theme="@style/Theme.Autotest.Splash"`）；`MainActivity` 移除 launcher 声明、主题恢复 `Theme.Autotest`。
- `Theme.Autotest.Splash`（上版已加，parent = `Theme.AutoTest.SplashScreen` + `postSplashScreenTheme`）保留：启动页冷启动阶段与系统启动画面衔接、退出后恢复宿主主题。

### D5: 保留上版衔接层

- SplashScreen API（`installSplashScreen` + `setKeepOnScreenCondition`）：启动页 `onCreate` 冷启动衔接（上版在 `AutoTestMainActivity` 中的调用随之迁移至启动页；`AutoTestMainActivity` 保留调用亦无害，规划以启动页为准）。
- `loading_layout`：主界面初始化兜底（预热异常、MainActivity 被直接拉起等场景），`initMainUi` 的 `try/catch/finally` 兜底隐藏不回归。
- `Theme.AutoTest.SplashScreen` 主题：启动页与主界面启动阶段共用。

## Risks / Trade-offs

- [热启动（后台切回）无启动页]（用户已确认）→ 内容立即可见无空白；若未来要求强制显示，可在 MainActivity 增加每次前台动画层开关（独立课题）。
- [预热线程与主界面并发] → 静态字段均为并发安全容器/幂等创建/volatile 标志，见 D2。
- [最小展示时长权衡] → 1200ms 常量可调：预热慢则动画持续到预热完成，预热快则至少保证动画可见一轮。
- [AAR 打包与宿主集成] → 新类/布局随 AAR 发布；宿主继承需重新打包 AAR（app 示例同步替换 `app/libs/` AAR）。
- [TSZ# 加密源码修改风险] → 按既有流程：工作区明文直接修改 → `git diff` 复核 → 编译验证。

## Migration Plan

1. 实现：启动页布局/字符串 → `AutoTestSplashActivity` → `AutoTestMainActivity` 预热入口与缓存复用 → app 宿主适配（`SplashActivity` + manifest launcher 切换）。
2. 验证：编译 auto-test（assembleRelease 打包新 AAR 替换 `app/libs/`）与 app（assembleDebug）；冷启动与划掉任务重开观察启动页动画 → 主界面；Home 切回无空白。
3. 回滚：移除启动页与预热改动、还原 manifest（launcher 回 `MainActivity`）即可；上版 SplashScreen 与 `loading_layout` 保留无害。
