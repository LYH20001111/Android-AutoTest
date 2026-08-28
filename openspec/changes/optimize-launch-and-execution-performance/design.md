# 技术设计：optimize-launch-and-execution-performance

## 决策 1：Splash 主题时序修复

**问题**：androidx `core-splashscreen` 要求 `installSplashScreen()` 调用时 Activity 的主题必须是 `Theme.SplashScreen` 家族。当前代码在宿主普通主题下调用 `installSplashScreen()`，随后 `setTheme()` 为时已晚——系统启动窗口（starting window）由 Manifest 声明的主题绘制，宿主未声明时即为纯白窗口，这是"打开空白页"的首要根因。

**方案**：
- 宿主在 `AndroidManifest.xml` 中为 Launcher Activity 声明 `android:theme="@style/Theme.AutoTest.SplashScreen"`（库文档强制要求，README 补充说明）
- `AutoTestSplashActivity` 删除 `onCreate` 中错位的 `setTheme()`；`installSplashScreen()` 保持在 `super.onCreate()` 之前
- `postSplashScreenTheme` 指向新增的 `Theme.AutoTest.Main`（继承 `Theme.AppCompat.Light.NoActionBar`，`windowBackground` 为启动背景色），splash 结束到主界面首帧之间无白帧

## 决策 2：颜色连续性

新增 `auto_test_splash_background`（`#FAF2E0`，沿用现有默认加载布局背景色，保持现有视觉），三处统一引用：
1. `windowSplashScreenBackground`（系统启动窗口 / API 31 以下由 core-splashscreen 模拟）
2. `auto_test_splash_layout.xml` 根 FrameLayout
3. `auto_test_splash_loading_default.xml` 根 LinearLayout

自定义加载布局的宿主可在自己布局中覆盖背景，不受影响。

## 决策 3：等待模型重写

删除 `waitForPreload()` 的忙等轮询线程，改为：

```
startPreload():
    记录 preloadStartTime (SystemClock.uptimeMillis)
    后台线程: try onPreloadData() catch 记录异常 finally {
        preloadDone.set(true)
        mainHandler.post(scheduleNavigation)
    }

scheduleNavigation():   // 主线程
    if (isFinishing() || isDestroyed()) return
    delay = max(0, getMinDisplayDuration() - (uptimeMillis - preloadStartTime))
    mainHandler.postDelayed(navigateToTarget, delay)

onDestroy():
    mainHandler.removeCallbacksAndMessages(null)
```

语义与原实现等价（预热完成 + 最小展示时长双条件），但零轮询、无线程泄漏风险、Activity 销毁安全。`setKeepOnScreenCondition(() -> !isPreloadDone())` 保持不变。

## 决策 4：配置与注解缓存

- `getConfig`：`private static volatile Properties CONFIG_CACHE`，双重检查锁懒加载；加载失败仍抛 `RuntimeException`（与原行为一致）
- 新增 `getAnnotationValueCached(element, annotationClass, memberName)`：`ConcurrentHashMap<CacheKey, String>`，`CacheKey` 由 element + memberName 组成（`Class`/`Method` 均有稳定 `equals`/`hashCode`）。值为 null 时用哨兵区分"未解析"与"解析为 null"，保证与无缓存版本结果完全一致
- 热路径替换：`ExecutionFragment` 6 处、`BaseTestCase` 每用例 4+ 处改用缓存版本；`BaseTestCase` 同时将 name 解析提前为局部变量，`finally` 块复用

## 决策 5：N+1 查询批量化

- `ResultDao` 新增 `@Query("SELECT * FROM result_data") List<ResultDataEntity> getAllResultData()`
- `AutoTestMainActivity` 启动加载：一次 `getAllResultItems()` + 一次 `getAllResultData()`，内存中用 `HashMap<String, List<ResultDataEntity>>` 按 `className` 分组，再按 items 顺序构建 `ResultItem` 列表（顺序与原来一致）
- `isStartTimeSet` 恢复：`ResultItem` 新增 `public void setStartTimeSetFlag(boolean)`，删除 `Field.setAccessible` 反射

## 决策 6：报告文件懒初始化

原 `onCreate` 主线程同步 `mkdirs` + `new FileOutputStream`，改为：
- `private static synchronized FileOutputStream ensureReportStream()`：首次调用时创建目录与流并缓存到静态字段
- `recordMessage` 在 DEBUG_MODE 分支通过 `ensureReportStream()` 获取流；创建失败仅打印异常不崩溃（原实现 catch 后 fos 为 null 本来就会在写入时 NPE，此改动反而修复了该隐患）
- `onCreate` 不再做任何文件 IO

## 决策 7：静态引用清理

`onDestroy` 中：`llMessage = null`；若 `fos != null` 则 close 并置 null（原实现 close 失败直接抛 `RuntimeException` 会导致销毁路径崩溃，改为打印异常）。`mContext` 保留（`getContext()` 被后台测试线程广泛使用，置空会引发 NPE），但 `recordMessage` 的消息展示路径对 `llMessage` 判空。

## 决策 8：ViewHolder 模式

`MyExpandableListAdapter` 增加 `GroupViewHolder` / `ChildViewHolder` 静态内部类，`convertView == null` 时 inflate + `setTag`，否则 `getTag` 直接取引用。渲染内容不变。

## 约束与兼容性

- 所有公开接口签名不变，宿主零适配成本（唯一要求是 Manifest 主题声明，属于新增集成约定）
- 不触碰工作区未提交修改（ViewBinding 重构、`paddingRight`）
- 演示宿主 `getMinDisplayDuration()` 保持 10000ms（用户确认）
