package com.newland.sdk.mtype.log;

import java.lang.reflect.Method;

import android.util.Log;

import com.newland.sdk.common.RunningModel;
//import com.newland.intelligent.jni.JniCmdInterface;


/**
 * Logger factory
 * <p>
 */
public class DeviceLoggerFactory {

    private static Object syncObj = new Object();

    private static IDeviceLoggerFactory instance = null;

    /**
     * 设置log级别，0表示全关，其他表示原日志规则，测试机开全部，关的话只关debug级别
     * -1表示默认原日志规则。
     */
    private static int loggerLevel = -1;

//	private static JniCmdInterface mCmdJNI = new JniCmdInterface();
//	private static byte[] mJniResponse = new byte[32];
//	private static int[]  mJniRespLen = new int[1];

    /**
     * This method is unable to initial one more file stored in
     * <tt>filename</tt>. The processing of specific user-defined logging mode
     * will be that the user may initialize the user-defined log mode via
     * {@link #init(IDeviceLoggerFactory)}.
     *
     * @param filename
     * @deprecated at 1.1.1
     */
    public static void init(String filename) {
        // do nothing now...
    }

    /**
     * Initialize the user-defined log mode
     * <p>
     *
     * @param deviceLoggerFactory Log mode interface
     */
    public void init(IDeviceLoggerFactory deviceLoggerFactory) {
        synchronized (syncObj) {
            instance = deviceLoggerFactory;
        }
    }

    private static class DefaultDeviceLogger implements DeviceLogger {

        private String TAG;

        DefaultDeviceLogger(String className) {
            this.TAG = className;
        }

        @Override
        public void warn(String msg) {
            if (DeviceLoggerFactory.loggerLevel != 0) {
                Log.w("[SDK:" + TAG + "]", msg);
            }
        }

        @Override
        public void warn(String msg, Throwable e) {
            if (DeviceLoggerFactory.loggerLevel != 0) {
                StackTraceElement[] trace = e.getStackTrace();
                String showMsg = "[SDK:" + TAG + "]";
                if (trace != null && trace.length > 0) {
                    showMsg = showMsg.concat("[" + String.valueOf(trace[0].getLineNumber()) + "]");
                }
                Log.w(showMsg, msg, e);
            }
        }

        @Override
        public void info(String msg, Throwable e) {
            if (DeviceLoggerFactory.loggerLevel != 0) {
                StackTraceElement[] trace = e.getStackTrace();
                String showMsg = "[SDK:" + TAG + "]";
                if (trace != null && trace.length > 0) {
                    showMsg = showMsg.concat("[" + String.valueOf(trace[0].getLineNumber()) + "]");
                }
                Log.i(showMsg, msg, e);
            }
        }

        @Override
        public void info(String msg) {
            if (DeviceLoggerFactory.loggerLevel != 0) {
                Log.i("[SDK:" + TAG + "]", msg);
            }
        }

        @Override
        public void error(String msg) {
            if (DeviceLoggerFactory.loggerLevel != 0) {
                Log.e("[SDK:" + TAG + "]", msg);
            }
        }

        @Override
        public void error(String msg, Throwable e) {
            if (DeviceLoggerFactory.loggerLevel != 0) {
                StackTraceElement[] trace = e.getStackTrace();
                String showMsg = "[SDK:" + TAG + "]";
                if (trace != null && trace.length > 0) {
                    showMsg = showMsg.concat("[" + String.valueOf(trace[0].getLineNumber()) + "]");
                }
                Log.e(showMsg, msg, e);
            }
        }

        @Override
        public void debug(String msg, Throwable e) {
            if (RunningModel.isDebugEnabled && DeviceLoggerFactory.loggerLevel != 0) {
                StackTraceElement[] trace = e.getStackTrace();
                String showMsg = "[SDK:" + TAG + "]";
                if (trace != null && trace.length > 0) {
                    showMsg = showMsg.concat("[" + String.valueOf(trace[0].getLineNumber()) + "]");
                }
                Log.d(showMsg, msg, e);
            }
        }

        @Override
        public void debug(String msg) {
            if (RunningModel.isDebugEnabled && DeviceLoggerFactory.loggerLevel != 0) {
                Log.d("[SDK:" + TAG + "]", msg);
            }
        }

        @Override
        public boolean isDebugEnabled() {
            return RunningModel.isDebugEnabled;
        }
    }

    private static class DefaultDeviceLoggerProxy implements DeviceLogger {

        private String tag;

        private DeviceLogger logger = null;

        private IDeviceLoggerFactory factory = null;

