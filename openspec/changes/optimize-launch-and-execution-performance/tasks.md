# 实施任务清单

## 1. Splash 白屏/空白页修复

- [x] 1.1 `auto_test_colors.xml` 新增 `auto_test_splash_background`（#FAF2E0）
- [x] 1.2 `auto_test_styles.xml`：`Theme.AutoTest.SplashScreen` 的 `windowSplashScreenBackground` 改为 `@color/auto_test_splash_background`；新增 `Theme.AutoTest.Main`（继承 `Theme.AppCompat.Light.NoActionBar`，`windowBackground` 为启动背景色）并作为 `postSplashScreenTheme`
- [x] 1.3 `auto_test_splash_layout.xml` 根布局背景改为 `@color/auto_test_splash_background`；`auto_test_splash_loading_default.xml` 根布局背景改为同一颜色资源
- [x] 1.4 `AutoTestSplashActivity`：删除错位的 `setTheme()`，保持 `installSplashScreen()` 在 `super.onCreate()` 之前
- [x] 1.5 `AutoTestSplashActivity`：删除忙等轮询线程，改为预热完成回调主线程 + `Handler.postDelayed` 补足最小时长；`onDestroy` 清理回调

## 2. 宿主集成适配

- [x] 2.1 `app/src/main/AndroidManifest.xml`：SplashActivity 声明 `android:theme="@style/Theme.AutoTest.SplashScreen"`
- [x] 2.2 `README.md` 集成说明补充：宿主 Launcher Activity 必须声明 splash 主题，否则冷启动出现空白窗口

## 3. 反射与配置缓存

- [x] 3.1 `ReflectionUtils.getConfig()` 增加静态 `Properties` 缓存（双重检查锁懒加载）
- [x] 3.2 `ReflectionUtils` 新增 `getAnnotationValueCached()`（ConcurrentHashMap 缓存，结果与无缓存版本一致）
- [x] 3.3 `ExecutionFragment` 6 处重复注解反射改用缓存版本
- [x] 3.4 `BaseTestCase` 每用例的 name/enDes/tip 注解解析合并为单次并复用

## 4. 启动加载与内存优化

- [x] 4.1 `ResultDao` 新增 `getAllResultData()` 批量查询
- [x] 4.2 `ResultItem` 新增 `setStartTimeSetFlag(boolean)`
- [x] 4.3 `AutoTestMainActivity` 启动加载改为两次查询 + 内存分组，删除逐项私有字段反射
- [x] 4.4 报告目录与 `FileOutputStream` 改为 `ensureReportStream()` 懒初始化，移出 `onCreate` 主线程
- [x] 4.5 `onDestroy` 清理 `llMessage`/`fos` 静态引用，销毁路径不再抛异常
- [x] 4.6 `MyExpandableListAdapter` 改用 ViewHolder 模式
- [x] 4.7 （真机验证后补）`BaseTestCase` 两个 `postValue` 重载直写 `fos` 导致 NPE 崩溃：`ensureReportStream()` 提升为公开访问器，所有写入点统一走懒初始化并判空

## 5. 验证

- [x] 5.1 `gradlew :auto-test:assembleDebug` 编译通过（本机使用缓存 Gradle 8.6，BUILD SUCCESSFUL in 34s）
- [x] 5.2 全量 `gradlew assembleDebug` 编译通过（含 app 模块，BUILD SUCCESSFUL in 58s）
- [ ] 5.3 人工验证项（需真机/模拟器）：冷启动无空白页、颜色无跳变、主界面历史数据加载正确、用例执行报告写入正常
