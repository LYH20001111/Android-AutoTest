# Android-Auto-Test
安卓：自动化测试框架



# 1. 应用使用介绍

## 1.1 build.gradle文件需要加入的

```groovy
android {
	... ...
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
	... ...
    implementation fileTree(include: ['*.jar', '*.aar'], dir: 'libs')
    implementation 'com.google.android.material:material:1.9.0'
    implementation 'net.sourceforge.jexcelapi:jxl:2.6.12'
}
```

## 1.2 导入aar文件

​	导入hudou-autotest-x.x.x.aar文件到libs目录下；

## 1.3 改造MainActivity 

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



## 1.4 添加相关页面

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



## 1.5 添加案例

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

## 1.6 页面优化

​	要是想希望页面更好看，可更换主题，可以将theme.xml 改为：

```xml
<resources xmlns:tools="http://schemas.android.com/tools">
    <!-- Base application theme. -->
    <style name="Theme.你的应用" parent="Theme.Material3.DayNight.NoActionBar">
    </style>
</resources>
```

​	但是需要再build.gradle中加入相关依赖：

```
	implementation 'com.google.android.material:material:1.9.0'
```

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>