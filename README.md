# Android-Auto-Test
安卓：自动化测试框架



# 1. 应用使用介绍

auto-test 以 AAR 形式发布到本仓库的 `local-maven-repo` 目录，并携带完整的依赖元数据。应用方**无需**手动引入 auto-test 内部使用的任何依赖（material、room、jxl、fastjson 等），Gradle 会按 POM 自动传递解析。

## 1.1 声明 Maven 仓库

将本仓库的 `local-maven-repo` 目录拷贝到你的工程可访问的位置，然后在仓库列表中声明它。

新项目（settings.gradle）：

```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        // 指向你拷贝的 local-maven-repo 目录
        maven { url = uri("<local-maven-repo 的路径>") }
    }
}
```

老项目（根 build.gradle）：

```groovy
allprojects {
    repositories {
        maven { url = uri("<local-maven-repo 的路径>") }
    }
}
```

## 1.2 添加依赖坐标

只需一行，版本号以 `auto-test/build.gradle` 中的 `appVersionName` 为准（当前 2.0.04）：

```groovy
dependencies {
    implementation 'com.github.LYH20001111:Android-AutoTest:2.0.04'
}
```

同时开启 viewBinding：

```groovy
android {
    buildFeatures {
        viewBinding = true
    }
}
```

## 1.3 发布 AAR（本仓库维护者）

```
./gradlew publishAutoTest
```

产物（aar / pom / .module / 校验和）写入 `local-maven-repo/`，需与版本号一并提交 Git。发布新版本的完整流程：

1. 修改根 `build.gradle` 的 `ext` 块：`autotestVersionCode`（版本码）与 `autotestVersionName`（版本名，即 Maven 坐标版本）；
2. 执行 `./gradlew publishAutoTest`；
3. 提交 `local-maven-repo/` 产物与版本号变更。

约定：

- 每次对外发布**递增版本号**，坐标版本、BuildConfig 版本、产物文件名均以根 `build.gradle` 的 `ext` 为单一来源，自动跟随；
- 确需覆盖同版本时，消费方需加 `--refresh-dependencies` 刷新 Gradle 缓存；
- 修改 auto-test 源码后，必须先执行发布命令，再构建 app（app 以 Maven 坐标消费）；
- 消费方无需任何 `resolutionStrategy` / force / exclude 全局配置，依赖传递与冲突处理由发布的元数据自动完成。

## 1.4 app-aar - AAR 模式示例应用

本项目包含一个完整的 AAR 模式示例应用 `:app-aar`，用于验证和演示使用已发布 AAR 的方式集成 auto-test。

### 📍 适用场景

这是**早期 Android AutoTest 项目**使用的原始模式：APP 不直接引用源码，而是依赖已打包好的 AAR 文件。这种模式有以下优势：

1. **独立测试**：可以单独测试 APP 对 auto-test 的调用，验证 AAR 是否正常工作
2. **真实模拟**：与最终生产环境的使用方式完全一致
3. **快速迭代**：修改 APP 逻辑时无需重新编译整个 auto-test 框架
4. **版本隔离**：可以测试不同版本的 AAR 与 APP 的兼容性

### 🚀 完整使用流程

#### 步骤 1：发布 AAR

```bash
./gradlew publishAutoTest
```

这会将最新的 `auto-test` AAR 发布到 `local-maven-repo` 目录。

#### 步骤 2：运行 app-aar

```bash
./gradlew :app-aar:installDebug
```

或者直接在 Android Studio 中选择 `app-aar` 模块并点击 Run。

### 🔑 关键区别：AAR 模式 vs 源码模式

| 特性 | 源码模式 (`app`) | AAR 模式 (`app-aar`) |
|------|------------------|---------------------|
| 依赖声明 | `include ':auto-test'` | `implementation "com.github.LYH20001111:Android-AutoTest:2.0.04"` |
| 依赖传递 | Gradle 自动解析源码依赖 | AAR 自带 POM，Gradle 自动下载传递依赖 |
| APP 自有依赖 | zxing、poi、lottie 等需要手动引入 | 同样需要手动引入（这些不属于 auto-test） |
| 编译速度 | 慢（全量编译） | 快（增量编译） |
| 使用场景 | 开发阶段 | 发布/测试阶段 |

### 📦 依赖对比详解

