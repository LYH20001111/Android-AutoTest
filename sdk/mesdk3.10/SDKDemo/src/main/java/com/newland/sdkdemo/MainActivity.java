package com.newland.sdkdemo;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.hardware.display.DisplayManager;
import android.newland.os.NlBuild;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
//import android.support.v4.app.FragmentActivity;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
import android.provider.Settings;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;


import com.newland.sdk.ModuleManage;
import com.newland.sdk.module.devicebasic.DeviceBasicModule;
import com.newland.sdk.module.externalPin.ExtPinpadModule;
import com.newland.sdk.module.externalPin.PinpadExtParams;
import com.newland.sdk.module.externalPin.PinpadInitExtParams;
import com.newland.sdk.module.light.IndicatorLightModule;
import com.newland.sdk.module.light.LightColor;
import com.newland.sdk.module.light.LightState;
import com.newland.sdk.module.pin.AccountInputType;
import com.newland.sdk.module.pin.AlgorithmMode;
import com.newland.sdk.module.pin.DefaultLayout;
import com.newland.sdk.module.pin.DukptDerivateUsage;
import com.newland.sdk.module.pin.KeyManagement;
import com.newland.sdk.module.pin.PinBlockMode;
import com.newland.sdk.module.pin.PinInputExtParams;
import com.newland.sdk.module.pin.PinInputListener;
import com.newland.sdk.module.pin.RNIBPinInputListener;
import com.newland.sdk.module.serialport.PortType;
import com.newland.sdk.mtype.common.ErrorMsg;
import com.newland.sdk.mtype.common.ErrorMsgHelper;
import com.newland.sdk.utils.ISOUtils;
import com.newland.sdkdemo.activity.KeyBoardNumberActivity;
import com.newland.sdkdemo.activity.KeyBoardNumberPresentation;
import com.newland.sdkdemo.activity.RNIB.DRNIBKeyBoardActivity;
import com.newland.sdkdemo.activity.RNIB.DRNIBKeyBoardPresentation;
import com.newland.sdkdemo.event.ModuleClickListener;
import com.newland.sdkdemo.event.PinEntryListener;
import com.newland.sdkdemo.fragment.BaseFragment;
import com.newland.sdkdemo.fragment.BridgeFragment;
import com.newland.sdkdemo.fragment.BuzzerFragment;
import com.newland.sdkdemo.fragment.CardReaderFragment;
import com.newland.sdkdemo.fragment.EMVFragment;
import com.newland.sdkdemo.fragment.DisplayScreenFragment;
import com.newland.sdkdemo.fragment.ExtKeyboardFragment;
import com.newland.sdkdemo.fragment.ExtRFCardFragment;
import com.newland.sdkdemo.fragment.ExternalCardReaderFragment;
import com.newland.sdkdemo.fragment.ExternalPinInputFragment;
import com.newland.sdkdemo.fragment.ExternalScanBoxFragment;
import com.newland.sdkdemo.fragment.ICCardFragment;
import com.newland.sdkdemo.fragment.LightFragment;
import com.newland.sdkdemo.fragment.mdb.MDBFragment;
import com.newland.sdkdemo.fragment.MenuFragment;
import com.newland.sdkdemo.fragment.ModulesFragment;
import com.newland.sdkdemo.fragment.PinDUKPTFragment;
import com.newland.sdkdemo.fragment.PinMKSKFragment;
import com.newland.sdkdemo.fragment.PrinterFragment;
import com.newland.sdkdemo.fragment.RFCardFragment;
import com.newland.sdkdemo.fragment.RKLFragment;
import com.newland.sdkdemo.fragment.SMFragment;
import com.newland.sdkdemo.fragment.ScannerFragment;
import com.newland.sdkdemo.fragment.SerialFragment;
import com.newland.sdkdemo.fragment.DeviceBasicFragment;
import com.newland.sdkdemo.fragment.SettingsFragment;
import com.newland.sdkdemo.fragment.USBFragment;
import com.newland.sdkdemo.fragment.dock.DockRootFragment;
import com.newland.sdkdemo.fragment.mdb.MDBFragment;
import com.newland.sdkdemo.showutil.ShowInfoUtil;
import com.newland.sdkdemo.utils.DialogUtils;
import com.newland.sdkdemo.utils.MessageTag;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Locale;

import static com.newland.sdk.me.cmd.CmdCode.PINPAD_INPUT;

/**
 * Author by bxy, Date on 2018/11/13 0013.
 */
public class MainActivity extends FragmentActivity implements View.OnClickListener {

