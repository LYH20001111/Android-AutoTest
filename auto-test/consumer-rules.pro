# 随 aar 下发给接入方的混淆规则。
# fat-aar 已把下列第三方库的 class 合并进本 aar，
# 若接入方开启混淆（minifyEnabled true），需保证这些类不被裁剪/改名，
# 否则运行期会因反射、R 类引用等出现崩溃。

-keepattributes *Annotation*,Signature,InnerClasses,EnclosingMethod

# ---------- JSON ----------
-keep class com.google.gson.** { *; }
-dontwarn com.google.gson.**
-keep class com.alibaba.fastjson.** { *; }
-dontwarn com.alibaba.fastjson.**

# ---------- 反射扫描 / 日志 ----------
-keep class org.reflections.** { *; }
-dontwarn org.reflections.**
-keep class org.slf4j.** { *; }
-dontwarn org.slf4j.**

# ---------- Excel ----------
-keep class jxl.** { *; }
-dontwarn jxl.**

# ---------- Room / SQLite ----------
-keep class androidx.room.** { *; }
-keep class androidx.sqlite.** { *; }
-dontwarn androidx.room.paging.**

# ---------- Navigation（运行时通过注解反射创建 Navigator） ----------
-keep class androidx.navigation.** { *; }
-dontwarn androidx.navigation.**

# ---------- AndroidX UI / Material ----------
-keep class androidx.appcompat.** { *; }
-keep class com.google.android.material.** { *; }
-keep class androidx.constraintlayout.** { *; }
-keep class androidx.recyclerview.** { *; }
-keep class androidx.swiperefreshlayout.** { *; }
-keep class androidx.core.** { *; }
-keep class androidx.fragment.** { *; }
-keep class androidx.activity.** { *; }
-keep class androidx.lifecycle.** { *; }
-dontwarn androidx.**

# ---------- Kotlin（navigation 传递引入） ----------
-keep class kotlin.Metadata { *; }
-keep class kotlin.coroutines.** { *; }
-keep class kotlinx.coroutines.** { *; }
-dontwarn kotlin.**
-dontwarn kotlinx.coroutines.**

# ---------- Guava ----------
-dontwarn com.google.common.**
-dontwarn sun.misc.**

# 合并进来的库自身的 R 类引用
-keepclassmembers class **.R$* {
    public static <fields>;
}
