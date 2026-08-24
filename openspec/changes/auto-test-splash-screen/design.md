## Context

使用 auto-test 模块的宿主应用在初始化阶段（数据库实例化、历史结果读取）存在明显空白期。当前 `AutoTestMainActivity` 的 `initMainUi()` 在 UI 线程执行耗时操作，期间界面无任何加载反馈。参见 proposal.md - Why。

## Goals / Non-Goals

**Goals:**
- 每次冷/温启动（真正打开应用）必经品牌启动页，替代空白等待期
- 启动页展示加载动画（品牌图标 + 旋转进度条 + 文案），持续反馈加载状态
- 启动页后台预热关键配置，主界面复用预热缓存快速渲染
- 提供可重写的接口供宿主适配自定义初始化行为
- 保留冷启动系统启动画面衔接（SplashScreen API）与主界面 `loading_layout` 兜底
- 宿主适配成本低：继承启动页 + launcher 切换

**Non-Goals:**
- 不缩短初始化本身的耗时（数据库迁移、分页策略等）——独立性能课题
- 不强制热启动（后台切回）显示启动页——内容立即可见、无空白
- 不使用 Lottie 等第三方动画库——使用原生 ProgressBar 零新依赖

## Decisions

### D1: 新增 AutoTestSplashActivity 作为启动页抽象类

抽象类（继承 `AppCompatActivity`），位于 auto-test `base/activity` 包：

- `protected abstract Class<? extends Activity> getTargetActivity()`：宿主返回主界面 Activity 类
- `protected void onPreloadData(Context context)`：可重写的预处理方法，宿主可在预热阶段执行自定义初始化；默认实现调用 `AutoTestMainActivity.preloadData(context)`
- `protected boolean isPreloadDone()`：查询预热是否完成，供宿主扩展判断条件
- `protected long getMinDisplayDuration()`：可重写的最小展示时长（默认 1200ms），保证动画可见一轮
- `onCreate` 流程：
  1. `SplashScreen.installSplashScreen(this)`（在 `super.onCreate()` 之前，冷启动时与系统启动画面衔接）
  2. `setContentView(启动页布局)`
  3. `setKeepOnScreenCondition(() -> !preloadDone)`——冷启动时保持系统启动画面直到预热线程真正开始
  4. 启动后台预热线程执行 `onPreloadData(context)`，完成置 `preloadDone = true`（volatile）
  5. 主线程等待预热完成且满足最小展示时长后，`startActivity(目标)` 并 `finish()`
- 时序：冷启动 → 系统启动画面（Android 12+ 系统级动画）→ 启动页（图标 + 旋转进度条 + 文案）→ 预热完成 + 最短时长 → 主界面（缓存命中快速渲染）

### D2: 预热缓存与主界面复用

`AutoTestMainActivity` 新增静态预热入口：

- `public static void preloadData(Context context)`：`db` 为空时 `AppDatabase.getInstance(context)`；后台读取历史结果（`getAllResultItems` + `getDataForItem` + `Class.forName` 反射还原 `ResultItem`）填充静态 `resultItemList`；完成后置 `preloaded = true`（volatile）
- `initMainUi()` 检测 `preloaded`：命中则跳过历史结果读取线程，直接进入 Fragment 创建 / ViewPager 组装（剩余工作快）；未命中（异常路径或 MainActivity 被直接拉起）走原逻辑
- 并发安全：`resultItemList` 为 `CopyOnWriteArrayList`、`db` 为 Room 单例幂等创建、标志位 volatile，预热线程与主界面无数据竞争

### D3: 启动页布局与动画

`auto_test_splash_layout.xml`：白底 FrameLayout + 居中纵向 LinearLayout：

- 品牌图标 `ImageView`（`android:id="@+id/splash_icon"`，默认 src 硬编码 `@drawable/auto_test_ic_launcher_foreground`，实际运行时由 `AutoTestSplashActivity.onCreate()` 通过 `getSplashIconResId()` 重写替换）
- 圆形 `ProgressBar`（原生组件，零新依赖）
- `TextView` 复用字符串 `loading_config_message`（「正在加载配置，请稍候…」）
- 视觉与主界面 `loading_layout` 一致（白底 + 同文案），跳转衔接无感知

### D4: 宿主适配（示例 app 模块）

- app 新增 `SplashActivity extends AutoTestSplashActivity`，`getTargetActivity()` 返回 `MainActivity.class`
- manifest：launcher intent-filter 移至 `SplashActivity`（`android:theme="@style/Theme.Autotest.Splash"`）；`MainActivity` 移除 launcher 声明、主题恢复 `Theme.Autotest`

### D5: 可重写接口设计

`AutoTestSplashActivity` 提供以下可重写接口：

| 方法 | 默认行为 | 宿主重写场景 |
|------|---------|-------------|
| `getTargetActivity()` | 抽象，必须实现 | 返回主界面 Activity 类 |
| `onPreloadData(Context)` | 调用 `AutoTestMainActivity.preloadData(context)` | 添加额外初始化步骤 |
| `getMinDisplayDuration()` | 1200ms | 自定义最小展示时长 |
| `isPreloadDone()` | 检查 `preloadDone` 标志位 | 添加额外完成条件 |
| `getSplashIconResId()` | `R.drawable.auto_test_ic_launcher_foreground` | 自定义启动页品牌图标 |

## Risks / Trade-offs

- [热启动（后台切回）无启动页] → 内容立即可见无空白；若未来要求强制显示，可在 MainActivity 增加每次前台动画层开关（独立课题）
- [预热线程与主界面并发] → 静态字段均为并发安全容器/幂等创建/volatile 标志，见 D2
- [最小展示时长权衡] → 1200ms 常量可调：预热慢则动画持续到预热完成，预热快则至少保证动画可见一轮
- [AAR 打包与宿主集成] → 新类/布局随 AAR 发布；宿主继承需重新打包 AAR（app 示例同步替换 `app/libs/` AAR）
- [R8 混淆] → 启动页抽象方法需为 `public` 而非 `protected`，否则 R8 混淆后宿主无法覆盖

## Migration Plan

1. 实现：启动页布局/字符串 → `AutoTestSplashActivity`（含可重写接口）→ `AutoTestMainActivity` 预热入口与缓存复用 → app 宿主适配（`SplashActivity` + manifest launcher 切换）
2. 验证：编译 auto-test（assembleRelease 打包新 AAR 替换 `app/libs/`）与 app（assembleDebug）；冷启动与划掉任务重开观察启动页动画 → 主界面；Home 切回无空白
3. 回滚：移除启动页与预热改动、还原 manifest（launcher 回 `MainActivity`）即可