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

## 1.4 改造MainActivity 

​	(1) . 将extends AppCompatActivity 改为 extends **AutoTestMainActivity**；并重写addNavigationFragment方法

```java
    @Override
    public void addNavigationFragment(List<Fragment> list) {
        //如果不想显示首页界面，可以通过removeIf删除,
        //list.removeIf(fragment -> fragment instanceof HomeFragment);
        list.add(new PSFragment());
        list.add(new SettingFragment());//在这里添加导航相关页面
    }
```

​	(2) . 并删除MainActivity中onCreate方法的setContentView(R.layout.activity_main);

```java
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);
    }
```



## 1.5 添加相关页面

```java
案例设计界面：继承 AutoTestTestListFragment
    
//继承AutoTestTestListFragment的布局，主要是用于测试项的编写，案例开发就在@TestItemClass(clz =)所列的class中设计；
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



## 1.6 添加案例

​	创建案例item，在 class 中 继承 **AutoTestTestItem**，并注解@TestItem(name = "XXXX", description = "YYYYY");

```java
@TestItem(name = "Test2", description = "测试项目2")
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



​     //每个案例通过注解@TestCase来实现；

​    //可通过recordMessage，recordPass，recordFail方法来输出信息在设备界面上；

```java
@TestItem(name = "Test1", description = "测试项目1")
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

​	可选：在该案例类中可以重写执行案例前后需执行的相关方法：

```java
    @Override
    public void onCaseStart(Method var1) {
    }

    @Override
    public void onCaseFinish(Method var1) {
    }
```

## 1.7 Splash 启动页集成（推荐）

​	创建启动页：继承 **AutoTestSplashActivity** 并重写 `getTargetActivity()` 指向主界面：

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

​	**必须**在 AndroidManifest 中为该 Activity 声明启动页主题，否则冷启动会先出现空白窗口：

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

​	启动页会在后台线程执行 `onPreloadData()`（可重写，用于预热数据库等关键配置），预热完成并满足最小展示时长后自动跳转主界面。

## 1.8 页面优化

​	要是想希望页面更好看，可更换主题，可以将theme.xml 改为：

```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Theme.你的应用" parent="Theme.Material3.DayNight.NoActionBar">
    </style>
</resources>
```

​	material 依赖已随 auto-test 以 api 方式传递提供，无需在 build.gradle 中重复声明；仅当你的工程未接入 auto-test 时才需要自行添加：

```
	implementation 'com.google.android.material:material:1.9.0'
```

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>