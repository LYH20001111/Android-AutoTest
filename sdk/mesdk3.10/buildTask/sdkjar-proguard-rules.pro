-libraryjars  './build/jar/rt.jar'
-libraryjars  './build/jar/android.jar'
-libraryjars  './build/jar/k21.jar'
-libraryjars  './build/jar/k21Transation.jar'
-libraryjars  './build/jar/newland.jar'
-libraryjars  './build/jar/intelligentlibrary.jar'
-libraryjars  './build/jar/ndk.jar'
-libraryjars  './build/jar/EmvJNIService.jar'
-libraryjars  './build/devicemodule/devicemodule.jar'
-libraryjars  './build/scanmodule/scanmodule.jar'
-libraryjars  './build/nmjs-api/nmjs-api.jar'
-libraryjars  './build/jar/support-annotations-28.0.0-rc02.jar'

#-optimizationpasses 5

######################################################################
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

#self defined
-keep public class com.newland.sdk.module.emv.EmvPackager {*;}
-keep public class com.newland.sdk.mtype.** {*;}
-keep public class com.newland.emv.** {*;}
-keep public class com.newland.intelligent.** {*;}
-keep public class com.newland.sdk.me.*
-keep public class com.newland.sdk.emvl3.** {*;}
-keep public interface com.newland.sdk.me.DeviceManager {*;}
-keep public interface com.newland.sdk.me.ConnectionListener {*;}
-keep public interface com.newland.intelligent.jni.CmdRspListener {*;}
-keep public class * extends DeviceConnParams {*;}
-keep public class android.newland.** {*;}
-keep public class com.newland.smmanager.** {*;}
-keep public class com.newland.ndk.** {*;}
-keep public class android.misc.* {*;}
-keep public class com.newland.sdk.common.RunningModel {*;}
-keep public class com.newland.sdk.me.module.cardreader.CardReaderHelper {*;}
-keep public class com.newland.sdk.me.cmd.CmdCode {*;}
-keepclassmembers class com.newland.sdk.me.ConnUtils {
	public static final <methods>;
}

-keepclassmembers class * extends com.newland.sdk.mtypex.conn.AbortableDeviceCommand {
	private <fields>;
}
-keepclassmembers class * extends com.newland.sdk.mtypex.conn.CommonDeviceCommand {
	private <fields>;
}
-keepclassmembers class * extends com.newland.sdk.mtypex.cmd.AbstractSuccessResponse {
	private <fields>;
}
-keepclassmembers class * implements com.newland.sdk.mtypex.serializer.Serializer{
	public <init> ();
}
-keepclassmembers class com.newland.sdk.me.cmd.serializer.*{
	public <init> ();
}
-keepclassmembers enum * {
    *;
}


#for android
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService
-keep public class com.newland.mesdk.BuildConfig

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

-dontwarn android.support.v4.**
