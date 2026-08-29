# App-AAR 模块 - AAR 模式测试应用

这个模块是一个示例应用，展示了如何使用 AAR 方式依赖 `auto-test` 框架。

## 📌 核心特点

### AAR 模式 vs 源码模式

**传统源码模式 (app 模块)**:
- 直接引用源码：`include ':auto-test'`
- 每次修改都需要重新编译整个项目
- APP 和 auto-test 耦合在一起

**AAR 模式 (app-aar 模块)**:
- 依赖已发布的 AAR 包：`implementation "com.github.LYH20001111:Android-AutoTest:2.0.04"`
- 独立编译和测试
- 模拟真实生产环境的使用方式

## 🚀 使用流程

### 1️⃣ 发布 AAR 到本地仓库

```bash
./gradlew publishAutoTest
```

这会将最新的 `auto-test` AAR 发布到 `local-maven-repo` 目录。

### 2️⃣ 构建并运行 app-aar

```bash
./gradlew :app-aar:installDebug
```

或者直接在 Android Studio 中选择 `app-aar` 模块并运行。

## 📦 依赖说明

### auto-test 的依赖（通过 AAR 传递）

auto-test AAR 已经打包了以下所有依赖（声明为 `api`）：
- ✅ androidx.appcompat
- ✅ com.google.android.material
- ✅ androidx.constraintlayout
- ✅ androidx.navigation.runtime
- ✅ androidx.recyclerview
- ✅ androidx.room
- ✅ org.reflections (反射)
- ✅ com.alibaba.fastjson
- ✅ com.google.code.gson
- ✅ net.sourceforge.jexcelapi (jxl)
- ✅ commons-cli
- ✅ slf4j-api
- ✅ 以及其他 JNI 库和资源

**APP 不需要重复引入这些依赖！**

### app-aar 自有依赖

以下依赖是 APP 自身功能所需的（不属于 auto-test）：
- ❌ zxing (扫码核心库)
- ❌ zxing-android-embedded (扫码集成)
- ❌ poi (Excel 处理)
- ❌ poi-ooxml (Excel OOXML 处理)
- ❌ commons-lang3 (字符串工具)
- ❌ lottie (动画播放)

这些依赖会被打包进 APP，而不是 AAR。

## 🔍 验证 AAR 是否工作

运行 app-aar 后，点击"启动测试"按钮会尝试加载 auto-test 类。如果成功，会显示版本信息。

## 📝 关键代码示例

```java
// MainActivity.java - 演示如何调用 auto-test AAR
@Override
protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    // 可以直接使用 auto-test 的类和工具
    Logger.d("APP", "App is running in AAR mode!");
    
    // 创建测试用例
    AutoTestCase testCase = new YourTestCase();
}
```

## ⚙️ ProGuard 配置

在 `proguard-rules.pro` 中配置了必要的混淆规则：
- 保留 auto-test 相关类
- 保留测试用例子类
- 保留各第三方库的必要类

## 💡 优势对比

| 特性 | 源码模式 | AAR 模式 |
|------|---------|---------|
| 编译速度 | 慢（全量编译） | 快（增量编译） |
| 隔离性 | 差（耦合紧密） | 好（独立模块） |
| 测试能力 | 困难 | 容易（可单独测试） |
| 版本管理 | 需要同步分支 | 可以用版本号 |
| 生产环境 | ✅ 就是源码 | ✅ 使用 AAR |

## 🎯 适用场景

✅ **推荐使用 AAR 模式**:
- 最终发布版本
- 跨团队协作（只提供 AAR）
- 稳定版本迭代
- 性能优化后的部署

❌ **不推荐**:
- 快速原型开发（用源码更快）
- 深度定制框架（建议 fork 源码）

---

> 提示：本模块是早期 Android AutoTest 项目的还原，用于确保 AAR 发布后的应用能正常工作。
