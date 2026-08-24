# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile


# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
   native <methods>;
}
# 不混淆资源类
-keepclassmembers class *.R$ {
public static *;
}
# 保留注解
-keepattributes *Annotation*

## 保留对外暴露的类和方法
#-keep class com.hudou.autotest.annotation.** { *; }
#-keep public class com.hudou.autotest.fragment.TestListFragment {
#    public *;
#}
#-keep public class com.hudou.autotest.base.item.BaseTestItem {
#    public *;
#}
#-keep class com.hudou.autotest.base.activity.BaseMainActivity { *; }

# 保留对外暴露的类和方法
-keep public class com.hudou.autotest.** {
    public *;
    protected *;
}

-keepclassmembers class * {
    protected abstract void *();
}

-keep class jxl.** { *; }
## 保留所有 ViewBinding 类
#-keep class * extends androidx.viewbinding.ViewBinding { *; }
#
## 保留所有 ViewBinding 类的 inflate 方法
#-keepclassmembers class * extends androidx.viewbinding.ViewBinding {
#    public static * inflate(...);
#}




