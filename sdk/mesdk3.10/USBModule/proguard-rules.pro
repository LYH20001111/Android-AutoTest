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
-optimizationpasses 5
-dontoptimize
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-allowaccessmodification
-keepattributes *Annotation*,Exceptions,InnerClasses,Signature,Deprecated,EnclosingMethod
-dontshrink
-dontwarn org.apache.**
-dontwarn javax.**
-dontwarn android.newland.**
-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-adaptresourcefilenames    **/*.properties,**/*.gif,**/*.dtd
-adaptresourcefilecontents

-keep public class com.annimon.stream.** {*;}
-keep public class com.newland.sdk.module.usb.** {*;}
-keep public class com.newland.sdk.ModuleManage{*;}

-keepclasseswithmembernames class com.newland.sdk.me.module.usb.MEUSB {
    public <init>(android.content.Context);
    public boolean isOTGOpen();
    public boolean openOTG();
    public boolean closeOTG();
    public void setWorkingSyncMode(boolean);
}

#for android
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService


-keepclasseswithmembernames class !org.apache.** {
  native <methods>;
}

-keepclasseswithmembernames class * {
  public <init>(android.content.Context, android.util.AttributeSet);
}

-keepclasseswithmembernames class * {
  public <init>(android.content.Context, android.util.AttributeSet, int);
}

-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}
-dontwarn android.newland.**
-dontwarn android.support.v4.**
-dontwarn java.lang.invoke.**