# Tasks: 启动加载指示

> 修订：上版 SplashScreen API 只覆盖冷启动，热/温启动无 Splash；改为独立启动页 `AutoTestSplashActivity`（每次冷/温启动必经，品牌画面 + 进度条动画 + 预热配置）。

## 1. auto-test 启动页资源

- [x] 1.1 新增 `auto_test_splash_layout.xml`：白底 FrameLayout + 居中 LinearLayout（品牌图标 ImageView 复用 `@drawable/auto_test_ic_launcher_foreground` + 圆形 ProgressBar + 复用 `loading_config_message` 文案）
- [x] 1.2 确认 `auto_test_styles.xml` 的 `Theme.AutoTest.SplashScreen` 存在（上版已加，启动页共用）

## 2. auto-test 启动页 Activity

- [x] 2.1 新增 `AutoTestSplashActivity`（抽象类，`base/activity` 包）：抽象方法 `getTargetActivity()`（public，R8 混淆库中 protected 方法名不可被宿主覆盖）；`onCreate` 在 `super.onCreate()` 之前 `SplashScreen.installSplashScreen(this)` → `setContentView` → `setKeepOnScreenCondition(() -> !preloadDone)` → 后台预热线程（`AutoTestMainActivity.preloadData`）→ 预热完成置 volatile 标志 → 满足最小展示时长（1200ms）后 `startActivity(目标)` + `finish()`
- [x] 2.2 `AutoTestMainActivity` 新增 `public static void preloadData(Context)`：`db` 为空则 `AppDatabase.getInstance`；抽取 `loadHistoryData()`（`getAllResultItems` + `getDataForItem` + `Class.forName` 还原）填充静态 `resultItemList`；完成置 `preloaded = true`

## 3. AutoTestMainActivity 复用预热缓存

- [x] 3.1 `initMainUi()` 检测 `preloaded` 命中则跳过历史结果读取线程，直接组装 Fragment/ViewPager，快速渲染
- [x] 3.2 未命中保持原逻辑（`try/catch/finally` 兜底隐藏 `loading_layout` 不回归）

## 4. app 宿主适配（示例）

- [x] 4.1 app 新增 `SplashActivity extends AutoTestSplashActivity`，`getTargetActivity()` 返回 `MainActivity.class`
- [x] 4.2 manifest：launcher intent-filter 移至 `SplashActivity`（`android:theme="@style/Theme.Autotest.Splash"`）；`MainActivity` 移除 launcher 声明、主题恢复 `Theme.Autotest`

## 5. 验证与收尾

- [x] 5.1 编译 `auto-test`（assembleRelease，R8）通过，打包新 AAR 替换 `app/libs/`（2.0.04 覆盖；首版 protected 抽象方法被 R8 混淆导致 app 编译失败，改 public 后通过）
- [x] 5.2 编译 `app`（assembleDebug）通过
- [x] 5.3 `git status --short` / `git diff` 复核仅预期文件变更，无 `.tmp` 遗留
