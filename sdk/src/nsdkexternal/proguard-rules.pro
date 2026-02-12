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

# 代码混淆压缩比，在 0~7 之间，默认为 5，一般不做修改
-optimizationpasses 5
# 混淆时不使用大小写混合，混淆后的类名为小写(大小写混淆容易导致 class 文件相互覆盖）
-dontusemixedcaseclassnames
# 混淆时采用的算法，是谷歌推荐的算法，一般不做更改
-optimizations !code/simplification/arithmetic,!field/,!class/merging/
# 保持注解
-keepattributes Annotation
# 忽略警告
-ignorewarnings
# 保持 native 方法不被混淆
-keepclasseswithmembernames class * {
   native <methods>;
}
# 不混淆资源类
-keepclassmembers class *.R$ {
public static *;
}
# 打印混淆的详细信息
-verbose
# 保持枚举 enum 类不被混淆
-keepclassmembers enum * {
    public static **[] values();
    public static ** valueOf(java.lang.String);
}
# 不跳过 library 中的非 public 的类
-dontskipnonpubliclibraryclassmembers
# 不进行预校验，Android 不需要，可加快混淆速度
-dontpreverify
# 不进行优化，优化可能会造成一些潜在风险
-dontoptimize
# 避免混淆泛型
-keepattributes Signature
-keepattributes Exceptions
-keepattributes InnerClasses
# 保留代码行号，方便异常信息的追踪
-keepattributes SourceFile,LineNumberTable

# support-v4 包
#-dontwarn android.support.**
#-keep class android.support.v4.app.** { *; }
#-keep interface android.support.v4.app.** { *; }
#-keep class android.support.v4.** { *; }

# 保留 Serializable 序列化的类不被混淆
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    !static !transient <fields>;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}

# 保留 Parcelable 序列化类不被混淆
-keep class * implements android.os.Parcelable {
  public static final android.os.Parcelable$Creator *;
}

# api 接口都要保持
-keep class com.newland.nsdk.core.api.common.**{*;}
-keep class com.newland.nsdk.core.api.external.**{*;}

-keep class com.newland.nsdk.core.common.uart3.UART3PortImpl{
public *;
}

# 外置的 emv 配置不能混淆
-keep class com.newland.nsdk.core.external.emvl3.configuration.**{public *;}

# nsdk
-keep class com.newland.nsdk.core.external.card.**{public *;}
-keep class com.newland.nsdk.core.external.ExtNSDKModuleManagerImpl{
public *;
}

# jni 要调用的类不能混淆，否则 jni 会找不到
-keep class com.newland.nsdk.core.common.keymanager.ST_SEC_KCV_DATA{
*;
}
-keep class com.newland.nsdk.core.common.keymanager.ST_SEC_ASYM_KEYIN_DATA{
*;
}

-keep class com.newland.nsdk.core.common.uart3.SerialPortJni{
public *;
}

-keep class com.newland.nsdk.core.external.command.contactlesscard.ExternalContactlessCardModule{
public *;
}
-keep class com.newland.nsdk.core.external.command.communication.bluetooth.BluetoothCommunicator{
public *;
}
-keep class com.newland.nsdk.core.external.command.communication.ExternalCommunicationManager{
public *;
}
-keep class com.newland.nsdk.core.external.Ext*{
public *;
}
-keep class com.newland.nsdk.core.external.command.ExternalCommandConfig{
public *;
}
-keep class com.newland.nsdk.core.external.command.contactlesscard.ExternalContactlessCardModule{
public *;
}

-keep class com.newland.nsdk.core.external.command.emv.aid.*{
public *;
}

-keep class com.newland.nsdk.core.external.command.emv.capk.*{
public *;
}
-keep class com.newland.nsdk.core.external.command.emv.listener.*{
public *;
}
-keep class com.newland.nsdk.core.external.command.emv.revocationlist.*{
public *;
}
-keep class com.newland.nsdk.core.external.command.emv.terminalconfiguration.*{
public *;
}
-keep class com.newland.nsdk.core.external.command.emv.exceptionlist.*{
public *;
}
-keep class com.newland.nsdk.core.external.command.emv.ExternalEmvL3Module{
public *;
}
#-keep class com.newland.nsdk.core.external.command.smartcard.ExternalSmartCardModule{
#*;
#}