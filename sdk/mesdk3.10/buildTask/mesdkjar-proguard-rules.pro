-libraryjars  '../ThirdParty/rt-1.8.0_181.jar'
-libraryjars  '../ThirdParty/android-23.jar'
-libraryjars  '../ThirdParty/support-annotations-28.0.0-rc02.jar'
-libraryjars  '../ThirdParty/appcompat-v7-28.0.0.jar'

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

-dontwarn org.apache.**
-adaptresourcefilenames    **/*.properties,**/*.gif,**/*.dtd
-adaptresourcefilecontents

#self defined
-keepclassmembers enum * {*;}
-keep public class com.newland.sdk.module.emv.** {*;}
-keep public class com.newland.sdk.me.module.pininput.KeyBoardParams {*;}
-keep public class com.newland.sdk.utils.ISOUtils {*;}
-keep public class com.newland.sdk.utils.TLVMsg {*;}
-keep public class com.newland.sdk.utils.TLVPackage {*;}
-keep public class com.newland.sdk.me.module.cardreader.K21CardReaderEvent{*;}
-keep public class com.newland.sdk.me.module.emv.MEEMVL2 {*;}
-keep public class com.newland.sdk.module.devicebasic.DeviceInfo {*;}
-keep public class com.newland.sdk.me.module.emv.EMVInnerUtils {*;}
-keep public class com.newland.sdk.me.module.emv.EMVParseUtil {*;}
-keep public class com.newland.mesdk.emvl3module.** {*;}
-keep public class com.newland.sdk.utils.** {*;}
-keep public class com.newland.sdk.module.** {*;}
-keep public class com.newland.sdk.module.scanner.** {*;}
-keep public class com.newland.sdk.module.emv.EmvPackager {*;}
-keep public class com.newland.sdk.ModuleManage{*;}
-keep public class com.newland.sdk.module.emv.SimpleEmvPackager {*;}
-keep public class com.newland.sdk.mtype.** {*;}
-keep public class com.newland.emv.** {*;}
-keep public class com.newland.sdk.emvl3.** {*;}
-keep public class com.newland.intelligent.** {*;}
-keep public class com.newland.sdk.intelligent.** {*;}
-keep public class com.newland.sdk.me.*
-keep public interface com.newland.sdk.me.DeviceManager {*;}
-keep public interface com.newland.sdk.me.ConnectionListener {*;}
-keep public interface com.newland.intelligent.jni.CmdRspListener {*;}
-keep public class com.newland.sdk.me.module.emv.EMVLevel2Const {*;}
-keep public class * extends DeviceConnParams {*;}
-keep public class * extends com.newland.sdk.mtype.conn.DeviceConnParams {*;}
-keep public class android.newland.** {*;}
-keep public class com.newland.smmanager.** {*;}
-keep public class com.newland.ndk.** {*;}
-keep public class com.newland.event.EventCallBack {*;}
-keep public class com.newland.event.EM_SYS_EVENT {*;}
-keep public class android.misc.* {*;}
-keep public class com.newland.sdk.me.module.emv.EMVLevel2Const$* {*;}
-keep public class com.newland.sdk.me.module.emv.AbstractEMVTransController {*;}
-keep public class com.newland.sdk.mtypex.module.common.emv.SoundPoolImpl{*;}
-keep public class com.newland.sdk.me.module.scanner.** {*;}
-keep public class com.newland.sdk.mesdk.scanmodule.** {*;}
-keep public class com.newland.sdk.common.RunningModel
-keep public class com.newland.sdk.common.** {*;}
-keep public class com.newland.mesdk.devicemodule.** {*;}
-keep public class com.newland.sdk.common.RunningModel {*;}
-keep public class com.newland.sdk.me.module.cardreader.CardReaderHelper {*;}
-keep public class com.newland.sdk.me.cmd.CmdCode {*;}
-keep public class com.newland.sdk.me.module.emv.MEEMVLevel2 {*;}
-keep public class com.newland.emvl2.jni.** {*;}
-keep public class com.newland.mesdk.emvl3module.** {*;}
-keep public class com.newland.sdk.emvl3.** {*;}
-keep public class android.misc.* {*;}
-keep public class com.newland.sdk.mtypex.tlv.** {*;}
-keep public class com.newland.intelligent.** {*;}

-keep public class com.newland.sdk.me.ConnUtils {*;}
-keep public class com.newland.sdk.me.utils.** {*;}
-keep public class com.newland.sdk.me.module.printer.TTFPrint{*;}
-keep public class com.newland.sdk.mtypex.module.common.emv.SoundPoolImpl{*;}

-keep public class com.newland.sdk.me.module.emvl3.jni.** {*;}
-keep public class com.newland.forth.** {*;}
-keep public class com.newland.sdk.me.DeviceManager
-keep public class com.newland.sdk.me.DeviceManager$* {*;}
-keep public class com.newland.sdk.me.module.externalPininput.MposComm {*;}
-keep public class com.newland.sdk.mtypex.cmd.AbstractSuccessResponse{*;}
-keep public class com.newland.sdk.mtypex.conn.DeviceResponseComplete{*;}
-keep public class com.newland.sdk.me.module.emvl3.impl.MENEmvL3Decorator{*;}

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
-keepclassmembers class * implements com.newland.sdk.mtypex.serializer.Serializer{
	public <init> ();
}

-keepclassmembers class com.newland.me.cmd.serializer.*{
	public <init> ();
}

-keepclassmembers class com.newland.sdk.me.cmd.serializer.*{
	public <init> ();
}
-keepclassmembers enum * {
    *;
}
-keepclasseswithmembernames class com.newland.sdk.me.module.usb.MEUSB {
    public <init>(android.content.Context);
    public boolean isOTGOpen();
    public boolean openOTG();
    public boolean closeOTG();
}

-keepclasseswithmembernames class com.newland.sdk.me.module.emvl3.impl.EmvL3Comm {
    public byte[] Communication(byte[]);
}


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
-keep public class com.newland.sdk.mtypex.usb.* {*;}
-keep public class com.newland.sdk.me.ME3xDriver
-keep public class com.newland.sdk.mtypex.AbstractMESeriesDriver
-keep public class com.newland.sdk.mtypex.bluetooth.BlueToothConnParams
-keep public class com.newland.sdk.mtypex.bluetooth.DeviceConnState
-keep public class com.newland.sdk.mtypex.cmd.AbstractSuccessResponse{*;}
-keep public class com.newland.sdk.mtypex.conn.DeviceResponseComplete{*;}

-keepclassmembers class * extends com.newland.sdk.mtypex.conn.DeviceConnector {
    public <init> ();
}

-keepclassmembers class * extends com.newland.sdk.mtype.conn.DeviceConnParams{
    public <init> ();
    public <methods>;
}

#for android
-keep public class * extends android.app.Activity 
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class com.android.vending.licensing.ILicensingService
-keep public class  com.newland.sdk.mtype.log.DeviceLogger
-keep public class  com.newland.sdk.mtype.util.InnerUtils

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
-keep  public class com.newland.sdk.mtypex.nseries.NSeriesDeviceExecutor

-dontwarn android.support.v4.**
-dontwarn android.newland.INLUART3Manager
-dontwarn android.newland.**
-dontwarn android.support.v4.**
-dontwarn android.newland.scan.**


-keep public class com.newland.sdk.module.printerPro.** {*;}
-keep public interface com.newland.sdk.module.printerPro.** {*;}
-keep public enum com.newland.sdk.module.printerPro.** {*;}

-keep class com.newland.sdk.me.module.printerPro.appimpl.internal.InternalPrinterModule{
    public <init>();
}
-keep class com.newland.sdk.me.module.printerPro.meimpl.AppPrinter {
    public <init>();
}

-keepclasseswithmembernames class com.newland.sdk.me.module.cardreader.MECardReader{
    public void setLastReaderTypes(com.newland.sdk.module.cardreader.CardType[]);
}

-keep public class com.newland.NlBluetooth.** {*;}
-keep public class com.newland.sdk.me.module.emvl3.impl.EmvL3Constant {*;}
-keep public class com.newland.sdk.me.module.emvl3.impl.EmvL3Constant$* {*;}
-keep public class com.newland.sdk.me.module.emv.FileUtils{*;}
-keep public class com.newland.sdk.me.module.printerPro.appimpl.internal.PrinterHelper{*;}

# 示例：忽略 com.newland.rkl 包下所有类的警告
-dontwarn com.newland.rkl.**
-dontwarn com.newland.nsdk.plugin.rkl.**