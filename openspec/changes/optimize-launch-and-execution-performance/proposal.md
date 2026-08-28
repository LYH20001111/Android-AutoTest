# 变更提案：优化宿主集成体验与运行性能（optimize-launch-and-execution-performance）

## Why

宿主应用（调用 auto-test 模块的应用）当前存在四类体验与性能问题：

1. **打开出现空白页/白屏闪烁**：
   - `AutoTestSplashActivity` 中 `SplashScreen.installSplashScreen()` 调用时 Activity 主题仍是宿主普通主题，`setTheme(Theme_AutoTest_SplashScreen)` 在 `super.onCreate()` 之后才执行，系统启动窗口拿不到 splash 主题（`AutoTestSplashActivity.java:42-47`）
   - 宿主未在 Manifest 为 Launcher Activity 声明 splash 主题，冷启动窗口为纯白
   - 三段颜色跳变：系统窗口白 → 根布局 `#FFFFFF` → 默认加载布局 `#FAF2E0`
2. **等待模型低效**：Splash 使用独立线程 `Thread.sleep(50)` 忙等轮询预热状态（`AutoTestSplashActivity.java:88-116`），浪费且时序脆弱
3. **反射与 IO 拖慢执行速度**：
   - `ReflectionUtils.getConfig()` 每次调用都重新读取解析 `config.properties`，无缓存
   - 同一注解值重复反射：`ExecutionFragment` 单个测试项调 6 次、`BaseTestCase` 单用例调 4+ 次
   - `AutoTestMainActivity.onCreate` 中 N+1 数据库查询（每个历史测试项单独查一次数据表）并逐项反射访问私有字段 `isStartTimeSet`
   - 主线程执行文件 IO（`mkdirs` + `FileOutputStream`），拖慢主界面首帧
   - `MyExpandableListAdapter` 复用 convertView 时仍每次 `findViewById`，无 ViewHolder
4. **内存/句柄泄漏风险**：`static LinearLayout llMessage`、`static FileOutputStream fos` 静态持有引用，Activity 销毁后不释放

## What Changes

### Splash 启动体验（能力 `splash-screen`）
- 修复主题时序：宿主在 Manifest 为 Launcher Activity 声明 `Theme.AutoTest.SplashScreen`；`AutoTestSplashActivity` 移除错位的 `setTheme()`，保证 `installSplashScreen()` 正确生效
- 颜色连续性：新增 `auto_test_splash_background` 颜色，统一「系统启动窗口（`windowSplashScreenBackground`）→ 根布局 → 默认加载布局」三段背景；`postSplashScreenTheme` 指向带匹配 `windowBackground` 的 `Theme.AutoTest.Main`，避免跳转瞬间白闪
- 等待模型重写：删除忙等轮询线程，预热线程完成后回调主线程，用 `Handler.postDelayed` 补足最小展示时长；`onDestroy` 清理回调，防止 Activity 提前销毁后仍跳转

### 运行性能（新能力 `runtime-performance`）
- `ReflectionUtils.getConfig()` 静态缓存 `Properties`，进程内只读取一次
- 新增带缓存的注解值读取方法；`ExecutionFragment`、`BaseTestCase` 热路径改用缓存版本
- `ResultDao` 新增批量查询 `getAllResultData()`；`AutoTestMainActivity` 一次查询 + 内存分组替代 N+1
- `ResultItem` 新增公开标志位写入方法，删除逐项私有字段反射
- 报告文件创建改为主界面启动后懒初始化（移出主线程首帧路径），语义不变
- `onDestroy` 清理 `llMessage`/`fos` 静态引用
- `MyExpandableListAdapter` 改用 ViewHolder 模式

## Capabilities

### New Capabilities
- `runtime-performance`: 模块运行性能能力，覆盖配置缓存、注解缓存、批量加载、启动 IO、静态引用清理、列表绑定复用

### Modified Capabilities
- `splash-screen`: 新增启动无缝衔接与等待模型相关需求（不改动已有长标题需求）

## Impact

- `auto-test/src/main/java/com/hudou/autotest/base/activity/AutoTestSplashActivity.java`：主题时序修复 + 等待模型重写
- `auto-test/src/main/res/values/auto_test_styles.xml`：splash 主题背景对齐 + 新增 `Theme.AutoTest.Main`
- `auto-test/src/main/res/values/auto_test_colors.xml`：新增 `auto_test_splash_background`
- `auto-test/src/main/res/layout/auto_test_splash_layout.xml`、`auto_test_splash_loading_default.xml`：背景色统一
- `auto-test/src/main/java/com/hudou/autotest/util/ReflectionUtils.java`：配置缓存 + 注解缓存
- `auto-test/src/main/java/com/hudou/autotest/fragment/ExecutionFragment.java`、`base/item/BaseTestCase.java`：改用缓存反射
- `auto-test/src/main/java/com/hudou/autotest/database/dao/ResultDao.java`：批量查询
- `auto-test/src/main/java/com/hudou/autotest/constant/ResultItem.java`：标志位公开写入
- `auto-test/src/main/java/com/hudou/autotest/base/activity/AutoTestMainActivity.java`：批量加载、IO 移出首帧、静态引用清理
- `auto-test/src/main/java/com/hudou/autotest/adapter/MyExpandableListAdapter.java`：ViewHolder
- `app/src/main/AndroidManifest.xml`：SplashActivity 声明 splash 主题
- `README.md`：宿主集成说明补充主题声明要求

## Non-Goals

- 不改变注解驱动架构与 `IAutoTestSplash` / `IAutoTestCore` 现有方法签名
- 不调整演示宿主 `getMinDisplayDuration()` 的 10 秒配置（用户确认保留）
- 不改动工作区未提交的 ViewBinding 重构与 `auto_test_item_type.xml` 的 `paddingRight` 修改
