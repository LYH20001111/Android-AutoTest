-libraryjars  './build/jar/rt.jar'
-libraryjars  './build/jar/android.jar'
-libraryjars  './build/jar/k21.jar'
-libraryjars  './build/jar/k21Transation.jar'
-libraryjars  './build/jar/newland.jar'
-libraryjars  './build/jar/intelligentlibrary.jar'
-libraryjars  './build/jar/ndk.jar'

#-optimizationpasses 5

############################################################################
-dontoptimize
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses
-dontpreverify
-allowaccessmodification
-keepattributes *Annotation*,Exceptions,InnerClasses,Signature,Deprecated,EnclosingMethod
-dontshrink

-verbose
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*

-dontwarn org.apache.**
-adaptresourcefilenames    **/*.properties,**/*.gif,**/*.dtd
-adaptresourcefilecontents
#self defined
-keepclassmembers enum * {*;}

##############   api  ####################
-keep public class com.newland.sdk.mtype.** {*;}
-keep public class android.newland.** {*;}
-keep public class com.newland.smmanager.** {*;}
-keep public class com.newland.ndk.** {*;}
-keep public class com.newland.sdk.common.** {*;}
-keep public class com.newland.sdk.common.RunningModel {*;}
-keep public class android.misc.* {*;}
-keep public class com.newland.sdk.mtypex.tlv.** {*;}
###########command###############
#interface defined
-keep public class com.newland.sdk.mtypex.cmd.DeviceResponse {*;}
-keep public class com.newland.sdk.mtypex.cmd.DeviceCommand {*;}
-keep public interface com.newland.intelligent.jni.CmdRspListener {*;}
#all extends command  typename
-keep public class * extends com.newland.sdk.mtypex.cmd.DeviceResponse
-keep public class * extends com.newland.sdk.mtypex.cmd.DeviceCommand
-keepclassmembers class com.newland.sdk.mtypex.cmd.DeviceResponse {
  public <init> ();
	protected <fields>;
	protected <methods>;
	public <methods>;
}
-keepclassmembers class com.newland.sdk.mtypex.cmd.DeviceCommand{
  public <init> ();
	protected <fields>;
	protected <methods>;
	public <methods>;
}
-keepclassmembers class com.newland.sdk.mtypex.conn.AbortableDeviceCommand {
  public <init> ();
	protected <fields>;
	protected <methods>;
	public <methods>;
}


#for command serialize/unserialze
-keepclassmembers class * extends com.newland.sdk.mtypex.conn.AbortableDeviceCommand {
	private <fields>;
}
-keepclassmembers class * extends com.newland.sdk.mtypex.conn.CommonDeviceCommand {
	private <fields>;
}
-keepclassmembers class * extends com.newland.sdk.mtypex.cmd.AbstractSuccessResponse {
	private <fields>;
}

#some descriptions
-keep public @interface com.newland.sdk.mtypex.cmd.* {*;}
-keep public enum com.newland.sdk.mtypex.cmd.* {*;}

###########serializer###############
#interface defined
-keep public class com.newland.sdk.mtypex.serializer.Serializer {*;}
-keep public class com.newland.sdk.mtypex.cmd.CommandSerializer
-keep public class com.newland.sdk.mtypex.serializer.AbstractEnumSerializer

#for reflect
-keepclassmembers class * implements com.newland.sdk.mtypex.serializer.Serializer{
  public <init> ();
	protected <fields>;
	protected <methods>;
	public <methods>;
}

-keep public class com.newland.sdk.mtypex.cmd.AbstractCommandSerializer{
  public <init> ();
	protected <fields>;
	protected <methods>;
	public <methods>;
}



#########engines###############
#interface
-keep public class com.newland.sdk.mtypex.nseries3.NS3Executor
-keep public class com.newland.sdk.mtypex.nseries3.NS3ConnParams
-keep public class com.newland.sdk.mtypex.nseries.NSConnV100ConnParams
-keep public class com.newland.sdk.mtypex.conn.DeviceExecutor {*;}
-keep public class com.newland.sdk.mtypex.conn.DeviceKeepAliveStrategy {*;}
-keep public class com.newland.sdk.mtypex.conn.DeviceConnection {*;}
-keep public class com.newland.sdk.mtypex.conn.DirectMessageListener {*;}
-keep public class com.newland.sdk.mtypex.conn.DirectMessageListenerManager {*;}

#abstract modules
-keep public class com.newland.sdk.mtypex.AbstractModule
-keep public class com.newland.sdk.mtypex.AbstractDevice
-keep public class com.newland.sdk.mtypex.AbstractDeviceDriver
-keep public class com.newland.sdk.mtypex.AbstractCommandInvoker$EventMaker {*;}
-keepclassmembers class com.newland.sdk.mtypex.AbstractModule {
  public <init> ();
	protected <fields>;
	protected <methods>;
	public <methods>;
}
-keepclassmembers class com.newland.sdk.mtypex.AbstractDevice {
  public <init> ();
	protected <fields>;
	protected <methods>;
	public <methods>;
}
-keepclassmembers class com.newland.sdk.mtypex.AbstractDeviceDriver {
  public <init> ();
	protected <fields>;
	protected <methods>;
	public <methods>;
}
-keepclassmembers abstract class com.newland.sdk.mtypex.AbstractCommandInvoker{
  public <init> ();
	protected <fields>;
	protected <methods>;
	public <methods>;
}

###########connector##################
-keep public class com.newland.sdk.mtypex.conn.DeviceConnector {*;}
-keep public class DeviceConnParams {*;}
-keep public class * extends com.newland.sdk.mtypex.conn.DeviceConnector
-keep public class * extends DeviceConnParams

-keepclassmembers class * extends com.newland.sdk.mtypex.conn.DeviceConnector {
  public <init> ();
}
-keepclassmembers class * extends com.newland.sdk.mtype.conn.DeviceConnParams{
  public <init> ();
	public <methods>;
}

##############emv################
-keep public class com.newland.sdk.module.emv.EmvPackager {*;}



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

-dontwarn android.support.v4.**
