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
}
```

## 1.2 导入aar文件

​	导入hudou-autotest-x.x.x.aar文件到libs目录下；

## 1.3 改造MainActivity 

​	(1) . 将extends AppCompatActivity 改为 extends BaseMainActivity；并重写addNavFragment方法

```java
    @Override
    public void addNavFragment(List<Fragment> list) {
        list.add(new PSFragment());//在这里添加相关页面
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
@Navigation(name = "PS")
@TestItemClass(clz = {TestItem1.class, TestItem2.class})
public class PSFragment extends TestListFragment {
}
```



## 1.5 添加案例

​	创建案例item，在 class 中 继承 BaseTestItem，并注解@TestItem(name = "XXXX", description = "YYYYY");

```java
@TestItem(name = "Test1", description = "测试项目1")
public class TestItem1 extends BaseTestItem {
}
```



​     //每个案例通过注解@TestCase来实现；

​    //可通过recordNormal，recordPass，recordFail方法来输出信息在设备界面上；

```java
@TestItem(name = "Test2", description = "测试项目2")
public class TestItem2 extends BaseTestItem {
    
    
    @Override
    public void onCaseStart(Method method) {
        super.onCaseStart(method);
    }

    @Override
    public void onCaseFinish(Method method) {
        super.onCaseFinish(method);
    }

    @TestCase(name = "TestItem2 test2_000")
    private void test2_000(){
        recordNormal("Hello World");
    }

    @TestCase(name = "TestItem2 test2_001")
    private void test2_001(){
        recordNormal("Ni Hao Shi Jie");
    }

    
}
```

​	可选：在该案例类中可以重写案例前后相关方法：

```java
    @Override
    public void onCaseStart(Method var1) {
    }

    @Override
    public void onCaseFinish(Method var1) {
    }
```

1.6 页面优化

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