    public static ArrayList<ModuleInfo> modulesFragment = new ArrayList<ModuleInfo>();
    private ActionBar actionBar;
    private int moduleIndex=0;
    private String newMessage="",message="";
    private TextView tvOperationMessage;
    private Html.ImageGetter imageGetter;
    private Button btnKeySysAlg,btnClear;
    private boolean isConnectDevice;
    private FrameLayout moduleMenu,moduleContent;
    private ModuleManage moduleManage;
    private PinEntryListener pinEntryListener;
    private Object ledObject = new Object();
    private static LedOperationRunnable ledOperationRunnable;

    private ShowInfoUtil showInfoUtil;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        showInfoUtil = new ShowInfoUtil(this);
        initUI();
        initData();

    }

    private void initData(){
        isConnectDevice = false;
        moduleManage = ModuleManage.getInstance();
        boolean connectStatus= moduleManage.init(this);
        moduleManage.setDebugMode(true);

        if(!connectStatus){
           showMessage(MainActivity.this.getString(R.string.msg_device_not_conn),MessageTag.TIP);
           return;
       }
        SDKExecutors.setThreadPoolRunning(true);
        setConnectState(true);
        modulesFragment.clear();
        modulesFragment.add(new ModuleInfo(new ModulesFragment(this, new ModuleClickListener() {
            @Override
            public void onModuleClickListener(int index) {
                if(!isConnectDevice){
                    showMessage(MainActivity.this.getString(R.string.msg_device_conn_first),MessageTag.TIP);
                    return;
                }
                setModuleFragment(index);
            }
        }),0,0));
        modulesFragment.add(new ModuleInfo(new MenuFragment(this, new ModuleClickListener() {
            @Override
            public void onModuleClickListener(int index) {
                setModuleFragment(index);
            }
        }),0,0));
        DeviceBasicModule deviceBasicModule=moduleManage.getDeviceBasicModule();
        if(deviceBasicModule.hasSecurityModule()){
            modulesFragment.add(new ModuleInfo(new PinDUKPTFragment(this),R.string.tv_pin_f,R.drawable.pin));
        }
        modulesFragment.add(new ModuleInfo(new EMVFragment(this),R.string.tv_emv_f,R.drawable.emv));
        AppConfig.INDEX_FRAGMENT_START = modulesFragment.size()-1;
        if(deviceBasicModule.hasSecurityModule()){
            modulesFragment.add(new ModuleInfo(new CardReaderFragment(this),R.string.tv_cardreader_f,R.drawable.cardreader));
            modulesFragment.add(new ModuleInfo(new ICCardFragment(this),R.string.tv_iccard_f,R.drawable.iccard));
            modulesFragment.add(new ModuleInfo(new RFCardFragment(this),R.string.tv_rfcard_f,R.drawable.rfcard));
            modulesFragment.add(new ModuleInfo(new LightFragment(this),R.string.tv_light_f,R.drawable.light));
            modulesFragment.add(new ModuleInfo(new PinMKSKFragment(this),R.string.tv_pin_f,R.drawable.pin));
            AppConfig.INDEX_FRAGMENT_PIN = modulesFragment.size()-1;
            modulesFragment.add(new ModuleInfo(new RKLFragment(this),R.string.tv_rkl_f,R.drawable.pin));
            modulesFragment.add(new ModuleInfo(new ScannerFragment(this),R.string.tv_scanner_f,R.drawable.scanner));
            Locale defalutlocale = Locale.getDefault();
            if (defalutlocale.getCountry().equalsIgnoreCase("CN")){
                modulesFragment.add(new ModuleInfo(new SMFragment(this),R.string.tv_sm_f,R.drawable.ternaml));
            }
        }
        modulesFragment.add(new ModuleInfo(new PrinterFragment(this),R.string.tv_printer_f,R.drawable.printer));
        modulesFragment.add(new ModuleInfo(new BuzzerFragment(this),R.string.tv_buzzer_f,R.drawable.emv));
        modulesFragment.add(new ModuleInfo(new ExternalPinInputFragment(this),R.string.tv_external_pin,R.drawable.pin));
        modulesFragment.add(new ModuleInfo(new ExtRFCardFragment(this), R.string.module_ext_rfcard, R.drawable.rfcard));
        modulesFragment.add(new ModuleInfo(new ExtKeyboardFragment(this), R.string.module_ext_keyboard, R.drawable.externalpin));
        modulesFragment.add(new ModuleInfo(new ExternalScanBoxFragment(this), R.string.module_ext_scanbox, R.drawable.cardreader));
        modulesFragment.add(new ModuleInfo(new SerialFragment(this),R.string.tv_usbserial_f,R.drawable.swip));
        modulesFragment.add(new ModuleInfo(new DisplayScreenFragment(this),R.string.tv_guest_display,R.drawable.light));
        modulesFragment.add(new ModuleInfo(new DeviceBasicFragment(this),R.string.tv_terminalmanage_f,R.drawable.ternaml));
        modulesFragment.add(new ModuleInfo(new SettingsFragment(this),R.string.tv_settings,R.drawable.ternaml));
        modulesFragment.add(new ModuleInfo(new USBFragment(this),R.string.tv_usb_f,R.drawable.ternaml));
        modulesFragment.add(new ModuleInfo(new ExternalCardReaderFragment(this),R.string.tv_external_cardrader,R.drawable.ternaml));
        modulesFragment.add(new ModuleInfo(new ScannerFragment(this),R.string.tv_scanner_f,R.drawable.scanner));
        modulesFragment.add(new ModuleInfo(new DockRootFragment(this),R.string.tv_dock_f,R.drawable.swip));
        modulesFragment.add(new ModuleInfo(new MDBFragment(this),R.string.tv_mdb_f,R.drawable.emv));

        setModuleFragment(0);
//        if(!Locale.getDefault().getLanguage().equalsIgnoreCase("zh")){
//            standByLed();
//        }
    }
    private void setModuleFragment(int index) {
        if(index >= modulesFragment.size()){
            return;
        }
        if(index == 0){
            btnKeySysAlg.setVisibility(View.VISIBLE);
            btnClear.setVisibility(View.GONE);
            moduleMenu.setVisibility(View.GONE);
        }else{
            btnKeySysAlg.setVisibility(View.GONE);
            btnClear.setVisibility(View.VISIBLE);
            moduleMenu.setVisibility(View.VISIBLE);
        }
        if((AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)||AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG))&&
                AppConfig.INDEX_FRAGMENT_PIN == index){
            index = 2;
        }
        moduleIndex = index;
        String title = modulesFragment.get(moduleIndex).fragment.title();if(title==null) title = "";
        String fragmentName = modulesFragment.get(moduleIndex).fragment.getClass().getSimpleName();
        actionBar.setTitle(title+"("+fragmentName+")");
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if(index != 0){
            ft.replace(R.id.id_module_menu,new BridgeFragment(getBasePager(1)));
        }
        ft.replace(R.id.id_module_content,new BridgeFragment(getBasePager(index)));
        ft.commit();
    }
    public void switchFragment(BaseFragment fragment){

        String title = fragment.title();if(title==null) title = "";
        String fragmentName = fragment.getClass().getSimpleName();
        actionBar.setTitle(title+"("+fragmentName+")");
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        fragment.initData();
        ft.replace(R.id.id_module_content,new BridgeFragment(fragment));
        ft.commit();
    }
    private BaseFragment getBasePager(int index) {
        try {
            BaseFragment baseFragment = modulesFragment.get(index).fragment;
            if (baseFragment != null && !baseFragment.isInitData()) {
                baseFragment.initData();
                baseFragment.setInitData(true);
            }
            return baseFragment;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()== android.R.id.home){
            setModuleFragment(0);
        }
        return super.onOptionsItemSelected(item);
    }

    private void initUI(){
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tvOperationMessage = (TextView) findViewById(R.id.id_info);
        showInfoUtil.setShowMessageView(tvOperationMessage);
        btnClear = findViewById(R.id.id_clear);btnClear.setOnClickListener(this);
        btnKeySysAlg = findViewById(R.id.id_keysysalg);btnKeySysAlg.setOnClickListener(this);
        imageGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                int id = Integer.parseInt(source);
                Drawable drawable = getResources().getDrawable(id);
                if (getWindowWidth() <= drawable.getIntrinsicWidth()) {
                    drawable.setBounds(0, 10, getWindowWidth(), drawable.getIntrinsicHeight());
                } else {
                    drawable.setBounds((getWindowWidth() - drawable.getIntrinsicWidth()) / 2, 10, drawable.getIntrinsicWidth() + (getWindowWidth() - drawable.getIntrinsicWidth()) / 2, drawable.getIntrinsicHeight());
                }
                return drawable;
            }
        };
        moduleMenu = (FrameLayout) findViewById(R.id.id_module_menu);
        moduleMenu.setVisibility(View.GONE);
        moduleContent = (FrameLayout) findViewById(R.id.id_module_content);
    }

    private int getWindowWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public void clearMessage() {
        showInfoUtil.cleanMessage();
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvOperationMessage.setText("");
                newMessage = "";
            }
        });
    }
    public void showMessage(final String mess, final int messageType) {
        com.newland.sdkdemo.showutil.MessageTag tag = com.newland.sdkdemo.showutil.MessageTag.NORMAL;
        switch (messageType) {
            case MessageTag.NORMAL:
                tag = com.newland.sdkdemo.showutil.MessageTag.NORMAL;
                break;
            case MessageTag.ERROR:
                tag = com.newland.sdkdemo.showutil.MessageTag.ERROR;
                break;
            case MessageTag.TIP:
                tag = com.newland.sdkdemo.showutil.MessageTag.TIP;
                break;
            case MessageTag.DATA:
                tag = com.newland.sdkdemo.showutil.MessageTag.DATA;
                break;
            default:
                break;
        }
        showInfoUtil.showMessage(mess,tag);

//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//                switch (messageType) {
//                    case MessageTag.NORMAL:
//                        message = "<font color='black'>" + mess + "</font>";
//                        break;
//                    case MessageTag.ERROR:
//                        message = "<font color='red'>" + mess + "</font>";
//                        break;
//                    case MessageTag.TIP:
//                        message = "<font color='#A0522D'>" + mess + "</font>";
//                        break;
//                    case MessageTag.DATA:
//                        message = "<font color='blue'>" + mess + "</font>";
//                        break;
//                    case MessageTag.WARN:
//                        message = "<u><font color='red'>" + mess + "</font></u>";
//                        break;
//                    default:
//                        break;
//                }
//                newMessage = message + "<br>" + newMessage;
//                tvOperationMessage.setText(Html.fromHtml(newMessage, imageGetter, null));
//            }
//        });
    }

    public void showImage(Bitmap bmp){
        showInfoUtil.showImage(bmp);
    }

    public void showPicMessage(final int id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newMessage = "<img src='" + id + "' />" + "<br>" + newMessage;
                tvOperationMessage.setText(Html.fromHtml(newMessage, imageGetter, null));
            }
        });

    }
    @Override
    protected void onDestroy() {
        System.out.println("-----------onDestroy--------");
        SDKExecutors.setThreadPoolRunning(false);
        BaseFragment.setFunRunning(false);
        setConnectState(false);
        moduleManage.destroy();
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        int id =  view.getId();
        switch (id){
            case R.id.id_clear:
                clearMessage();
                break;
            case R.id.id_keysysalg:
                choiceKeySysAlg();
                break;
        }
    }

    private void choiceKeySysAlg(){
        String[] keySysAlg = new String[]{"MKSK-DES","MKSK-SM4","MKSK-AES","DUKPT-DES","DUKPT-AES"};
        DialogUtils.createSingleChoiceDialog(this, this.getString(R.string.msg_select_key_manager), keySysAlg, new DialogUtils.SingleChoiceDialogCallback() {
            @Override
            public void onResult(int id) {
                if(id == 0){
                    AppConfig.KEY_SYS_ALG = AppConfig.MKSK_DES;
                    showMessage(MainActivity.this.getString(R.string.msg_choose_mksk_des),MessageTag.NORMAL);
                }else if(id == 1){
                    AppConfig.KEY_SYS_ALG = AppConfig.MKSK_SM4;
                    showMessage(MainActivity.this.getString(R.string.msg_choose_mksk_sm4),MessageTag.NORMAL);
                }else if(id == 2){
                    AppConfig.KEY_SYS_ALG = AppConfig.MKSK_AES;
                    showMessage(MainActivity.this.getString(R.string.msg_choose_mksk_aes),MessageTag.NORMAL);
                }else if(id == 3){
                    AppConfig.KEY_SYS_ALG = AppConfig.DUKPT_DES;
                    showMessage(MainActivity.this.getString(R.string.msg_choose_dukpt_des),MessageTag.NORMAL);
                }else if(id == 4){
                    AppConfig.KEY_SYS_ALG = AppConfig.DUKPT_AES;
                    showMessage(MainActivity.this.getString(R.string.msg_choose_dukpt_aes),MessageTag.NORMAL);
                }
            }
        });
    }
    public void setConnectState(final boolean isConnect){
        isConnectDevice = isConnect;
    }
    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
       if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {
            if (moduleIndex!=0) {
                setModuleFragment(0);
            } else {
                finish();
            }
            return false;
        }
        return super.dispatchKeyEvent(event);
    }
    public BaseFragment getFragment(int moduleIndex){
        if(moduleIndex >= modulesFragment.size() || moduleIndex < 0 ){
            return null;
        }
        if((AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)||AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG))&&
                AppConfig.INDEX_FRAGMENT_PIN == moduleIndex){
            moduleIndex = 2;
        }
        return modulesFragment.get(moduleIndex).fragment;
    }
    /*************************************************************/
    public void startOnlinePinInput(String accNo,boolean isExternalPinpad,boolean isRNIB){
        KeyManagement keyManagement = null;
        AlgorithmMode algorithmMode = null;
        pinEntryListener = AppConfig.getPinEntryListener();
        int wkPinIndex = 1;
        if (AppConfig.MKSK_DES.equals(AppConfig.KEY_SYS_ALG)) {
            wkPinIndex = AppConfig.Pin.MKSK_DES_INDEX_WK_PIN;
            keyManagement = KeyManagement.MKSK;
            algorithmMode = AlgorithmMode.DES;
        } else if (AppConfig.MKSK_SM4.equals(AppConfig.KEY_SYS_ALG)) {
            wkPinIndex = AppConfig.Pin.MKSK_SM4_INDEX_WK_PIN;
            keyManagement = KeyManagement.MKSK;
            algorithmMode = AlgorithmMode.SM4;
        } else if (AppConfig.MKSK_AES.equals(AppConfig.KEY_SYS_ALG)) {
            wkPinIndex = AppConfig.Pin.MKSK_AES_INDEX_WK_PIN;
            keyManagement = KeyManagement.MKSK;
            algorithmMode = AlgorithmMode.AES;
        } else if (AppConfig.DUKPT_DES.equals(AppConfig.KEY_SYS_ALG)) {
            wkPinIndex = AppConfig.Pin.DUKPT_DES_INDEX;
            keyManagement = KeyManagement.DUKPT;
            algorithmMode = AlgorithmMode.DES;
        }else if (AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG)) {
            wkPinIndex = AppConfig.Pin.DUKPT_AES_INDEX;
            keyManagement = KeyManagement.DUKPT;
            algorithmMode = AlgorithmMode.AES;
        }
        PinInputExtParams pinInputExtParams = new PinInputExtParams();
        if(AppConfig.DUKPT_AES.equals(AppConfig.KEY_SYS_ALG)){
            pinInputExtParams.setDukptDerivateUsage(DukptDerivateUsage.PIN);
            pinInputExtParams.setDerivateKeyLen(16);
            pinInputExtParams.setPinBlockMode(PinBlockMode.ISO9564_FORMAT_4);
        }

        if(isExternalPinpad){
            ExtPinpadModule extPinpadModule = moduleManage.getExtPinpadModule();
            PinpadInitExtParams pinpadInitExtParams = null;
            if (AppConfig.isUsePinpadByDockUSB) {
                pinpadInitExtParams = new PinpadInitExtParams(PortType.BLEBASE_USB1,null,null,null);
            }else if(AppConfig.isUsePinpadByDockRS232){
                pinpadInitExtParams = new PinpadInitExtParams(PortType.BLEBASE_RS232,null,null,null);
            }else {
                pinpadInitExtParams = new PinpadInitExtParams(PortType.USB,null,null,null);
            }
            boolean initResult = extPinpadModule.init(pinpadInitExtParams);
            if(!initResult){
                showMessage("init externl pinpad failed",MessageTag.ERROR);
                pinInputListener.onError(-1,"init externl pinpad failed");
                return;
            }
            PinpadExtParams params = new PinpadExtParams();
            params.setFirstLineMessage("input pin");
            extPinpadModule.startExternalPinInput(keyManagement,algorithmMode,AppConfig.Pin.MKSK_DES_INDEX_MK,wkPinIndex,accNo, 60, pinInputListener, params);
            return;
        }

        pinInputExtParams.setInputMaxLen(12); //1.Use the maximum value to limit the password length.
        //pinInputExtParams.setPwdLengthRange(new byte[]{0x00, 0x04, 0x08, 0x0c});//2.Restrict passwords to a specific length.
        pinInputExtParams.setAcctInputType(AccountInputType.USE_ACCOUNT);
        DefaultLayout keyBoardConfig = new DefaultLayout(true);
        keyBoardConfig.setIsHalfScreen(false);
        keyBoardConfig.setHalfScreenShowPs(false);
        keyBoardConfig.setDividerSize(0);
        keyBoardConfig.setRoundSize(0);
        //For the blind keyboard, STYLE_1 and STYLE_2 are used for devices except X800, while STYLE_3 is only for the X800 device.
        keyBoardConfig.setLayoutStyle(DefaultLayout.Style.STYLE_1);
        //keyBoardConfig.setCancelKeyAttr(new DefaultLayout.KeyAttribute(DefaultLayout.Key.CANCEL,0xfff24c4d,null,-1,-1, BitmapFactory.decodeResource(this.getResources(),R.drawable.keyboard_cancel)));
        //keyBoardConfig.setBackSpaceKeyAttr(new DefaultLayout.KeyAttribute(DefaultLayout.Key.BACKSPACE,0xfff3e250,null,-1,-1, BitmapFactory.decodeResource(this.getResources(),R.drawable.keyboard_backspace)));
        //keyBoardConfig.setConfirmAttr(new DefaultLayout.KeyAttribute(DefaultLayout.Key.CONFIRM,0xff70d145,null,-1,-1, BitmapFactory.decodeResource(this.getResources(),R.drawable.keyboard_enter)));
        //keyBoardConfig.setNumKeyAttr(new DefaultLayout.KeyAttribute(DefaultLayout.Key.NUM,0xfff5f5f9, null,-1,0xff000000,null));
        pinInputExtParams.setDefaultLayout(keyBoardConfig);


        if(isRNIB){//true indicates the blind keyboard.
            moduleManage.getPinpadModule().startPinInput(keyManagement, algorithmMode, wkPinIndex,
                    accNo, 60, rnibPinInputListener, pinInputExtParams);
        }else {
            moduleManage.getPinpadModule().startPinInput(keyManagement, algorithmMode, wkPinIndex,
                    accNo, 60, pinInputListener, pinInputExtParams);
        }

