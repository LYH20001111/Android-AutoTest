package com.newland.sdk.me.module.externalKeyboard;

import android.content.Context;
import android.newland.os.NlBuild;
import android.os.Build;

import com.newland.sdk.mtype.ExModuleType;
import com.newland.sdk.mtype.ModuleType;
import com.newland.sdk.mtype.log.DeviceLogger;
import com.newland.sdk.mtype.log.DeviceLoggerFactory;
import com.newland.sdk.module.externalKeyboard.KeyBoardCode;
import com.newland.sdk.module.externalKeyboard.ExtKeyboardModule;
import com.newland.sdk.module.externalKeyboard.KeyboardListener;
import com.newland.sdk.mtypex.AbstractDevice;
import com.newland.sdk.mtypex.AbstractModule;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MEExternalKeyboard extends AbstractModule implements ExtKeyboardModule {

    private DeviceLogger devicelogger = DeviceLoggerFactory.getLogger("MEExternalKeyboard");

    private final Object keyBoardSync = new Object();
    private final Object validKeysSync = new Object();
    private static boolean keyboardRunning = false;
    private Timer mTimer;
    private int showMsgFlag = 0;
    public static boolean isPauseGetKeyCode = false;
    private String keyCodeThreadName ="keyCodeThreadName";
    private Thread keyCodeThread = null;
    private KeyBoardHelper keyBoardHelp;
    private Context context;

    private Pattern patternNum = Pattern.compile("(([0-9]+\\.?[0-9]+)|([0-9]+\\.?)|([0-9]+))");
    private Pattern patternZH  = Pattern.compile("[\\u4e00-\\u9fa5]+");
    private Pattern patternMsg = Pattern.compile("[\\u4e00-\\u9fa5]+|[\\.|\\d]+");
    private KeyBoardCode[] mValidKeys = new KeyBoardCode[]{KeyBoardCode.KEY_NUM,KeyBoardCode.KEY_DOT,KeyBoardCode.KEY_OK,
            KeyBoardCode.KEY_BACKSPACE,KeyBoardCode.KEY_CANCEL,KeyBoardCode.KEY_INVALID,
            KeyBoardCode.KEY_FUN1,KeyBoardCode.KEY_FUN2,KeyBoardCode.KEY_FUN3,KeyBoardCode.KEY_FUN4};

    private static final int COMM_ERROR_CODE = -1;
    private static final int INPUT_ERROR_CODE = -2;

    private List<Byte> keyValueList = new ArrayList<Byte>();
    private Object keyValueListSync = new Object();
    private static Thread keyCodeDistributeThread = null;

    public MEExternalKeyboard(AbstractDevice owner, Context context) {
        super(owner);
        this.context = context;
        keyBoardHelp = new KeyBoardHelper(owner,context);
    }

    @Override
    public boolean isStandardModule() {
        return false;
    }

    @Override
    public ModuleType getStandardModuleType() {
        return null;
    }

    @Override
    public String getExModuleType() {
        return ExModuleType.KEYBOARD;
    }

    @Override
    public boolean isValid() {
        try {
            if (getKeyboardRunning()) {
                return true;
            }
            return keyBoardHelp.isEnabled();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean startKeyInput(final int timeout, final KeyboardListener listener) {
        synchronized (keyBoardSync) {
            if (getKeyboardRunning() == true) {
                devicelogger.debug("[startKeyInput]StartKeyboardInput already start.");
                return false;
            }
            if (listener == null) {
                devicelogger.debug("[startKeyInput]StartKeyboardInput error. listener=" + listener);
                return false;
            }
            boolean isStart = startKeyboardInput(timeout, listener);
            if (!isStart) {
                devicelogger.debug("[startKeyInput]StartKeyboardInput error. " + isStart);
                return false;
            }
            setValidKeys(new KeyBoardCode[]{KeyBoardCode.KEY_NUM,KeyBoardCode.KEY_DOT,KeyBoardCode.KEY_OK,
                    KeyBoardCode.KEY_BACKSPACE,KeyBoardCode.KEY_CANCEL,KeyBoardCode.KEY_INVALID,
                    KeyBoardCode.KEY_FUN1,KeyBoardCode.KEY_FUN2,KeyBoardCode.KEY_FUN3,KeyBoardCode.KEY_FUN4});
            int waitCount = 0;
            while (keyCodeThread != null && waitCount < 13){
                devicelogger.debug("[startKeyInput]KeyCodeThreadExitWait......waitCount="+waitCount+" currentThreadName="+Thread.currentThread().getName()+" keyCodeThread="+keyCodeThread);
                waitCount++;
                try {
                    setKeyboardRunning(false);
                    Thread.sleep(50);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            if(keyCodeThread != null) {
                devicelogger.debug("[startKeyInput]KeyboardThreadRuning Already Open ......");
                return false;
            }
            setKeyboardRunning(true);
            keyValueList.clear();
            keyCodeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        Thread.sleep(5);
                        keyCodeThreadName = Thread.currentThread().getName();
                        devicelogger.debug("[startKeyInput]keyCodeThreadName=" + keyCodeThreadName);
                        keyBoardHelp.startKeyBoardInput();
                        if(getKeyboardRunning()){
                            listener.onStart();
                        }
                        while (getKeyboardRunning()) {
                            byte[] keyCode = new byte[1];
                            int ret = keyBoardHelp.getKeyValue(keyCode);
//                            Log.d("DEBUG", "run: "+">>>getKeyValue ret="+ret+" "+keyCode[0]+" keyCodeThread="+keyCodeThread+" KeyboardRuning="+KeyboardRuning);
                            if (ret == keyBoardHelp.ACK_RWERR || ret == keyBoardHelp.ACK_CHECKERR) {
                                if(ret == keyBoardHelp.ACK_CHECKERR && Build.MODEL.toLowerCase().contains("f7")){
                                    devicelogger.error("[startKeyInput]f7 ACK_CHECKERR comm error!!!");
                                    continue;
                                }
                                devicelogger.error("[startKeyInput]comm error!!!");
                                keyCodeThread = null;
                                stopInput();
                                listener.onError();
                                break;
                            } else if ((ret == keyBoardHelp.ACK_OK && keyCode[0] == 0x00)) {
                                continue;
                            } else if(ret == keyBoardHelp.ACK_PAUSE){
                                devicelogger.debug("[startKeyInput]Pause get key code.");
                                Thread.sleep(10);
                                continue;
                            }
                            synchronized (keyValueListSync){
                                keyValueList.add(keyCode[0]);
                                devicelogger.debug("[startKeyInput]keyValueList size="+keyValueList.size());
                            }
                        }
                        keyCodeThreadName = "keyCodeThreadName";
                        keyCodeThread = null;
                        devicelogger.debug("[startKeyInput]Exit KeyCodeThread!!!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        keyCodeThread = null;
                        stopInput();
                        listener.onError();
                    }
                }
            });
            keyCodeThread.start();

            keyCodeDistributeThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (getKeyboardRunning()) {
                            byte[] keyCode = new byte[1];
                            int size = 0;
                            synchronized (keyValueListSync) {
                                size = keyValueList.size();
                                if (size != 0) {
                                    keyCode[0] = keyValueList.get(0);
                                    keyValueList.remove(0);
                                }
                            }
                            if(size == 0){
                                Thread.sleep(10);
                                continue;
                            }
                            devicelogger.debug("[keyCodeDistributeThread]Input keyCode=" + keyCode[0]);
                            if (keyCode[0] > 0x29 && keyCode[0] < 0x40) {
                                if (keyCode[0] == 0x2E) {
                                    if (!isValidKeys(KeyBoardCode.KEY_DOT))
                                        continue;
                                    listener.onKeyPress(KeyBoardCode.KEY_DOT, ".");
                                } else {
                                    if (!isValidKeys(KeyBoardCode.KEY_NUM))
                                        continue;
                                    listener.onKeyPress(KeyBoardCode.KEY_NUM, Integer.valueOf(new String(keyCode)) + "");
                                }
                            } else if (keyCode[0] == 0x0A) {
                                if (!isValidKeys(KeyBoardCode.KEY_BACKSPACE))
                                    continue;
                                listener.onKeyPress(KeyBoardCode.KEY_BACKSPACE, "");
                            } else if (keyCode[0] == 0x0d) {
                                if (!isValidKeys(KeyBoardCode.KEY_OK))
                                    continue;
                                listener.onKeyPress(KeyBoardCode.KEY_OK, "");
                                continue;
                            } else if (keyCode[0] == 0x1B) {
                                if (!isValidKeys(KeyBoardCode.KEY_CANCEL))
                                    continue;
                                listener.onKeyPress(KeyBoardCode.KEY_CANCEL, "");
                            } else if (keyCode[0] == 0x01) {
                                if (!isValidKeys(KeyBoardCode.KEY_FUN1))
                                    continue;
                                listener.onKeyPress(KeyBoardCode.KEY_FUN1, "");
                            } else if (keyCode[0] == 0x09) {
                                if (!isValidKeys(KeyBoardCode.KEY_FUN2))
                                    continue;
                                listener.onKeyPress(KeyBoardCode.KEY_FUN2, "");
                            } else if (keyCode[0] == 0x02) {
                                if (!isValidKeys(KeyBoardCode.KEY_FUN3))
                                    continue;
                                listener.onKeyPress(KeyBoardCode.KEY_FUN3, "");
                            } else if (keyCode[0] == 0x03) {
                                if (!isValidKeys(KeyBoardCode.KEY_FUN4))
                                    continue;
                                listener.onKeyPress(KeyBoardCode.KEY_FUN4, "");
                            } else {
                                if (!isValidKeys(KeyBoardCode.KEY_INVALID))
                                    continue;
                                listener.onKeyPress(KeyBoardCode.KEY_INVALID, "");
                                continue;
                            }
                        }
                        keyCodeDistributeThread = null;
                        devicelogger.debug("[keyCodeDistributeThread]Exit keyCodeDistributeThread!!!");
                    } catch (Exception e) {
                        e.printStackTrace();
                        keyCodeDistributeThread = null;
                        stopInput();
                        listener.onError();
                    }
                }
            });
            keyCodeDistributeThread.start();
            return true;
        }
    }

    private boolean startKeyboardInput(int timeout,final KeyboardListener listener){
        try {
            if(timeout < 0){
                devicelogger.debug("[startKeyboardInput] timeout = "+timeout);
                return false;
            }
            boolean isEnabled = keyBoardHelp.isEnabled();
            if(!isEnabled){
                devicelogger.debug("[startKeyboardInput] disEnabled.");
                return false;
            }
            setKeyboardRunning(true);
            if(timeout > 0){
                mTimer = new Timer();
                mTimer.schedule(new TimerTask() {
                    public void run() {
                        devicelogger.error("[startKeyboardInput]stopInput timeOut."+Thread.currentThread().getName());
                        stopInput();
                        listener.onTimeOut();
                    }
                }, timeout*1000);
            } else {
                if (mTimer != null) {
                    mTimer.cancel();
                    mTimer = null;
                }
            }
            showMsgFlag = 0;
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean isValidKeys(KeyBoardCode keycode){
        synchronized (validKeysSync){
            try {
                for(int i=0;i < mValidKeys.length; i++){
                    if(mValidKeys[i].equals(keycode)){
                        return true;
                    }
                }
                devicelogger.debug("[isValidKeys]No callback required. "+keycode.toString());
                return false;
            } catch (Exception e) {
                e.printStackTrace();
                mValidKeys = new KeyBoardCode[]{KeyBoardCode.KEY_NUM,KeyBoardCode.KEY_DOT,KeyBoardCode.KEY_OK,
                        KeyBoardCode.KEY_BACKSPACE,KeyBoardCode.KEY_CANCEL,KeyBoardCode.KEY_INVALID,
                        KeyBoardCode.KEY_FUN1,KeyBoardCode.KEY_FUN2,KeyBoardCode.KEY_FUN3,KeyBoardCode.KEY_FUN4};
            }
            return false;
        }
    }

    @Override
    public boolean stopInput() {
        synchronized (keyBoardSync) {
            try {
                setKeyboardRunning(false);
                devicelogger.debug("[stopInput]stopKeyboard  keyCodeThread="+keyCodeThread +" KeyboardRunning="+keyboardRunning+" mTimer="+mTimer);
                try {
                    if (keyCodeThread != null) {
                        devicelogger.debug("[stopInput]keyCodeThread.join");
                        keyCodeThread.join(2 * 1000);
                    }
                } catch (Exception e) {
                    devicelogger.debug("[stopInput]keyCodeThread.join Exception");
                }
                try {
                    if(keyCodeDistributeThread != null){
                        devicelogger.debug("[stopInput]keyCodeDistributeThread.join");
                        keyCodeDistributeThread.join(2 * 1000);
                    }
                } catch (Exception e) {
                    devicelogger.debug("[stopInput]keyCodeDistributeThread.join Exception");
                }
                if (mTimer != null) {
                    mTimer.cancel();
                }
                devicelogger.debug("[stopInput]stopKeyboard keyCodeThread="+keyCodeThread);
            } catch (Exception e) {
                e.printStackTrace();
            }finally {
                try {
                    mTimer = null;
                    keyCodeThread = null;
                    keyCodeDistributeThread = null;
                    keyBoardHelp.stopKeyBoardInput();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }
    }

    @Override
    public boolean showMessage(String message) {
        isPauseGetKeyCode = true;
        synchronized (keyBoardSync) {
            try {
                devicelogger.debug("[showMessage]showLEDMessage msg...=" + message);
                if (message == null) {
                    return false;
                }
                if(message.equals("")){
                    keyBoardHelp.clearScreen();
                    return true;
                }
                if (message.length() > 10) {
                    return false;
                }
                boolean isRight = false;
                Matcher m = patternMsg.matcher(message);
                while (m.find()) {
                    String str = m.group();
                    devicelogger.debug("[showMessage]showLEDMessage str="+str);
                    char c = str.charAt(0);
                    if((c>='0'&& c<='9')||(c == '.')){
                        Matcher isValid = patternNum.matcher(str);
                        if(!isValid.matches()){
                            devicelogger.debug("[showMessage]showLEDMessage is valid1");
                            return false;
                        }
                    }else{
                        Matcher isValid = patternZH.matcher(str);
                        if(!isValid.matches()){
                            devicelogger.debug("[showMessage]showLEDMessage is valid2");
                            return false;
                        }
                    }
                    isRight = true;
                }
                if(!isRight){
                    devicelogger.debug("[showMessage]showLEDMessage is valid3");
                    return false;
                }
                showMsgFlag = 1;
                keyBoardHelp.showMessage(message, context.getResources().getAssets().open("BmpFonts.DZK"));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return false;
        }
    }

    @Override
    public void setValidKeys(KeyBoardCode[] validKeys) {
        synchronized (validKeysSync){
            try {
                if(validKeys == null){
                    return;
                }
                for(int i=0; i < validKeys.length; i++){
                    if(!(validKeys[i] instanceof KeyBoardCode)){
                        return;
                    }
                }
                this.mValidKeys = validKeys;
            } catch (Exception e) {
                e.printStackTrace();
                mValidKeys = new KeyBoardCode[]{KeyBoardCode.KEY_NUM,KeyBoardCode.KEY_DOT,KeyBoardCode.KEY_OK,
                        KeyBoardCode.KEY_BACKSPACE,KeyBoardCode.KEY_CANCEL,KeyBoardCode.KEY_INVALID,
                        KeyBoardCode.KEY_FUN1,KeyBoardCode.KEY_FUN2,KeyBoardCode.KEY_FUN3,KeyBoardCode.KEY_FUN4};
            }
        }
    }

    @Override
    public void setClickSound(boolean clickSound) {
        try {
            isPauseGetKeyCode = true;
            synchronized (keyBoardSync) {
                keyBoardHelp.setKeyTone(clickSound);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static final Object keyboardRunningSync = new Object();

    private static void setKeyboardRunning(boolean isRun){
        synchronized (keyboardRunningSync){
            keyboardRunning = isRun;
        }
    }
    public static boolean getKeyboardRunning(){
        synchronized (keyboardRunningSync) {
            return keyboardRunning;
        }
    }
}