        DefaultDeviceLoggerProxy(String tag) {
            this.tag = tag;
        }

        @Override
        public void warn(String msg) {
            getLogger().warn(msg);
        }

        @Override
        public void warn(String msg, Throwable e) {
            getLogger().warn(msg, e);
        }

        @Override
        public void info(String msg, Throwable e) {
            getLogger().info(msg, e);
        }

        @Override
        public void info(String msg) {
            getLogger().info(msg);
        }

        @Override
        public void error(String msg) {
            getLogger().error(msg);
        }

        @Override
        public void error(String msg, Throwable e) {
            getLogger().error(msg, e);
        }

        @Override
        public void debug(String msg, Throwable e) {
            getLogger().debug(msg, e);
        }

        @Override
        public void debug(String msg) {
            getLogger().debug(msg);
        }

        @Override
        public boolean isDebugEnabled() {
            return getLogger().isDebugEnabled();
        }

        private DeviceLogger getLogger() {
            boolean hasBeenChanged = false;
            synchronized (syncObj) {
                if (factory != instance && instance != null) { // 如果由新的实例，且不为空，则赋值工厂
                    hasBeenChanged = true;
                    factory = instance;
                }
            }
            if (hasBeenChanged && factory != null) { // 若修改了工厂，且工厂不为空，则尝试使用工厂构造
                logger = factory.getLogger(tag);
            }
            if (logger == null) { // 若最终的结果是logger仍然为空，则构造一个默认的logger
                logger = new DefaultDeviceLogger(tag);
            }

            return logger;
        }
    }

    private static String getSysProperty(String key, String defaultValue) {
        String value = defaultValue;
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method method = c.getMethod("get", String.class);
            value = (String) (method.invoke(c, key));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }

    /**
     * Get the log
     *
     * @param className ClassName
     * @return
     */
    public static final DeviceLogger getLogger(String className) {
        // 系统配置优先级最高 update2018.05.29
        String prop = getSysProperty("persist.sys.nl_lib_debug", "0");
        if (prop.length() >= 2 && (prop.charAt(1) == '1' || prop.charAt(1) == '2')) {
//            RunningModel.isDebugEnabled = true;
            setLoggerLevel(1, 1, 1, DeviceLoggerFactory.loggerLevel);
        }
        return new DefaultDeviceLoggerProxy(className);
    }

    public static final DeviceLogger getLogger(Class<?> clazz) {
        return getLogger(clazz.getName());
    }

    /**
     * @param ndkLev
     * 0	关闭所有ndk日志
     * 1	打开所有ndk日志
     * 2	打开ndk Debug日志
     * 3	打开ndk Error日志
     * 4	打开ndk Verbose日志(未实现)
     * 6	打开ndk Info日志(未实现)
     * 7	打开ndk warning日志(未实现)
     * @param sdtpLev
     * 0	关闭所有sdtp日志
     * 1	打开所有sdtp日志
     * 2	打开sdtp Debug日志
     * 3	打开sdtp Error日志
     * 4	打开sdtp Verbose日志(未实现)
     * 6	打开sdtp Info日志(未实现)
     * 7	打开sdtp warning日志(未实现)
     * @param intelLev
     * 0   关闭所有intel 1、2级日志
     * 1   打开所有intel 1、2日志
     */
    private static int mLogFlag[] = {-1, -1, -1};

    public static boolean setLoggerLevel(int ndkLev, int sdtpLev, int intelLev, int loggerLevel) {
        try {
//			if(ndkLev < 0 || ndkLev > 3 || sdtpLev < 0 || sdtpLev > 3 || intelLev < 0 || intelLev > 1){
//				return false;
//			}
//			if(mLogFlag[0] != ndkLev || mLogFlag[1] != sdtpLev || mLogFlag[2] != intelLev){
//				String ndkLevStr = "0"+String.valueOf(ndkLev);
//				String sdtpLevStr = "0"+String.valueOf(sdtpLev);
//				String intelLevStr = "0"+String.valueOf(intelLev);
//				byte[] requestData = InnerUtils.hex2byte("1F01"+ndkLevStr+sdtpLevStr+intelLevStr);
//				mCmdJNI.jniMposLibCmd(requestData, requestData.length, mJniResponse, mJniRespLen);
//			}
//			mLogFlag[0] = ndkLev;
//			mLogFlag[1] = sdtpLev;
//			mLogFlag[2] = intelLev;
            DeviceLoggerFactory.loggerLevel = loggerLevel;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static int getLoggerLevel() {
        return loggerLevel;
    }
}