#### `app` 模块（源码模式）的依赖：

```groovy
dependencies {
    // 引用源码（本地）
    implementation project(':auto-test')
    
    // APP 自有功能依赖
    implementation 'com.google.zxing:core:3.4.0'
    implementation('com.journeyapps:zxing-android-embedded:4.2.0') {
        exclude group: 'com.google.guava', module: 'listenablefuture'
    }
    implementation 'org.apache.poi:poi:3.16'
    implementation 'org.apache.commons:commons-lang3:3.12.0'
    implementation('com.airbnb.android:lottie:5.2.0') {
        exclude group: 'com.google.guava', module: 'listenablefuture'
    }
}
```

#### `app-aar` 模块（AAR 模式）的依赖：

```groovy
dependencies {
    // 引用已发布的 AAR（坐标）
    implementation "com.github.LYH20001111:Android-AutoTest:${rootProject.ext.autotestVersionName}"
    
    // ⚠️ 注意：APP 自有功能依赖仍然需要手动引入！
    // 因为这些依赖属于 APP，不属于 auto-test 框架
    implementation 'com.google.zxing:core:3.4.0'
    implementation('com.journeyapps:zxing-android-embedded:4.2.0') {
        exclude group: 'com.google.guava', module: 'listenablefuture'
    }
    implementation 'org.apache.poi:poi:3.16'
    implementation('org.apache.poi:poi-ooxml:3.16') {
        exclude group: 'org.apache.xmlbeans', module: 'xmlbeans'
    }
    implementation 'org.apache.commons:commons-lang3:3.12.0'
    implementation('com.airbnb.android:lottie:5.2.0') {
        exclude group: 'com.google.guava', module: 'listenablefuture'
    }
}
```

### ✅ 关键理解

1. **auto-test AAR 已经打包了它所依赖的所有库**（通过 `api` 声明），例如：
   - androidx.appcompat
   - com.google.android.material  
   - androidx.room
   - org.reflections
   - fastjson/gson
   - jxl (Excel)
   - slf4j-api
   - ...等等

2. **APP 不需要重复引入这些依赖**，Gradle 会通过 POM 元数据自动解析传递依赖。

3. **但 APP 自身功能需要的依赖必须手动引入**，例如：
   - ZXing（扫码）- 这是 APP 的功能，不是 auto-test 框架的
   - Apache POI（Excel 处理）- APP 的业务需求
   - Lottie（动画）- APP 的 UI 需求

4. 如果只写一行 `implementation 'com.github.LYH20001111:Android-AutoTest:2.0.04'` 而不加其他依赖，**扫码和 Excel 功能会报错找不到类**。

### 🎯 验证成功

运行 `app-aar` 后，点击"启动测试"按钮，如果看到"AAR 依赖加载成功！"和版本信息，说明 AAR 集成正确。

---

