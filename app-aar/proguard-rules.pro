# App-proguard rules for app-aar module
# AAR 已经包含了所需的依赖，这里只需要添加应用特有的混淆规则即可

# Keep AutoTest 相关类
-keep class com.hudou.autotest.** { *; }

# Keep test cases
-keep class ** extends com.hudou.autotest.test.base.AutoTestCase { *; }

# ZXing (扫码)
-keep class com.google.zxing.** { *; }
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}

# Gson
-keep class com.alibaba.fastjson.** { *; }
-keep class com.google.gson.** { *; }
-dontwarn com.alibaba.fastjson.**

# Apache POI (Excel)
-keep class org.apache.poi.** { *; }
-dontwarn org.apache.poi.**

# Lottie (动画)
-keep class com.airbnb.lottie.** { *; }
-dontwarn com.airbnb.lottie.**

# Room (如果 APP 直接使用)
-keep class androidx.room.** { *; }

# Reflection (如果使用)
-keep class org.reflections.** { *; }

# SLF4J (日志)
-keep class org.slf4j.** { *; }
-dontwarn org.slf4j.**