//        if(isX800()){
//            AppConfig.accNo = accNo;
//            runOnUiThread(()->{
//                Display display = getPresentationDisplay(this);
//                if(display != null ) {
//                    if(isRNIB){
//                        DRNIBKeyBoardPresentation presentation = new DRNIBKeyBoardPresentation(this,display);
//                        presentation.show();
//                    }else{
//                        KeyBoardNumberPresentation presentation = new KeyBoardNumberPresentation(this,display);
//                        presentation.show();
//                    }
//                }
//            });
//        }else{
//            if(isRNIB) {
//                Intent intent = new Intent(this, DRNIBKeyBoardActivity.class);
//                intent.putExtra("accNo", accNo);
//                startActivityForResult(intent, 002);
//            }else{
//                Intent intent = new Intent(this, KeyBoardNumberActivity.class);
//                intent.putExtra("accNo", accNo);
//                startActivityForResult(intent, 002);
//            }
//
//        }
        clearMessage();
    }
    public void startOfflinePinInput(byte[] modulus,byte[] exponent,boolean isExternalPinpad){
        PinInputExtParams pinInputExtParams = new PinInputExtParams();
        pinInputExtParams.setInputMaxLen(12);
        //pinInputExtParams.setPwdLengthRange(new byte[]{0x00, 0x04, 0x08, 0x0c});//2.Restrict passwords to a specific length.
        DefaultLayout keyBoardConfig = new DefaultLayout(false);
        pinInputExtParams.setDefaultLayout(keyBoardConfig);
        pinEntryListener = AppConfig.getPinEntryListener();
        if(isExternalPinpad){
            ExtPinpadModule extPinpadModule = moduleManage.getExtPinpadModule();
            PinpadInitExtParams pinpadInitExtParams = null;
            if (AppConfig.isUsePinpadByDockUSB) {
                pinpadInitExtParams = new PinpadInitExtParams(PortType.BLEBASE_USB1,null,null,null);
            }else if(AppConfig.isUsePinpadByDockRS232){
                pinpadInitExtParams = new PinpadInitExtParams(PortType.BLEBASE_RS232,null,null,null);
            }else {
                pinpadInitExtParams = new PinpadInitExtParams(PortType.USB,null,null,null);
            }
            extPinpadModule.init(pinpadInitExtParams);
            PinpadExtParams pinpadExtParams = new PinpadExtParams();
            pinpadExtParams.setCardInPinpad(true);
            extPinpadModule.startOfflinePinInput(AppConfig.Pin.DUKPT_DES_INDEX,AlgorithmMode.DES,60,modulus, exponent, pinInputListener,pinpadExtParams);
            return;
        }

        boolean isRNIB = false;//true indicates the blind keyboard.
        if(isRNIB){
            moduleManage.getPinpadModule().startOfflinePinInput(60,modulus, exponent, rnibPinInputListener,pinInputExtParams);
        }else {
            moduleManage.getPinpadModule().startOfflinePinInput(60,modulus, exponent, pinInputListener,pinInputExtParams);
        }
//        if(isX800()){
//            AppConfig.modulus = modulus;
//            AppConfig.exponent = exponent;
//            runOnUiThread(()->{
//                Display display = getPresentationDisplay(this);
//                if(display != null ) {
//                    if(isRNIB){
//                        DRNIBKeyBoardPresentation presentation = new DRNIBKeyBoardPresentation(this,display);
//                        presentation.show();
//                    }else {
//                        OfflineKeyBoardNumberPresentation presentation = new OfflineKeyBoardNumberPresentation(this, display);
//                        presentation.show();
//                    }
//                }
//            });
//        }else {
//            if(isRNIB) {
//                Intent intent = new Intent(this, DRNIBKeyBoardActivity.class);
//                startActivityForResult(intent, 002);
//            }else {
//                Intent intent = new Intent(this, OfflineKeyBoardNumberActivity.class);
//                intent.putExtra("modulus", modulus);
//                intent.putExtra("exponent", exponent);
//                startActivityForResult(intent, 002);
//            }
//        }
        clearMessage();
    }

    private PinInputListener pinInputListener = new PinInputListener() {
        @Override
        public void onKeyPress() {
//            showMessage("onKeyPress",MessageTag.DATA);
        }

        @Override
        public void onBackspace() {
//            showMessage("onBackspace",MessageTag.DATA);
        }

        @Override
        public void onCancel() {
            showMessage("onCancel",MessageTag.DATA);
            if(pinEntryListener!=null){
                pinEntryListener.onFinish(null);
            }
        }

        @Override
        public void onFinish(int pinblockLen, byte[] pinblock,byte[] ksn) {
            showMessage("onFinish,pinblockLen:" + pinblockLen + ";pinblock:" + (pinblock == null ? null : ISOUtils.hexString(pinblock))+"ksn:"+(ksn==null?null:ISOUtils.hexString(ksn)),MessageTag.DATA);
            if (pinblockLen == 0 && pinEntryListener!=null) {
                pinEntryListener.onFinish(new byte[]{});
            }else if(pinEntryListener!=null){
                pinEntryListener.onFinish(pinblock);
            }
        }

        @Override
        public void onTimeout() {
            showMessage("onTimeout",MessageTag.DATA);
            if(pinEntryListener!=null){
                pinEntryListener.onFinish(null);
            }
        }

        @Override
        public void onError(int errorCode, String message) {
            showMessage("onError " + errorCode + " message:" + message,MessageTag.DATA);
            if(pinEntryListener!=null){
                pinEntryListener.onFinish(null);
            }
        }
    };

    private RNIBPinInputListener rnibPinInputListener = new RNIBPinInputListener() {
        @Override
        public void onSlidNumberKey() {
            showMessage("onSlidNumberKey",MessageTag.NORMAL);
        }

        @Override
        public void onSlidNoDigitKey() {
            showMessage("onSlidNoDigitKey",MessageTag.NORMAL);
        }

        @Override
        public void onSlidBackSpace() {
            showMessage("onSlidBackSpace",MessageTag.NORMAL);
        }

        @Override
        public void onSlidEnter() {
            showMessage("onSlidEnter",MessageTag.NORMAL);
        }

        @Override
        public void onSlidCancel() {
            showMessage("onSlidCancel",MessageTag.NORMAL);
        }

        @Override
        public void onSlidUp() {
            showMessage("onSlidUp",MessageTag.NORMAL);
        }

        @Override
        public void onSlidDown() {
            showMessage("onSlidDown",MessageTag.NORMAL);
        }

        @Override
        public void onSlidLeft() {
            showMessage("onSlidLeft",MessageTag.NORMAL);
        }

        @Override
        public void onSlidRight() {
            showMessage("onSlidRight",MessageTag.NORMAL);
        }

        @Override
        public void onKeyPress() {
//            showMessage("onKeyPress",MessageTag.DATA);
        }

        @Override
        public void onBackspace() {
//            showMessage("onBackspace",MessageTag.DATA);
        }

        @Override
        public void onCancel() {
            showMessage("onCancel",MessageTag.DATA);
            if(pinEntryListener!=null){
                pinEntryListener.onFinish(null);
            }
        }

        @Override
        public void onFinish(int pinblockLen, byte[] pinblock,byte[] ksn) {
            showMessage("onFinish,pinblockLen:" + pinblockLen + ";pinblock:" + (pinblock == null ? null : ISOUtils.hexString(pinblock))+"ksn:"+(ksn==null?null:ISOUtils.hexString(ksn)),MessageTag.DATA);
            if (pinblockLen == 0 && pinEntryListener!=null) {
                pinEntryListener.onFinish(new byte[]{});
            }else if(pinEntryListener!=null){
                pinEntryListener.onFinish(pinblock);
            }
        }

        @Override
        public void onTimeout() {
            showMessage("onTimeout",MessageTag.DATA);
            if(pinEntryListener!=null){
                pinEntryListener.onFinish(null);
            }
        }

        @Override
        public void onError(int errorCode, String message) {
            showMessage("onError " + errorCode + " message:" + message,MessageTag.DATA);
            if(pinEntryListener!=null){
                pinEntryListener.onFinish(null);
            }
        }
    };
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 002) {
            if (resultCode == RESULT_OK) {
                byte[] pin = data.getByteArrayExtra("pin");
                if (pin!=null && pin.length == 0) {
                    showMessage(getString(R.string.msg_free_pwd), MessageTag.TIP);
                } else if(pin!=null && Arrays.equals(pin, new byte[8]) ){
                    Log.i("onActivityResult", getString(R.string.msg_offline_pwd_enter_succ) );
                    int pinLen = data.getIntExtra("pinLength", 0);
                    showMessage(getString(R.string.msg_offline_pwd_length)+ pinLen, MessageTag.DATA);
                }else{
                    Log.i("onActivityResult", getString(R.string.msg_enter_succ) + pin);
                    showMessage(getString(R.string.msg_enter_succ) + (pin==null?null:ISOUtils.hexString(pin)), MessageTag.DATA);
                }
            } else if (resultCode == RESULT_CANCELED) {
                showMessage(getString(R.string.msg_n900_cancel_enter_pwd) + "\r\n", MessageTag.TIP);
            }else if(resultCode == -2){
                showMessage(getString(R.string.input_pin_fail) + "\r\n", MessageTag.ERROR);
                ErrorMsg msg = ErrorMsgHelper.getInstance().getErrorMsg(PINPAD_INPUT);
                showMessage("ErrCode:"+msg.getErrCode()+" ErrMsg:"+msg.getErrMsg()+" OtherMsg:"+msg.getOtherMsg()+"\r\n", MessageTag.ERROR);
            }
        }
    }

    public class ModuleInfo {
        public BaseFragment fragment;
        public int nameId;
        public int picId;
        public ModuleInfo(BaseFragment fragment,int nameId,int picId){
            fragment.setModuleManage(moduleManage);
            this.fragment = fragment;
            this.nameId = nameId;
            this.picId = picId;
        }
    }
    public void standByLed(){
        ledOperationRunnable = new LedOperationRunnable();
        new Thread(ledOperationRunnable).start();
    }

    public class LedOperationRunnable implements Runnable {

        @Override
        public void run() {
            try {
                while(true){
                    if(!AppConfig.isStandBy){
                        continue;
                    }
                    IndicatorLightModule indicatorLightModule = moduleManage.getIndicatorLightModule();
                    Log.i("MainActivity","StandByMode led on");
                    indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE},LightState.TURNON);
                    SystemClock.sleep(200);
                    Log.i("MainActivity","StandByMode led off");
                    indicatorLightModule.operateLight(new LightColor[]{LightColor.BLUE},LightState.TURNOFF);
                    SystemClock.sleep(5000);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        public void enterStandByMode() {
            synchronized (ledObject) {
                Log.d("MainActivity","enterStandByMode");
                AppConfig.isStandBy = true;
            }
        }
        public void outStandByMode() {
            synchronized (ledObject) {
                Log.d("MainActivity","outStandByMode");
                AppConfig.isStandBy = false;
            }
        }
    }

    public static LedOperationRunnable getLedOperationRunnable() {
        return ledOperationRunnable;
    }

    private Display getPresentationDisplay(Context context){
        if(!isX800()){
            return null;
        }
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return null;
        }
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
            if(!Settings.canDrawOverlays(context)){
                return null;
            }
        }
        DisplayManager mDisplayManager = (DisplayManager) context.getSystemService(Context.DISPLAY_SERVICE);
        Display[] displays = mDisplayManager.getDisplays();
        if(null == displays || displays.length <= 1){
            return null;
        }
        return displays[displays.length -1];
    }
    private static boolean isX800(){
        return "X800".equals(NlBuild.VERSION.MODEL);
    }
}