更多使用说明见 [后续章节](#15-改造-mainactivity)。

## 2.1 通过 JitPack 引入（推荐外部工程使用）

如果你想引入 auto-test，但不想拷贝 `local-maven-repo` 目录，可以通过 JitPack 直接引入。JitPack 会根据 GitHub tag 自动构建 AAR 并发布。

### 前提条件

- GitHub 仓库公开（当前仓库需设为公开状态）
- Git tag 已推送到远程仓库（如 v2.0.04）

### 引入步骤

你的工程在 settings.gradle（新项目）或根 build.gradle 的 allprojects 中声明 jitpack.io 仓库：

**settings.gradle**（推荐）：
```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**或者根 build.gradle**（老项目）：
```groovy
allprojects {
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

然后在 dependencies 中添加一行：

```groovy
dependencies {
    implementation 'com.github.LYH20001111:Android-AutoTest:v2.0.04'
}
```

同时开启 viewBinding：

```groovy
android {
    buildFeatures {
        viewBinding = true
    }
}
```

### 注意事项

- 版本号格式为 `v2.0.04`，对应 Git tag v2.0.04
- JitPack 会从源码重新编译，不是读取本地已发布的 aar
- 首次构建可能较慢，可在 https://jitpack.com/LYH20001111/Android-AutoTest/v2.0.04 查看构建状态
- 后续发新版本只需递增 autotestVersionName → 打新 tag → push → 消费方更新版本号即可

---

## 2.0 改造 MainActivity (源码模式示例)

	(1) . 将 extends AppCompatActivity 改为 extends **AutoTestMainActivity**；并重写 addNavigationFragment 方法

```java
    @Override
    public void addNavigationFragment(List<Fragment> list) {
        //如果不想显示首页界面，可以通过 removeIf 删除，
        //list.removeIf(fragment -> fragment instanceof HomeFragment);
        list.add(new PSFragment());
        list.add(new SettingFragment());//在这里添加导航相关页面
    }
```

	(2) . 并删除 MainActivity 中 onCreate 方法的 setContentView(R.layout.activity_main);

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
    }
```



## 2.2 添加相关页面

```java
案例设计界面：继承 AutoTestTestListFragment
    
//继承 AutoTestTestListFragment 的布局，主要是用于测试项的编写，案例开发就在@TestItemClass(clz =) 所列的 class 中设计；
@Navigation(name = "PS")
@TestItemClass(clz = {TestItem1.class, TestItem2.class})
public class PSFragment extends AutoTestTestListFragment {

    @Override
    public String onNameTitle() {
        return null;
    }//用于标题项的更换，默认为‘测试项目’；
}


扩展设置界面：继承 AutoTestSettingFragment
public class SettingFragment extends AutoTestSettingFragment {


    @Override
    public void onFragmentVisibility() {
        super.onFragmentVisibility();
        Toast.makeText(MainActivity.mContext, "Setting Selected", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onAddActions() {
        super.onAddActions();
    }

    @Override
    public String onSetReportPath() {
        //changeEditPathCap(EditCap.ON);
        return super.onSetReportPath();
    }

//    @Override
//    public List<String> addAssetsDirs() {
//        return new ArrayList<String>(){{
//            add("document");
//            add("test");
//        }};
//    }


    @Override
    public List<String> addAssetsDirs() {
        return super.addAssetsDirs();
    }
}
```




## 2.3 添加案例

	创建案例 item，在 class 中 继承 **AutoTestTestItem**，并注解@TestItem(name = "XXXX", description = "YYYYY");

```java
@TestItem(name = "Test2", description = "测试项目 2")
public class TestItem2 extends AutoTestTestItem {
    @Override
    public void onCaseStart(Method method) {
        super.onCaseStart(method);
        recordMessage("=============" + method.getName() + "=============");
    }

    @Override
    public void onCaseFinish(Method method) {
        super.onCaseFinish(method);
        recordPass();
    }

    @TestCase(name = "TestItem2 test2_000")
    private void test2_000(){
        for (int i = 0; i < 100; i++) {
            recordMessage("Hello World" + i);
        }
    }

    @TestCase(name = "TestItem2 test2_001")
    private void test2_001(){
        recordMessage("Ni Hao Shi Jie");
    }

}
```




     //每个案例通过注解@TestCase 来实现；

    //可通过 recordMessage，recordPass，recordFail 方法来输出信息在设备界面上；

```java
@TestItem(name = "Test1", description = "测试项目 1")
public class TestItem1 extends AutoTestTestItem {

    @Override
    public void onCaseStart(Method method) {
        super.onCaseStart(method);
        recordMessage("=============" + method.getName() + "=============");
    }

    @Override
    public void onCaseFinish(Method method) {
        super.onCaseFinish(method);
        recordPass();
    }


//    @TestCase(name = "TestItem2 test2_001")
//    private void test1_000(){
//        recordMessage("wo hen hao");
//    }
}
```

	可选：在该案例类中可以重写执行案例前后需执行的相关方法：

```java
    @Override
    public void onCaseStart(Method var1) {
    }

    @Override
    public void onCaseFinish(Method var1) {
    }
```

## 2.4 Splash 启动页集成（推荐）

	创建启动页：继承 **AutoTestSplashActivity** 并重写 `getTargetActivity()` 指向主界面：

```java
public class SplashActivity extends AutoTestSplashActivity {
    @Override
    public Class<?> getTargetActivity() {
        return MainActivity.class;
    }
    // 可选：自定义标题/图标/加载布局/最小展示时长
    // getSplashTitle() / getSplashIconResId() / getSplashLoadingLayoutResId() / getMinDisplayDuration()
}
```

	**必须**在 AndroidManifest 中为该 Activity 声明启动页主题，否则冷启动会先出现空白窗口：

```xml
<activity
    android:name=".SplashActivity"
    android:exported="true"
    android:theme="@style/Theme.AutoTest.SplashScreen">
    <intent-filter>
        <action android:name="android.intent.action.MAIN" />
        <category android:name="android.intent.category.LAUNCHER" />
    </intent-filter>
</activity>
```

	启动页会在后台线程执行 `onPreloadData()`（可重写，用于预热数据库等关键配置），预热完成并满足最小展示时长后自动跳转主界面。

## 2.5 页面优化

	要是想希望页面更好看，可更换主题，可以将 theme.xml 改为：

```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Theme.你的应用" parent="Theme.Material3.DayNight.NoActionBar">
    </style>
</resources>
```

	material 依赖已随 auto-test 以 api 方式传递提供，无需在 build.gradle 中重复声明；仅当你的工程未接入 auto-test 时才需要自行添加：

```
	implementation 'com.google.android.material:material:1.9.0'
```

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

---

# 3. Maven Central 发布

## 3.1 前提条件

要发布到 Maven Central，你需要：

1. **申请 OSSRH 账户**：
   - 访问 [https://issues.sonatype.org/](https://issues.sonatype.org/) 创建账号
   - 创建一个新的 Issue，选择 "Publishing Support" 分类
   - 描述你要发布的 Group ID（如 `com.github.LYH20001111`）
   - Sonatype 会验证你是否拥有该域名的所有权

2. **生成 GPG 密钥对**：
   ```bash
   # 安装 GPG
   # macOS: brew install gnupg
   # Ubuntu: sudo apt-get install gnupg
   
   # 生成密钥
   gpg --full-generate-key
   
   # 查看密钥指纹（后面要用）
   gpg --list-keys
   
   # 导出私钥（用于签名）
   gpg --export-secret-keys <KEY_ID> > ~/.gradle/secring.gpg
   ```

3. **配置 Gradle 属性**：
   在项目的 `gradle.properties` 中添加：
   ```properties
   mavenCentralUsername=<your-ossrh-username>
   mavenCentralPassword=<your-ossrh-password>
   signing.keyId=<last-8-characters-of-key-fingerprint>
   signing.password=<your-gpg-passphrase>
   signing.secretKeyRingFile=<path-to-secring.gpg>
   ```
   
   或者使用环境变量（更安全）：
   ```bash
   export ORG_GRADLE_PROJECT_mavenCentralUsername=<username>
   export ORG_GRADLE_PROJECT_mavenCentralPassword=<password>
   export ORG_GRADLE_PROJECT_signingKey=$(base64 -w 0 ~/.gradle/secring.gpg)
   export ORG_GRADLE_PROJECT_signingPassword=<passphrase>
   ```

## 3.2 执行发布

```bash
# 发布到 OSSRH Staging Repository
./gradlew :auto-test:publishToMavenCentral
```

发布成功后，在 [Nexus Repository Manager](https://s01.oss.sonatype.org/) 查看暂存的构件。

## 3.3 验证与正式发布

1. 在 Nexus 中找到你的 Staging Repository
2. 测试版本（可选但推荐）：
   ```groovy
   dependencyResolutionManagement {
       repositories {
           google()
           mavenCentral()
           // 添加 Staging 仓库用于测试
           maven { url = uri('https://s01.oss.sonatype.org/content/repositories/comgithublyh20001111-XXXX/') }
       }
   }
   ```
3. 如果一切正常，在 Nexus 中点击 "Close"，然后点击 "Release"

## 3.4 消费方使用

发布成功后，任何工程都可以直接使用：

```groovy
dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
    }
}

dependencies {
    implementation 'com.github.LYH20001111:Android-AutoTest:2.0.04'
}
```

## 3.5 常见问题

### Q: 如何检查域名所有权？
A: Sonatype 会在 Issue 中要求你提供 DNS 记录证明，或者让你创建一个临时的 GitHub gist 包含你的 Key ID。

### Q: GPG 密钥过期了怎么办？
A: 更新密钥并重新上传到 Sonatype。他们需要你手动上传公钥：`gpg --export -a <KEY_ID>`

### Q: 构建时报错 "Signing failed"？
A: 确保已正确设置 `signing.keyId` 和 `signing.secretKeyRingFile` 属性，且私钥没有被删除。
