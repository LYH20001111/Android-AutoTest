## 1. auto-test 启动页资源

- [x] 1.1 新增 `auto_test_splash_layout.xml`：白底 FrameLayout + 居中 LinearLayout（品牌图标 ImageView `android:id="@+id/splash_icon"` 默认硬编码 `@drawable/auto_test_ic_launcher_foreground`，运行时会由 `getSplashIconResId()` 重写替换 + 圆形 ProgressBar + 复用 `loading_config_message` 文案）
- [x] 1.2 新增 `Theme.AutoTest.SplashScreen` 主题（`auto_test_styles.xml`），启动页 `onCreate` 中通过 `setTheme()` 使用该主题；`loading_config_message` 字符串已添加至 `auto_test_strings.xml`

## 2. auto-test 启动页 Activity

- [x] 2.1 新增 `AutoTestSplashActivity`（抽象类，`base/activity` 包）：抽象方法 `getTargetActivity()`（public，防 R8 混淆）；可重写方法 `onPreloadData(Context)`、`getMinDisplayDuration()`、`isPreloadDone()`、`getSplashIconResId()`
- [x] 2.2 `onCreate` 实现：`super.onCreate()` 之前 `SplashScreen.installSplashScreen(this)` → `setTheme(SplashScreen)` → `setContentView(启动页布局)` → `findViewById(R.id.splash_icon).setImageResource(getSplashIconResId())` → `setKeepOnScreenCondition(() -> !preloadDone)` → 后台线程执行 `onPreloadData(context)` → 预热完成置 volatile 标志 → 满足最小展示时长后 `startActivity(目标)` + `finish()`
- [ ] 2.3 `AutoTestMainActivity` 新增 `preloadData` 静态方法 — **源文件加密（TSZ#），无法直接修改**。替代方案：`AutoTestSplashActivity.onPreloadData()` 通过反射初始化 `AppDatabase`，实现同等预热效果

## 3. AutoTestMainActivity 复用预热缓存

- [ ] 3.1 `initMainUi()` 检测 `preloaded` 命中则跳过历史结果读取线程 — **源文件加密（TSZ#），无法直接修改**。替代方案：启动页预热 DB 后，主界面 `initMainUi()` 中 DB 查询因缓存命中而加速
- [ ] 3.2 未命中保持原逻辑 — **源文件加密（TSZ#），无法直接修改**。保留原 `loading_layout` 兜底逻辑不变

## 4. app 宿主适配（示例）

- [x] 4.1 app 新增 `SplashActivity extends AutoTestSplashActivity`，`getTargetActivity()` 返回 `MainActivity.class`
- [x] 4.2 manifest：launcher intent-filter 移至 `SplashActivity`（`android:theme="@style/Theme.Autotest.Splash"`）；`MainActivity` 移除 launcher 声明、主题恢复 `Theme.Autotest`

## 5. 验证与收尾

- [ ] 5.1 编译 `auto-test`（assembleRelease，R8）通过，打包新 AAR 替换 `app/libs/`
- [ ] 5.2 编译 `app`（assembleDebug）通过
- [ ] 5.3 `git status --short` / `git diff` 复核仅预期文件变更，无 `.tmp` 遗留