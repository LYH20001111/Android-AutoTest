package com.newland.nsdkdemo.common;

import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.FileProvider;
import android.text.Html;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.newland.nsdk.core.api.common.exception.NSDKException;
import com.newland.nsdk.core.api.common.utils.ISOUtils;
import com.newland.nsdk.core.api.common.utils.LogUtils;
import com.newland.nsdk.core.api.common.utils.NativeDebugLevel;
import com.newland.nsdk.core.internal.NSDKModuleManagerImpl;
import com.newland.nsdkdemo.R;
import com.newland.nsdkdemo.common.event.ModuleClickListener;
import com.newland.nsdkdemo.common.fragment.BaseFragment;
import com.newland.nsdkdemo.common.fragment.BridgeFragment;
import com.newland.nsdkdemo.common.fragment.MenuFragment;
import com.newland.nsdkdemo.common.fragment.ModuleInfo;
import com.newland.nsdkdemo.common.fragment.ModulesFragment;
import com.newland.nsdkdemo.common.utils.DialogUtils;
import com.newland.nsdkdemo.common.utils.MessageTag;
import com.newland.nsdkdemo.external.ExtInitiator;
import com.newland.nsdkdemo.internal.Initiator;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class MainActivity extends FragmentActivity implements View.OnClickListener {
    public static final String IMAGE_TYPE_ID = "ID";
    public static final String IMAGE_TYPE_HEX_STRING = "Hex string";
    public static final int MENU_CONNECT_DEVICE = 1;
    public static final int MENU_PING = 2;
    public static final int MENU_DISCONNECT = 3;
    public static final int MENU_SET_DEBUG_MODE = 4;

    public static final int DEVICE_MODE_POS = 0;
    public static final int DEVICE_MODE_POS_EXT = 1;
    public static final int DEVICE_MODE_PHONE_EXT = 2;
    public static ArrayList<ModuleInfo> moduleFragments = new ArrayList<ModuleInfo>();
    private static final int REQUEST_TAKE_PHOTO = 1;
    private static final int REQUEST_BARCODE_SCANNER = 2;
    private ActionBar actionBar;
    private int moduleIndex = 0;
    private String newMessage = "", message = "";
    private TextView tvOperationMessage;
    private Html.ImageGetter imageGetter;
    private Button btnClear;
    private boolean isConnectDevice;
    private FrameLayout moduleMenu, moduleContent;
    private File file;

    private int deviceMode = DEVICE_MODE_POS_EXT;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();
        initData();
        EventBus.getDefault().register(this);
    }

    private void initData() {
        isConnectDevice = false;

        if (deviceMode == DEVICE_MODE_POS || deviceMode == DEVICE_MODE_POS_EXT) {
            Initiator.getInstance().init(this);
        }
        if (deviceMode == DEVICE_MODE_PHONE_EXT || deviceMode == DEVICE_MODE_POS_EXT) {
            ExtInitiator.getInstance().init(this);
        }

        SDKExecutors.setThreadPoolRunning(true);
        setConnectState(true);
        initModuleFragments();
    }

    private void initModuleFragments() {
        moduleFragments.clear();
        moduleFragments.add(new ModuleInfo(new ModulesFragment(this, new ModuleClickListener() {
            @Override
            public void onModuleClickListener(int index) {
                if (!isConnectDevice) {
                    showMessage(MainActivity.this.getString(R.string.msg_device_conn_first), MessageTag.TIP);
                    return;
                }
                setModuleFragment(index);
            }
        }), 0, 0));
        moduleFragments.add(new ModuleInfo(new MenuFragment(this, new ModuleClickListener() {
            @Override
            public void onModuleClickListener(int index) {
                setModuleFragment(index);
            }
        }), 0, 0));

        AppConfig.INDEX_FRAGMENT_START = 2;

        if (deviceMode == DEVICE_MODE_POS || deviceMode == DEVICE_MODE_POS_EXT) {
            Initiator.getInstance().addFragments(moduleFragments);
        }

        if (deviceMode == DEVICE_MODE_PHONE_EXT || deviceMode == DEVICE_MODE_POS_EXT) {
            ExtInitiator.getInstance().addFragments(moduleFragments);
        }
        setModuleFragment(0);
    }

    private void setModuleFragment(int index) {
        if (index >= moduleFragments.size()) {
            return;
        }
        if (index == 0) {
            moduleMenu.setVisibility(View.GONE);
        } else {
            moduleMenu.setVisibility(View.VISIBLE);
        }
        moduleIndex = index;
        String title = moduleFragments.get(moduleIndex).fragment.title();
        if (title == null) {
            title = "";
        }
        String fragmentName = moduleFragments.get(moduleIndex).fragment.getClass().getSimpleName();
        actionBar.setTitle(title + "(" + fragmentName + ")");
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction ft = manager.beginTransaction();
        if (index != 0) {
            ft.replace(R.id.id_module_menu, new BridgeFragment(getBasePager(1)));
        }
        ft.replace(R.id.id_module_content, new BridgeFragment(getBasePager(index)));
        ft.commit();
    }

    private BaseFragment getBasePager(int index) {
        try {
            BaseFragment baseFragment = moduleFragments.get(index).fragment;
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
        switch (item.getItemId()) {
            case android.R.id.home:
                setModuleFragment(0);
                return super.onOptionsItemSelected(item);
            case MENU_CONNECT_DEVICE:
                ExtInitiator.getInstance().switchConnectType();
                break;
            case MENU_PING:
                ExtInitiator.getInstance().ping();
                break;
            case MENU_DISCONNECT:
                ExtInitiator.getInstance().disconnect();
                break;
            case MENU_SET_DEBUG_MODE:
                setDebugMode();
                break;
            default:
                break;
        }

        return true;
    }

    private void initUI() {
        actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        tvOperationMessage = findViewById(R.id.id_info);
        btnClear = findViewById(R.id.id_clear);
        btnClear.setOnClickListener(this);

        imageGetter = new Html.ImageGetter() {
            @Override
            public Drawable getDrawable(String source) {
                String[] imageInfo = source.split(",");
                Drawable drawable = null;
                if (IMAGE_TYPE_ID.equals(imageInfo[0])) {
                    int id = Integer.parseInt(imageInfo[1]);
                    drawable = getResources().getDrawable(id);
                    if (getWindowWidth() <= drawable.getIntrinsicWidth()) {
                        drawable.setBounds(0, 10, getWindowWidth(), drawable.getIntrinsicHeight());
                    } else {
                        drawable.setBounds((getWindowWidth() - drawable.getIntrinsicWidth()) / 2, 10, drawable.getIntrinsicWidth() + (getWindowWidth() - drawable.getIntrinsicWidth()) / 2, drawable.getIntrinsicHeight());
                    }
                } else if (IMAGE_TYPE_HEX_STRING.equals(imageInfo[0])) {
                    byte[] data = ISOUtils.hex2byte(imageInfo[1]);
                    Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                    drawable = new BitmapDrawable(getResources(), bitmap);
                    drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
                }

                return drawable;
            }
        };
        moduleMenu = findViewById(R.id.id_module_menu);
        moduleMenu.setVisibility(View.GONE);
        moduleContent = findViewById(R.id.id_module_content);
    }

    private int getWindowWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    public void clearMessage() {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tvOperationMessage.setText("");
                newMessage = "";
            }
        });
    }

    public void showMessage(final String mess, final int messageType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (messageType) {
                    case MessageTag.NORMAL:
                        message = "<font color='black'>" + mess + "</font>";
                        break;
                    case MessageTag.ERROR:
                        message = "<font color='red'>" + mess + "</font>";
                        break;
                    case MessageTag.TIP:
                        message = "<font color='green'>" + mess + "</font>";
                        break;
                    case MessageTag.DATA:
                        message = "<font color='blue'>" + mess + "</font>";
                        break;
                    case MessageTag.WARN:
                        message = "<u><font color='red'>" + mess + "</font></u>";
                        break;
                    default:
                        break;
                }
                newMessage = message + "<br>" + newMessage;
                tvOperationMessage.setText(Html.fromHtml(newMessage, imageGetter, null));
            }
        });
    }

    public void updateFirstLineMessage(final String mess, final int messageType) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                switch (messageType) {
                    case MessageTag.NORMAL:
                        message = "<font color='black'>" + mess + "</font>";
                        break;
                    case MessageTag.ERROR:
                        message = "<font color='red'>" + mess + "</font>";
                        break;
                    case MessageTag.TIP:
                        message = "<font color='green'>" + mess + "</font>";
                        break;
                    case MessageTag.DATA:
                        message = "<font color='blue'>" + mess + "</font>";
                        break;
                    case MessageTag.WARN:
                        message = "<u><font color='red'>" + mess + "</font></u>";
                        break;
                    default:
                        break;
                }
                String breakTag = "<br>";
                newMessage = newMessage.substring(newMessage.indexOf(breakTag) + breakTag.length());
                newMessage = message + "<br>" + newMessage;
                tvOperationMessage.setText(Html.fromHtml(newMessage, imageGetter, null));
            }
        });
    }

    public void showPicMessage(final int id) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newMessage = "<img src='" + IMAGE_TYPE_ID + "," + id + "'/>" + "<br>" + newMessage;
                tvOperationMessage.setText(Html.fromHtml(newMessage, imageGetter, null));
            }
        });
    }

    public void showPicMessage(final String hexString) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                newMessage = "<img src='" + IMAGE_TYPE_HEX_STRING + "," + hexString + "'/>" + "<br>" + newMessage;
                tvOperationMessage.setText(Html.fromHtml(newMessage, imageGetter, null));
            }
        });
    }

    @Override
    protected void onDestroy() {
        SDKExecutors.setThreadPoolRunning(false);
        BaseFragment.setFunRunning(false);
        setConnectState(false);

        if (deviceMode == DEVICE_MODE_POS || deviceMode == DEVICE_MODE_POS_EXT) {
            Initiator.getInstance().release();
        }
        if (deviceMode == DEVICE_MODE_PHONE_EXT || deviceMode == DEVICE_MODE_POS_EXT) {
            ExtInitiator.getInstance().release();
        }

        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.id_clear:
                clearMessage();
                break;
        }
    }

    public void setConnectState(final boolean isConnect) {
        isConnectDevice = isConnect;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getKeyCode() == KeyEvent.KEYCODE_BACK && event.getAction() != KeyEvent.ACTION_UP) {
            if (moduleIndex != 0) {
                setModuleFragment(0);
            } else {
                finish();
            }
            return false;
        }
        return super.dispatchKeyEvent(event);
    }

    public BaseFragment getFragment(int moduleIndex) {
        if (moduleIndex >= moduleFragments.size() || moduleIndex < 0) {
            return null;
        }
        return moduleFragments.get(moduleIndex).fragment;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (deviceMode == DEVICE_MODE_POS_EXT || deviceMode == DEVICE_MODE_PHONE_EXT) {
            menu.add(Menu.NONE, MENU_CONNECT_DEVICE, MENU_CONNECT_DEVICE, getString(R.string.tv_extcomm_connect_device));
            menu.add(Menu.NONE, MENU_PING, MENU_PING, getString(R.string.tv_extcomm_ping));
            menu.add(Menu.NONE, MENU_DISCONNECT, MENU_DISCONNECT, getString(R.string.tv_extcomm_disconnect));
            menu.add(Menu.NONE, MENU_SET_DEBUG_MODE, MENU_SET_DEBUG_MODE, getString(R.string.tv_set_debug_mode));
        }
        return true;
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(MessageEvent event) {
        if(event.getMessageType() == MessageTag.ERROR) {
//            String errMsg = event.getMessage();
//            String[] splitErrMsg = errMsg.split("=");
//            int errCode = Integer.parseInt(splitErrMsg[1].trim());
//            showMessage(NSDKModuleManagerImpl.getInstance().getErrMsg(errCode), MessageTag.ERROR);
            showMessage(event.getMessage(), MessageTag.ERROR);
        }else {
            showMessage(event.getMessage(), event.getMessageType());
        }
    };

    public interface onActivityResultListener {
        void onActivityResult(Bitmap bitmap);
    }
    private onActivityResultListener listener;
    public void setListener(onActivityResultListener listener, File file) {
        this.listener = listener;
        this.file = file;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (listener != null && file != null) {
            if (requestCode == REQUEST_TAKE_PHOTO) {
                if (resultCode == Activity.RESULT_OK) {
                    try {
                        InputStream in = getContentResolver().openInputStream(FileProvider.getUriForFile(this, "com.newland.nsdkdemo.fileprovider", file));
                        Bitmap bitmap = BitmapFactory.decodeStream(in);
                        listener.onActivityResult(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    listener.onActivityResult(null);
                }
            }
        }
        if (requestCode == REQUEST_BARCODE_SCANNER) {
            if (resultCode == Activity.RESULT_OK && data != null) {
                String result = data.getStringExtra(AppConfig.SharedPreferenceConfig.SINGLE_SCANNING_RESULT);
                showMessage(result, MessageTag.NORMAL);
            }
        }

    }

    private void setDebugMode() {
        DialogUtils.createCustomDialog(this, getString(R.string.tv_set_debug_mode), null, R.layout.dialog_set_debug_mode, new DialogUtils.CustomDialogCallback() {
            @Override
            public void onResult(int id, View view) {
                RadioButton rbOn = view.findViewById(R.id.rb_setDebugMode_on);
                RadioButton rbOff = view.findViewById(R.id.rb_setDebugMode_off);
                NativeDebugLevel nativeDebugLevel = NativeDebugLevel.ALL_OFF;
                if (rbOn.isChecked()) {
                    nativeDebugLevel = NativeDebugLevel.ALL_ON;
                }
                if (rbOff.isChecked()) {
                    nativeDebugLevel = NativeDebugLevel.ALL_OFF;
                }

                try {
                    NSDKModuleManagerImpl.getInstance().setNativeDebugMode(nativeDebugLevel);
                    showMessage(getString(R.string.tv_set_debug_mode), MessageTag.NORMAL);
                } catch (NSDKException e) {
                    showMessage(e.getMessage(), MessageTag.ERROR);
                }
            }
        });
    }

}
